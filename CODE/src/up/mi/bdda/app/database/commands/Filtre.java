package up.mi.bdda.app.database.commands;

import java.util.Objects;

public record Filtre(String colonne, String valeur, String operateur) {
    
    public Filtre{
        Objects.requireNonNull(colonne);
        Objects.requireNonNull(valeur);
        Objects.requireNonNull(operateur);
    }
    
}
