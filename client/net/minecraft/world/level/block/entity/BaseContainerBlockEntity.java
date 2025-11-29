package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class BaseContainerBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
   private LockCode lockKey;
   private @Nullable Component name;

   protected BaseContainerBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
      this.lockKey = LockCode.NO_LOCK;
   }

   protected void loadAdditional(ValueInput var1) {
      super.loadAdditional(var1);
      this.lockKey = LockCode.fromTag(var1);
      this.name = parseCustomNameSafe(var1, "CustomName");
   }

   protected void saveAdditional(ValueOutput var1) {
      super.saveAdditional(var1);
      this.lockKey.addToTag(var1);
      var1.storeNullable("CustomName", ComponentSerialization.CODEC, this.name);
   }

   public Component getName() {
      return this.name != null ? this.name : this.getDefaultName();
   }

   public Component getDisplayName() {
      return this.getName();
   }

   public @Nullable Component getCustomName() {
      return this.name;
   }

   protected abstract Component getDefaultName();

   public boolean canOpen(Player var1) {
      return this.lockKey.canUnlock(var1);
   }

   public static void sendChestLockedNotifications(Vec3 var0, Player var1, Component var2) {
      Level var3 = var1.level();
      var1.displayClientMessage(Component.translatable("container.isLocked", var2), true);
      if (!var3.isClientSide()) {
         var3.playSound((Entity)null, var0.x(), var0.y(), var0.z(), SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   public boolean isLocked() {
      return !this.lockKey.equals(LockCode.NO_LOCK);
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> var1);

   public boolean isEmpty() {
      for(ItemStack var2 : this.getItems()) {
         if (!var2.isEmpty()) {
            return false;
         }
      }

      return true;
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

   public @Nullable AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      if (this.canOpen(var3)) {
         return this.createMenu(var1, var2);
      } else {
         sendChestLockedNotifications(this.getBlockPos().getCenter(), var3, this.getDisplayName());
         return null;
      }
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);

   protected void applyImplicitComponents(DataComponentGetter var1) {
      super.applyImplicitComponents(var1);
      this.name = (Component)var1.get(DataComponents.CUSTOM_NAME);
      this.lockKey = (LockCode)var1.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
      ((ItemContainerContents)var1.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyInto(this.getItems());
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.CUSTOM_NAME, this.name);
      if (this.isLocked()) {
         var1.set(DataComponents.LOCK, this.lockKey);
      }

      var1.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
   }

   public void removeComponentsFromTag(ValueOutput var1) {
      var1.discard("CustomName");
      var1.discard("lock");
      var1.discard("Items");
   }
}
