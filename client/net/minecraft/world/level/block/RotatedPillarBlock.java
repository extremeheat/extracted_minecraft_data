package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class RotatedPillarBlock extends Block {
   public static final MapCodec<RotatedPillarBlock> CODEC = simpleCodec(RotatedPillarBlock::new);
   public static final EnumProperty<Direction.Axis> AXIS;

   public MapCodec<? extends RotatedPillarBlock> codec() {
      return CODEC;
   }

   public RotatedPillarBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return rotatePillar(var1, var2);
   }

   public static BlockState rotatePillar(BlockState var0, Rotation var1) {
      switch (var1) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            switch ((Direction.Axis)var0.getValue(AXIS)) {
               case X -> {
                  return (BlockState)var0.setValue(AXIS, Direction.Axis.Z);
               }
               case Z -> {
                  return (BlockState)var0.setValue(AXIS, Direction.Axis.X);
               }
               default -> {
                  return var0;
               }
            }
         default:
            return var0;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AXIS);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(AXIS, var1.getClickedFace().getAxis());
   }

   static {
      AXIS = BlockStateProperties.AXIS;
   }
}
