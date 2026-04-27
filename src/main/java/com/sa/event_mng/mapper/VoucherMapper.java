package com.sa.event_mng.mapper;

import com.sa.event_mng.dto.request.VoucherRequest;
import com.sa.event_mng.dto.response.VoucherResponse;
import com.sa.event_mng.model.entity.Voucher;
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
