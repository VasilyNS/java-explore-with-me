package ru.practicum.ewmservice.model;

import lombok.*;

import javax.persistence.*;

/**
 * Локация
 */
@Entity
@Table(name = "location", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlaceLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;                        // Имя локации

    @Column(name = "lat")
    private Float lat;                          // Широта локации

    @Column(name = "lon")
    private Float lon;                          // Долгота локации

    @Column(name = "radius")
    private Float radius;                       // Радиус локации в километрах

}
