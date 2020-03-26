package org.opensrp.dto;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class LastIdDTO {

    @JsonProperty
    private Integer status;
    @JsonProperty
    private Long lastUsedId;

    public LastIdDTO(){
        this.status = 0;
    }

    public LastIdDTO(Long lastUsedId) {
        this.lastUsedId = lastUsedId;
    }

    public Long getLastUsedId() {
        return lastUsedId;
    }

    public void setLastUsedId(Long lastUsedId) {
        this.lastUsedId = lastUsedId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}