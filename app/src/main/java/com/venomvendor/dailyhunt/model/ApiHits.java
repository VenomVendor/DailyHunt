package com.venomvendor.dailyhunt.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "api_hits"
})
public class ApiHits {

    /**
     * Since no status from response, consider all response as success.
     */
    @JsonProperty("status")
    private String status = "success";

    @JsonProperty("error")
    private String error;

    @JsonProperty("api_hits")
    private String apiHits;

    /**
     * @return The apiHits
     */
    @JsonProperty("api_hits")
    public String getApiHits() {
        return apiHits;
    }

    /**
     * @param apiHits The api_hits
     */
    @JsonProperty("api_hits")
    public void setApiHits(String apiHits) {
        this.apiHits = apiHits;
    }

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

}
