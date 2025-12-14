package com.salleo.service;

import com.salleo.entity.AuditLog;
import com.salleo.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService extends BaseCrudService<AuditLog> {
    public AuditLogService(AuditLogRepository repo) { super(repo); }
}

