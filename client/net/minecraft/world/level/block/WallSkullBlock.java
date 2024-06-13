package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallSkullBlock extends AbstractSkullBlock {
   public static final MapCodec<WallSkullBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(SkullBlock.Type.CODEC.fieldOf("kind").forGetter(AbstractSkullBlock::getType), propertiesCodec()).apply(var0, WallSkullBlock::new)
   );
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.NORTH,
         Block.box(4.0, 4.0, 8.0, 12.0, 12.0, 16.0),
         Direction.SOUTH,
         Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 8.0),
         Direction.EAST,
         Block.box(0.0, 4.0, 4.0, 8.0, 12.0, 12.0),
         Direction.WEST,
         Block.box(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)
      )
   );

   @Override
   public MapCodec<? extends WallSkullBlock> codec() {
      return CODEC;
   }

   protected WallSkullBlock(SkullBlock.Type var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   @Override
   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return AABBS.get(var1.getValue(FACING));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = super.getStateForPlacement(var1);
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      Direction[] var5 = var1.getNearestLookingDirections();

      for (Direction var9 : var5) {
         if (var9.getAxis().isHorizontal()) {
            Direction var10 = var9.getOpposite();
            var2 = var2.setValue(FACING, var10);
            if (!var3.getBlockState(var4.relative(var9)).canBeReplaced(var1)) {
               return var2;
            }
         }
      }

      return null;
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(FACING);
   }
}