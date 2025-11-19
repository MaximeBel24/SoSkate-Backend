package com.soskate.api.dto.photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for paginated photo lists.
 * Contains photos and pagination metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoListResponse {

    private List<PhotoResponse> photos;

    private Integer totalCount;

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;

    /**
     * Check if there are more pages available.
     */
    public boolean hasNext() {
        return page != null && totalPages != null && page < totalPages;
    }

    /**
     * Check if there is a previous page.
     */
    public boolean hasPrevious() {
        return page != null && page > 1;
    }
}