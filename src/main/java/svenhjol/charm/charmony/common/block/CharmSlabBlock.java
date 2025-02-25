package svenhjol.charm.charmony.common.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import svenhjol.charm.charmony.iface.CustomMaterial;
import svenhjol.charm.charmony.iface.IgniteProvider;

import java.util.function.Supplier;

public class CharmSlabBlock extends SlabBlock implements IgniteProvider {
    protected final CustomMaterial material;

    public CharmSlabBlock(CustomMaterial material) {
        super(material.blockProperties()
            .strength(2.0f, 3.0f));

        this.material = material;
    }

    @Override
    public int igniteChance() {
        return material.isFlammable() ? 5 : 0;
    }

    @Override
    public int burnChance() {
        return material.isFlammable() ? 20 : 0;
    }

    public static class BlockItem extends net.minecraft.world.item.BlockItem {
        public <T extends Block> BlockItem(Supplier<T> block) {
            super(block.get(), new Item.Properties());
        }
    }
}
