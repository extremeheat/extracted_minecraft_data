package net.minecraft.world.level.block.entity;

import java.util.Iterator;
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
   private LockCode lockKey;
   @Nullable
   private Component name;

   protected BaseContainerBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
      this.lockKey = LockCode.NO_LOCK;
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.lockKey = LockCode.fromTag(var1);
      if (var1.contains("CustomName", 8)) {
         this.name = parseCustomNameSafe(var1.getString("CustomName"), var2);
      }

   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      this.lockKey.addToTag(var1);
      if (this.name != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name, var2));
      }

   }

   public Component getName() {
      return this.name != null ? this.name : this.getDefaultName();
   }

   public Component getDisplayName() {
      return this.getName();
   }

   @Nullable
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

   public boolean isEmpty() {
      Iterator var1 = this.getItems().iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.isEmpty());

      return false;
   }

   public ItemStack getItem(int var1) {
      return (ItemStack)this.getItems().get(var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      ItemStack var3 = ContainerHelper.removeItem(this.getItems(), var1, var2);
      if (!var3.isEmpty()) {
         this.setChanged();
      }

      return var3;
   }

   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.getItems(), var1);
   }

   public void setItem(int var1, ItemStack var2) {
      this.getItems().set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
      this.setChanged();
   }

   public boolean stillValid(Player var1) {
      return Container.stillValidBlockEntity(this, var1);
   }

   public void clearContent() {
      this.getItems().clear();
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      return this.canOpen(var3) ? this.createMenu(var1, var2) : null;
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);

   protected void applyImplicitComponents(BlockEntity.DataComponentInput var1) {
      super.applyImplicitComponents(var1);
      this.name = (Component)var1.get(DataComponents.CUSTOM_NAME);
      this.lockKey = (LockCode)var1.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
      ((ItemContainerContents)var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyInto(this.getItems());
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.CUSTOM_NAME, this.name);
      if (!this.lockKey.equals(LockCode.NO_LOCK)) {
         var1.set(DataComponents.LOCK, this.lockKey);
      }

      var1.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
   }

   public void removeComponentsFromTag(CompoundTag var1) {
      var1.remove("CustomName");
      var1.remove("Lock");
      var1.remove("Items");
   }
}
