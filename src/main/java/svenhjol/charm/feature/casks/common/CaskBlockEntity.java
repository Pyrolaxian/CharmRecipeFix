package svenhjol.charm.feature.casks.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.state.BlockState;
import svenhjol.charm.charmony.Resolve;
import svenhjol.charm.charmony.common.block.entity.CharmBlockEntity;
import svenhjol.charm.feature.casks.Casks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaskBlockEntity extends CharmBlockEntity<Casks> implements Nameable {
    private static final Casks CASKS = Resolve.feature(Casks.class);

    public static final String NAME_TAG = "name";
    public static final String BOTTLES_TAG = "bottles";
    public static final String EFFECTS_TAG = "effects";
    public static final String DURATIONS_TAG = "duration";
    public static final String AMPLIFIERS_TAG = "amplifier";
    public static final String DILUTIONS_TAG = "dilutions";
    public static final String FERMENTATION_TAG = "fermentation";

    // Mapped by data components.
    public Component name;
    public int bottles = 0;
    public double fermentation = CaskData.DEFAULT_FERMENTATION;
    public Map<ResourceLocation, Integer> durations = new HashMap<>();
    public Map<ResourceLocation, Integer> amplifiers = new HashMap<>();
    public Map<ResourceLocation, Integer> dilutions = new HashMap<>();
    public List<ResourceLocation> effects = new ArrayList<>();

    public CaskBlockEntity(BlockPos pos, BlockState state) {
        super(CASKS.registers.blockEntity.get(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        if (tag.contains(NAME_TAG)) {
            this.name = Component.Serializer.fromJson(tag.getString(NAME_TAG), provider);
        }
        if (tag.contains(FERMENTATION_TAG)) {
            this.fermentation = tag.getDouble(FERMENTATION_TAG);
        }
        this.bottles = tag.getInt(BOTTLES_TAG);
        this.effects.clear();
        this.durations.clear();
        this.amplifiers.clear();
        this.dilutions.clear();

        ListTag list = tag.getList(EFFECTS_TAG, 8);
        list.stream()
            .map(Tag::getAsString)
            .map(i -> i.replace("\"", "")) // madness
            .forEach(item -> this.effects.add(ResourceLocation.parse(item)));

        CompoundTag durations = tag.getCompound(DURATIONS_TAG);
        CompoundTag amplifiers = tag.getCompound(AMPLIFIERS_TAG);
        CompoundTag dilutions = tag.getCompound(DILUTIONS_TAG);
        this.effects.forEach(effect -> {
            this.durations.put(effect, durations.getInt(effect.toString()));
            this.amplifiers.put(effect, amplifiers.getInt(effect.toString()));
            this.dilutions.put(effect, dilutions.getInt(effect.toString()));
        });
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        if (this.name != null) {
            tag.putString(NAME_TAG, Component.Serializer.toJson(this.name, provider));
        }
        
        tag.putInt(BOTTLES_TAG, this.bottles);
        tag.putDouble(FERMENTATION_TAG, this.fermentation);

        CompoundTag durations = new CompoundTag();
        CompoundTag amplifiers = new CompoundTag();
        CompoundTag dilutions = new CompoundTag();

        ListTag effects = new ListTag();
        this.effects.forEach(effect -> {
            effects.add(StringTag.valueOf(effect.toString()));
            durations.putInt(effect.toString(), this.durations.get(effect));
            amplifiers.putInt(effect.toString(), this.amplifiers.get(effect));
            dilutions.putInt(effect.toString(), this.dilutions.get(effect));
        });

        tag.put(EFFECTS_TAG, effects);
        tag.put(DURATIONS_TAG, durations);
        tag.put(AMPLIFIERS_TAG, amplifiers);
        tag.put(DILUTIONS_TAG, dilutions);
    }

    @SuppressWarnings("Java8MapApi")
    public boolean add(ItemStack input) {
        if (!feature().handlers.isValidPotion(input)) {
            return false;
        }

        var potion = feature().handlers.getPotion(input);
        List<MobEffectInstance> potionEffects = new ArrayList<>();
        potion.getAllEffects().forEach(potionEffects::add);

        if (bottles < feature().maxBottles()) {

            // Reset effects if fresh cask
            if (bottles == 0) {
                this.effects.clear();
            }

            // Potions without effects just dilute the mix
            if (!potion.is(Potions.WATER) || !potionEffects.isEmpty()) {
                var currentEffects = potionEffects.isEmpty() && !potion.customEffects().isEmpty() ? potion.customEffects() : potionEffects;
                
                currentEffects.forEach(effect -> {
                    var changedAmplifier = false;
                    var duration = effect.getDuration();
                    var amplifier = effect.getAmplifier();
                    var holder = effect.getEffect();
                    var type = holder.value();
                    var instantenous = type.isInstantenous();
                    var effectId = BuiltInRegistries.MOB_EFFECT.getKey(type);

                    if (effectId == null) {
                        return;
                    }

                    if (!effects.contains(effectId)) {
                        effects.add(effectId);
                    }

                    if (!instantenous) {
                        // For effects that have duration.
                        if (amplifiers.containsKey(effectId)) {
                            int existingAmplifier = amplifiers.get(effectId);
                            changedAmplifier = amplifier != existingAmplifier;
                        }
                        amplifiers.put(effectId, amplifier);

                        if (!durations.containsKey(effectId)) {
                            durations.put(effectId, duration);
                        } else {
                            var existingDuration = durations.get(effectId);
                            if (changedAmplifier) {
                                durations.put(effectId, duration);
                            } else {
                                durations.put(effectId, existingDuration + duration);
                            }
                        }
                    } else {
                        // For effects that apply immediately (instant health, harming etc).
                        var existingDuration = durations.getOrDefault(effectId, 0);
                        var existingAmplifier = amplifiers.getOrDefault(effectId, -1);
                        durations.put(effectId, existingDuration + 1); // Keep track of how many we've added.
                        amplifiers.put(effectId, existingAmplifier == -1 ? amplifier : Math.min(existingAmplifier, amplifier)); // Always take the smallest amplifier.
                    }
                });
            }

            bottles++;

            effects.forEach(effectId -> {
                if (!dilutions.containsKey(effectId)) {
                    dilutions.put(effectId, bottles);
                } else {
                    int existingDilution = dilutions.get(effectId);
                    dilutions.put(effectId, existingDilution + 1);
                }
            });

            if (level != null) {
                level.playSound(null, getBlockPos(), feature().registers.addSound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            }

            debugShowContents();
            setChanged();
            return true;
        }

        return false;
    }

    @Nullable
    public ItemStack take() {
        if (this.bottles > 0) {
            var bottle = getBottle();
            removeBottle();

            // Play sound
            if (level != null) {
                var pos = getBlockPos();
                if (bottles > 0) {
                    level.playSound(null, pos, feature().registers.takeSound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                } else {
                    level.playSound(null, pos, feature().registers.emptySound.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }

            debugShowContents();
            return bottle;
        }

        return null;
    }

    /**
     * Create one bottle of potion from the cask's contents.
     */
    private ItemStack getBottle() {
        List<MobEffectInstance> effects = new ArrayList<>();
        List<ResourceLocation> effectsToRemove = new ArrayList<>();

        for (var effectId : this.effects) {
            var opt = BuiltInRegistries.MOB_EFFECT.getHolder(effectId);
            if (opt.isEmpty()) continue;
            
            var holder = opt.get();
            var type = holder.value();

            int duration = this.durations.get(effectId);
            int amplifier = this.amplifiers.get(effectId);
            int dilution = this.dilutions.get(effectId);
            
            if (!type.isInstantenous()) {
                effects.add(new MobEffectInstance(holder, duration / dilution, amplifier));
            } else {
                if (duration > 0) {
                    --duration;
                    this.durations.put(effectId, duration);
                    effects.add(new MobEffectInstance(holder, 1, Math.max(0, amplifier)));
                }
                if (duration == 0) {
                    effectsToRemove.add(effectId);
                }
            }
        }
        
        if (!effectsToRemove.isEmpty()) {
            for (var effectId : effectsToRemove) {
                this.effects.remove(effectId);
            }
            this.setChanged();
        }

        if (!effects.isEmpty()) {
            var customName = this.name != null ? this.name : Component.translatable("item.charm.home_brew");
            return feature().handlers.makeCustomPotion(customName, effects, this.fermentation);
        } else {
            return feature().handlers.getFilledWaterBottle();
        }
    }

    private void removeBottle() {
        // if no more bottles in the cask, flush out the cask data
        if (--bottles <= 0) {
            this.flush();
        }

        setChanged();
    }

    private void flush() {
        this.effects.clear();
        this.durations.clear();
        this.dilutions.clear();
        this.amplifiers.clear();
        this.bottles = 0;
        this.fermentation = 1.0d;

        debugShowContents();
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        var caskData = input.getOrDefault(feature().registers.caskData.get(), CaskData.EMPTY);

        this.name = input.get(DataComponents.CUSTOM_NAME);
        this.bottles = caskData.bottles();
        this.fermentation = caskData.fermentation();
        this.effects = caskData.effects();
        this.durations = caskData.durations();
        this.amplifiers = caskData.amplifiers();
        this.dilutions = caskData.dilutions();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        var caskData = feature().registers.caskData.get();

        builder.set(DataComponents.CUSTOM_NAME, this.name);
        builder.set(caskData, new CaskData(
            this.bottles,
            this.fermentation,
            this.effects,
            this.durations,
            this.amplifiers,
            this.dilutions
        ));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getName() {
        if (name != null) {
            return name;
        }
        return this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @Override
    public Class<Casks> typeForFeature() {
        return Casks.class;
    }
    
    void debugShowContents() {
        var log = feature().log();
        
        log.dev("-- Cask contents --");
        if (!effects.isEmpty()) {
            log.dev("Effects:");
            effects.forEach(e -> log.dev("  " + e.toString()));
        }
        if (!durations.isEmpty()) {
            log.dev("Durations:");
            durations.forEach((key, value) -> log.dev("  " + key.toString() + " = " + value));
        }
        if (!amplifiers.isEmpty()) {
            log.dev("Amplifiers:");
            amplifiers.forEach((key, value) -> log.dev("  " + key.toString() + " = " + value));
        }
        if (!durations.isEmpty()) {
            log.dev("Dilutions:");
            dilutions.forEach((key, value) -> log.dev("  " + key.toString() + " = " + value));
        }
        log.dev("Bottles: " + bottles);
        log.dev("Fermentation: " + fermentation);
    }

    Component getDefaultName() {
        return Component.translatable("container.charm.cask");
    }
}
