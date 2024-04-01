package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FarmBlock extends Block {
   public static final MapCodec<FarmBlock> CODEC = simpleCodec(FarmBlock::new);
   public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
   public static final int MAX_MOISTURE = 7;

   @Override
   public MapCodec<FarmBlock> codec() {
      return CODEC;
   }

   protected FarmBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, Integer.valueOf(0)));
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.UP && !var1.canSurvive(var4, var5)) {
         var4.scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.above());
      return !var4.isSolid() || var4.getBlock() instanceof FenceGateBlock || var4.getBlock() instanceof MovingPistonBlock;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return !this.defaultBlockState().canSurvive(var1.getLevel(), var1.getClickedPos())
         ? (var1.getLevel().isPotato() ? Blocks.TERREDEPOMME : Blocks.DIRT).defaultBlockState()
         : super.getStateForPlacement(var1);
   }

   @Override
   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         turnToDirt(null, var1, var2, var3);
      }
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = var1.getValue(MOISTURE);
      if (!isNearWater(var2, var3) && !var2.isRainingAt(var3.above())) {
         if (var5 > 0) {
            var2.setBlock(var3, var1.setValue(MOISTURE, Integer.valueOf(var5 - 1)), 2);
         } else if (!shouldMaintainFarmland(var2, var3)) {
            turnToDirt(null, var1, var2, var3);
         }
      } else if (var5 < 7) {
         var2.setBlock(var3, var1.setValue(MOISTURE, Integer.valueOf(7)), 2);
      }
   }

   @Override
   public void fallOn(Level var1, BlockState var2, BlockPos var3, Entity var4, float var5) {
      if (!var1.isClientSide
         && var1.random.nextFloat() < var5 - 0.5F
         && var4 instanceof LivingEntity
         && (var4 instanceof Player || var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
         && var4.getBbWidth() * var4.getBbWidth() * var4.getBbHeight() > 0.512F) {
         turnToDirt(var4, var2, var1, var3);
      }

      super.fallOn(var1, var2, var3, var4, var5);
   }

   public static void turnToDirt(@Nullable Entity var0, BlockState var1, Level var2, BlockPos var3) {
      BlockState var4 = pushEntitiesUp(var1, (var2.isPotato() ? Blocks.TERREDEPOMME : Blocks.DIRT).defaultBlockState(), var2, var3);
      var2.setBlockAndUpdate(var3, var4);
      var2.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var0, var4));
   }

   private static boolean shouldMaintainFarmland(BlockGetter var0, BlockPos var1) {
      return var0.isPotato() ? true : var0.getBlockState(var1.above()).is(BlockTags.MAINTAINS_FARMLAND);
   }

   private static boolean isNearWater(LevelReader var0, BlockPos var1) {
      for(BlockPos var3 : BlockPos.betweenClosed(var1.offset(-4, 0, -4), var1.offset(4, 1, 4))) {
         if (var0.getFluidState(var3).is(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(MOISTURE);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
