package com.kautiainen.antti.btechgame.game;

import java.math.RoundingMode;


/**
 * record representing rules modifiers.
 * @param name The name of the modifier.
 * @param rule The name of the modified rule.
 * @param modifier The rule modifier amount.
 * @param rounding The rounding of the 
 */
public record RulesModifier(String name, String rule, double modifier, RoundingMode rounding) {

    public static RulesModifier roundDown(String name, String rule, double modifier) {
        return new RulesModifier(name, rule, modifier, RoundingMode.DOWN);
    }

    public static RulesModifier roundUp(String name, String rule, double modifier) {
        return new RulesModifier(name, rule, modifier, RoundingMode.UP);
    }
}
