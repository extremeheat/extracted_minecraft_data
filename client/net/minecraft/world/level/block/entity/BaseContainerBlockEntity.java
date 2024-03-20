package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseContainerBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
   private LockCode lockKey = LockCode.NO_LOCK;
   @Nullable
   private Component name;

   protected BaseContainerBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
   }

   @Override
   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      super.load(var1, var2);
      this.lockKey = LockCode.fromTag(var1);
      if (var1.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(var1.getString("CustomName"), var2);
      }
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      this.lockKey.addToTag(var1);
      if (this.name != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name, var2));
      }
   }

   @Override
   public Component getName() {
      return this.name != null ? this.name : this.getDefaultName();
   }

   @Override
   public Component getDisplayName() {
      return this.getName();
   }

   @Nullable
   @Override
   public Component getCustomName() {
      return this.name;
   }

   protected abstract Component getDefaultName();

   public boolean canOpen(Player var1) {
      return canUnlock(var1, this.lockKey, this.getDisplayName());
   }

   public static boolean canUnlock(Player var0, LockCode var1, Component var2) {
      if (!var0.isSpectator() && !var1.unlocksWith(var0.getMainHandItem())) {
         var0.displayClientMessage(Component.translatable("container.isLocked", var2), true);
         var0.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> var1);

   @Override
   public boolean isEmpty() {
      for(ItemStack var2 : this.getItems()) {
         if (!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public ItemStack getItem(int var1) {
      return this.getItems().get(var1);
   }

   @Override
   public ItemStack removeItem(int var1, int var2) {
      ItemStack var3 = ContainerHelper.removeItem(this.getItems(), var1, var2);
      if (!var3.isEmpty()) {
         this.setChanged();
      }

      return var3;
   }

   @Override
   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.getItems(), var1);
   }

   @Override
   public void setItem(int var1, ItemStack var2) {
      this.getItems().set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
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

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      return this.canOpen(var3) ? this.createMenu(var1, var2) : null;
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);

   @Override
   public void applyComponents(DataComponentMap var1) {
      this.name = var1.get(DataComponents.CUSTOM_NAME);
      this.lockKey = var1.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
      var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getItems());
   }

   @Override
   public void collectComponents(DataComponentMap.Builder var1) {
      var1.set(DataComponents.CUSTOM_NAME, this.name);
      if (!this.lockKey.equals(LockCode.NO_LOCK)) {
         var1.set(DataComponents.LOCK, this.lockKey);
      }

      var1.set(DataComponents.CONTAINER, ItemContainerContents.copyOf(this.getItems()));
   }

   @Override
   public void removeComponentsFromTag(CompoundTag var1) {
      var1.remove("CustomName");
      var1.remove("Lock");
      var1.remove("Items");
   }
}