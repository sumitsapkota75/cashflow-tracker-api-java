package org.braketime.machinetrackerapi.services;

import org.braketime.machinetrackerapi.Dtos.WinnerCreateRequest;
import org.braketime.machinetrackerapi.domain.Winner;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.repository.WinnerRepository;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class WinnerService {
    private final WinnerRepository winnerRepository;


    public Winner createWinner(WinnerCreateRequest request) {
        String userName = SecurityUtils.username();
        String businessId = SecurityUtils.businessId();


        // calculate remaining amount
        BigDecimal remainingAmount = BigDecimal.ZERO;
        BigDecimal amountPaid = request.getAmountPaid() == null ? BigDecimal.ZERO : request.getAmountPaid();
        remainingAmount = request.getTotalWinAmount().subtract(amountPaid);

        // hadnle status
        String status;
        if (remainingAmount.compareTo(request.getTotalWinAmount()) <= 0){
            status = "PAID";
        }else {
            status = request.getStatus();
        }

        Winner winner = Winner.builder()
                .playerName(request.getPlayerName())
                .playerContact(request.getPlayerContact())
                .winningDate(request.getWinningDate())
                .totalWinAmount(request.getTotalWinAmount())
                .amountPaid(request.getAmountPaid())
                .remainingAmount(remainingAmount)
                .status(status)
                .businessId(businessId)
                .createdByUsername(userName)
                .createdAt(LocalDateTime.now())
                .build();

        return winnerRepository.save(winner);
    }

    public List<Winner> getWinnerByBusinessId() {
        String businessId = SecurityUtils.businessId();
        return winnerRepository.findAllByBusinessIdOrderByCreatedAtDesc(businessId)
                .orElseThrow(() -> new NotFoundException("Winner not found for businessId: " + businessId));
    }
}
