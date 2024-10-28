package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SnowyDirtBlock extends Block {
   public static final MapCodec<SnowyDirtBlock> CODEC = simpleCodec(SnowyDirtBlock::new);
   public static final BooleanProperty SNOWY;

   protected MapCodec<? extends SnowyDirtBlock> codec() {
      return CODEC;
   }

   protected SnowyDirtBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(SNOWY, false));
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return var5 == Direction.UP ? (BlockState)var1.setValue(SNOWY, isSnowySetting(var7)) : super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = var1.getLevel().getBlockState(var1.getClickedPos().above());
      return (BlockState)this.defaultBlockState().setValue(SNOWY, isSnowySetting(var2));
   }

   private static boolean isSnowySetting(BlockState var0) {
      return var0.is(BlockTags.SNOW);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SNOWY);
   }

   static {
      SNOWY = BlockStateProperties.SNOWY;
   }
}
