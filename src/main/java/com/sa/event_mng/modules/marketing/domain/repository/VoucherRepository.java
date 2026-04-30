package com.sa.event_mng.modules.marketing.domain.repository;

import com.sa.event_mng.modules.marketing.domain.model.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);
    org.springframework.data.domain.Page<Voucher> findByCreatorId(Long creatorId, org.springframework.data.domain.Pageable pageable);
}
