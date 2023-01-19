package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MagmaBlock extends Block {
   private static final int BUBBLE_COLUMN_CHECK_DELAY = 20;

   public MagmaBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      if (!var4.isSteppingCarefully() && var4 instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)var4)) {
         var4.hurt(DamageSource.HOT_FLOOR, 1.0F);
      }

      super.stepOn(var1, var2, var3, var4);
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BubbleColumnBlock.updateColumn(var2, var3.above(), var1);
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.UP && var3.is(Blocks.WATER)) {
         var4.scheduleTick(var5, this, 20);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      BlockPos var5 = var3.above();
      if (var2.getFluidState(var3).is(FluidTags.WATER)) {
         var2.playSound(null, var3, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (var2.random.nextFloat() - var2.random.nextFloat()) * 0.8F);
         var2.sendParticles(
            ParticleTypes.LARGE_SMOKE, (double)var5.getX() + 0.5, (double)var5.getY() + 0.25, (double)var5.getZ() + 0.5, 8, 0.5, 0.25, 0.5, 0.0
         );
      }
   }

   @Override
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.scheduleTick(var3, this, 20);
   }
}
