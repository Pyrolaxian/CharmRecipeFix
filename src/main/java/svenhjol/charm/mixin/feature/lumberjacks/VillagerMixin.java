package svenhjol.charm.mixin.feature.lumberjacks;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.charmony.Resolve;
import svenhjol.charm.feature.lumberjacks.Lumberjacks;

@Mixin(Villager.class)
public abstract class VillagerMixin {
    @Shadow public abstract VillagerData getVillagerData();

    /**
     * Simple hook to trigger the advancement when a player interacts with a lumberjack.
     * TODO: make a custom event for villager interaction.
     */
    @Inject(
        method = "startTrading",
        at = @At("TAIL")
    )
    private void hookStartTrading(Player player, CallbackInfo ci) {
        var lumberjacks = Resolve.feature(Lumberjacks.class);
        var data = getVillagerData();
        if (data.getProfession() == lumberjacks.registers.profession.get()) {
            lumberjacks.advancements.tradedWithLumberjack(player);
        }
    }
}
