package svenhjol.charm.feature.core.custom_recipes.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import svenhjol.charm.charmony.Resolve;
import svenhjol.charm.charmony.common.helper.CommonFeatureHelper;
import svenhjol.charm.charmony.enums.Side;
import svenhjol.charm.charmony.feature.FeatureHolder;
import svenhjol.charm.charmony.helper.ResourceLocationHelper;
import svenhjol.charm.feature.core.custom_recipes.CustomRecipes;

import java.util.*;

public final class Handlers extends FeatureHolder<CustomRecipes> {
    private final List<String> fuzzyRemove = new ArrayList<>();
    private final List<String> exactRemove = new ArrayList<>();

    /**
     * Holds a reference to the global RecipeManager.
     */
    public RecipeManager managerHolder;

    public Handlers(CustomRecipes feature) {
        super(feature);
    }

    public void packReload(String reason) {
        feature().log().debug("Reloading Charm custom recipe filtering: " + reason);

        exactRemove.clear();
        fuzzyRemove.clear();

        for (var condition : feature().providers.conditions) {
            if (condition.test()) continue;
            condition.recipes().forEach(remove -> {
                if (remove.contains("*") || !remove.contains(":")) {
                    fuzzyRemove.add(remove);
                } else {
                    exactRemove.add(remove);
                }
            });
        }
    }

    /**
     * Called by sorting recipe manager to sort recipes by name so Charm's take priority.
     * @see RecipeSorter
     */
    public Map<ResourceLocation, RecipeHolder<?>> sortAndFilterByName(Map<ResourceLocation, RecipeHolder<?>> byName) {
        feature().log().debug("Sorting recipes by name.");

        ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> builder = ImmutableMap.builder();

        Map<ResourceLocation, RecipeHolder<?>> charmonyRecipeHolders = new HashMap<>();
        Map<ResourceLocation, RecipeHolder<?>> otherRecipeHolders = new HashMap<>();
        
        for (var id : byName.keySet()) {
            var recipeHolder = byName.get(id);
            
            if (Resolve.hasLoader(Side.COMMON, id.getNamespace())) {
                charmonyRecipeHolders.put(id, recipeHolder);
            } else {
                otherRecipeHolders.put(id, recipeHolder);
            }
        }
        
        charmonyRecipeHolders.forEach(builder::put);
        otherRecipeHolders.forEach(builder::put);

        return builder.build();
    }

    /**
     * Called by sorting recipe manager to sort recipes by type so Charm's take priority.
     * @see RecipeSorter
     */
    public Multimap<RecipeType<?>, RecipeHolder<?>> sortAndFilterByType(Multimap<RecipeType<?>, RecipeHolder<?>> byType) {
        feature().log().debug("Sorting recipes by type.");

        ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> builder = ImmutableMultimap.builder();

        for (var type : byType.keySet()) {
            var recipes = byType.get(type);
            feature().log().debug("Recipe type " + type.toString() + " contains " + recipes.size() + " recipes.");

            var charmonyRecipes = recipes.stream().filter(r -> Resolve.hasLoader(Side.COMMON, r.id().getNamespace()));
            var otherRecipes = recipes.stream().filter(r -> !Resolve.hasLoader(Side.COMMON, r.id().getNamespace()));
            var holders = new LinkedList<RecipeHolder<?>>();

            // Sort and filter the charmony recipes.
            charmonyRecipes
                .filter(r -> !shouldRemove(r.id())).sorted(Comparator.comparing(r -> r.id().getPath()))
                .forEach(holders::add);

            // Add all other recipes after these.
            otherRecipes.forEach(holders::add);

            holders.forEach(holder -> builder.put(type, holder));
            feature().log().debug("Recipe type " + type + " reassembled with " + holders.size() + " recipes");
        }

        return builder.build();
    }

    public boolean shouldRemove(ResourceLocation id) {
        return CommonFeatureHelper.isDisabledCharmonyFeature(id)
            || ResourceLocationHelper.match(id, exactRemove, fuzzyRemove);
    }
}
