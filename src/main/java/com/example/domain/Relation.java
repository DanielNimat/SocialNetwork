package com.example.domain;


import com.example.utils.Constants;

import java.time.LocalDateTime;

public class Relation extends Entity<Tuple<Long,Long>> {

    private String date;
    private Long id1;
    private Long id2;
    private String status;

    public Relation(Long id1, Long id2) {
        this.id1 = id1;
        this.id2 = id2;
        this.date = LocalDateTime.now().format(Constants.DATE_FORMATTER);
        this.status="Pending";
    }

    /**
     * @return the date when the friendship was created
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date must not be null
     * sets the relation's create date
     */
    public void setDate(String date) {
        this.date = date;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
