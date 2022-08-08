package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
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
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private static final Logger LOGGER = LogUtils.getLogger();
   private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes = ImmutableMap.of();
   private Map<ResourceLocation, Recipe<?>> byName = ImmutableMap.of();
   private boolean hasErrors;

   public RecipeManager() {
      super(GSON, "recipes");
   }

   protected void apply(Map<ResourceLocation, JsonElement> var1, ResourceManager var2, ProfilerFiller var3) {
      this.hasErrors = false;
      HashMap var4 = Maps.newHashMap();
      ImmutableMap.Builder var5 = ImmutableMap.builder();
      Iterator var6 = var1.entrySet().iterator();

      while(var6.hasNext()) {
         Map.Entry var7 = (Map.Entry)var6.next();
         ResourceLocation var8 = (ResourceLocation)var7.getKey();

         try {
            Recipe var9 = fromJson(var8, GsonHelper.convertToJsonObject((JsonElement)var7.getValue(), "top element"));
            ((ImmutableMap.Builder)var4.computeIfAbsent(var9.getType(), (var0) -> {
               return ImmutableMap.builder();
            })).put(var8, var9);
            var5.put(var8, var9);
         } catch (IllegalArgumentException | JsonParseException var10) {
            LOGGER.error("Parsing error loading recipe {}", var8, var10);
         }
      }

      this.recipes = (Map)var4.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (var0) -> {
         return ((ImmutableMap.Builder)var0.getValue()).build();
      }));
      this.byName = var5.build();
      LOGGER.info("Loaded {} recipes", var4.size());
   }

   public boolean hadErrorsLoading() {
      return this.hasErrors;
   }

   public <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(RecipeType<T> var1, C var2, Level var3) {
      return this.byType(var1).values().stream().filter((var2x) -> {
         return var2x.matches(var2, var3);
      }).findFirst();
   }

   public <C extends Container, T extends Recipe<C>> Optional<Pair<ResourceLocation, T>> getRecipeFor(RecipeType<T> var1, C var2, Level var3, @Nullable ResourceLocation var4) {
      Map var5 = this.byType(var1);
      if (var4 != null) {
         Recipe var6 = (Recipe)var5.get(var4);
         if (var6 != null && var6.matches(var2, var3)) {
            return Optional.of(Pair.of(var4, var6));
         }
      }

      return var5.entrySet().stream().filter((var2x) -> {
         return ((Recipe)var2x.getValue()).matches(var2, var3);
      }).findFirst().map((var0) -> {
         return Pair.of((ResourceLocation)var0.getKey(), (Recipe)var0.getValue());
      });
   }

   public <C extends Container, T extends Recipe<C>> List<T> getAllRecipesFor(RecipeType<T> var1) {
      return List.copyOf(this.byType(var1).values());
   }

   public <C extends Container, T extends Recipe<C>> List<T> getRecipesFor(RecipeType<T> var1, C var2, Level var3) {
      return (List)this.byType(var1).values().stream().filter((var2x) -> {
         return var2x.matches(var2, var3);
      }).sorted(Comparator.comparing((var0) -> {
         return var0.getResultItem().getDescriptionId();
      })).collect(Collectors.toList());
   }

   private <C extends Container, T extends Recipe<C>> Map<ResourceLocation, T> byType(RecipeType<T> var1) {
      return (Map)this.recipes.getOrDefault(var1, Collections.emptyMap());
   }

   public <C extends Container, T extends Recipe<C>> NonNullList<ItemStack> getRemainingItemsFor(RecipeType<T> var1, C var2, Level var3) {
      Optional var4 = this.getRecipeFor(var1, var2, var3);
      if (var4.isPresent()) {
         return ((Recipe)var4.get()).getRemainingItems(var2);
      } else {
         NonNullList var5 = NonNullList.withSize(var2.getContainerSize(), ItemStack.EMPTY);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            var5.set(var6, var2.getItem(var6));
         }

         return var5;
      }
   }

   public Optional<? extends Recipe<?>> byKey(ResourceLocation var1) {
      return Optional.ofNullable((Recipe)this.byName.get(var1));
   }

   public Collection<Recipe<?>> getRecipes() {
      return (Collection)this.recipes.values().stream().flatMap((var0) -> {
         return var0.values().stream();
      }).collect(Collectors.toSet());
   }

   public Stream<ResourceLocation> getRecipeIds() {
      return this.recipes.values().stream().flatMap((var0) -> {
         return var0.keySet().stream();
      });
   }

   public static Recipe<?> fromJson(ResourceLocation var0, JsonObject var1) {
      String var2 = GsonHelper.getAsString(var1, "type");
      return ((RecipeSerializer)Registry.RECIPE_SERIALIZER.getOptional(new ResourceLocation(var2)).orElseThrow(() -> {
         return new JsonSyntaxException("Invalid or unsupported recipe type '" + var2 + "'");
      })).fromJson(var0, var1);
   }

   public void replaceRecipes(Iterable<Recipe<?>> var1) {
      this.hasErrors = false;
      HashMap var2 = Maps.newHashMap();
      ImmutableMap.Builder var3 = ImmutableMap.builder();
      var1.forEach((var2x) -> {
         Map var3x = (Map)var2.computeIfAbsent(var2x.getType(), (var0) -> {
            return Maps.newHashMap();
         });
         ResourceLocation var4 = var2x.getId();
         Recipe var5 = (Recipe)var3x.put(var4, var2x);
         var3.put(var4, var2x);
         if (var5 != null) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + var4);
         }
      });
      this.recipes = ImmutableMap.copyOf(var2);
      this.byName = var3.build();
   }

   public static <C extends Container, T extends Recipe<C>> CachedCheck<C, T> createCheck(final RecipeType<T> var0) {
      return new CachedCheck<C, T>() {
         @Nullable
         private ResourceLocation lastRecipe;

         public Optional<T> getRecipeFor(C var1, Level var2) {
            RecipeManager var3 = var2.getRecipeManager();
            Optional var4 = var3.getRecipeFor(var0, var1, var2, this.lastRecipe);
            if (var4.isPresent()) {
               Pair var5 = (Pair)var4.get();
               this.lastRecipe = (ResourceLocation)var5.getFirst();
               return Optional.of((Recipe)var5.getSecond());
            } else {
               return Optional.empty();
            }
         }
      };
   }

   public interface CachedCheck<C extends Container, T extends Recipe<C>> {
      Optional<T> getRecipeFor(C var1, Level var2);
   }
}
