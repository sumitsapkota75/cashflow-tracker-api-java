package org.braketime.machinetrackerapi.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.BusinessRequest;
import org.braketime.machinetrackerapi.Dtos.ImageUploadResult;
import org.braketime.machinetrackerapi.Dtos.MachineEntryRequest;
import org.braketime.machinetrackerapi.Dtos.MachineEntryResponse;
import org.braketime.machinetrackerapi.domain.MachineEntry;
import org.braketime.machinetrackerapi.domain.Period;
import org.braketime.machinetrackerapi.enums.PeriodStatus;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.mapper.MachineEntryMapper;
import org.braketime.machinetrackerapi.repository.MachineEntryRepository;
import org.braketime.machinetrackerapi.repository.PeriodRepositoy;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class MachineEntryService {

    private final PeriodRepositoy periodRepositoy;
    private final MachineEntryMapper machineEntryMapper;
    private final MachineEntryRepository machineEntryRepository;
    private final ImageStorageService imageStorageService;

    public MachineEntryResponse createEntry(MachineEntryRequest request, List<MultipartFile> files, String username) {
        final String userId = SecurityUtils.userId();
        final String businessId = SecurityUtils.businessId();
        final LocalDateTime now = LocalDateTime.now();

        // 1) Fetch open period
        final Period openPeriod = periodRepositoy.findByBusinessIdAndStatus(businessId, PeriodStatus.OPEN)
                .orElseThrow(() -> new NotFoundException("No open period found"));

        // 2) Validate machineId
        final String machineId = request.getMachineId();
        if (machineId == null || machineId.isBlank()) {
            throw new IllegalArgumentException("machineId is required");
        }

        // 3) Previous entry (same machine, same period)
        final MachineEntry prev = machineEntryRepository
                .findTopByPeriodIdAndMachineIdOrderByOpenedAtDesc(openPeriod.getId(), machineId);

        // 4) Normalize inputs (null-safe)
        final BigDecimal currIn = z(request.getReportCashIn());
        final BigDecimal currOut = z(request.getReportCashOut()); // stored for reporting only (not used in calc)
        final BigDecimal physical = z(request.getPhysicalCash());
        final BigDecimal safeDrop = z(request.getSafeDroppedAmount());

        // 5) Calculate difference (CASH-IN ONLY)
        final BigDecimal prevIn = (prev == null) ? null : z(prev.getReportCashIn());
        final BigDecimal deltaIn = (prev == null) ? null : currIn.subtract(prevIn);

        // first entry in period => per your rule: difference = 0
        final BigDecimal expectedPhysical = (prev == null) ? null : deltaIn;
        final BigDecimal difference;
        final boolean hasPreviousEntry = (prev != null);

        if (prev == null) {
            difference = BigDecimal.ZERO;
        } else {
            if (deltaIn.compareTo(BigDecimal.ZERO) < 0) {
                log.error(
                        "[MACHINE_ENTRY_CALC_ERROR] cashIn decreased | businessId={} periodId={} machineId={} prevIn={} currIn={} deltaIn={}",
                        businessId, openPeriod.getId(), machineId, prevIn, currIn, deltaIn
                );
                throw new IllegalStateException("Machine report cash-in decreased; possible reset or wrong report entered.");
            }
            difference = physical.subtract(expectedPhysical);
        }

        final String status = (difference.compareTo(BigDecimal.ZERO) == 0) ? "MATCHED" : "MISMATCH";

        // --- Single audit log line (easy to verify) ---
        log.info(
                "[MACHINE_ENTRY_AUDIT] mode=CASH_IN_ONLY businessId={} periodId={} machineId={} userId={} username={} " +
                        "prevEntryId={} prevIn={} currIn={} deltaIn={} expectedPhysical={} physicalCash={} difference={} status={} " +
                        "reportCashOut(ignored)={} safeDrop={}",
                businessId, openPeriod.getId(), machineId, userId, username,
                (prev == null ? "NONE" : prev.getId()),
                (prevIn == null ? "N/A" : prevIn),
                currIn,
                (deltaIn == null ? "N/A" : deltaIn),
                (expectedPhysical == null ? "N/A" : expectedPhysical),
                physical,
                difference,
                status,
                currOut,
                safeDrop
        );

        // 6) Build entity + set server-controlled fields
        final MachineEntry entry = machineEntryMapper.toEntity(request);
        entry.setBusinessId(businessId);
        entry.setOpenedByUserId(userId);
        entry.setPeriodId(openPeriod.getId());
        entry.setOpenedAt(now);
        entry.setUsername(username);
        entry.setHasPreviousEntry(hasPreviousEntry);
        entry.setDifference(difference);
        entry.setStatus(status);

        List<ImageUploadResult> uploads = List.of();
        if (files != null && !files.isEmpty()) {
            uploads = imageStorageService.uploadImages(files);
            List<String> urls = uploads.stream()
                    .map(ImageUploadResult::getUrl)
                    .collect(Collectors.toList());
            if (entry.getImages() == null) {
                entry.setImages(new ArrayList<>());
            }
            entry.getImages().addAll(urls);
        }

        try {
            machineEntryRepository.save(entry);
        } catch (Exception ex) {
            List<String> fileIds = uploads.stream()
                    .map(ImageUploadResult::getFileId)
                    .collect(Collectors.toList());
            imageStorageService.deleteImagesById(fileIds);
            if (ex instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException("Failed to save machine entry with images", ex);
        }

        log.info("[MACHINE_ENTRY_SAVED] entryId={} status={} difference={}", entry.getId(), status, difference);

        // 7) Aggregate into Period
        final BigDecimal periodSafeDropBefore = z(openPeriod.getSafeDrop());
        final BigDecimal periodPhysicalBefore = z(openPeriod.getPhysicalCashCollected());

        final BigDecimal periodSafeDropAfter = periodSafeDropBefore.add(safeDrop);
        final BigDecimal periodPhysicalAfter = periodPhysicalBefore.add(physical);

        log.info(
                "[PERIOD_AGGREGATE_AUDIT] periodId={} safeDrop: {} + {} = {} | physicalCashCollected: {} + {} = {}",
                openPeriod.getId(),
                periodSafeDropBefore, safeDrop, periodSafeDropAfter,
                periodPhysicalBefore, physical, periodPhysicalAfter
        );

        openPeriod.setSafeDrop(periodSafeDropAfter);
        openPeriod.setPhysicalCashCollected(periodPhysicalAfter);
        periodRepositoy.save(openPeriod);

        return machineEntryMapper.toResponse(entry);
    }





    public List<MachineEntryResponse> getEntriesForPeriod(
            String periodID, String businessId,LocalDate startDate, LocalDate endDate
    ){
        List<MachineEntry> entries;
        if (startDate != null && endDate != null) {
            entries =  machineEntryRepository.findByBusinessIdAndOpenedAtBetween(
                    businessId,
                    startDate.atStartOfDay(),
                    endDate.atTime(LocalTime.MAX)
            );
        } else {
            log.info("business id and period id: {} {}",businessId,periodID);
            entries =  machineEntryRepository.findByBusinessIdAndPeriodIdOrderByOpenedAtDesc(businessId, periodID);
        }
        return entries.stream().map(machineEntryMapper::toResponse).toList();

    }

    public MachineEntry getLastMachineEntryForPeriod(String periodID,String machineId){
        return machineEntryRepository.findTopByPeriodIdAndMachineIdOrderByOpenedAtDesc(periodID,machineId);
    }

    private static BigDecimal z(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
