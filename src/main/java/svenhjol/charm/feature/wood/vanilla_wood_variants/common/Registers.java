package svenhjol.charm.feature.wood.vanilla_wood_variants.common;

import svenhjol.charm.feature.core.custom_wood.CustomWood;
import svenhjol.charm.feature.wood.vanilla_wood_variants.VanillaWoodVariants;
import svenhjol.charm.charmony.common.enums.VanillaWood;
import svenhjol.charm.charmony.feature.RegisterHolder;

import java.util.Collections;

public final class Registers extends RegisterHolder<VanillaWoodVariants> {
    public Registers(VanillaWoodVariants feature) {
        super(feature);

        var types = VanillaWood.getTypes();
        Collections.reverse(types);

        for (var variant : types) {
            var definition = new WoodDefinition(variant);
            CustomWood.register(feature, definition);
        }
    }
}
