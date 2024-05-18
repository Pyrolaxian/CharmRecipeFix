package svenhjol.charm.feature.player_pressure_plates.common;

import net.minecraft.world.level.ItemLike;
import svenhjol.charm.api.iface.IWandererTrade;
import svenhjol.charm.api.iface.IWandererTradeProvider;
import svenhjol.charm.feature.player_pressure_plates.PlayerPressurePlates;
import svenhjol.charm.foundation.feature.ProviderHolder;

import java.util.List;

public final class Providers extends ProviderHolder<PlayerPressurePlates> implements IWandererTradeProvider {
    public Providers(PlayerPressurePlates feature) {
        super(feature);
    }

    @Override
    public List<IWandererTrade> getWandererTrades() {
        return List.of(new IWandererTrade() {
            @Override
            public ItemLike getItem() {
                return feature().registers.block.get();
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public int getCost() {
                return 6;
            }
        });
    }
}