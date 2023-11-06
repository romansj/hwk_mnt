package com.jromans.hwk.transactions.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findByAccountSrcIgnoreCase(String accountOrigin, Pageable pageable);

}
