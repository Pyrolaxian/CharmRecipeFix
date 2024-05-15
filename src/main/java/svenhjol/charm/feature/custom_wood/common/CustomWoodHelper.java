package svenhjol.charm.feature.custom_wood.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import svenhjol.charm.foundation.item.CharmHangingSignItem;
import svenhjol.charm.foundation.item.CharmSignItem;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

// TODO: move all static stuff to custom wood handler class.
@SuppressWarnings("unused")
public class CustomWoodHelper {
    static final Map<Boat.Type, Supplier<BoatItem>> BOAT_ITEM = new HashMap<>();
    static final Map<Boat.Type, Supplier<BoatItem>> CHEST_BOAT_ITEM = new HashMap<>();
    static final Map<Boat.Type, ResourceLocation> BOAT_PLANKS = new HashMap<>();
    static final Map<String, Map<String, List<Supplier<? extends Item>>>> CREATIVE_TAB_ITEMS = new HashMap<>();
    static final List<Supplier<CharmSignItem>> SIGN_ITEMS = new ArrayList<>();
    static final List<Supplier<CharmHangingSignItem>> HANGING_SIGN_ITEMS = new ArrayList<>();

    public static final String BARRELS = "barrels";
    public static final String BOATS = "boats";
    public static final String BOOKSHELVES = "bookshelves";
    public static final String CHESTS = "chests";
    public static final String CHEST_BOATS = "chest_boats";
    public static final String CHISELED_BOOKSHELVES = "chiseled_bookshelves";
    public static final String BUTTONS = "buttons";
    public static final String DOORS = "doors";
    public static final String FENCES = "fences";
    public static final String GATES = "gates";
    public static final String HANGING_SIGNS = "hanging_signs";
    public static final String LADDERS = "ladders";
    public static final String LEAVES = "leaves";
    public static final String LOGS = "logs";
    public static final String PLANKS = "planks";
    public static final String PRESSURE_PLATES = "pressure_plates";
    public static final String SAPLINGS = "saplings";
    public static final String SIGNS = "signs";
    public static final String SLABS = "slabs";
    public static final String STAIRS = "stairs";
    public static final String STRIPPED_LOGS = "stripped_logs";
    public static final String STRIPPED_WOODS = "stripped_wood";
    public static final String TRAPDOORS = "trapdoors";
    public static final String TRAPPED_CHESTS = "trapped_chests";
    public static final String WOODS = "woods";
    public static final List<String> BUILDING_BLOCKS = List.of(
        BUTTONS, DOORS, FENCES, GATES, LOGS, PLANKS, PRESSURE_PLATES,
        SLABS, STAIRS, STRIPPED_LOGS, STRIPPED_WOODS, TRAPDOORS, WOODS
    );

    public static void addCreativeTabItem(String modId, String group, Supplier<? extends Item> item) {
        CREATIVE_TAB_ITEMS.computeIfAbsent(modId, m -> new LinkedHashMap<>())
            .computeIfAbsent(group, a -> new LinkedList<>()).add(item);
    }

    public static Map<String, Map<String, List<Supplier<? extends Item>>>> getCreativeTabItems() {
        return CREATIVE_TAB_ITEMS;
    }

    public static void setItemForBoat(Boat.Type type, Supplier<BoatItem> item) {
        BOAT_ITEM.put(type, item);
    }

    public static void setItemForChestBoat(Boat.Type type, Supplier<BoatItem> item) {
        CHEST_BOAT_ITEM.put(type, item);
    }

    public static void setPlanksForBoat(Boat.Type type, ResourceLocation id) {
        BOAT_PLANKS.put(type, id);
    }

    public static void addSignItem(Supplier<CharmSignItem> item) {
        if (!SIGN_ITEMS.contains(item)) {
            SIGN_ITEMS.add(item);
        }
    }

    public static void addHangingSignItem(Supplier<CharmHangingSignItem> item) {
        if (!HANGING_SIGN_ITEMS.contains(item)) {
            HANGING_SIGN_ITEMS.add(item);
        }
    }

    @Nullable
    public static BoatItem getBoatByType(Boat.Type boatType) {
        return BOAT_ITEM.getOrDefault(boatType, () -> null).get();
    }

    @Nullable
    public static BoatItem getChestBoatByType(Boat.Type boatType) {
        return CHEST_BOAT_ITEM.getOrDefault(boatType, () -> null).get();
    }

    public static List<Supplier<CharmSignItem>> getSignItems() {
        return SIGN_ITEMS;
    }

    public static List<Supplier<CharmHangingSignItem>> getHangingSignItems() {
        return HANGING_SIGN_ITEMS;
    }

    public static Map<Boat.Type, ResourceLocation> getBoatPlanks() {
        return BOAT_PLANKS;
    }
}
