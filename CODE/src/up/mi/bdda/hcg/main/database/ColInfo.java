package up.mi.bdda.hcg.main.database;

import java.util.Objects;

/**
 * Cette classe doit stoker les informations de la colonne de la relation.
 */
public record ColInfo(String nomColonnes, Type typeColonne) {
    public ColInfo {
        Objects.requireNonNull(nomColonnes);
        Objects.requireNonNull(typeColonne);
    }
}
