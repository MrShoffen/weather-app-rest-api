package org.mrshoffen.weather.model.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "locations")
@Entity//todo add constraint to annotation
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "state")
    private String state;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Integer userId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;
}
