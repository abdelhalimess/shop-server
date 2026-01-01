package fr.fullstack.shopapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "opening_hours")
public class OpeningHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 1=Lundi, 7=Dimanche
    @NotNull
    @Min(1) @Max(7)
    private int day;

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss") // <--- Transforme [8,0] en "08:00:00"
    private LocalTime openAt;

    @NotNull
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime closeAt;

    // C'est ICI que se fait le lien. La variable s'appelle "shop".
    // Donc dans Shop.java, le mappedBy doit être "shop".
    @ManyToOne
    @JoinColumn(name = "shop_id")
    @JsonIgnore // Pour éviter la boucle infinie JSON
    private Shop shop;
}