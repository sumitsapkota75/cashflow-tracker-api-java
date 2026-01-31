package org.braketime.machinetrackerapi.services;

import org.braketime.machinetrackerapi.Dtos.UpdatePaymentPlanRequest;
import org.braketime.machinetrackerapi.Dtos.WinnerCreateRequest;
import org.braketime.machinetrackerapi.domain.Period;
import org.braketime.machinetrackerapi.domain.Winner;
import org.braketime.machinetrackerapi.domain.WinnerPayout;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.repository.PeriodRepositoy;
import org.braketime.machinetrackerapi.repository.WinnerRepository;
import org.braketime.machinetrackerapi.repository.WinnerPayoutRepository;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WinnerService {
    private final PeriodRepositoy periodRepositoy;
    private final PeriodService periodService;
    private WinnerRepository winnerRepository;
    private final WinnerPayoutRepository winnerPayoutRepository;


    public Winner createWinner(WinnerCreateRequest request) {
        String userName = SecurityUtils.username();
        String businessId = SecurityUtils.businessId();


        // calculate remaining amount
        BigDecimal totalWin = Optional.ofNullable(request.getTotalWinAmount())
                .orElse(BigDecimal.ZERO);

        BigDecimal amountPaid = Optional.ofNullable(request.getAmountPaid())
                .orElse(BigDecimal.ZERO);

        BigDecimal remainingAmount = totalWin.subtract(amountPaid);

        String status;

        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            status = "PAID";
        }
        else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            status = "PARTIALLY_PAID";
        }
        else {
            status = "PENDING";
        }



        Winner winner = Winner.builder()
                .playerName(request.getPlayerName())
                .playerContact(request.getPlayerContact())
                .winningDate(request.getWinningDate())
            .totalWinAmount(request.getTotalWinAmount())
                .amountPaid(request.getAmountPaid())
                .paymentPlan(request.getPaymentPlan())
                .remainingAmount(remainingAmount)
                .status(status)
                .businessId(businessId)
                .createdByUsername(userName)
                .createdAt(LocalDateTime.now())
                .build();

        Winner winnerData = winnerRepository.save(winner);

        // get active period:
        Period activePeriod = periodService.getOpenPeriod();
        // create a winner payout object
        if (amountPaid.compareTo(BigDecimal.ZERO) >= 0){
            WinnerPayout payout = new WinnerPayout();
            payout.setWinnerId(winnerData.getId());
            payout.setWinnerName(winnerData.getPlayerName());
            payout.setAmount(amountPaid);
            payout.setBusinessId(businessId);
            payout.setPeriodId(activePeriod.getId());
            payout.setPayoutDate(LocalDateTime.now());
            payout.setStatus("IN_PROGRESS");
            payout.setRemarks("Initial Payout");
            payout.setReasonType("WINNER_PAYOUT");
            payout.setCreatedByUser(userName);

            winnerPayoutRepository.save(payout);
        }

        return winnerData;

    }

    public Winner updatePaymentPlan(UpdatePaymentPlanRequest request, String winnerId){
        Winner winner = winnerRepository.findById(winnerId).orElseThrow(()-> new NotFoundException("Winner not found for provided id"));
        winner.setPaymentPlan(request.getPaymentPlan());
        return winnerRepository.save(winner);

    }

    public List<Winner> getWinnerByBusinessId() {
        String businessId = SecurityUtils.businessId();
        return winnerRepository.findAllByBusinessIdOrderByCreatedAtDesc(businessId)
                .orElseThrow(() -> new NotFoundException("Winner not found for businessId: " + businessId));
    }

    public Winner getWinnerById(String winnerId){
        return winnerRepository.findById(winnerId).orElseThrow(()-> new NotFoundException("Winner not found"));
    }
}
