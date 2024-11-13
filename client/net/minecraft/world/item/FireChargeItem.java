package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class FireChargeItem extends Item implements ProjectileItem {
   public FireChargeItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      boolean var5 = false;
      if (!CampfireBlock.canLight(var4) && !CandleBlock.canLight(var4) && !CandleCakeBlock.canLight(var4)) {
         var3 = var3.relative(var1.getClickedFace());
         if (BaseFireBlock.canBePlacedAt(var2, var3, var1.getHorizontalDirection())) {
            this.playSound(var2, var3);
            var2.setBlockAndUpdate(var3, BaseFireBlock.getState(var2, var3));
            var2.gameEvent(var1.getPlayer(), GameEvent.BLOCK_PLACE, var3);
            var5 = true;
         }
      } else {
         this.playSound(var2, var3);
         var2.setBlockAndUpdate(var3, (BlockState)var4.setValue(BlockStateProperties.LIT, true));
         var2.gameEvent(var1.getPlayer(), GameEvent.BLOCK_CHANGE, var3);
         var5 = true;
      }

      if (var5) {
         var1.getItemInHand().shrink(1);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.FAIL;
      }
   }

   private void playSound(Level var1, BlockPos var2) {
      RandomSource var3 = var1.getRandom();
      var1.playSound((Player)null, (BlockPos)var2, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (var3.nextFloat() - var3.nextFloat()) * 0.2F + 1.0F);
   }

   public Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4) {
      RandomSource var5 = var1.getRandom();
      double var6 = var5.triangle((double)var4.getStepX(), 0.11485000000000001);
      double var8 = var5.triangle((double)var4.getStepY(), 0.11485000000000001);
      double var10 = var5.triangle((double)var4.getStepZ(), 0.11485000000000001);
      Vec3 var12 = new Vec3(var6, var8, var10);
      SmallFireball var13 = new SmallFireball(var1, var2.x(), var2.y(), var2.z(), var12.normalize());
      var13.setItem(var3);
      return var13;
   }

   public void shoot(Projectile var1, double var2, double var4, double var6, float var8, float var9) {
   }

   public ProjectileItem.DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.builder().positionFunction((var0, var1) -> DispenserBlock.getDispensePosition(var0, 1.0, Vec3.ZERO)).uncertainty(6.6666665F).power(1.0F).overrideDispenseEvent(1018).build();
   }
}
