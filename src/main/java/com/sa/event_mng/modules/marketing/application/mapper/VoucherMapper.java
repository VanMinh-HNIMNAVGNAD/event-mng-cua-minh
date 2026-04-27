package com.sa.event_mng.modules.marketing.application.mapper;

import com.sa.event_mng.modules.marketing.application.dto.request.VoucherRequest;
import com.sa.event_mng.modules.marketing.application.dto.response.VoucherResponse;
import com.sa.event_mng.modules.marketing.domain.model.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VoucherMapper {

    Voucher toVoucher(VoucherRequest request);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventName", source = "event.name")
    @Mapping(target = "creatorName", source = "creator.fullName")
    VoucherResponse toVoucherResponse(Voucher voucher);

    void updateVoucher(@MappingTarget Voucher voucher, VoucherRequest request);
}
