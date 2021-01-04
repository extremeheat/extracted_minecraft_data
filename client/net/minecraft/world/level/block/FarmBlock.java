package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FarmBlock extends Block {
   public static final IntegerProperty MOISTURE;
   protected static final VoxelShape SHAPE;

   protected FarmBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(MOISTURE, 0));
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.UP && !var1.canSurvive(var4, var5)) {
         var4.getBlockTicks().scheduleTick(var5, this, 1);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.above());
      return !var4.getMaterial().isSolid() || var4.getBlock() instanceof FenceGateBlock;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return !this.defaultBlockState().canSurvive(var1.getLevel(), var1.getClickedPos()) ? Blocks.DIRT.defaultBlockState() : super.getStateForPlacement(var1);
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (!var1.canSurvive(var2, var3)) {
         turnToDirt(var1, var2, var3);
      } else {
         int var5 = (Integer)var1.getValue(MOISTURE);
         if (!isNearWater(var2, var3) && !var2.isRainingAt(var3.above())) {
            if (var5 > 0) {
               var2.setBlock(var3, (BlockState)var1.setValue(MOISTURE, var5 - 1), 2);
            } else if (!isUnderCrops(var2, var3)) {
               turnToDirt(var1, var2, var3);
            }
         } else if (var5 < 7) {
            var2.setBlock(var3, (BlockState)var1.setValue(MOISTURE, 7), 2);
         }

      }
   }

   public void fallOn(Level var1, BlockPos var2, Entity var3, float var4) {
      if (!var1.isClientSide && var1.random.nextFloat() < var4 - 0.5F && var3 instanceof LivingEntity && (var3 instanceof Player || var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) && var3.getBbWidth() * var3.getBbWidth() * var3.getBbHeight() > 0.512F) {
         turnToDirt(var1.getBlockState(var2), var1, var2);
      }

      super.fallOn(var1, var2, var3, var4);
   }

   public static void turnToDirt(BlockState var0, Level var1, BlockPos var2) {
      var1.setBlockAndUpdate(var2, pushEntitiesUp(var0, Blocks.DIRT.defaultBlockState(), var1, var2));
   }

   private static boolean isUnderCrops(BlockGetter var0, BlockPos var1) {
      Block var2 = var0.getBlockState(var1.above()).getBlock();
      return var2 instanceof CropBlock || var2 instanceof StemBlock || var2 instanceof AttachedStemBlock;
   }

   private static boolean isNearWater(LevelReader var0, BlockPos var1) {
      Iterator var2 = BlockPos.betweenClosed(var1.offset(-4, 0, -4), var1.offset(4, 1, 4)).iterator();

      BlockPos var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (BlockPos)var2.next();
      } while(!var0.getFluidState(var3).is(FluidTags.WATER));

      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(MOISTURE);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      MOISTURE = BlockStateProperties.MOISTURE;
      SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);
   }
}
