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
    //--Enum(22)
    GET_ALL_GENDER_ENUMS(22001, "Get all Genders successfully"),
    //--Supplier(23)
    GET_SUPPLIERS_PAGES(23001, "Get Suppliers pages successfully"),
    ADD_SUPPLIER(23002, "Add Supplier successfully"),
    DELETE_SUPPLIER(23003, "Delete Supplier successfully"),
    UPDATE_SUPPLIER(23004, "Update Supplier successfully"),
    //--Warehouse(23)
    GET_WAREHOUSES_PAGES(24001, "Get Warehouses pages successfully"),
    ADD_WAREHOUSE(24002, "Add Warehouse successfully"),
    DELETE_WAREHOUSE(24003, "Delete Warehouse successfully"),
    UPDATE_WAREHOUSE(24004, "Update Warehouse successfully"),
    //--Goods(24)
    GET_FULL_INFO_GOODS_PAGES(25001, "Get Goods from each Warehouse pages successfully"),
    GET_SIMPLE_GOODS_PAGES(25002, "Get Simple Goods information pages successfully"),
    ADD_GOODS(25003, "Add new Goods successfully"),
    UPDATE_GOODS(25004, "Update Goods successfully"),
    DELETE_GOODS(25005, "Delete Goods successfully"),
    GET_FULL_INFO_GOODS_PAGES_BY_IP_BILL(25006, "Get Goods from each Warehouse pages of Import Bill successfully"),
    GET_FULL_INFO_GOODS_PAGES_BY_EX_BILL(25007, "Get Goods from each Warehouse pages of Export Bill successfully"),
    //--ImportBill(25)
    GET_IMPORT_BILL_PAGES(26001, "Get Import Bill pages successfully"),
    ADD_IMPORT_BILL(26002, "Add new Import Bill successfully"),
    DISCARD_IMPORT_BILL(26003, "Discard Import Bill successfully"),
    ;

    int code;
    String message;
}
