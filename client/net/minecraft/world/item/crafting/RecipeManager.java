package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class RecipeManager extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private static final Logger LOGGER = LogUtils.getLogger();
   private Map<RecipeType<?>, Map<ResourceLocation, RecipeHolder<?>>> recipes = ImmutableMap.of();
   private Map<ResourceLocation, RecipeHolder<?>> byName = ImmutableMap.of();
   private boolean hasErrors;

   public RecipeManager() {
      super(GSON, "recipes");
   }

   protected void apply(Map<ResourceLocation, JsonElement> var1, ResourceManager var2, ProfilerFiller var3) {
      this.hasErrors = false;
      HashMap var4 = Maps.newHashMap();
      Builder var5 = ImmutableMap.builder();

      for(Entry var7 : var1.entrySet()) {
         ResourceLocation var8 = (ResourceLocation)var7.getKey();

         try {
            RecipeHolder var9 = fromJson(var8, GsonHelper.convertToJsonObject((JsonElement)var7.getValue(), "top element"));
            ((Builder)var4.computeIfAbsent(var9.value().getType(), var0 -> ImmutableMap.builder())).put(var8, var9);
            var5.put(var8, var9);
         } catch (IllegalArgumentException | JsonParseException var10) {
            LOGGER.error("Parsing error loading recipe {}", var8, var10);
         }
      }

      this.recipes = var4.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, var0 -> ((Builder)var0.getValue()).build()));
      this.byName = var5.build();
      LOGGER.info("Loaded {} recipes", var4.size());
   }

   public boolean hadErrorsLoading() {
      return this.hasErrors;
   }

   public <C extends Container, T extends Recipe<C>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> var1, C var2, Level var3) {
      return this.byType(var1).values().stream().filter(var2x -> var2x.value().matches((C)var2, var3)).findFirst();
   }

   public <C extends Container, T extends Recipe<C>> Optional<Pair<ResourceLocation, RecipeHolder<T>>> getRecipeFor(
      RecipeType<T> var1, C var2, Level var3, @Nullable ResourceLocation var4
   ) {
      Map var5 = this.byType(var1);
      if (var4 != null) {
         RecipeHolder var6 = (RecipeHolder)var5.get(var4);
         if (var6 != null && var6.value().matches(var2, var3)) {
            return Optional.of((T)Pair.of(var4, var6));
         }
      }

      return var5.entrySet()
         .stream()
         .filter(var2x -> ((RecipeHolder)var2x.getValue()).value().matches(var2, var3))
         .findFirst()
         .map(var0 -> Pair.of((ResourceLocation)var0.getKey(), (RecipeHolder)var0.getValue()));
   }

   public <C extends Container, T extends Recipe<C>> List<RecipeHolder<T>> getAllRecipesFor(RecipeType<T> var1) {
      return List.copyOf(this.byType(var1).values());
   }

   public <C extends Container, T extends Recipe<C>> List<RecipeHolder<T>> getRecipesFor(RecipeType<T> var1, C var2, Level var3) {
      return this.byType(var1)
         .values()
         .stream()
         .filter(var2x -> var2x.value().matches(var2, var3))
         .sorted(Comparator.comparing(var1x -> var1x.value().getResultItem(var3.registryAccess()).getDescriptionId()))
         .collect(Collectors.toList());
   }

   private <C extends Container, T extends Recipe<C>> Map<ResourceLocation, RecipeHolder<T>> byType(RecipeType<T> var1) {
      return this.recipes.getOrDefault(var1, Collections.emptyMap());
   }

   public <C extends Container, T extends Recipe<C>> NonNullList<ItemStack> getRemainingItemsFor(RecipeType<T> var1, C var2, Level var3) {
      Optional var4 = this.getRecipeFor(var1, var2, var3);
      if (var4.isPresent()) {
         return ((RecipeHolder)var4.get()).value().getRemainingItems(var2);
      } else {
         NonNullList var5 = NonNullList.withSize(var2.getContainerSize(), ItemStack.EMPTY);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            var5.set(var6, var2.getItem(var6));
         }

         return var5;
      }
   }

   public Optional<RecipeHolder<?>> byKey(ResourceLocation var1) {
      return Optional.ofNullable(this.byName.get(var1));
   }

   public Collection<RecipeHolder<?>> getRecipes() {
      return this.recipes.values().stream().flatMap(var0 -> var0.values().stream()).collect(Collectors.toSet());
   }

   public Stream<ResourceLocation> getRecipeIds() {
      return this.recipes.values().stream().flatMap(var0 -> var0.keySet().stream());
   }

   protected static RecipeHolder<?> fromJson(ResourceLocation var0, JsonObject var1) {
      Recipe var2 = Util.getOrThrow(Recipe.CODEC.parse(JsonOps.INSTANCE, var1), JsonParseException::new);
      return new RecipeHolder(var0, var2);
   }

   public void replaceRecipes(Iterable<RecipeHolder<?>> var1) {
      this.hasErrors = false;
      HashMap var2 = Maps.newHashMap();
      Builder var3 = ImmutableMap.builder();
      var1.forEach(var2x -> {
         Map var3x = var2.computeIfAbsent(var2x.value().getType(), var0x -> Maps.newHashMap());
         ResourceLocation var4 = var2x.id();
         RecipeHolder var5 = var3x.put(var4, var2x);
         var3.put(var4, var2x);
         if (var5 != null) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + var4);
         }
      });
      this.recipes = ImmutableMap.copyOf(var2);
      this.byName = var3.build();
   }

   public static <C extends Container, T extends Recipe<C>> RecipeManager.CachedCheck<C, T> createCheck(final RecipeType<T> var0) {
      return new RecipeManager.CachedCheck<C, T>() {
         @Nullable
         private ResourceLocation lastRecipe;

         @Override
         public Optional<RecipeHolder<T>> getRecipeFor(C var1, Level var2) {
            RecipeManager var3 = var2.getRecipeManager();
            Optional var4 = var3.getRecipeFor(var0, var1, var2, this.lastRecipe);
            if (var4.isPresent()) {
               Pair var5 = (Pair)var4.get();
               this.lastRecipe = (ResourceLocation)var5.getFirst();
               return Optional.of((RecipeHolder<T>)var5.getSecond());
            } else {
               return Optional.empty();
            }
         }
      };
   }

   public interface CachedCheck<C extends Container, T extends Recipe<C>> {
      Optional<RecipeHolder<T>> getRecipeFor(C var1, Level var2);
   }
}
