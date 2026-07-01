package com.sa.event_mng.modules.identity.infrastructure.task;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sa.event_mng.modules.identity.domain.repository.InvalidatedTokenRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TokenCleanupService {

    InvalidatedTokenRepository invalidatedTokenRepository;

    @Scheduled(fixedRate = 7200000)
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("CLEANUP: Bắt đầu xóa các token đã hết hạn khỏi danh sách đen...");
        invalidatedTokenRepository.deleteByExpiryTimeBefore(new Date());
        log.info("CLEANUP: Các mã thông báo hết hạn đã được xóa.");
    }
}
