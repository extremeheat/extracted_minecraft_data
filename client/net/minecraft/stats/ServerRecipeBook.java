package net.minecraft.stats;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookRemovePacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import org.slf4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   public static final String RECIPE_BOOK_TAG = "recipeBook";
   private static final Logger LOGGER = LogUtils.getLogger();
   private final DisplayResolver displayResolver;
   @VisibleForTesting
   protected final Set<ResourceKey<Recipe<?>>> known = Sets.newIdentityHashSet();
   @VisibleForTesting
   protected final Set<ResourceKey<Recipe<?>>> highlight = Sets.newIdentityHashSet();

   public ServerRecipeBook(DisplayResolver var1) {
      super();
      this.displayResolver = var1;
   }

   public void add(ResourceKey<Recipe<?>> var1) {
      this.known.add(var1);
   }

   public boolean contains(ResourceKey<Recipe<?>> var1) {
      return this.known.contains(var1);
   }

   public void remove(ResourceKey<Recipe<?>> var1) {
      this.known.remove(var1);
      this.highlight.remove(var1);
   }

   public void removeHighlight(ResourceKey<Recipe<?>> var1) {
      this.highlight.remove(var1);
   }

   private void addHighlight(ResourceKey<Recipe<?>> var1) {
      this.highlight.add(var1);
   }

   public int addRecipes(Collection<RecipeHolder<?>> var1, ServerPlayer var2) {
      ArrayList var3 = new ArrayList();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         RecipeHolder var5 = (RecipeHolder)var4.next();
         ResourceKey var6 = var5.id();
         if (!this.known.contains(var6) && !var5.value().isSpecial()) {
            this.add(var6);
            this.addHighlight(var6);
            this.displayResolver.displaysForRecipe(var6, (var2x) -> {
               var3.add(new ClientboundRecipeBookAddPacket.Entry(var2x, var5.value().showNotification(), true));
            });
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(var2, var5);
         }
      }

      if (!var3.isEmpty()) {
         var2.connection.send(new ClientboundRecipeBookAddPacket(var3, false));
      }

      return var3.size();
   }

   public int removeRecipes(Collection<RecipeHolder<?>> var1, ServerPlayer var2) {
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         RecipeHolder var5 = (RecipeHolder)var4.next();
         ResourceKey var6 = var5.id();
         if (this.known.contains(var6)) {
            this.remove(var6);
            this.displayResolver.displaysForRecipe(var6, (var1x) -> {
               var3.add(var1x.id());
            });
         }
      }

      if (!var3.isEmpty()) {
         var2.connection.send(new ClientboundRecipeBookRemovePacket(var3));
      }

      return var3.size();
   }

   public CompoundTag toNbt() {
      CompoundTag var1 = new CompoundTag();
      this.getBookSettings().write(var1);
      ListTag var2 = new ListTag();
      Iterator var3 = this.known.iterator();

      while(var3.hasNext()) {
         ResourceKey var4 = (ResourceKey)var3.next();
         var2.add(StringTag.valueOf(var4.location().toString()));
      }

      var1.put("recipes", var2);
      ListTag var6 = new ListTag();
      Iterator var7 = this.highlight.iterator();

      while(var7.hasNext()) {
         ResourceKey var5 = (ResourceKey)var7.next();
         var6.add(StringTag.valueOf(var5.location().toString()));
      }

      var1.put("toBeDisplayed", var6);
      return var1;
   }

   public void fromNbt(CompoundTag var1, Predicate<ResourceKey<Recipe<?>>> var2) {
      this.setBookSettings(RecipeBookSettings.read(var1));
      ListTag var3 = var1.getList("recipes", 8);
      this.loadRecipes(var3, this::add, var2);
      ListTag var4 = var1.getList("toBeDisplayed", 8);
      this.loadRecipes(var4, this::addHighlight, var2);
   }

   private void loadRecipes(ListTag var1, Consumer<ResourceKey<Recipe<?>>> var2, Predicate<ResourceKey<Recipe<?>>> var3) {
      for(int var4 = 0; var4 < var1.size(); ++var4) {
         String var5 = var1.getString(var4);

         try {
            ResourceKey var6 = ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(var5));
            if (!var3.test(var6)) {
               LOGGER.error("Tried to load unrecognized recipe: {} removed now.", var6);
            } else {
               var2.accept(var6);
            }
         } catch (ResourceLocationException var7) {
            LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", var5);
         }
      }

   }

   public void sendInitialRecipeBook(ServerPlayer var1) {
      var1.connection.send(new ClientboundRecipeBookSettingsPacket(this.getBookSettings()));
      ArrayList var2 = new ArrayList(this.known.size());
      Iterator var3 = this.known.iterator();

      while(var3.hasNext()) {
         ResourceKey var4 = (ResourceKey)var3.next();
         this.displayResolver.displaysForRecipe(var4, (var3x) -> {
            var2.add(new ClientboundRecipeBookAddPacket.Entry(var3x, false, this.highlight.contains(var4)));
         });
      }

      var1.connection.send(new ClientboundRecipeBookAddPacket(var2, true));
   }

   public void copyOverData(ServerRecipeBook var1) {
      this.known.clear();
      this.highlight.clear();
      this.bookSettings.replaceFrom(var1.bookSettings);
      this.known.addAll(var1.known);
      this.highlight.addAll(var1.highlight);
   }

   @FunctionalInterface
   public interface DisplayResolver {
      void displaysForRecipe(ResourceKey<Recipe<?>> var1, Consumer<RecipeDisplayEntry> var2);
   }
}
