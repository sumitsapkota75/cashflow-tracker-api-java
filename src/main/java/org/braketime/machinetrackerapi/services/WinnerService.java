package org.braketime.machinetrackerapi.services;

import org.braketime.machinetrackerapi.Dtos.WinnerCreateRequest;
import org.braketime.machinetrackerapi.domain.Winner;
import org.braketime.machinetrackerapi.exception.NotFoundException;
import org.braketime.machinetrackerapi.repository.WinnerRepository;
import org.braketime.machinetrackerapi.security.SecurityUtils;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.List;

@Service
@AllArgsConstructor
public class WinnerService {
    private final WinnerRepository winnerRepository;


    public Winner createWinner(WinnerCreateRequest request) {
        String userName = SecurityUtils.username();
        String businessId = SecurityUtils.businessId();

        // calculate remaining amount
        if (request.getAmountPaid() != null) {
            request.setRemainingAmount(request.getTotalWinAmount().subtract(request.getAmountPaid()));
        } else {
            request.setRemainingAmount(request.getTotalWinAmount());
        }

        Winner winner = Winner.builder()
                .playerName(request.getPlayerName())
                .playerContact(request.getPlayerContact())
                .winningDate(request.getWinningDate())
                .totalWinAmount(request.getTotalWinAmount())
                .amountPaid(request.getAmountPaid())
                .remainingAmount(request.getRemainingAmount())
                .status(request.getStatus())
                .businessId(businessId)
                .createdByUsername(userName)
                .build();

        return winnerRepository.save(winner);
    }

    public List<Winner> getWinnerByBusinessId() {
        String businessId = SecurityUtils.businessId();
        return winnerRepository.findAllByBusinessIdOrderByCreatedAtDesc(businessId)
                .orElseThrow(() -> new NotFoundException("Winner not found for businessId: " + businessId));
    }
}
