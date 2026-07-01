package com.expensetracker.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Uniform error body returned by {@link GlobalExceptionHandler}.
 *
 * <p>{@code fieldErrors} is only included in the JSON response when it is
 * non-null (i.e. for {@code @Valid} validation failures), keeping the
 * response compact for other error types.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int    status;
    private String error;
    private String message;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /** Field-level validation errors: {@code fieldName → errorMessage}. */
    private Map<String, String> fieldErrors;
}
