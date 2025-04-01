package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class TrophyBlock extends HorizontalDirectionalBlock {
   public static final MapCodec<TrophyBlock> CODEC = simpleCodec(TrophyBlock::new);
   public static final EnumProperty<TrophyType> TYPE;

   protected MapCodec<TrophyBlock> codec() {
      return CODEC;
   }

   public TrophyBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, TrophyType.GOLD));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(BlockStateProperties.TROPHY_TYPE, FACING);
   }

   public @Nullable BlockState getStateForPlacement(BlockPlaceContext var1) {
      TrophyType var2 = (TrophyType)var1.getItemInHand().getOrDefault(DataComponents.TROPHY_TYPE, TrophyType.GOLD);
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite())).setValue(TYPE, var2);
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      ItemStack var5 = super.getCloneItemStack(var1, var2, var3, var4);
      var5.set(DataComponents.TROPHY_TYPE, (TrophyType)var3.getValue(TYPE));
      return var5;
   }

   public static ItemStack createTrophy(TrophyType var0) {
      ItemStack var1 = new ItemStack(Items.TROPHY);
      var1.set(DataComponents.TROPHY_TYPE, var0);
      return var1;
   }

   static {
      TYPE = BlockStateProperties.TROPHY_TYPE;
   }
}
