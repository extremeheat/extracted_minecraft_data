package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DispenserBlockEntity extends RandomizableContainerBlockEntity {
   public static final int CONTAINER_SIZE = 9;
   private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

   protected DispenserBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
   }

   public DispenserBlockEntity(BlockPos var1, BlockState var2) {
      this(BlockEntityType.DISPENSER, var1, var2);
   }

   @Override
   public int getContainerSize() {
      return 9;
   }

   public int getRandomSlot(RandomSource var1) {
      this.unpackLootTable(null);
      int var2 = -1;
      int var3 = 1;

      for(int var4 = 0; var4 < this.items.size(); ++var4) {
         if (!this.items.get(var4).isEmpty() && var1.nextInt(var3++) == 0) {
            var2 = var4;
         }
      }

      return var2;
   }

   public int addItem(ItemStack var1) {
      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         if (this.items.get(var2).isEmpty()) {
            this.setItem(var2, var1);
            return var2;
         }
      }

      return -1;
   }

   @Override
   protected Component getDefaultName() {
      return Component.translatable("container.dispenser");
   }

   @Override
   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      super.load(var1, var2);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1)) {
         ContainerHelper.loadAllItems(var1, this.items, var2);
      }
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items, var2);
      }
   }

   @Override
   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   @Override
   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new DispenserMenu(var1, var2, this);
   }
}
