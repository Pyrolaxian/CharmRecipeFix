package svenhjol.charm.feature.casks.common.dispenser;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import svenhjol.charm.charmony.common.dispenser.CompositeDispenseItemBehavior;
import svenhjol.charm.charmony.common.dispenser.ConditionalDispenseItemBehavior;
import svenhjol.charm.charmony.feature.FeatureResolver;
import svenhjol.charm.feature.casks.Casks;
import svenhjol.charm.feature.casks.common.CaskBlockEntity;

import java.util.Optional;

public class GlassBottleBehavior implements FeatureResolver<Casks>, ConditionalDispenseItemBehavior {
    private ItemStack stack;
    
    @Override
    public boolean accept(CompositeDispenseItemBehavior behavior, BlockSource blockSource, ItemStack stack) {
        var serverLevel = blockSource.level();
        var dispenserState = blockSource.state();
        var pos = blockSource.pos().relative(dispenserState.getValue(DispenserBlock.FACING));

        if (serverLevel.getBlockEntity(pos) instanceof CaskBlockEntity cask) {
            var opt = feature().handlers.dispenserTakeFromCask(cask);
            if (opt.isPresent()) {
                this.stack = this.takeLiquid(behavior, blockSource, stack, opt.get());
                return true;
            }
        }
        
        return false;
    }

    @Override
    public Optional<ItemStack> stack() {
        return Optional.ofNullable(stack);
    }

    @Override
    public Class<Casks> typeForFeature() {
        return Casks.class;
    }

    private ItemStack takeLiquid(CompositeDispenseItemBehavior behavior, BlockSource blockSource, ItemStack itemStack, ItemStack itemStack2) {
        blockSource.level().gameEvent(null, GameEvent.FLUID_PICKUP, blockSource.pos());
        return behavior.consumeWithRemainder(blockSource, itemStack, itemStack2);
    }
}
