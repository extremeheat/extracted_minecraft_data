package net.minecraft.world.inventory;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;

public class BrewingStandMenu extends AbstractContainerMenu {
   private final Container brewingStand;
   private final ContainerData brewingStandData;
   private final Slot ingredientSlot;

   public BrewingStandMenu(int var1, Inventory var2) {
      this(var1, var2, new SimpleContainer(5), new SimpleContainerData(2));
   }

   public BrewingStandMenu(int var1, Inventory var2, Container var3, ContainerData var4) {
      super(MenuType.BREWING_STAND, var1);
      checkContainerSize(var3, 5);
      checkContainerDataCount(var4, 2);
      this.brewingStand = var3;
      this.brewingStandData = var4;
      this.addSlot(new BrewingStandMenu.PotionSlot(var3, 0, 56, 51));
      this.addSlot(new BrewingStandMenu.PotionSlot(var3, 1, 79, 58));
      this.addSlot(new BrewingStandMenu.PotionSlot(var3, 2, 102, 51));
      this.ingredientSlot = this.addSlot(new BrewingStandMenu.IngredientsSlot(var3, 3, 79, 17));
      this.addSlot(new BrewingStandMenu.FuelSlot(var3, 4, 17, 17));
      this.addDataSlots(var4);

      int var5;
      for(var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new Slot(var2, var6 + var5 * 9 + 9, 8 + var6 * 18, 84 + var5 * 18));
         }
      }

      for(var5 = 0; var5 < 9; ++var5) {
         this.addSlot(new Slot(var2, var5, 8 + var5 * 18, 142));
      }

   }

   public boolean stillValid(Player var1) {
      return this.brewingStand.stillValid(var1);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if ((var2 < 0 || var2 > 2) && var2 != 3 && var2 != 4) {
            if (BrewingStandMenu.FuelSlot.mayPlaceItem(var3)) {
               if (this.moveItemStackTo(var5, 4, 5, false) || this.ingredientSlot.mayPlace(var5) && !this.moveItemStackTo(var5, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.ingredientSlot.mayPlace(var5)) {
               if (!this.moveItemStackTo(var5, 3, 4, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (BrewingStandMenu.PotionSlot.mayPlaceItem(var3) && var3.getCount() == 1) {
               if (!this.moveItemStackTo(var5, 0, 3, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 5 && var2 < 32) {
               if (!this.moveItemStackTo(var5, 32, 41, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 32 && var2 < 41) {
               if (!this.moveItemStackTo(var5, 5, 32, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(var5, 5, 41, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.moveItemStackTo(var5, 5, 41, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         }

         if (var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }

   public int getFuel() {
      return this.brewingStandData.get(1);
   }

   public int getBrewingTicks() {
      return this.brewingStandData.get(0);
   }

   static class FuelSlot extends Slot {
      public FuelSlot(Container var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean mayPlace(ItemStack var1) {
         return mayPlaceItem(var1);
      }

      public static boolean mayPlaceItem(ItemStack var0) {
         return var0.getItem() == Items.BLAZE_POWDER;
      }

      public int getMaxStackSize() {
         return 64;
      }
   }

   static class IngredientsSlot extends Slot {
      public IngredientsSlot(Container var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean mayPlace(ItemStack var1) {
         return PotionBrewing.isIngredient(var1);
      }

      public int getMaxStackSize() {
         return 64;
      }
   }

   static class PotionSlot extends Slot {
      public PotionSlot(Container var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean mayPlace(ItemStack var1) {
         return mayPlaceItem(var1);
      }

      public int getMaxStackSize() {
         return 1;
      }

      public ItemStack onTake(Player var1, ItemStack var2) {
         Potion var3 = PotionUtils.getPotion(var2);
         if (var1 instanceof ServerPlayer) {
            CriteriaTriggers.BREWED_POTION.trigger((ServerPlayer)var1, var3);
         }

         super.onTake(var1, var2);
         return var2;
      }

      public static boolean mayPlaceItem(ItemStack var0) {
         Item var1 = var0.getItem();
         return var1 == Items.POTION || var1 == Items.SPLASH_POTION || var1 == Items.LINGERING_POTION || var1 == Items.GLASS_BOTTLE;
      }
   }
}
