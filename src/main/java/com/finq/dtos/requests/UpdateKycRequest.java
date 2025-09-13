package com.finq.dtos.requests;

import com.finq.enums.KycStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateKycRequest {
    private KycStatus kycStatus;
    private String rejectionReason;
}

