package com.yes.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChargeDto implements Serializable {
    private Long userId;
    private Long amount;
    private String paymentMethod;
}
