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

   public RotatedPillarBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return rotatePillar(state, rotation);
   }

   public static BlockState rotatePillar(final BlockState state, final Rotation rotation) {
      switch (rotation) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            switch ((Direction.Axis)state.getValue(AXIS)) {
               case X -> {
                  return (BlockState)state.setValue(AXIS, Direction.Axis.Z);
               }
               case Z -> {
                  return (BlockState)state.setValue(AXIS, Direction.Axis.X);
               }
               default -> {
                  return state;
               }
            }
         default:
            return state;
      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(AXIS);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
   }

   static {
      AXIS = BlockStateProperties.AXIS;
   }
}
