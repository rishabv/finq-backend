package com.finq.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BasePaginationResponse<T> extends BaseApiResponse {
    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;

    public BasePaginationResponse(T data, Integer totalPages, Integer pageNumber, Integer pageSize) {
        super(data);
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }
}
