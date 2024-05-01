package svenhjol.charm.feature.variant_wood;

import svenhjol.charm.foundation.Feature;
import svenhjol.charm.foundation.Registration;
import svenhjol.charm.foundation.client.ClientFeature;
import svenhjol.charm.foundation.common.CommonFeature;

import java.util.Optional;

public class VariantWoodClient extends ClientFeature {
    @Override
    public Class<? extends CommonFeature> commonClass() {
        return VariantWood.class;
    }

    @Override
    public Optional<Registration<? extends Feature>> registration() {
        return Optional.of(new ClientRegistration(this));
    }
}
