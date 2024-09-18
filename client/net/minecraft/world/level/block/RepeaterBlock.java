package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RepeaterBlock extends DiodeBlock {
   public static final MapCodec<RepeaterBlock> CODEC = simpleCodec(RepeaterBlock::new);
   public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
   public static final IntegerProperty DELAY = BlockStateProperties.DELAY;

   @Override
   public MapCodec<RepeaterBlock> codec() {
      return CODEC;
   }

   protected RepeaterBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(DELAY, Integer.valueOf(1))
            .setValue(LOCKED, Boolean.valueOf(false))
            .setValue(POWERED, Boolean.valueOf(false))
      );
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var4.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         var2.setBlock(var3, var1.cycle(DELAY), 3);
         return InteractionResult.SUCCESS;
      }
   }

   @Override
   protected int getDelay(BlockState var1) {
      return var1.getValue(DELAY) * 2;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = super.getStateForPlacement(var1);
      return var2.setValue(LOCKED, Boolean.valueOf(this.isLocked(var1.getLevel(), var1.getClickedPos(), var2)));
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      if (var5 == Direction.DOWN && !this.canSurviveOn(var2, var6, var7)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         return !var2.isClientSide() && var5.getAxis() != var1.getValue(FACING).getAxis()
            ? var1.setValue(LOCKED, Boolean.valueOf(this.isLocked(var2, var4, var1)))
            : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }

   @Override
   public boolean isLocked(LevelReader var1, BlockPos var2, BlockState var3) {
      return this.getAlternateSignal(var1, var2, var3) > 0;
   }

   @Override
   protected boolean sideInputDiodesOnly() {
      return true;
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(POWERED)) {
         Direction var5 = var1.getValue(FACING);
         double var6 = (double)var3.getX() + 0.5 + (var4.nextDouble() - 0.5) * 0.2;
         double var8 = (double)var3.getY() + 0.4 + (var4.nextDouble() - 0.5) * 0.2;
         double var10 = (double)var3.getZ() + 0.5 + (var4.nextDouble() - 0.5) * 0.2;
         float var12 = -5.0F;
         if (var4.nextBoolean()) {
            var12 = (float)(var1.getValue(DELAY) * 2 - 1);
         }

         var12 /= 16.0F;
         double var13 = (double)(var12 * (float)var5.getStepX());
         double var15 = (double)(var12 * (float)var5.getStepZ());
         var2.addParticle(DustParticleOptions.REDSTONE, var6 + var13, var8, var10 + var15, 0.0, 0.0, 0.0);
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, DELAY, LOCKED, POWERED);
   }
}
