package svenhjol.charm.feature.amethyst_note_block.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import svenhjol.charm.feature.amethyst_note_block.AmethystNoteBlock;
import svenhjol.charm.foundation.feature.Register;
import svenhjol.charm.foundation.helper.EnumHelper;

import java.util.function.Supplier;

public final class Registers extends Register<AmethystNoteBlock> {
    public Supplier<SoundEvent> sound;

    public Registers() {
        sound = feature().registry().soundEvent("amethyst");
    }

    /**
     * Sound registration usually happens after the custom note block enum is processed.
     * soundEvent is made accessible so we can safely set it to the registered sound here.
     */
    @Override
    public void onEnabled() {
        var registry = BuiltInRegistries.SOUND_EVENT;

        registry.getResourceKey(sound.get())
            .flatMap(registry::getHolder)
            .ifPresent(
                holder -> EnumHelper.setNoteBlockSound(
                    AmethystNoteBlock.NOTE_BLOCK_ID,
                    holder,
                    NoteBlockInstrument.Type.BASE_BLOCK
                ));
    }

    @Override
    protected Class<AmethystNoteBlock> type() {
        return AmethystNoteBlock.class;
    }
}
