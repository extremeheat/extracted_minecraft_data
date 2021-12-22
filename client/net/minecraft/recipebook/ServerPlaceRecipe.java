package net.minecraft.recipebook;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlaceRecipe<C extends Container> implements PlaceRecipe<Integer> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final StackedContents stackedContents = new StackedContents();
   protected Inventory inventory;
   protected RecipeBookMenu<C> menu;

   public ServerPlaceRecipe(RecipeBookMenu<C> var1) {
      super();
      this.menu = var1;
   }

   public void recipeClicked(ServerPlayer var1, @Nullable Recipe<C> var2, boolean var3) {
      if (var2 != null && var1.getRecipeBook().contains(var2)) {
         this.inventory = var1.getInventory();
         if (this.testClearGrid() || var1.isCreative()) {
            this.stackedContents.clear();
            var1.getInventory().fillStackedContents(this.stackedContents);
            this.menu.fillCraftSlotsStackedContents(this.stackedContents);
            if (this.stackedContents.canCraft(var2, (IntList)null)) {
               this.handleRecipeClicked(var2, var3);
            } else {
               this.clearGrid(true);
               var1.connection.send(new ClientboundPlaceGhostRecipePacket(var1.containerMenu.containerId, var2));
            }

            var1.getInventory().setChanged();
         }
      }
   }

   protected void clearGrid(boolean var1) {
      for(int var2 = 0; var2 < this.menu.getSize(); ++var2) {
         if (this.menu.shouldMoveToInventory(var2)) {
            ItemStack var3 = this.menu.getSlot(var2).getItem().copy();
            this.inventory.placeItemBackInInventory(var3, false);
            this.menu.getSlot(var2).set(var3);
         }
      }

      this.menu.clearCraftingContent();
   }

   protected void handleRecipeClicked(Recipe<C> var1, boolean var2) {
      boolean var3 = this.menu.recipeMatches(var1);
      int var4 = this.stackedContents.getBiggestCraftableStack(var1, (IntList)null);
      int var5;
      if (var3) {
         for(var5 = 0; var5 < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++var5) {
            if (var5 != this.menu.getResultSlotIndex()) {
               ItemStack var6 = this.menu.getSlot(var5).getItem();
               if (!var6.isEmpty() && Math.min(var4, var6.getMaxStackSize()) < var6.getCount() + 1) {
                  return;
               }
            }
         }
      }

      var5 = this.getStackSize(var2, var4, var3);
      IntArrayList var11 = new IntArrayList();
      if (this.stackedContents.canCraft(var1, var11, var5)) {
         int var7 = var5;
         IntListIterator var8 = var11.iterator();

         while(var8.hasNext()) {
            int var9 = (Integer)var8.next();
            int var10 = StackedContents.fromStackingIndex(var9).getMaxStackSize();
            if (var10 < var7) {
               var7 = var10;
            }
         }

         if (this.stackedContents.canCraft(var1, var11, var7)) {
            this.clearGrid(false);
            this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), var1, var11.iterator(), var7);
         }
      }

   }

   public void addItemToSlot(Iterator<Integer> var1, int var2, int var3, int var4, int var5) {
      Slot var6 = this.menu.getSlot(var2);
      ItemStack var7 = StackedContents.fromStackingIndex((Integer)var1.next());
      if (!var7.isEmpty()) {
         for(int var8 = 0; var8 < var3; ++var8) {
            this.moveItemToGrid(var6, var7);
         }
      }

   }

   protected int getStackSize(boolean var1, int var2, boolean var3) {
      int var4 = 1;
      if (var1) {
         var4 = var2;
      } else if (var3) {
         var4 = 64;

         for(int var5 = 0; var5 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++var5) {
            if (var5 != this.menu.getResultSlotIndex()) {
               ItemStack var6 = this.menu.getSlot(var5).getItem();
               if (!var6.isEmpty() && var4 > var6.getCount()) {
                  var4 = var6.getCount();
               }
            }
         }

         if (var4 < 64) {
            ++var4;
         }
      }

      return var4;
   }

   protected void moveItemToGrid(Slot var1, ItemStack var2) {
      int var3 = this.inventory.findSlotMatchingUnusedItem(var2);
      if (var3 != -1) {
         ItemStack var4 = this.inventory.getItem(var3).copy();
         if (!var4.isEmpty()) {
            if (var4.getCount() > 1) {
               this.inventory.removeItem(var3, 1);
            } else {
               this.inventory.removeItemNoUpdate(var3);
            }

            var4.setCount(1);
            if (var1.getItem().isEmpty()) {
               var1.set(var4);
            } else {
               var1.getItem().grow(1);
            }

         }
      }
   }

   private boolean testClearGrid() {
      ArrayList var1 = Lists.newArrayList();
      int var2 = this.getAmountOfFreeSlotsInInventory();

      for(int var3 = 0; var3 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++var3) {
         if (var3 != this.menu.getResultSlotIndex()) {
            ItemStack var4 = this.menu.getSlot(var3).getItem().copy();
            if (!var4.isEmpty()) {
               int var5 = this.inventory.getSlotWithRemainingSpace(var4);
               if (var5 == -1 && var1.size() <= var2) {
                  Iterator var6 = var1.iterator();

                  while(var6.hasNext()) {
                     ItemStack var7 = (ItemStack)var6.next();
                     if (var7.sameItem(var4) && var7.getCount() != var7.getMaxStackSize() && var7.getCount() + var4.getCount() <= var7.getMaxStackSize()) {
                        var7.grow(var4.getCount());
                        var4.setCount(0);
                        break;
                     }
                  }

                  if (!var4.isEmpty()) {
                     if (var1.size() >= var2) {
                        return false;
                     }

                     var1.add(var4);
                  }
               } else if (var5 == -1) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private int getAmountOfFreeSlotsInInventory() {
      int var1 = 0;
      Iterator var2 = this.inventory.items.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         if (var3.isEmpty()) {
            ++var1;
         }
      }

      return var1;
   }
}
