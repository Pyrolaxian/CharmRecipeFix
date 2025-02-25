package svenhjol.charm.feature.item_hover_sorting;

import svenhjol.charm.feature.item_hover_sorting.client.Handlers;
import svenhjol.charm.feature.item_hover_sorting.client.Registers;
import svenhjol.charm.charmony.annotation.Feature;
import svenhjol.charm.charmony.client.ClientFeature;
import svenhjol.charm.charmony.client.ClientLoader;

@Feature
public final class ItemHoverSortingClient extends ClientFeature {
    public final Registers registers;
    public final Handlers handlers;

    public ItemHoverSortingClient(ClientLoader loader) {
        super(loader);

        registers = new Registers(this);
        handlers = new Handlers(this);
    }
}
