package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FrogspawnBlock extends Block {
   public static final MapCodec<FrogspawnBlock> CODEC = simpleCodec(FrogspawnBlock::new);
   private static final int MIN_TADPOLES_SPAWN = 2;
   private static final int MAX_TADPOLES_SPAWN = 5;
   private static final int DEFAULT_MIN_HATCH_TICK_DELAY = 3600;
   private static final int DEFAULT_MAX_HATCH_TICK_DELAY = 12000;
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.5, 16.0);
   private static int minHatchTickDelay = 3600;
   private static int maxHatchTickDelay = 12000;

   public MapCodec<FrogspawnBlock> codec() {
      return CODEC;
   }

   public FrogspawnBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return mayPlaceOn(var2, var3.below());
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.scheduleTick(var3, this, getFrogspawnHatchDelay(var2.getRandom()));
   }

   private static int getFrogspawnHatchDelay(RandomSource var0) {
      return var0.nextInt(minHatchTickDelay, maxHatchTickDelay);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return !this.canSurvive(var1, var2, var4) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!this.canSurvive(var1, var2, var3)) {
         this.destroyBlock(var2, var3);
      } else {
         this.hatchFrogspawn(var2, var3, var4);
      }
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4.getType().equals(EntityType.FALLING_BLOCK)) {
         this.destroyBlock(var2, var3);
      }

   }

   private static boolean mayPlaceOn(BlockGetter var0, BlockPos var1) {
      FluidState var2 = var0.getFluidState(var1);
      FluidState var3 = var0.getFluidState(var1.above());
      return var2.getType() == Fluids.WATER && var3.getType() == Fluids.EMPTY;
   }

   private void hatchFrogspawn(ServerLevel var1, BlockPos var2, RandomSource var3) {
      this.destroyBlock(var1, var2);
      var1.playSound((Player)null, var2, SoundEvents.FROGSPAWN_HATCH, SoundSource.BLOCKS, 1.0F, 1.0F);
      this.spawnTadpoles(var1, var2, var3);
   }

   private void destroyBlock(Level var1, BlockPos var2) {
      var1.destroyBlock(var2, false);
   }

   private void spawnTadpoles(ServerLevel var1, BlockPos var2, RandomSource var3) {
      int var4 = var3.nextInt(2, 6);

      for(int var5 = 1; var5 <= var4; ++var5) {
         Tadpole var6 = EntityType.TADPOLE.create(var1, EntitySpawnReason.BREEDING);
         if (var6 != null) {
            double var7 = (double)var2.getX() + this.getRandomTadpolePositionOffset(var3);
            double var9 = (double)var2.getZ() + this.getRandomTadpolePositionOffset(var3);
            int var11 = var3.nextInt(1, 361);
            var6.moveTo(var7, (double)var2.getY() - 0.5, var9, (float)var11, 0.0F);
            var6.setPersistenceRequired();
            var1.addFreshEntity(var6);
         }
      }

   }

   private double getRandomTadpolePositionOffset(RandomSource var1) {
      double var2 = 0.20000000298023224;
      return Mth.clamp(var1.nextDouble(), 0.20000000298023224, 0.7999999970197678);
   }

   @VisibleForTesting
   public static void setHatchDelay(int var0, int var1) {
      minHatchTickDelay = var0;
      maxHatchTickDelay = var1;
   }

   @VisibleForTesting
   public static void setDefaultHatchDelay() {
      minHatchTickDelay = 3600;
      maxHatchTickDelay = 12000;
   }
}
