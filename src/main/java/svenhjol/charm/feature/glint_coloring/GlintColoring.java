package svenhjol.charm.feature.glint_coloring;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import svenhjol.charm.feature.glint_coloring.common.Handlers;
import svenhjol.charm.feature.glint_coloring.common.Registers;
import svenhjol.charm.charmony.Resolve;
import svenhjol.charm.charmony.annotation.Feature;
import svenhjol.charm.charmony.common.CommonFeature;
import svenhjol.charm.charmony.common.CommonLoader;
import svenhjol.charm.charmony.helper.ConfigHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BooleanSupplier;

@Feature(description = """
    Customizable item enchantment colors. This feature is a helper for other Charm features and mods.
    If disabled then other features and mods that rely on it will not function properly.""")
public final class GlintColoring extends CommonFeature {
    public final Registers registers;
    public final Handlers handlers;

    public GlintColoring(CommonLoader loader) {
        super(loader);

        registers = new Registers(this);
        handlers = new Handlers(this);
    }

    @Override
    public List<BooleanSupplier> checks() {
        return List.of(() -> !ConfigHelper.isModLoaded("optifabric"));
    }

    /**
     * Helper method for other mods to quickly get the dyecolor of the given stack.
     */
    @Nullable
    public static DyeColor get(@Nullable ItemStack stack) {
        return Resolve.feature(GlintColoring.class).handlers.get(stack);
    }

    /**
     * Helper method for other mods to quickly check if the stack has a custom glint.
     */
    public static boolean has(@Nullable ItemStack stack) {
        return Resolve.feature(GlintColoring.class).handlers.has(stack);
    }

    /**
     * Apply a colored glint to a stack, specified by dyecolor.
     */
    public static void apply(ItemStack stack, DyeColor color) {
        Resolve.feature(GlintColoring.class).handlers.apply(stack, color);
    }

    /**
     * Remove the custom glint from a stack.
     */
    public static void remove(ItemStack stack) {
        Resolve.feature(GlintColoring.class).handlers.remove(stack);
    }
}
