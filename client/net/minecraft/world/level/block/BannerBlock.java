package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BannerBlock extends AbstractBannerBlock {
   public static final IntegerProperty ROTATION;
   private static final Map<DyeColor, Block> BY_COLOR;
   private static final VoxelShape SHAPE;

   public BannerBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0));
      BY_COLOR.put(var1, this);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).getMaterial().isSolid();
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(ROTATION, Mth.floor((double)((180.0F + var1.getRotation()) * 16.0F / 360.0F) + 0.5D) & 15);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(ROTATION, var2.rotate((Integer)var1.getValue(ROTATION), 16));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return (BlockState)var1.setValue(ROTATION, var2.mirror((Integer)var1.getValue(ROTATION), 16));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ROTATION);
   }

   public static Block byColor(DyeColor var0) {
      return (Block)BY_COLOR.getOrDefault(var0, Blocks.WHITE_BANNER);
   }

   static {
      ROTATION = BlockStateProperties.ROTATION_16;
      BY_COLOR = Maps.newHashMap();
      SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
   }
}
