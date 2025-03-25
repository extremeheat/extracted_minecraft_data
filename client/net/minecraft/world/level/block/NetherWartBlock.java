package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherWartBlock extends VegetationBlock {
   public static final MapCodec<NetherWartBlock> CODEC = simpleCodec(NetherWartBlock::new);
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE;
   private static final VoxelShape[] SHAPES;

   public MapCodec<NetherWartBlock> codec() {
      return CODEC;
   }

   protected NetherWartBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPES[(Integer)var1.getValue(AGE)];
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.SOUL_SAND);
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return (Integer)var1.getValue(AGE) < 3;
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = (Integer)var1.getValue(AGE);
      if (var5 < 3 && var4.nextInt(10) == 0) {
         var1 = (BlockState)var1.setValue(AGE, var5 + 1);
         var2.setBlock(var3, var1, 2);
      }

   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      return new ItemStack(Items.NETHER_WART);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   static {
      AGE = BlockStateProperties.AGE_3;
      SHAPES = Block.boxes(3, (var0) -> Block.column(16.0, 0.0, (double)(5 + var0 * 3)));
   }
}
