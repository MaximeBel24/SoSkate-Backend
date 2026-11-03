package com.soskate.api.exceptions.customer;

import com.soskate.api.exceptions.common.ApiException;
import com.soskate.api.exceptions.common.ErrorCode;

public class CustomerNotFoundException extends ApiException {

    public CustomerNotFoundException(String message) {
        super(ErrorCode.CUSTOMER_NOT_FOUND, message);
    }

    public CustomerNotFoundException(Long id) {
        super(ErrorCode.CUSTOMER_NOT_FOUND, "Customer not found with id: " + id);
    }
}
