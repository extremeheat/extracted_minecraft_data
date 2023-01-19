package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AttachedStemBlock extends BushBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   protected static final float AABB_OFFSET = 2.0F;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.SOUTH,
         Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 16.0),
         Direction.WEST,
         Block.box(0.0, 0.0, 6.0, 10.0, 10.0, 10.0),
         Direction.NORTH,
         Block.box(6.0, 0.0, 0.0, 10.0, 10.0, 10.0),
         Direction.EAST,
         Block.box(6.0, 0.0, 6.0, 16.0, 10.0, 10.0)
      )
   );
   private final StemGrownBlock fruit;
   private final Supplier<Item> seedSupplier;

   protected AttachedStemBlock(StemGrownBlock var1, Supplier<Item> var2, BlockBehaviour.Properties var3) {
      super(var3);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
      this.fruit = var1;
      this.seedSupplier = var2;
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return AABBS.get(var1.getValue(FACING));
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return !var3.is(this.fruit) && var2 == var1.getValue(FACING)
         ? this.fruit.getStem().defaultBlockState().setValue(StemBlock.AGE, Integer.valueOf(7))
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.is(Blocks.FARMLAND);
   }

   @Override
   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(this.seedSupplier.get());
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }
}
