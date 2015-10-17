package com.venomvendor.dailyhunt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.venomvendor.dailyhunt.util.Constants;

import java.io.Serializable;

public class ResponseStatus implements Serializable {

    /**
     * Since no status from response, consider all response as success.
     */
    @JsonProperty("status")
    private String status = Constants.SUCCESS;

    @JsonProperty("error")
    private String error = "{ Unknown Error }";

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return this.getStatus().equalsIgnoreCase(Constants.SUCCESS);
    }
}
