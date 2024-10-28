package net.minecraft.stats;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   public static final String RECIPE_BOOK_TAG = "recipeBook";
   private static final Logger LOGGER = LogUtils.getLogger();

   public ServerRecipeBook() {
      super();
   }

   public int addRecipes(Collection<RecipeHolder<?>> var1, ServerPlayer var2) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         RecipeHolder var6 = (RecipeHolder)var5.next();
         ResourceLocation var7 = var6.id();
         if (!this.known.contains(var7) && !var6.value().isSpecial()) {
            this.add(var7);
            this.addHighlight(var7);
            var3.add(var7);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(var2, var6);
            ++var4;
         }
      }

      if (var3.size() > 0) {
         this.sendRecipes(ClientboundRecipePacket.State.ADD, var2, var3);
      }

      return var4;
   }

   public int removeRecipes(Collection<RecipeHolder<?>> var1, ServerPlayer var2) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         RecipeHolder var6 = (RecipeHolder)var5.next();
         ResourceLocation var7 = var6.id();
         if (this.known.contains(var7)) {
            this.remove(var7);
            var3.add(var7);
            ++var4;
         }
      }

      this.sendRecipes(ClientboundRecipePacket.State.REMOVE, var2, var3);
      return var4;
   }

   private void sendRecipes(ClientboundRecipePacket.State var1, ServerPlayer var2, List<ResourceLocation> var3) {
      var2.connection.send(new ClientboundRecipePacket(var1, var3, Collections.emptyList(), this.getBookSettings()));
   }

   public CompoundTag toNbt() {
      CompoundTag var1 = new CompoundTag();
      this.getBookSettings().write(var1);
      ListTag var2 = new ListTag();
      Iterator var3 = this.known.iterator();

      while(var3.hasNext()) {
         ResourceLocation var4 = (ResourceLocation)var3.next();
         var2.add(StringTag.valueOf(var4.toString()));
      }

      var1.put("recipes", var2);
      ListTag var6 = new ListTag();
      Iterator var7 = this.highlight.iterator();

      while(var7.hasNext()) {
         ResourceLocation var5 = (ResourceLocation)var7.next();
         var6.add(StringTag.valueOf(var5.toString()));
      }

      var1.put("toBeDisplayed", var6);
      return var1;
   }

   public void fromNbt(CompoundTag var1, RecipeManager var2) {
      this.setBookSettings(RecipeBookSettings.read(var1));
      ListTag var3 = var1.getList("recipes", 8);
      this.loadRecipes(var3, this::add, var2);
      ListTag var4 = var1.getList("toBeDisplayed", 8);
      this.loadRecipes(var4, this::addHighlight, var2);
   }

   private void loadRecipes(ListTag var1, Consumer<RecipeHolder<?>> var2, RecipeManager var3) {
      for(int var4 = 0; var4 < var1.size(); ++var4) {
         String var5 = var1.getString(var4);

         try {
            ResourceLocation var6 = ResourceLocation.parse(var5);
            Optional var7 = var3.byKey(var6);
            if (var7.isEmpty()) {
               LOGGER.error("Tried to load unrecognized recipe: {} removed now.", var6);
            } else {
               var2.accept((RecipeHolder)var7.get());
            }
         } catch (ResourceLocationException var8) {
            LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", var5);
         }
      }

   }

   public void sendInitialRecipeBook(ServerPlayer var1) {
      var1.connection.send(new ClientboundRecipePacket(ClientboundRecipePacket.State.INIT, this.known, this.highlight, this.getBookSettings()));
   }
}
