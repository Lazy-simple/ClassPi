package com.classpi.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private Long total;
    private Long page;
    private Long pageSize;
    private Long totalPages;
    private List<T> records;
    
    public static <T> PageResult<T> of(Long total, Long page, Long pageSize, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotalPages((total + pageSize - 1) / pageSize);
        result.setRecords(records);
        return result;
    }
}
