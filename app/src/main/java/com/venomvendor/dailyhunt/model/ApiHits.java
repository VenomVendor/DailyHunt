package com.venomvendor.dailyhunt.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "api_hits"
})
public class ApiHits extends ResponseStatus {

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

}
