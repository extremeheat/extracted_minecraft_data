package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LeavesBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<LeavesBlock> CODEC = simpleCodec(LeavesBlock::new);
   public static final int DECAY_DISTANCE = 7;
   public static final IntegerProperty DISTANCE;
   public static final BooleanProperty PERSISTENT;
   public static final BooleanProperty WATERLOGGED;
   private static final int TICK_DELAY = 1;

   public MapCodec<? extends LeavesBlock> codec() {
      return CODEC;
   }

   public LeavesBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(DISTANCE, 7)).setValue(PERSISTENT, false)).setValue(WATERLOGGED, false));
   }

   protected VoxelShape getBlockSupportShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return (Integer)var1.getValue(DISTANCE) == 7 && !(Boolean)var1.getValue(PERSISTENT);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (this.decaying(var1)) {
         dropResources(var1, var2, var3);
         var2.removeBlock(var3, false);
      }

   }

   protected boolean decaying(BlockState var1) {
      return !(Boolean)var1.getValue(PERSISTENT) && (Integer)var1.getValue(DISTANCE) == 7;
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      var2.setBlock(var3, updateDistance(var1, var2, var3), 3);
   }

   protected int getLightBlock(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 1;
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      int var7 = getDistanceAt(var3) + 1;
      if (var7 != 1 || (Integer)var1.getValue(DISTANCE) != var7) {
         var4.scheduleTick(var5, (Block)this, 1);
      }

      return var1;
   }

   private static BlockState updateDistance(BlockState var0, LevelAccessor var1, BlockPos var2) {
      int var3 = 7;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      Direction[] var5 = Direction.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         var4.setWithOffset(var2, (Direction)var8);
         var3 = Math.min(var3, getDistanceAt(var1.getBlockState(var4)) + 1);
         if (var3 == 1) {
            break;
         }
      }

      return (BlockState)var0.setValue(DISTANCE, var3);
   }

   private static int getDistanceAt(BlockState var0) {
      return getOptionalDistanceAt(var0).orElse(7);
   }

   public static OptionalInt getOptionalDistanceAt(BlockState var0) {
      if (var0.is(BlockTags.LOGS)) {
         return OptionalInt.of(0);
      } else {
         return var0.hasProperty(DISTANCE) ? OptionalInt.of((Integer)var0.getValue(DISTANCE)) : OptionalInt.empty();
      }
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var2.isRainingAt(var3.above())) {
         if (var4.nextInt(15) == 1) {
            BlockPos var5 = var3.below();
            BlockState var6 = var2.getBlockState(var5);
            if (!var6.canOcclude() || !var6.isFaceSturdy(var2, var5, Direction.UP)) {
               ParticleUtils.spawnParticleBelow(var2, var3, var4, ParticleTypes.DRIPPING_WATER);
            }
         }
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(DISTANCE, PERSISTENT, WATERLOGGED);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      BlockState var3 = (BlockState)((BlockState)this.defaultBlockState().setValue(PERSISTENT, true)).setValue(WATERLOGGED, var2.getType() == Fluids.WATER);
      return updateDistance(var3, var1.getLevel(), var1.getClickedPos());
   }

   static {
      DISTANCE = BlockStateProperties.DISTANCE;
      PERSISTENT = BlockStateProperties.PERSISTENT;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
   }
}
