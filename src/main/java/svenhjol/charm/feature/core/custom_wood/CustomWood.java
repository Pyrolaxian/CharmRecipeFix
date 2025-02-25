package svenhjol.charm.feature.core.custom_wood;

import svenhjol.charm.charmony.Resolve;
import svenhjol.charm.charmony.annotation.Feature;
import svenhjol.charm.charmony.common.CommonFeature;
import svenhjol.charm.charmony.common.CommonLoader;
import svenhjol.charm.charmony.feature.ChildFeature;
import svenhjol.charm.charmony.iface.CustomWoodMaterial;
import svenhjol.charm.feature.core.Core;
import svenhjol.charm.feature.core.custom_wood.common.CustomWoodDefinition;
import svenhjol.charm.feature.core.custom_wood.common.CustomWoodHolder;
import svenhjol.charm.feature.core.custom_wood.common.Handlers;
import svenhjol.charm.feature.core.custom_wood.common.Registers;

import java.util.Optional;

@Feature(priority = 5, description = "Handles custom wood.")
public final class CustomWood extends CommonFeature implements ChildFeature<Core> {
    public final Registers registers;
    public final Handlers handlers;

    public CustomWood(CommonLoader loader) {
        super(loader);

        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    @Override
    public boolean canBeDisabled() {
        return false;
    }

    /**
     * Helper method for other features to register custom wood types.
     */
    @SuppressWarnings("unused")
    public static void register(CommonFeature owner, CustomWoodDefinition definition) {
        Resolve.feature(CustomWood.class).registers.register(owner, definition);
    }

    /**
     * Helper method to get a custom wood definition for a material.
     */
    public static CustomWoodHolder holder(CustomWoodMaterial material) {
        return Optional.of(Resolve.feature(CustomWood.class).registers.holders.get(material)).orElseThrow();
    }

    @Override
    public Class<Core> typeForParent() {
        return Core.class;
    }
}
