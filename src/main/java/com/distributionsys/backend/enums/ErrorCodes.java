package com.distributionsys.backend.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCodes {
    //--Generals(10)
    UNAWARE_ERR(10000, "Unaware exception's thrown from resource server", BAD_REQUEST),
    VALIDATOR_ERR_RESPONSE(10001, "Invalid variable type or format of field '${field}'", BAD_REQUEST),
    PARSE_JSON_ERR(10002, "Invalid variable type or format of field '${field}'", BAD_REQUEST),
    CONSTRAINT_VIOLATION(10004, "Constraint was triggered because of invalid data", BAD_REQUEST),
    INVALID_IDS_COLLECTION(10004, "Collection of Ids is invalid", BAD_REQUEST),
    INVALID_PRIMARY(10005, "Can not get object because id or primary fields are invalid", BAD_REQUEST),
    FORBIDDEN_UPDATING(10006, "Can not update or delete a depended object", BAD_REQUEST),
    INVALID_FILTERING_FIELD_OR_VALUE(10008, "Invalid filtering field or value", BAD_REQUEST),
    INVALID_SORTING_FIELD_OR_VALUE(10009, "Invalid sorting field or value", BAD_REQUEST),
    HIDDEN_OTP_IS_KILLED(10011, "Session was opened too long, please do it again!", BAD_REQUEST),
    HIDDEN_OTP_NOT_FOUND(10012, "Please don't use weird form to submit action!", BAD_REQUEST),
    OTP_IS_KILLED(10013, "OTP has been expired, please do it again!", BAD_REQUEST),
    OTP_NOT_FOUND(10014, "OTP is wrong!", BAD_REQUEST),
    USER_EXISTING(10015, "User is already existing", BAD_REQUEST),
    RETRY_TOO_MANY_TIMES(10016, "There are too many threads working now, please try it later!" , BAD_REQUEST),
    DATA_CAN_NOT_ROLLBACK_BY_VIOLATION(10017, "Can not complete action because of data violation", BAD_REQUEST),
    USER_NOT_FOUND(10018, "User not found", NOT_FOUND),

    //--Auth(11)
    INVALID_CREDENTIALS(11001, "Email or Password is invalid", UNAUTHORIZED),
    INVALID_TOKEN(11002, "Token or its claims are invalid", UNAUTHORIZED),
    EXPIRED_TOKEN(11003, "Token is expired", FORBIDDEN),
    FORBIDDEN_USER(11004, "User not found or access denied", BAD_REQUEST),
    LOGIN_SESSION_EXPIRED(11005, "Login session is expired, please login again", BAD_REQUEST),
    DUPLICATED_EMAIL(11006, "Email is already existing", BAD_REQUEST),
    AUTHORITY_NOT_FOUND(11007, "Authority not found", NOT_FOUND),
    //--Enums(12)
    INVALID_GENDER_ID(12001, "Gender Id is invalid", BAD_REQUEST),
    //--Supplier(13)
    DUPLICATE_SUPPLIER(13001, "Supplier is already exists!", BAD_REQUEST),
    UPDATE_SUPPLIER(13002, "Supplier to be in used, please do not update it!", BAD_REQUEST),
    DELETE_SUPPLIER(13003, "Supplier to be in used, please do not delete it!", BAD_REQUEST),
    //--Warehouse(14)
    DUPLICATE_WAREHOUSE(14001, "Warehouse is already exists!", BAD_REQUEST),
    UPDATE_WAREHOUSE(14002, "Warehouse to be in used, please do not update it!", BAD_REQUEST),
    DELETE_WAREHOUSE(14003, "Warehouse to be in used, please do not delete it!", BAD_REQUEST),
    //--Goods(15)
    DUPLICATE_GOODS(15001, "Goods name is already exists!", BAD_REQUEST),
    UPDATE_GOODS(15002, "Goods to be in used, please do not update it!", BAD_REQUEST),
    DELETE_GOODS(15003, "Goods to be in used, please do not delete it!", BAD_REQUEST),    
    //--Import(16)
    //--Export(17)
    NOT_ENOUGH_QUANTITY_TO_EXPORT(17001, "Data updated and there are some Goods quantity doesn't have enough!", BAD_REQUEST),
    ;
//    --Client


    int code;
    String message;
    HttpStatus httpStatus;
}
