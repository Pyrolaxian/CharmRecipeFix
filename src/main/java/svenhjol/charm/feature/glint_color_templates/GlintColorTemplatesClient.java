package svenhjol.charm.feature.glint_color_templates;

import svenhjol.charm.charmony.annotation.Feature;
import svenhjol.charm.charmony.client.ClientFeature;
import svenhjol.charm.charmony.client.ClientLoader;
import svenhjol.charm.charmony.feature.LinkedFeature;
import svenhjol.charm.feature.glint_color_templates.client.Registers;

@Feature
public final class GlintColorTemplatesClient extends ClientFeature implements LinkedFeature<GlintColorTemplates> {
    public final Registers registers;

    public GlintColorTemplatesClient(ClientLoader loader) {
        super(loader);

        registers = new Registers(this);
    }

    @Override
    public Class<GlintColorTemplates> typeForLinked() {
        return GlintColorTemplates.class;
    }
}
