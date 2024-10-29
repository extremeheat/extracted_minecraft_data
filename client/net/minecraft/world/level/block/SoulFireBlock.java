package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return this.canSurvive(var1, var2, var4) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
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
