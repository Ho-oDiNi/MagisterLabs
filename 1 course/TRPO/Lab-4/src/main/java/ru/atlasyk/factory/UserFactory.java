package ru.atlasyk.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.atlasyk.base.usertype.StringUserTypeImpl;
import ru.atlasyk.fraction.Fraction;
import ru.atlasyk.interfaces.UserType;

public class UserFactory {

    private final Map<String, UserType> prototypes;

    public UserFactory() {
        prototypes = new HashMap<>();
        prototypes.put(StringUserTypeImpl.class.getSimpleName(), new StringUserTypeImpl(""));
        prototypes.put(Fraction.class.getSimpleName(), Fraction.of(0, 0, 1));
    }

    public List<String> getTypeNameList() {
        return new ArrayList<>(prototypes.keySet());
    }

    public UserType getBuilderByName(String name) {
        return prototypes.getOrDefault(name, null);
    }

}
