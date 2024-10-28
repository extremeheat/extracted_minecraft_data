package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SoulFireBlock extends BaseFireBlock {
   public static final MapCodec<SoulFireBlock> CODEC = simpleCodec(SoulFireBlock::new);

   public MapCodec<SoulFireBlock> codec() {
      return CODEC;
   }

   public SoulFireBlock(BlockBehaviour.Properties var1) {
      super(var1, 2.0F);
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return this.canSurvive(var1, var4, var5) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return canSurviveOnBlock(var2.getBlockState(var3.below()));
   }

   public static boolean canSurviveOnBlock(BlockState var0) {
      return var0.is(BlockTags.SOUL_FIRE_BASE_BLOCKS);
   }

   protected boolean canBurn(BlockState var1) {
      return true;
   }
}
