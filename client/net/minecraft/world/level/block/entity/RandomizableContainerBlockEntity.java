package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RandomizableContainerBlockEntity extends BaseContainerBlockEntity implements RandomizableContainer {
   @Nullable
   protected ResourceLocation lootTable;
   protected long lootTableSeed;

   protected RandomizableContainerBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
   }

   @Nullable
   @Override
   public ResourceLocation getLootTable() {
      return this.lootTable;
   }

   @Override
   public void setLootTable(@Nullable ResourceLocation var1) {
      this.lootTable = var1;
   }

   @Override
   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   @Override
   public void setLootTableSeed(long var1) {
      this.lootTableSeed = var1;
   }

   @Override
   public boolean isEmpty() {
      this.unpackLootTable(null);
      return this.getItems().stream().allMatch(ItemStack::isEmpty);
   }

   @Override
   public ItemStack getItem(int var1) {
      this.unpackLootTable(null);
      return this.getItems().get(var1);
   }

   @Override
   public ItemStack removeItem(int var1, int var2) {
      this.unpackLootTable(null);
      ItemStack var3 = ContainerHelper.removeItem(this.getItems(), var1, var2);
      if (!var3.isEmpty()) {
         this.setChanged();
      }

      return var3;
   }

   @Override
   public ItemStack removeItemNoUpdate(int var1) {
      this.unpackLootTable(null);
      return ContainerHelper.takeItem(this.getItems(), var1);
   }

   @Override
   public void setItem(int var1, ItemStack var2) {
      this.unpackLootTable(null);
      this.getItems().set(var1, var2);
      if (var2.getCount() > this.getMaxStackSize()) {
         var2.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   @Override
   public boolean stillValid(Player var1) {
      return Container.stillValidBlockEntity(this, var1);
   }

   @Override
   public void clearContent() {
      this.getItems().clear();
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> var1);

   @Override
   public boolean canOpen(Player var1) {
      return super.canOpen(var1) && (this.lootTable == null || !var1.isSpectator());
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      if (this.canOpen(var3)) {
         this.unpackLootTable(var2.player);
         return this.createMenu(var1, var2);
      } else {
         return null;
      }
   }
}
