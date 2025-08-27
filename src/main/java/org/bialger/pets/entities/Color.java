package org.bialger.pets.entities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Color {
    RED,
    GREEN,
    BLUE,
    BLACK,
    WHITE,
    GRAY,
    BROWN,
    ORANGE,
    YELLOW,
    MIXED,
    OTHER;

    public static List<String> getAllColorNames() {
        return Arrays.stream(Color.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
