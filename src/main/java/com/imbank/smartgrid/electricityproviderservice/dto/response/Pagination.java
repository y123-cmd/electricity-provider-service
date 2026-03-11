package com.imbank.smartgrid.electricityproviderservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Pagination {
    private Integer page;
    private Integer size;
    private Long totalRecords;
    private Integer totalPage;
}
