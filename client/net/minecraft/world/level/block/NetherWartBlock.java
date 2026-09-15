package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
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
   public static final int MAX_AGE = 3;
   public static final IntegerProperty AGE;
   private static final VoxelShape[] SHAPES;

   protected NetherWartBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPES[(Integer)state.getValue(AGE)];
   }

   protected boolean mayPlaceOn(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return state.is(BlockTags.SUPPORTS_NETHER_WART);
   }

   protected boolean isRandomlyTicking(final BlockState state) {
      return (Integer)state.getValue(AGE) < 3;
   }

   protected void randomTick(BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      int age = (Integer)state.getValue(AGE);
      if (age < 3 && random.nextInt(10) == 0) {
         state = (BlockState)state.setValue(AGE, age + 1);
         level.setBlock(pos, state, 2);
      }

   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return new ItemStack(Items.NETHER_WART);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }

   static {
      AGE = BlockStateProperties.AGE_3;
      SHAPES = Block.boxes(3, (age) -> Block.column(16.0, 0.0, (double)(5 + age * 3)));
   }
}
