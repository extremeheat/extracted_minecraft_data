package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class InfestedRotatedPillarBlock extends InfestedBlock {
   public static final MapCodec<InfestedRotatedPillarBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("host").forGetter(InfestedBlock::getHostBlock), propertiesCodec())
            .apply(var0, InfestedRotatedPillarBlock::new)
   );

   @Override
   public MapCodec<InfestedRotatedPillarBlock> codec() {
      return CODEC;
   }

   public InfestedRotatedPillarBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState(this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y));
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return RotatedPillarBlock.rotatePillar(var1, var2);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(RotatedPillarBlock.AXIS);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(RotatedPillarBlock.AXIS, var1.getClickedFace().getAxis());
   }
}
