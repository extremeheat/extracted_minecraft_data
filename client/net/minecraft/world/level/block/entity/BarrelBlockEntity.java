package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BarrelBlockEntity extends RandomizableContainerBlockEntity {
   private NonNullList<ItemStack> items;
   private ContainerOpenersCounter openersCounter;

   public BarrelBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BARREL, var1, var2);
      this.items = NonNullList.withSize(27, ItemStack.EMPTY);
      this.openersCounter = new ContainerOpenersCounter() {
         protected void onOpen(Level var1, BlockPos var2, BlockState var3) {
            BarrelBlockEntity.this.playSound(var3, SoundEvents.BARREL_OPEN);
            BarrelBlockEntity.this.updateBlockState(var3, true);
         }

         protected void onClose(Level var1, BlockPos var2, BlockState var3) {
            BarrelBlockEntity.this.playSound(var3, SoundEvents.BARREL_CLOSE);
            BarrelBlockEntity.this.updateBlockState(var3, false);
         }

         protected void openerCountChanged(Level var1, BlockPos var2, BlockState var3, int var4, int var5) {
         }

         protected boolean isOwnContainer(Player var1) {
            if (var1.containerMenu instanceof ChestMenu) {
               Container var2 = ((ChestMenu)var1.containerMenu).getContainer();
               return var2 == BarrelBlockEntity.this;
            } else {
               return false;
            }
         }
      };
   }

   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items);
      }

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

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.barrel");
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return ChestMenu.threeRows(var1, var2, this);
   }

   public void startOpen(Player var1) {
      if (!this.remove && !var1.isSpectator()) {
         this.openersCounter.incrementOpeners(var1, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   public void stopOpen(Player var1) {
      if (!this.remove && !var1.isSpectator()) {
         this.openersCounter.decrementOpeners(var1, this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   public void recheckOpen() {
      if (!this.remove) {
         this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   void updateBlockState(BlockState var1, boolean var2) {
      this.level.setBlock(this.getBlockPos(), (BlockState)var1.setValue(BarrelBlock.OPEN, var2), 3);
   }

   void playSound(BlockState var1, SoundEvent var2) {
      Vec3i var3 = ((Direction)var1.getValue(BarrelBlock.FACING)).getNormal();
      double var4 = (double)this.worldPosition.getX() + 0.5D + (double)var3.getX() / 2.0D;
      double var6 = (double)this.worldPosition.getY() + 0.5D + (double)var3.getY() / 2.0D;
      double var8 = (double)this.worldPosition.getZ() + 0.5D + (double)var3.getZ() / 2.0D;
      this.level.playSound((Player)null, var4, var6, var8, var2, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
   }
}
