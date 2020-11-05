package net.minecraft.world.level.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HoneyBlock extends HalfTransparentBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

   public HoneyBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   private static boolean doesEntityDoHoneyBlockSlideEffects(Entity var0) {
      return var0 instanceof LivingEntity || var0 instanceof AbstractMinecart || var0 instanceof PrimedTnt || var0 instanceof Boat;
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public void fallOn(Level var1, BlockPos var2, Entity var3, float var4) {
      var3.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
      if (!var1.isClientSide) {
         var1.broadcastEntityEvent(var3, (byte)54);
      }

      if (var3.causeFallDamage(var4, 0.2F)) {
         var3.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
      }

   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (this.isSlidingDown(var3, var4)) {
         this.maybeDoSlideAchievement(var4, var3);
         this.doSlideMovement(var4);
         this.maybeDoSlideEffects(var2, var4);
      }

      super.entityInside(var1, var2, var3, var4);
   }

   private boolean isSlidingDown(BlockPos var1, Entity var2) {
      if (var2.isOnGround()) {
         return false;
      } else if (var2.getY() > (double)var1.getY() + 0.9375D - 1.0E-7D) {
         return false;
      } else if (var2.getDeltaMovement().y >= -0.08D) {
         return false;
      } else {
         double var3 = Math.abs((double)var1.getX() + 0.5D - var2.getX());
         double var5 = Math.abs((double)var1.getZ() + 0.5D - var2.getZ());
         double var7 = 0.4375D + (double)(var2.getBbWidth() / 2.0F);
         return var3 + 1.0E-7D > var7 || var5 + 1.0E-7D > var7;
      }
   }

   private void maybeDoSlideAchievement(Entity var1, BlockPos var2) {
      if (var1 instanceof ServerPlayer && var1.level.getGameTime() % 20L == 0L) {
         CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger((ServerPlayer)var1, var1.level.getBlockState(var2));
      }

   }

   private void doSlideMovement(Entity var1) {
      Vec3 var2 = var1.getDeltaMovement();
      if (var2.y < -0.13D) {
         double var3 = -0.05D / var2.y;
         var1.setDeltaMovement(new Vec3(var2.x * var3, -0.05D, var2.z * var3));
      } else {
         var1.setDeltaMovement(new Vec3(var2.x, -0.05D, var2.z));
      }

      var1.fallDistance = 0.0F;
   }

   private void maybeDoSlideEffects(Level var1, Entity var2) {
      if (doesEntityDoHoneyBlockSlideEffects(var2)) {
         if (var1.random.nextInt(5) == 0) {
            var2.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
         }

         if (!var1.isClientSide && var1.random.nextInt(5) == 0) {
            var1.broadcastEntityEvent(var2, (byte)53);
         }
      }

   }

   public static void showSlideParticles(Entity var0) {
      showParticles(var0, 5);
   }

   public static void showJumpParticles(Entity var0) {
      showParticles(var0, 10);
   }

   private static void showParticles(Entity var0, int var1) {
      if (var0.level.isClientSide) {
         BlockState var2 = Blocks.HONEY_BLOCK.defaultBlockState();

         for(int var3 = 0; var3 < var1; ++var3) {
            var0.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var2), var0.getX(), var0.getY(), var0.getZ(), 0.0D, 0.0D, 0.0D);
         }

      }
   }
}
