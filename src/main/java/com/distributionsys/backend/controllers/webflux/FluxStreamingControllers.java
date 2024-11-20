package com.distributionsys.backend.controllers.webflux;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/sse/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FluxStreamingControllers {

    @GetMapping(
        value = "/user/v1/flux-current-quantity-of-goods",
        produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> fluxCurrentQuantityOfGoods() {
        return null;
    }
}
