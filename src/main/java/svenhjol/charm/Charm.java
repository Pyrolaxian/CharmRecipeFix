package svenhjol.charm;

import net.minecraft.resources.ResourceLocation;
import svenhjol.charm.feature.advancements.Advancements;
import svenhjol.charm.feature.amethyst_note_block.AmethystNoteBlock;
import svenhjol.charm.feature.azalea_wood.AzaleaWood;
import svenhjol.charm.feature.chairs.Chairs;
import svenhjol.charm.feature.colored_glints.ColoredGlints;
import svenhjol.charm.feature.core.Core;
import svenhjol.charm.feature.custom_wood.CustomWood;
import svenhjol.charm.feature.diagnostics.Diagnostics;
import svenhjol.charm.feature.firing.Firing;
import svenhjol.charm.feature.potion_of_radiance.PotionOfRadiance;
import svenhjol.charm.feature.recipes.Recipes;
import svenhjol.charm.feature.smooth_glowstone.SmoothGlowstone;
import svenhjol.charm.feature.vanilla_wood_variants.VanillaWoodVariants;
import svenhjol.charm.feature.variant_pistons.VariantPistons;
import svenhjol.charm.feature.variant_wood.VariantWood;
import svenhjol.charm.feature.woodcutters.Woodcutters;
import svenhjol.charm.feature.woodcutting.Woodcutting;
import svenhjol.charm.foundation.common.CommonFeature;

import java.util.List;

public class Charm {
    public static final String ID = "charm";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(ID, path);
    }

    public static List<Class<? extends CommonFeature>> features() {
        return List.of(
            Advancements.class,
            AmethystNoteBlock.class,
            AzaleaWood.class,
            Chairs.class,
            ColoredGlints.class,
            Core.class,
            CustomWood.class,
            Diagnostics.class,
            Firing.class,
            PotionOfRadiance.class,
            Recipes.class,
            SmoothGlowstone.class,
            VariantPistons.class,
            VanillaWoodVariants.class,
            VariantWood.class,
            Woodcutters.class,
            Woodcutting.class
        );
    }
}
