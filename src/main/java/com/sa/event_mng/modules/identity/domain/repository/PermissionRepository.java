package com.sa.event_mng.modules.identity.domain.repository;

import com.sa.event_mng.modules.identity.domain.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
}
