package org.example.projetdemargement.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salle") // 👈 Très important pour que JPA utilise la bonne table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Salle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salle)) return false;
        Salle salle = (Salle) o;
        return id != null && id.equals(salle.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
