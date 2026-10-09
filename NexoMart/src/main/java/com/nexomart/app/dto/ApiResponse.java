package com.nexomart.app.dto;

/**
 * Standard JSON response envelope for all API endpoints.
 * Success shape:  { "success": true,  "data": { ... }, "error": null }
 * Error shape:    { "success": false, "data": null,    "error": { "code": "...", "message": "..." } }
 */
public class ApiResponse {

    private boolean success;
    private Object data;
    private ErrorDetail error;

    public ApiResponse() {}

    private ApiResponse(boolean success, Object data, ErrorDetail error) {
        this.success = success;
        this.data    = data;
        this.error   = error;
    }

    /** Factory for successful responses. */
    public static ApiResponse ok(Object data) {
        return new ApiResponse(true, data, null);
    }

    /** Factory for error responses. */
    public static ApiResponse error(String code, String message) {
        return new ApiResponse(false, null, new ErrorDetail(code, message));
    }

    public boolean isSuccess()    { return success; }
    public Object getData()       { return data; }
    public ErrorDetail getError() { return error; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setData(Object data)        { this.data = data; }
    public void setError(ErrorDetail error) { this.error = error; }

    /** Nested error detail object. */
    public static class ErrorDetail {
        private String code;
        private String message;

        public ErrorDetail(String code, String message) {
            this.code    = code;
            this.message = message;
        }

        public String getCode()    { return code; }
        public String getMessage() { return message; }
    }
}
