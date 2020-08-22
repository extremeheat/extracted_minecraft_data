package net.minecraft.world.level.block.entity;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BarrelBlockEntity extends RandomizableContainerBlockEntity {
   private NonNullList items;
   private int openCount;

   private BarrelBlockEntity(BlockEntityType var1) {
      super(var1);
      this.items = NonNullList.withSize(27, ItemStack.EMPTY);
   }

   public BarrelBlockEntity() {
      this(BlockEntityType.BARREL);
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items);
      }

      return var1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1)) {
         ContainerHelper.loadAllItems(var1, this.items);
      }

   }

   public int getContainerSize() {
      return 27;
   }

   protected NonNullList getItems() {
      return this.items;
   }

   protected void setItems(NonNullList var1) {
      this.items = var1;
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.barrel", new Object[0]);
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return ChestMenu.threeRows(var1, var2, this);
   }

   public void startOpen(Player var1) {
      if (!var1.isSpectator()) {
         if (this.openCount < 0) {
            this.openCount = 0;
         }

         ++this.openCount;
         BlockState var2 = this.getBlockState();
         boolean var3 = (Boolean)var2.getValue(BarrelBlock.OPEN);
         if (!var3) {
            this.playSound(var2, SoundEvents.BARREL_OPEN);
            this.updateBlockState(var2, true);
         }

         this.scheduleRecheck();
      }

   }

   private void scheduleRecheck() {
      this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 5);
   }

   public void recheckOpen() {
      int var1 = this.worldPosition.getX();
      int var2 = this.worldPosition.getY();
      int var3 = this.worldPosition.getZ();
      this.openCount = ChestBlockEntity.getOpenCount(this.level, this, var1, var2, var3);
      if (this.openCount > 0) {
         this.scheduleRecheck();
      } else {
         BlockState var4 = this.getBlockState();
         if (var4.getBlock() != Blocks.BARREL) {
            this.setRemoved();
            return;
         }

         boolean var5 = (Boolean)var4.getValue(BarrelBlock.OPEN);
         if (var5) {
            this.playSound(var4, SoundEvents.BARREL_CLOSE);
            this.updateBlockState(var4, false);
         }
      }

   }

   public void stopOpen(Player var1) {
      if (!var1.isSpectator()) {
         --this.openCount;
      }

   }

   private void updateBlockState(BlockState var1, boolean var2) {
      this.level.setBlock(this.getBlockPos(), (BlockState)var1.setValue(BarrelBlock.OPEN, var2), 3);
   }

   private void playSound(BlockState var1, SoundEvent var2) {
      Vec3i var3 = ((Direction)var1.getValue(BarrelBlock.FACING)).getNormal();
      double var4 = (double)this.worldPosition.getX() + 0.5D + (double)var3.getX() / 2.0D;
      double var6 = (double)this.worldPosition.getY() + 0.5D + (double)var3.getY() / 2.0D;
      double var8 = (double)this.worldPosition.getZ() + 0.5D + (double)var3.getZ() / 2.0D;
      this.level.playSound((Player)null, var4, var6, var8, var2, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
   }
}
