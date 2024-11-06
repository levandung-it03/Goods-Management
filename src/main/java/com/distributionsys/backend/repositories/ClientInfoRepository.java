package com.distributionsys.backend.repositories;

import com.distributionsys.backend.entities.sql.ClientInfo;
import com.distributionsys.backend.annotations.dev.Overload;
import com.distributionsys.backend.dtos.request.ClientInfoAndStatusRequest;
import com.distributionsys.backend.dtos.response.ClientInfoAndStatusResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientInfoRepository extends JpaRepository<ClientInfo, Long> {

    @Query("""
        SELECT new com.distributionsys.backend.dtos.response.ClientInfoAndStatusResponse(
            c.clientInfoId, c.firstName, c.lastName, c.gender, c.user.email, c.dob, c.user.userId, c.user.active,
            c.user.createdTime
        ) FROM ClientInfo c
    """)
    Page<ClientInfoAndStatusResponse> findAllClientInfoAndStatus(Pageable pageableCof);

    @Overload
    @Query("""
        SELECT new com.distributionsys.backend.dtos.response.ClientInfoAndStatusResponse(
            c.clientInfoId, c.firstName, c.lastName, c.gender, c.user.email, c.dob, c.user.userId, c.user.active,
            c.user.createdTime
        ) FROM ClientInfo c
        WHERE (:#{#filterObj.firstName} IS NULL OR c.firstName LIKE CONCAT('%',:#{#filterObj.firstName},'%'))
        AND (:#{#filterObj.active} IS NULL OR c.user.active = :#{#filterObj.active})
        AND (:#{#filterObj.lastName} IS NULL OR c.lastName LIKE CONCAT('%',:#{#filterObj.lastName},'%'))
        AND (:#{#filterObj.email} IS NULL   OR c.user.email LIKE CONCAT('%',:#{#filterObj.email},'%'))
        AND (:#{#filterObj.gender} IS NULL  OR c.gender = :#{#filterObj.gender})
        AND (:#{#filterObj.fromDob} IS NULL OR :#{#filterObj.fromDob} <= c.dob)
        AND (:#{#filterObj.toDob} IS NULL   OR c.dob <= :#{#filterObj.toDob})
        AND (:#{#filterObj.fromCreatedTime} IS NULL OR :#{#filterObj.fromCreatedTime} <= c.user.createdTime)
        AND (:#{#filterObj.toCreatedTime} IS NULL   OR c.user.createdTime <= :#{#filterObj.toCreatedTime})
    """)
    Page<ClientInfoAndStatusResponse> findAllClientInfoAndStatus(@Param("filterObj") ClientInfoAndStatusRequest request,
                                                                 Pageable pageableCof);

    Optional<ClientInfo> findByUserEmail(String subject);

    @Modifying
    @Transactional
    @Query("""
        UPDATE ClientInfo c SET
            c.firstName = :#{#updatedObj.firstName},
            c.lastName = :#{#updatedObj.lastName},
            c.dob = :#{#updatedObj.dob},
            c.gender = :#{#updatedObj.gender}
        WHERE c.clientInfoId = :#{#updatedObj.clientInfoId}
    """)
    void updateClientInfoByClientInfoId(@Param("updatedObj") ClientInfo clientInfo);
}
