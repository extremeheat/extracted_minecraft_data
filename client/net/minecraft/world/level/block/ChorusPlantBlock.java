package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class ChorusPlantBlock extends PipeBlock {
   public static final MapCodec<ChorusPlantBlock> CODEC = simpleCodec(ChorusPlantBlock::new);

   public MapCodec<ChorusPlantBlock> codec() {
      return CODEC;
   }

   protected ChorusPlantBlock(BlockBehaviour.Properties var1) {
      super(0.3125F, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false)).setValue(UP, false)).setValue(DOWN, false));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return getStateWithConnections(var1.getLevel(), var1.getClickedPos(), this.defaultBlockState());
   }

   public static BlockState getStateWithConnections(BlockGetter var0, BlockPos var1, BlockState var2) {
      BlockState var3 = var0.getBlockState(var1.below());
      BlockState var4 = var0.getBlockState(var1.above());
      BlockState var5 = var0.getBlockState(var1.north());
      BlockState var6 = var0.getBlockState(var1.east());
      BlockState var7 = var0.getBlockState(var1.south());
      BlockState var8 = var0.getBlockState(var1.west());
      Block var9 = var2.getBlock();
      return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)var2.trySetValue(DOWN, var3.is(var9) || var3.is(Blocks.CHORUS_FLOWER) || var3.is(Blocks.END_STONE))).trySetValue(UP, var4.is(var9) || var4.is(Blocks.CHORUS_FLOWER))).trySetValue(NORTH, var5.is(var9) || var5.is(Blocks.CHORUS_FLOWER))).trySetValue(EAST, var6.is(var9) || var6.is(Blocks.CHORUS_FLOWER))).trySetValue(SOUTH, var7.is(var9) || var7.is(Blocks.CHORUS_FLOWER))).trySetValue(WEST, var8.is(var9) || var8.is(Blocks.CHORUS_FLOWER));
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         var4.scheduleTick(var5, (Block)this, 1);
         return super.updateShape(var1, var2, var3, var4, var5, var6);
      } else {
         boolean var7 = var3.is(this) || var3.is(Blocks.CHORUS_FLOWER) || var2 == Direction.DOWN && var3.is(Blocks.END_STONE);
         return (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2), var7);
      }
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!var1.canSurvive(var2, var3)) {
         var2.destroyBlock(var3, true);
      }

   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      boolean var5 = !var2.getBlockState(var3.above()).isAir() && !var4.isAir();
      Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

      BlockState var10;
      do {
         BlockPos var8;
         BlockState var9;
         do {
            if (!var6.hasNext()) {
               return var4.is(this) || var4.is(Blocks.END_STONE);
            }

            Direction var7 = (Direction)var6.next();
            var8 = var3.relative(var7);
            var9 = var2.getBlockState(var8);
         } while(!var9.is(this));

         if (var5) {
            return false;
         }

         var10 = var2.getBlockState(var8.below());
      } while(!var10.is(this) && !var10.is(Blocks.END_STONE));

      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
