package com.distributionsys.backend.services.webflux;

import com.distributionsys.backend.dtos.request.FluxGoodsFromWarehouseRequest;
import com.distributionsys.backend.enums.ErrorCodes;
import com.distributionsys.backend.exceptions.ApplicationException;
import com.distributionsys.backend.repositories.UserRepository;
import com.distributionsys.backend.repositories.WarehouseGoodsRepository;
import com.distributionsys.backend.services.auth.JwtService;
import com.distributionsys.backend.services.redis.FluxedGoodsFromWarehouseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@EnableAsync
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FluxedAsyncService {
    FluxedGoodsFromWarehouseService fluxedGoodsFromWarehouseService;
    JwtService jwtService;
    WarehouseGoodsRepository warehouseGoodsRepository;
    UserRepository userRepository;

    @Async
    protected CompletableFuture<Long> prepare(String accessToken, FluxGoodsFromWarehouseRequest request)
        throws ApplicationException {
        var goodsFromWarehouses = warehouseGoodsRepository.findAllById(request.getGoodsFromWarehouseIds());
        var currentUser = userRepository
            .findByEmail(jwtService.readPayload(accessToken).get("sub"))
            .orElseThrow(() -> new ApplicationException(ErrorCodes.INVALID_TOKEN));
        if (goodsFromWarehouses.isEmpty())
            throw new ApplicationException(ErrorCodes.INVALID_PRIMARY);
        fluxedGoodsFromWarehouseService.saveFluxedGoodsOfCurrentSession(currentUser.getUserId(), goodsFromWarehouses);
        return CompletableFuture.completedFuture(currentUser.getUserId());
    }

    public Mono<Long> prepareFluxedGoodsFromWarehouseStreaming(String token, FluxGoodsFromWarehouseRequest request) {
        return Mono.fromFuture(() -> this.prepare(token, request));
    }
}
