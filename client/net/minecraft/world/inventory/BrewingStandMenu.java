package net.minecraft.world.inventory;

import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;

public class BrewingStandMenu extends AbstractContainerMenu {
   static final ResourceLocation EMPTY_SLOT_FUEL = ResourceLocation.withDefaultNamespace("container/slot/brewing_fuel");
   static final ResourceLocation EMPTY_SLOT_POTION = ResourceLocation.withDefaultNamespace("container/slot/potion");
   private static final int BOTTLE_SLOT_START = 0;
   private static final int BOTTLE_SLOT_END = 2;
   private static final int INGREDIENT_SLOT = 3;
   private static final int FUEL_SLOT = 4;
   private static final int SLOT_COUNT = 5;
   private static final int DATA_COUNT = 2;
   private static final int INV_SLOT_START = 5;
   private static final int INV_SLOT_END = 32;
   private static final int USE_ROW_SLOT_START = 32;
   private static final int USE_ROW_SLOT_END = 41;
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
      PotionBrewing var5 = var2.player.level().potionBrewing();
      this.addSlot(new PotionSlot(var3, 0, 56, 51));
      this.addSlot(new PotionSlot(var3, 1, 79, 58));
      this.addSlot(new PotionSlot(var3, 2, 102, 51));
      this.ingredientSlot = this.addSlot(new IngredientsSlot(var5, var3, 3, 79, 17));
      this.addSlot(new FuelSlot(var3, 4, 17, 17));
      this.addDataSlots(var4);
      this.addStandardInventorySlots(var2, 8, 84);
   }

   public boolean stillValid(Player var1) {
      return this.brewingStand.stillValid(var1);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
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
            } else if (BrewingStandMenu.PotionSlot.mayPlaceItem(var3)) {
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
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var3);
      }

      return var3;
   }

   public int getFuel() {
      return this.brewingStandData.get(1);
   }

   public int getBrewingTicks() {
      return this.brewingStandData.get(0);
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

      public void onTake(Player var1, ItemStack var2) {
         Optional var3 = ((PotionContents)var2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).potion();
         if (var3.isPresent() && var1 instanceof ServerPlayer var4) {
            CriteriaTriggers.BREWED_POTION.trigger(var4, (Holder)var3.get());
         }

         super.onTake(var1, var2);
      }

      public static boolean mayPlaceItem(ItemStack var0) {
         return var0.is(Items.POTION) || var0.is(Items.SPLASH_POTION) || var0.is(Items.LINGERING_POTION) || var0.is(Items.GLASS_BOTTLE);
      }

      public ResourceLocation getNoItemIcon() {
         return BrewingStandMenu.EMPTY_SLOT_POTION;
      }
   }

   static class IngredientsSlot extends Slot {
      private final PotionBrewing potionBrewing;

      public IngredientsSlot(PotionBrewing var1, Container var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
         this.potionBrewing = var1;
      }

      public boolean mayPlace(ItemStack var1) {
         return this.potionBrewing.isIngredient(var1);
      }
   }

   static class FuelSlot extends Slot {
      public FuelSlot(Container var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean mayPlace(ItemStack var1) {
         return mayPlaceItem(var1);
      }

      public static boolean mayPlaceItem(ItemStack var0) {
         return var0.is(ItemTags.BREWING_FUEL);
      }

      public ResourceLocation getNoItemIcon() {
         return BrewingStandMenu.EMPTY_SLOT_FUEL;
      }
   }
}
