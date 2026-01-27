package org.braketime.machinetrackerapi.services;

import lombok.AllArgsConstructor;
import org.braketime.machinetrackerapi.Dtos.WinnerPayoutCreateRequest;
import org.braketime.machinetrackerapi.domain.Winner;
import org.braketime.machinetrackerapi.domain.WinnerPayout;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.repository.WinnerPayoutRepository;
import org.braketime.machinetrackerapi.repository.WinnerRepository;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class WinnerPayoutService {

    private final WinnerPayoutRepository winnerPayoutRepository;
    private final WinnerRepository winnerRepository;

    public WinnerPayout createPayout(WinnerPayoutCreateRequest request){

    String userName = SecurityUtils.username();
    String businessId = SecurityUtils.businessId();

    // get winner data
    Winner winner = null;
    String status = null;
    boolean isWinnerPayout = "WINNER_PAYOUT".equals(request.getReasonType());
    String winnerId = request.getWinnerID();

    if (isWinnerPayout && winnerId != null && !winnerId.isBlank()) {
        winner = winnerRepository.findById(winnerId)
                .orElseThrow(() -> new NotFoundException("Winner not found"));

        BigDecimal payoutAmount = request.getAmount() == null ? BigDecimal.ZERO : request.getAmount();
        BigDecimal currentPaid = winner.getAmountPaid() == null ? BigDecimal.ZERO : winner.getAmountPaid();
        BigDecimal totalWin = winner.getTotalWinAmount() == null ? BigDecimal.ZERO : winner.getTotalWinAmount();;

        // New total paid amount
        BigDecimal newAmountPaid = currentPaid.add(payoutAmount);
        winner.setAmountPaid(newAmountPaid);

        // Remaining amount
        BigDecimal remaining = totalWin.subtract(newAmountPaid);
        winner.setRemainingAmount(remaining.max(BigDecimal.ZERO));

        if (newAmountPaid.compareTo(totalWin) >= 0) {
            status = "PAID";
        }
        else if (newAmountPaid.compareTo(BigDecimal.ZERO) > 0) {
            status = "PARTIALLY_PAID";
        }
        else {
            status = "PENDING";
        }
        winner.setStatus(status);
        winnerRepository.save(winner);
    }

    WinnerPayout winnerPayout = WinnerPayout.builder()
            .winnerId(winnerId)
            .businessId(businessId)
            .winnerName(request.getWinnerName())
            .createdByUser(userName)
            .amount(request.getAmount())
            .payoutDate(LocalDateTime.now())
            .reasonType(request.getReasonType())
            .status(status)
            .remarks(request.getRemarks())
            .build();

    return winnerPayoutRepository.save(winnerPayout);

    }

    public List<WinnerPayout> getPayoutsByWinnerId(String winnerId){
        return winnerPayoutRepository.findAllByWinnerIdOrderByPayoutDateDesc(winnerId);
    }
    public Page<WinnerPayout> getPayoutsByBusinessId(Pageable pageable){
        String businessId = SecurityUtils.businessId();
        return winnerPayoutRepository.findAllByBusinessIdOrderByPayoutDateDesc(businessId,pageable);
    }
}
