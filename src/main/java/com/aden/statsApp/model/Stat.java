package com.aden.statsApp.model;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Stat {
    String statName;
    int count;
}
