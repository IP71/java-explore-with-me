package ru.practicum.ewm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    @EqualsAndHashCode.Exclude
    private Integer hits;

    public void addHit() {
        this.hits++;
    }
}
