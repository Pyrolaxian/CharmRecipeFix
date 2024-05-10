package svenhjol.charm.feature.azalea_wood.client;

import net.minecraft.client.renderer.RenderType;
import svenhjol.charm.feature.azalea_wood.AzaleaWoodClient;
import svenhjol.charm.feature.azalea_wood.common.Material;
import svenhjol.charm.feature.custom_wood.CustomWood;
import svenhjol.charm.foundation.feature.RegisterHolder;

public final class Registers extends RegisterHolder<AzaleaWoodClient> {
    public Registers(AzaleaWoodClient feature) {
        super(feature);

        var registry = feature.registry();
        var holder = CustomWood.holder(Material.AZALEA);

        var door = holder.door().orElseThrow();
        var trapdoor = holder.trapdoor().orElseThrow();

        // Cut out transparent areas of doors and trapdoors.
        registry.blockRenderType(door.block, RenderType::cutout);
        registry.blockRenderType(trapdoor.block, RenderType::cutout);
    }
}
