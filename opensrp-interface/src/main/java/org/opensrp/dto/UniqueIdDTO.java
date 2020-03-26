package org.opensrp.dto;

import java.util.ArrayList;
import java.util.List;

public class UniqueIdDTO {


    private List<Long> ids;

    public UniqueIdDTO(){
        this.ids = new ArrayList<Long>();
    }

    public UniqueIdDTO(List<Long> ids) {
        this.ids = ids;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}