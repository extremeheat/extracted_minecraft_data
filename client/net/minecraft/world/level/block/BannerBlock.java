package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BannerBlock extends AbstractBannerBlock {
   public static final MapCodec<BannerBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(DyeColor.CODEC.fieldOf("color").forGetter(AbstractBannerBlock::getColor), propertiesCodec()).apply(var0, BannerBlock::new)
   );
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
   private static final Map<DyeColor, Block> BY_COLOR = Maps.newHashMap();
   private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

   @Override
   public MapCodec<BannerBlock> codec() {
      return CODEC;
   }

   public BannerBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, Integer.valueOf(0)));
      BY_COLOR.put(var1, this);
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).isSolid();
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(ROTATION, Integer.valueOf(RotationSegment.convertToSegment(var1.getRotation() + 180.0F)));
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(ROTATION, Integer.valueOf(var2.rotate(var1.getValue(ROTATION), 16)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.setValue(ROTATION, Integer.valueOf(var2.mirror(var1.getValue(ROTATION), 16)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(ROTATION);
   }

   public static Block byColor(DyeColor var0) {
      return BY_COLOR.getOrDefault(var0, Blocks.WHITE_BANNER);
   }
}
