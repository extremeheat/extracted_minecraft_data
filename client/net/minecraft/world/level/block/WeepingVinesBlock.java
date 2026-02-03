package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeepingVinesBlock extends GrowingPlantHeadBlock {
   public static final MapCodec<WeepingVinesBlock> CODEC = simpleCodec(WeepingVinesBlock::new);
   private static final VoxelShape SHAPE = Block.column(8.0, 9.0, 16.0);

   public MapCodec<WeepingVinesBlock> codec() {
      return CODEC;
   }

   public WeepingVinesBlock(final BlockBehaviour.Properties properties) {
      super(properties, Direction.DOWN, SHAPE, false, 0.1);
   }

   protected int getBlocksToGrowWhenBonemealed(final RandomSource random) {
      return NetherVines.getBlocksToGrowWhenBonemealed(random);
   }

   protected Block getBodyBlock() {
      return Blocks.WEEPING_VINES_PLANT;
   }

   protected boolean canGrowInto(final BlockState state) {
      return NetherVines.isValidGrowthState(state);
   }
}
