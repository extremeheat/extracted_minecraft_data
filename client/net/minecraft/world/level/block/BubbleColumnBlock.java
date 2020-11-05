package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BubbleColumnBlock extends Block implements BucketPickup {
   public static final BooleanProperty DRAG_DOWN;

   public BubbleColumnBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DRAG_DOWN, true));
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      BlockState var5 = var2.getBlockState(var3.above());
      if (var5.isAir()) {
         var4.onAboveBubbleCol((Boolean)var1.getValue(DRAG_DOWN));
         if (!var2.isClientSide) {
            ServerLevel var6 = (ServerLevel)var2;

            for(int var7 = 0; var7 < 2; ++var7) {
               var6.sendParticles(ParticleTypes.SPLASH, (double)var3.getX() + var2.random.nextDouble(), (double)(var3.getY() + 1), (double)var3.getZ() + var2.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
               var6.sendParticles(ParticleTypes.BUBBLE, (double)var3.getX() + var2.random.nextDouble(), (double)(var3.getY() + 1), (double)var3.getZ() + var2.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
            }
         }
      } else {
         var4.onInsideBubbleColumn((Boolean)var1.getValue(DRAG_DOWN));
      }

   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      growColumn(var2, var3.above(), getDrag(var2, var3.below()));
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      growColumn(var2, var3.above(), getDrag(var2, var3));
   }

   public FluidState getFluidState(BlockState var1) {
      return Fluids.WATER.getSource(false);
   }

   public static void growColumn(LevelAccessor var0, BlockPos var1, boolean var2) {
      if (canExistIn(var0, var1)) {
         var0.setBlock(var1, (BlockState)Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, var2), 2);
      }

   }

   public static boolean canExistIn(LevelAccessor var0, BlockPos var1) {
      FluidState var2 = var0.getFluidState(var1);
      return var0.getBlockState(var1).is(Blocks.WATER) && var2.getAmount() >= 8 && var2.isSource();
   }

   private static boolean getDrag(BlockGetter var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (var2.is(Blocks.BUBBLE_COLUMN)) {
         return (Boolean)var2.getValue(DRAG_DOWN);
      } else {
         return !var2.is(Blocks.SOUL_SAND);
      }
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      double var5 = (double)var3.getX();
      double var7 = (double)var3.getY();
      double var9 = (double)var3.getZ();
      if ((Boolean)var1.getValue(DRAG_DOWN)) {
         var2.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, var5 + 0.5D, var7 + 0.8D, var9, 0.0D, 0.0D, 0.0D);
         if (var4.nextInt(200) == 0) {
            var2.playLocalSound(var5, var7, var9, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }
      } else {
         var2.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, var5 + 0.5D, var7, var9 + 0.5D, 0.0D, 0.04D, 0.0D);
         var2.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, var5 + (double)var4.nextFloat(), var7 + (double)var4.nextFloat(), var9 + (double)var4.nextFloat(), 0.0D, 0.04D, 0.0D);
         if (var4.nextInt(200) == 0) {
            var2.playLocalSound(var5, var7, var9, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.2F + var4.nextFloat() * 0.2F, 0.9F + var4.nextFloat() * 0.15F, false);
         }
      }

   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         return Blocks.WATER.defaultBlockState();
      } else {
         if (var2 == Direction.DOWN) {
            var4.setBlock(var5, (BlockState)Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, getDrag(var4, var6)), 2);
         } else if (var2 == Direction.UP && !var3.is(Blocks.BUBBLE_COLUMN) && canExistIn(var4, var6)) {
            var4.getBlockTicks().scheduleTick(var5, this, 5);
         }

         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      return var4.is(Blocks.BUBBLE_COLUMN) || var4.is(Blocks.MAGMA_BLOCK) || var4.is(Blocks.SOUL_SAND);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DRAG_DOWN);
   }

   public Fluid takeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3) {
      var1.setBlock(var2, Blocks.AIR.defaultBlockState(), 11);
      return Fluids.WATER;
   }

   static {
      DRAG_DOWN = BlockStateProperties.DRAG;
   }
}
