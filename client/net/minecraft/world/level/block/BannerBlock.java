package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BannerBlock extends AbstractBannerBlock {
   public static final MapCodec<BannerBlock> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DyeColor.CODEC.fieldOf("color").forGetter(AbstractBannerBlock::getColor), propertiesCodec()).apply(i, BannerBlock::new));
   public static final IntegerProperty ROTATION;
   private static final VoxelShape SHAPE;

   public MapCodec<BannerBlock> codec() {
      return CODEC;
   }

   public BannerBlock(final DyeColor color, final BlockBehaviour.Properties properties) {
      super(color, properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ROTATION, 0));
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return level.getBlockState(pos.below()).isSolid();
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation() + 180.0F));
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      return directionToNeighbour == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(ROTATION, rotation.rotate((Integer)state.getValue(ROTATION), 16));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return (BlockState)state.setValue(ROTATION, mirror.mirror((Integer)state.getValue(ROTATION), 16));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(ROTATION);
   }

   static {
      ROTATION = BlockStateProperties.ROTATION_16;
      SHAPE = Block.column(8.0, 0.0, 16.0);
   }
}
