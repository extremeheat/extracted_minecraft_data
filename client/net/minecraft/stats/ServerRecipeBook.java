package net.minecraft.stats;

import com.google.common.collect.Lists;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBook extends RecipeBook {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RecipeManager manager;

   public ServerRecipeBook(RecipeManager var1) {
      super();
      this.manager = var1;
   }

   public int addRecipes(Collection<Recipe<?>> var1, ServerPlayer var2) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Recipe var6 = (Recipe)var5.next();
         ResourceLocation var7 = var6.getId();
         if (!this.known.contains(var7) && !var6.isSpecial()) {
            this.add(var7);
            this.addHighlight(var7);
            var3.add(var7);
            CriteriaTriggers.RECIPE_UNLOCKED.trigger(var2, var6);
            ++var4;
         }
      }

      this.sendRecipes(ClientboundRecipePacket.State.ADD, var2, var3);
      return var4;
   }

   public int removeRecipes(Collection<Recipe<?>> var1, ServerPlayer var2) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Recipe var6 = (Recipe)var5.next();
         ResourceLocation var7 = var6.getId();
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
      var2.connection.send(new ClientboundRecipePacket(var1, var3, Collections.emptyList(), this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
   }

   public CompoundTag toNbt() {
      CompoundTag var1 = new CompoundTag();
      var1.putBoolean("isGuiOpen", this.guiOpen);
      var1.putBoolean("isFilteringCraftable", this.filteringCraftable);
      var1.putBoolean("isFurnaceGuiOpen", this.furnaceGuiOpen);
      var1.putBoolean("isFurnaceFilteringCraftable", this.furnaceFilteringCraftable);
      ListTag var2 = new ListTag();
      Iterator var3 = this.known.iterator();

      while(var3.hasNext()) {
         ResourceLocation var4 = (ResourceLocation)var3.next();
         var2.add(new StringTag(var4.toString()));
      }

      var1.put("recipes", var2);
      ListTag var6 = new ListTag();
      Iterator var7 = this.highlight.iterator();

      while(var7.hasNext()) {
         ResourceLocation var5 = (ResourceLocation)var7.next();
         var6.add(new StringTag(var5.toString()));
      }

      var1.put("toBeDisplayed", var6);
      return var1;
   }

   public void fromNbt(CompoundTag var1) {
      this.guiOpen = var1.getBoolean("isGuiOpen");
      this.filteringCraftable = var1.getBoolean("isFilteringCraftable");
      this.furnaceGuiOpen = var1.getBoolean("isFurnaceGuiOpen");
      this.furnaceFilteringCraftable = var1.getBoolean("isFurnaceFilteringCraftable");
      ListTag var2 = var1.getList("recipes", 8);
      this.loadRecipes(var2, this::add);
      ListTag var3 = var1.getList("toBeDisplayed", 8);
      this.loadRecipes(var3, this::addHighlight);
   }

   private void loadRecipes(ListTag var1, Consumer<Recipe<?>> var2) {
      for(int var3 = 0; var3 < var1.size(); ++var3) {
         String var4 = var1.getString(var3);

         try {
            ResourceLocation var5 = new ResourceLocation(var4);
            Optional var6 = this.manager.byKey(var5);
            if (!var6.isPresent()) {
               LOGGER.error("Tried to load unrecognized recipe: {} removed now.", var5);
            } else {
               var2.accept(var6.get());
            }
         } catch (ResourceLocationException var7) {
            LOGGER.error("Tried to load improperly formatted recipe: {} removed now.", var4);
         }
      }

   }

   public void sendInitialRecipeBook(ServerPlayer var1) {
      var1.connection.send(new ClientboundRecipePacket(ClientboundRecipePacket.State.INIT, this.known, this.highlight, this.guiOpen, this.filteringCraftable, this.furnaceGuiOpen, this.furnaceFilteringCraftable));
   }
}
