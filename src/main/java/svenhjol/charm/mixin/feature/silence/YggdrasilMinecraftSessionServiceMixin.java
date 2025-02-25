package svenhjol.charm.mixin.feature.silence;

import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.feature.silence.Silence;

import java.util.UUID;

@Mixin(YggdrasilMinecraftSessionService.class)
public class YggdrasilMinecraftSessionServiceMixin {
    @Inject(
        method = "fetchProfileUncached",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void hookFetchProfileUncached(UUID profileId, boolean requireSecure, CallbackInfoReturnable<ProfileResult> cir) {
        if (Silence.disableDevEnvironmentConnections()) {
            cir.setReturnValue(null);
        }
    }
}
