package svenhjol.charm.feature.glint_coloring;

import svenhjol.charm.charmony.annotation.Feature;
import svenhjol.charm.charmony.client.ClientFeature;
import svenhjol.charm.charmony.client.ClientLoader;
import svenhjol.charm.charmony.feature.LinkedFeature;
import svenhjol.charm.feature.glint_coloring.client.Handlers;

@Feature(description = """
    Allows the default enchantment glint color to be customized.
    An item with its own custom enchantment glint color will not be overridden by this feature.""")
public final class GlintColoringClient extends ClientFeature implements LinkedFeature<GlintColoring> {
    public final Handlers handlers;

    public GlintColoringClient(ClientLoader loader) {
        super(loader);

        handlers = new Handlers(this);
    }

    @Override
    public void onEnabled() {
        handlers.setEnabled(true);
    }

    @Override
    public Class<GlintColoring> typeForLinked() {
        return GlintColoring.class;
    }
}
