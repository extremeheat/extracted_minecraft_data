package net.minecraft.world.level.block;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BubbleColumnBlock extends Block implements BucketPickup {
   public static final BooleanProperty DRAG_DOWN = BlockStateProperties.DRAG;
   private static final int CHECK_PERIOD = 5;

   public BubbleColumnBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(DRAG_DOWN, Boolean.valueOf(true)));
   }

   @Override
   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      BlockState var5 = var2.getBlockState(var3.above());
      if (var5.isAir()) {
         var4.onAboveBubbleCol(var1.getValue(DRAG_DOWN));
         if (!var2.isClientSide) {
            ServerLevel var6 = (ServerLevel)var2;

            for(int var7 = 0; var7 < 2; ++var7) {
               var6.sendParticles(
                  ParticleTypes.SPLASH,
                  (double)var3.getX() + var2.random.nextDouble(),
                  (double)(var3.getY() + 1),
                  (double)var3.getZ() + var2.random.nextDouble(),
                  1,
                  0.0,
                  0.0,
                  0.0,
                  1.0
               );
               var6.sendParticles(
                  ParticleTypes.BUBBLE,
                  (double)var3.getX() + var2.random.nextDouble(),
                  (double)(var3.getY() + 1),
                  (double)var3.getZ() + var2.random.nextDouble(),
                  1,
                  0.0,
                  0.01,
                  0.0,
                  0.2
               );
            }
         }
      } else {
         var4.onInsideBubbleColumn(var1.getValue(DRAG_DOWN));
      }
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      updateColumn(var2, var3, var1, var2.getBlockState(var3.below()));
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return Fluids.WATER.getSource(false);
   }

   public static void updateColumn(LevelAccessor var0, BlockPos var1, BlockState var2) {
      updateColumn(var0, var1, var0.getBlockState(var1), var2);
   }

   public static void updateColumn(LevelAccessor var0, BlockPos var1, BlockState var2, BlockState var3) {
      if (canExistIn(var2)) {
         BlockState var4 = getColumnState(var3);
         var0.setBlock(var1, var4, 2);
         BlockPos.MutableBlockPos var5 = var1.mutable().move(Direction.UP);

         while(canExistIn(var0.getBlockState(var5))) {
            if (!var0.setBlock(var5, var4, 2)) {
               return;
            }

            var5.move(Direction.UP);
         }
      }
   }

   private static boolean canExistIn(BlockState var0) {
      return var0.is(Blocks.BUBBLE_COLUMN) || var0.is(Blocks.WATER) && var0.getFluidState().getAmount() >= 8 && var0.getFluidState().isSource();
   }

   private static BlockState getColumnState(BlockState var0) {
      if (var0.is(Blocks.BUBBLE_COLUMN)) {
         return var0;
      } else if (var0.is(Blocks.SOUL_SAND)) {
         return Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(false));
      } else {
         return var0.is(Blocks.MAGMA_BLOCK)
            ? Blocks.BUBBLE_COLUMN.defaultBlockState().setValue(DRAG_DOWN, Boolean.valueOf(true))
            : Blocks.WATER.defaultBlockState();
      }
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      double var5 = (double)var3.getX();
      double var7 = (double)var3.getY();
      double var9 = (double)var3.getZ();
      if (var1.getValue(DRAG_DOWN)) {
         var2.addAlwaysVisibleParticle(ParticleTypes.CURRENT_DOWN, var5 + 0.5, var7 + 0.8, var9, 0.0, 0.0, 0.0);
         if (var4.nextInt(200) == 0) {
            var2.playLocalSound(
               var5,
               var7,
               var9,
               SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
               SoundSource.BLOCKS,
               0.2F + var4.nextFloat() * 0.2F,
               0.9F + var4.nextFloat() * 0.15F,
               false
            );
         }
      } else {
         var2.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_COLUMN_UP, var5 + 0.5, var7, var9 + 0.5, 0.0, 0.04, 0.0);
         var2.addAlwaysVisibleParticle(
            ParticleTypes.BUBBLE_COLUMN_UP, var5 + (double)var4.nextFloat(), var7 + (double)var4.nextFloat(), var9 + (double)var4.nextFloat(), 0.0, 0.04, 0.0
         );
         if (var4.nextInt(200) == 0) {
            var2.playLocalSound(
               var5,
               var7,
               var9,
               SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
               SoundSource.BLOCKS,
               0.2F + var4.nextFloat() * 0.2F,
               0.9F + var4.nextFloat() * 0.15F,
               false
            );
         }
      }
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      if (!var1.canSurvive(var4, var5) || var2 == Direction.DOWN || var2 == Direction.UP && !var3.is(Blocks.BUBBLE_COLUMN) && canExistIn(var3)) {
         var4.scheduleTick(var5, this, 5);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      return var4.is(Blocks.BUBBLE_COLUMN) || var4.is(Blocks.MAGMA_BLOCK) || var4.is(Blocks.SOUL_SAND);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DRAG_DOWN);
   }

   @Override
   public ItemStack pickupBlock(@Nullable Player var1, LevelAccessor var2, BlockPos var3, BlockState var4) {
      var2.setBlock(var3, Blocks.AIR.defaultBlockState(), 11);
      return new ItemStack(Items.WATER_BUCKET);
   }

   @Override
   public Optional<SoundEvent> getPickupSound() {
      return Fluids.WATER.getPickupSound();
   }
}
