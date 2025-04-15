package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class RailBlock extends BaseRailBlock {
   public static final MapCodec<RailBlock> CODEC = simpleCodec(RailBlock::new);
   public static final EnumProperty<RailShape> SHAPE;

   public MapCodec<RailBlock> codec() {
      return CODEC;
   }

   protected RailBlock(BlockBehaviour.Properties var1) {
      super(false, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, false));
   }

   protected void updateState(BlockState var1, Level var2, BlockPos var3, Block var4) {
      if (var4.defaultBlockState().isSignalSource() && (new RailState(var2, var3, var1)).countPotentialConnections() == 3) {
         this.updateDir(var2, var3, var1, false);
      }

   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      RailShape var3 = (RailShape)var1.getValue(SHAPE);
      RailShape var4 = this.rotate(var3, var2);
      return (BlockState)var1.setValue(SHAPE, var4);
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      RailShape var3 = (RailShape)var1.getValue(SHAPE);
      RailShape var4 = this.mirror(var3, var2);
      return (BlockState)var1.setValue(SHAPE, var4);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SHAPE, WATERLOGGED);
   }

   static {
      SHAPE = BlockStateProperties.RAIL_SHAPE;
   }
}
