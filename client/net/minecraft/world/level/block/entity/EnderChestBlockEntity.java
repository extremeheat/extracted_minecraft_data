package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EnderChestBlockEntity extends BlockEntity implements LidBlockEntity {
   private final ChestLidController chestLidController = new ChestLidController();
   private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
      protected void onOpen(Level var1, BlockPos var2, BlockState var3) {
         var1.playSound((Player)null, (double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F, var1.random.nextFloat() * 0.1F + 0.9F);
      }

      protected void onClose(Level var1, BlockPos var2, BlockState var3) {
         var1.playSound((Player)null, (double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, var1.random.nextFloat() * 0.1F + 0.9F);
      }

      protected void openerCountChanged(Level var1, BlockPos var2, BlockState var3, int var4, int var5) {
         var1.blockEvent(EnderChestBlockEntity.this.worldPosition, Blocks.ENDER_CHEST, 1, var5);
      }

      protected boolean isOwnContainer(Player var1) {
         return var1.getEnderChestInventory().isActiveChest(EnderChestBlockEntity.this);
      }
   };

   public EnderChestBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.ENDER_CHEST, var1, var2);
   }

   public static void lidAnimateTick(Level var0, BlockPos var1, BlockState var2, EnderChestBlockEntity var3) {
      var3.chestLidController.tickLid();
   }

   public boolean triggerEvent(int var1, int var2) {
      if (var1 == 1) {
         this.chestLidController.shouldBeOpen(var2 > 0);
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
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

   public boolean stillValid(Player var1) {
      if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         return !(var1.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
      }
   }

   public void recheckOpen() {
      if (!this.remove) {
         this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   public float getOpenNess(float var1) {
      return this.chestLidController.getOpenness(var1);
   }
}
