package svenhjol.charm.feature.coral_squids;

import svenhjol.charm.feature.coral_squids.common.Providers;
import svenhjol.charm.feature.coral_squids.common.Registers;
import svenhjol.charm.foundation.annotation.Configurable;
import svenhjol.charm.foundation.annotation.Feature;
import svenhjol.charm.foundation.common.CommonFeature;
import svenhjol.charm.foundation.common.CommonLoader;

@Feature(description = "Coral Squids spawn near coral in warm oceans.")
public final class CoralSquids extends CommonFeature {
    public final Registers registers;
    public final Providers providers;

    @Configurable(name = "Drop chance", description = "Chance (out of 1.0) of a coral squid dropping coral when killed.")
    public static double dropChance = 0.2D;

    public CoralSquids(CommonLoader loader) {
        super(loader);

        registers = new Registers(this);
        providers = new Providers(this);
    }
}