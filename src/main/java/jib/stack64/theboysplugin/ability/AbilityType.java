package jib.stack64.theboysplugin.ability;

import java.lang.reflect.Field;
import java.util.Objects;

public class AbilityType {
    public static final Ability ATRAIN = new ATrainAbility();
    public static final Ability HOMELANDER = new HomelanderAbility();

    // Finds and returns an ability by its identifier.
    public static Ability getByIdentifier(String identifier) {
        Field[] fields = AbilityType.class.getDeclaredFields();
        for (Field f : fields) {
            Object v;
            try {
                v = f.get(AbilityType.class);
            } catch (Exception e) {
                break;
            }
            if ((Objects.equals(((Ability) v).id, identifier))) {
                return (Ability)v;
            }
        }
        return null;
    }
}
