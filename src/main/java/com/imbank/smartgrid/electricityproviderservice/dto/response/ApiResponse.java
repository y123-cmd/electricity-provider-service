package com.imbank.smartgrid.electricityproviderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer status;
    private String message;
    private T data;
    private Pagination pagination;

    public ApiResponse(Integer status, String message, T data){
        this.status = status;
        this.message = message;
        this.data = data;
        this.pagination = null;
    }
}
