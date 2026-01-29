package org.braketime.machinetrackerapi.Dtos;


import lombok.Data;

import java.util.List;

@Data
public class UpdatePaymentPlanRequest {
    private List<PaymentPlan> paymentPlan;
}
