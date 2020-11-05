package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseContainerBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
   private LockCode lockKey;
   private Component name;

   protected BaseContainerBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
      this.lockKey = LockCode.NO_LOCK;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.lockKey = LockCode.fromTag(var1);
      if (var1.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(var1.getString("CustomName"));
      }

   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      this.lockKey.addToTag(var1);
      if (this.name != null) {
         var1.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      return var1;
   }

   public void setCustomName(Component var1) {
      this.name = var1;
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
         var0.displayClientMessage(new TranslatableComponent("container.isLocked", new Object[]{var2}), true);
         var0.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
         return false;
      } else {
         return true;
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      return this.canOpen(var3) ? this.createMenu(var1, var2) : null;
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);
}
