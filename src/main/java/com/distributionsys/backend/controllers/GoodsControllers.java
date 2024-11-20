package com.distributionsys.backend.controllers;

import com.distributionsys.backend.dtos.request.PaginatedTableRequest;
import com.distributionsys.backend.dtos.request.SimpleGoodsRequest;
import com.distributionsys.backend.dtos.response.ApiResponseObject;
import com.distributionsys.backend.dtos.response.SimpleGoodsResponse;
import com.distributionsys.backend.dtos.response.TablePagesResponse;
import com.distributionsys.backend.entities.sql.relationships.WarehouseGoods;
import com.distributionsys.backend.enums.SucceedCodes;
import com.distributionsys.backend.services.GoodsService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoodsControllers {
    GoodsService goodsService;

    @GetMapping("/user/v1/get-goods-with-warehouse-and-supplier-info-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<WarehouseGoods>>> getWarehouseGoodsPages(
        @Valid PaginatedTableRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_WAREHOUSE_GOODS_PAGES,
            goodsService.getWarehouseGoodsPages(request));
    }

    @GetMapping("/user/v1/get-simple-goods-pages")
    public ResponseEntity<ApiResponseObject<TablePagesResponse<SimpleGoodsResponse>>> getSimpleGoodsPages(
        @Valid SimpleGoodsRequest request) {
        return ApiResponseObject.buildSuccessResponse(SucceedCodes.GET_SIMPLE_GOODS_PAGES,
            goodsService.getSimpleGoodsPages(request));
    }
}
