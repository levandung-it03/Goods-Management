package com.distributionsys.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SucceedCodes {
    //--Auth(21)
    AUTHENTICATION(21001, "Authenticate successfully"),
    REFRESHING_TOKEN(21002, "Refresh Token successfully"),
    LOGOUT(21003, "Logout successfully"),
    GET_OTP(21004, "Get OTP successfully"),
    VERIFY_OTP(21005, "Verify OTP successfully"),
    SEND_RANDOM_PASSWORD(21006, "Send random password into your email successfully"),
    GET_OTP_TO_CHANGE_PASSWORD(21007, "Authentication successfully and sent OTP"),
    CHANGE_PASSWORD(21008, "Change password successfully"),
    CHECK_ASYNC_EXECUTOR_FLAG(21009, "Check async executor flag successfully"),
    //--Enum(22)
    GET_ALL_GENDER_ENUMS(22001, "Get all Genders successfully"),
    //--Supplier(23)
    GET_SUPPLIERS_PAGES(23001, "Get Suppliers pages successfully"),
    ADD_SUPPLIER(23002, "Add Supplier successfully"),
    DELETE_SUPPLIER(23003, "Delete Supplier successfully"),
    UPDATE_SUPPLIER(23004, "Update Supplier successfully"),
    //--Warehouse(24)
    GET_WAREHOUSES_PAGES(24001, "Get Warehouses pages successfully"),
    ADD_WAREHOUSE(24002, "Add Warehouse successfully"),
    DELETE_WAREHOUSE(24003, "Delete Warehouse successfully"),
    UPDATE_WAREHOUSE(24004, "Update Warehouse successfully"),
    //--Goods(25)
    GET_FULL_INFO_GOODS_PAGES(25001, "Get Goods from each Warehouse pages successfully"),
    GET_SIMPLE_GOODS_PAGES(25002, "Get Simple Goods information pages successfully"),
    ADD_GOODS(25003, "Add new Goods successfully"),
    UPDATE_GOODS(25004, "Update Goods successfully"),
    DELETE_GOODS(25005, "Delete Goods successfully"),
    GET_FULL_INFO_GOODS_PAGES_BY_IP_BILL(25006, "Get Goods from each Warehouse pages of Import Bill successfully"),
    GET_FULL_INFO_GOODS_PAGES_BY_EX_BILL(25007, "Get Goods from each Warehouse pages of Export Bill successfully"),
    PREPARE_FLUX_GOODS_QUANTITY(25008, "Prepare Flux Quantity successfully" ),
    CANCEL_FLUX_GOODS_QUANTITY(25009, "Cancel Flux Quantity successfully" ),
    //--ImportBill(26)
    GET_IMPORT_BILL_PAGES(26001, "Get Import Bill pages successfully"),
    ADD_IMPORT_BILL(26002, "Add new Import Bill successfully"),
    PENDING_IMPORT_BILL(26003, "Creating Import Bill request received by system, this may take a while!"),
    CREATE_IMPORT_BILL(26004, "New Import Bill has been created successfully!"),
    DISCARD_IMPORT_BILL(26005, "Discard Import Bill successfully!"),
    GET_RECENT_IMPORT_BILL_LIST(26006, "Get recent Import Bill successfully!"),
    GET_IMPORT_BILL_DETAIL(26007,"Get Import Bill Detail successfully!"),
    //--ExportBill(27)
    GET_EXPORT_BILL_PAGES(27001, "Get Export Bill pages successfully"),
    PENDING_EXPORT_BILL(27002, "Creating Export Bill request received by system, this may take a while!"),
    GET_RECENT_EXPORT_BILL_LIST(27003, "Get recent Export Bill successfully!"),
    GET_EXPORT_BILL_DETAIL(27004, "Get Export Bill Detail successfully!"),
    //--ClientInfo(28)
    GET_CLIENT_INFO(28001, "Get Client Info successfully"),
    UPDATE_CLIENT_INFO(28002, "Update Client Info successfully"),
    //--Statistic(29)
    GET_STATISTICS_SUCCESS(29001, "Get statistics successfully"),
    GET_GOODS_QUANTITY(29002, "Get goods quantity successfully!"),
    GET_EXPORT_IMPORT_TREND(29003, "Get export import trend successfully!"),
    //--Admin(30)
    GET_TOTAL_CLIENT(30001, "Clients found successfully"),
    DEACTIVATE_CLIENT(30002, "Deactivate client successfully"),
    NEW_CLIENT_CREATED(30003, "New client created successfully"),
    GET_CLIENTS(30004, "Clients found successfully"),
    ;

    int code;
    String message;
}
