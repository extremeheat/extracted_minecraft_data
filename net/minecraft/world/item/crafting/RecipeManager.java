package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager extends SimpleJsonResourceReloadListener {
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private static final Logger LOGGER = LogManager.getLogger();
   private Map recipes = ImmutableMap.of();
   private boolean hasErrors;

   public RecipeManager() {
      super(GSON, "recipes");
   }

   protected void apply(Map var1, ResourceManager var2, ProfilerFiller var3) {
      this.hasErrors = false;
      HashMap var4 = Maps.newHashMap();
      Iterator var5 = var1.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         ResourceLocation var7 = (ResourceLocation)var6.getKey();

         try {
            Recipe var8 = fromJson(var7, (JsonObject)var6.getValue());
            ((Builder)var4.computeIfAbsent(var8.getType(), (var0) -> {
               return ImmutableMap.builder();
            })).put(var7, var8);
         } catch (IllegalArgumentException | JsonParseException var9) {
            LOGGER.error("Parsing error loading recipe {}", var7, var9);
         }
      }

      this.recipes = (Map)var4.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (var0) -> {
         return ((Builder)var0.getValue()).build();
      }));
      LOGGER.info("Loaded {} recipes", var4.size());
   }

   public Optional getRecipeFor(RecipeType var1, Container var2, Level var3) {
      return this.byType(var1).values().stream().flatMap((var3x) -> {
         return Util.toStream(var1.tryMatch(var3x, var3, var2));
      }).findFirst();
   }

   public List getRecipesFor(RecipeType var1, Container var2, Level var3) {
      return (List)this.byType(var1).values().stream().flatMap((var3x) -> {
         return Util.toStream(var1.tryMatch(var3x, var3, var2));
      }).sorted(Comparator.comparing((var0) -> {
         return var0.getResultItem().getDescriptionId();
      })).collect(Collectors.toList());
   }

   private Map byType(RecipeType var1) {
      return (Map)this.recipes.getOrDefault(var1, Collections.emptyMap());
   }

   public NonNullList getRemainingItemsFor(RecipeType var1, Container var2, Level var3) {
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

   public Optional byKey(ResourceLocation var1) {
      return this.recipes.values().stream().map((var1x) -> {
         return (Recipe)var1x.get(var1);
      }).filter(Objects::nonNull).findFirst();
   }

   public Collection getRecipes() {
      return (Collection)this.recipes.values().stream().flatMap((var0) -> {
         return var0.values().stream();
      }).collect(Collectors.toSet());
   }

   public Stream getRecipeIds() {
      return this.recipes.values().stream().flatMap((var0) -> {
         return var0.keySet().stream();
      });
   }

   public static Recipe fromJson(ResourceLocation var0, JsonObject var1) {
      String var2 = GsonHelper.getAsString(var1, "type");
      return ((RecipeSerializer)Registry.RECIPE_SERIALIZER.getOptional(new ResourceLocation(var2)).orElseThrow(() -> {
         return new JsonSyntaxException("Invalid or unsupported recipe type '" + var2 + "'");
      })).fromJson(var0, var1);
   }

   public void replaceRecipes(Iterable var1) {
      this.hasErrors = false;
      HashMap var2 = Maps.newHashMap();
      var1.forEach((var1x) -> {
         Map var2x = (Map)var2.computeIfAbsent(var1x.getType(), (var0) -> {
            return Maps.newHashMap();
         });
         Recipe var3 = (Recipe)var2x.put(var1x.getId(), var1x);
         if (var3 != null) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + var1x.getId());
         }
      });
      this.recipes = ImmutableMap.copyOf(var2);
   }
}
