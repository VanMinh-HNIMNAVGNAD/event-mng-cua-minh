package com.sa.event_mng.modules.marketing.application.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherValidationRequest {
    String code;
    Map<Object, Double> eventAmounts;
}
