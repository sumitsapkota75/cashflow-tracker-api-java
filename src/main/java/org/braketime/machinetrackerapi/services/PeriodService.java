package org.braketime.machinetrackerapi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.braketime.machinetrackerapi.Dtos.ClosePeriodRequest;
import org.braketime.machinetrackerapi.Dtos.ImageUploadResult;
import org.braketime.machinetrackerapi.Dtos.OpenPeriodRequest;
import org.braketime.machinetrackerapi.Dtos.PeriodResponse;
import org.braketime.machinetrackerapi.domain.Period;
import org.braketime.machinetrackerapi.enums.PeriodStatus;
import org.braketime.machinetrackerapi.exception.BadRequestException;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.mapper.PeriodMapper;
import org.braketime.machinetrackerapi.repository.PeriodRepositoy;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeriodService {
    private final PeriodRepositoy periodRepository;
    private final PeriodMapper periodMapper;
    private final ImageStorageService imageStorageService;

    public PeriodResponse openPeriod(OpenPeriodRequest request, String userId){
        String businessId = SecurityUtils.businessId();
        LocalDate businessDate =
                request.getBusinessDate() != null
                        ? request.getBusinessDate()
                        : LocalDate.now();

        // make sure there exists only one open period for the business
        boolean alreadyOpen =
                periodRepository.existsByBusinessIdAndBusinessDateAndStatus(
                        businessId,
                        businessDate,
                        PeriodStatus.OPEN
                );

        if (alreadyOpen) {
            throw new BadRequestException("Period already open for this business date");
        }

        Period period = periodMapper.toEntity(request);

        period.setBusinessDate(businessDate);
        period.setStatus(PeriodStatus.OPEN);
        period.setOpenedAt(LocalDateTime.now());
        period.setOpenedByUserId(userId);
        period.setBusinessId(businessId);
        period.setNetOpen(request.getTotalCashInOpen().subtract(request.getTotalCashOutOpen()));

        periodRepository.save(period);
        return periodMapper.toResponse(period);
    }


    public PeriodResponse closePeriod(ClosePeriodRequest request, List<MultipartFile> files, String userId){
        Period period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(()-> new NotFoundException("Selected period not found"));

        if (period.getStatus() == PeriodStatus.CLOSED){
            throw new IllegalStateException("Period already closed");
        }


        // ⚠️ Placeholder:
        // populate total safe drop amount, and total payout in period
        // get all the payouts for period.


        periodMapper.update(request, period);
        period.setStatus(PeriodStatus.CLOSED);
        period.setTotalCashInClose(request.getTotalCashInClose());
        period.setTotalCashOutClose(request.getTotalCashOutClose());
        period.setClosedAt(LocalDateTime.now());
        period.setClosedByUserId(userId);
        period.setNetClose(request.getTotalCashInClose().subtract(request.getTotalCashOutClose()));

        List<ImageUploadResult> uploads = List.of();
        if (files != null && !files.isEmpty()) {
            uploads = imageStorageService.uploadImages(files);
            List<String> urls = uploads.stream()
                    .map(ImageUploadResult::getUrl)
                    .collect(Collectors.toList());
            if (period.getImages() == null) {
                period.setImages(new ArrayList<>());
            }
            period.getImages().addAll(urls);
        }

        try {
            periodRepository.save(period);
            return periodMapper.toResponse(period);
        } catch (Exception ex) {
            List<String> fileIds = uploads.stream()
                    .map(ImageUploadResult::getFileId)
                    .collect(Collectors.toList());
            imageStorageService.deleteImagesById(fileIds);
            if (ex instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new RuntimeException("Failed to close period with images", ex);
        }

    }



    public PeriodResponse getActivePeriod(String businessId) {
        return periodRepository.findByBusinessIdAndStatus(
                businessId, PeriodStatus.OPEN
        ).map(periodMapper::toResponse).orElseThrow(() ->
                new NotFoundException("No active period found")
        );
    }

    // get all periods for business ID
    public Page<PeriodResponse> getAllPeriod(String businessId, Pageable pageable) {
        return periodRepository.findAllByBusinessId(businessId,pageable)
                .map(periodMapper::toResponse);
    }

    // get period by period_id
    public PeriodResponse getPeriodById(String periodId) {
        return periodRepository.findById(periodId).map(periodMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Period not found"));
    }

    // get open period
    public Period getOpenPeriod(){
        String businessID = SecurityUtils.businessId();
        return periodRepository.findFirstByBusinessIdAndStatusOrderByOpenedAtDesc(businessID,PeriodStatus.OPEN).orElseThrow(()->new NotFoundException("Open period not found"));
    }

    // get most recent closed period
    public PeriodResponse getRecentClosedPeriod(){
        String businessID = SecurityUtils.businessId();
        return periodRepository.findFirstByBusinessIdAndStatusOrderByOpenedAtDesc(businessID,PeriodStatus.CLOSED)
                .map(periodMapper::toResponse)
                .orElseThrow(()->new NotFoundException("Most recent closed period not found"));

    }

}
