package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionSpecialEffects;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionControlMenu extends AbstractContainerMenu {
   public static final int SKY_SLOT = 0;
   private static final int INV_SLOT_START = 1;
   private static final int INV_SLOT_END = 28;
   private static final int USE_ROW_SLOT_START = 28;
   private static final int USE_ROW_SLOT_END = 37;
   private final Container funkySlots;
   private final ContainerLevelAccess access;

   public DimensionControlMenu(int var1, Inventory var2, List<Integer> var3) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public DimensionControlMenu(int var1, Inventory var2, ContainerLevelAccess var3) {
      super(MenuType.DIMENSION_CONTROL, var1);
      this.funkySlots = new SimpleContainer(1) {
         public void setChanged() {
            super.setChanged();
            DimensionControlMenu.this.slotsChanged(this);
         }
      };
      this.access = var3;
      this.addSlot(new Slot(this.funkySlots, 0, 129, 34) {
         public boolean mayPlace(ItemStack var1) {
            return var1.has(DataComponents.SKY);
         }
      });
      this.addStandardInventorySlots(var2, 8, 84);
      var3.execute((var1x, var2x) -> var1x.dimensionType().dimensionSpecialEffects().sky().ifPresent((var1) -> {
            ItemStack var2 = new ItemStack(Items.SKY_BOX);
            var2.set(DataComponents.SKY, var1);
            this.funkySlots.setItem(0, var2);
         }));
   }

   public void slotsChanged(Container var1) {
      super.slotsChanged(var1);
      if (var1 == this.funkySlots) {
         this.updateSky();
      }

   }

   private void updateSky() {
      ItemStack var1 = this.funkySlots.getItem(0);
      DimensionSpecialEffects.Sky var2 = (DimensionSpecialEffects.Sky)var1.get(DataComponents.SKY);
      this.access.execute((var1x, var2x) -> {
         Holder var3 = var1x.dimensionTypeRegistration();
         DimensionType var4 = ((DimensionType)var3.value()).withSpecialEffects((UnaryOperator)((var1) -> var1.withSkyType(Optional.ofNullable(var2))));
         var1x.setDimensionType(Holder.direct(var4));
      });
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.DIMENSION_CONTROL);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 0) {
            if (!this.moveItemStackTo(var5, 1, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.moveItemStackTo(var5, 0, 1, false)) {
               return ItemStack.EMPTY;
            }

            if (var2 >= 1 && var2 < 28) {
               if (!this.moveItemStackTo(var5, 28, 37, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 28 && var2 < 37 && !this.moveItemStackTo(var5, 1, 28, false)) {
               return ItemStack.EMPTY;
            }
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
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
}
