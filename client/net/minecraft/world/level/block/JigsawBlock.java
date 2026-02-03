package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.BlockHitResult;

public class JigsawBlock extends Block implements EntityBlock, GameMasterBlock {
   public static final MapCodec<JigsawBlock> CODEC = simpleCodec(JigsawBlock::new);
   public static final EnumProperty<FrontAndTop> ORIENTATION;

   public MapCodec<JigsawBlock> codec() {
      return CODEC;
   }

   protected JigsawBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(ORIENTATION, FrontAndTop.NORTH_UP));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(ORIENTATION);
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(ORIENTATION, rotation.rotation().rotate((FrontAndTop)state.getValue(ORIENTATION)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return (BlockState)state.setValue(ORIENTATION, mirror.rotation().rotate((FrontAndTop)state.getValue(ORIENTATION)));
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      Direction front = context.getClickedFace();
      Direction top;
      if (front.getAxis() == Direction.Axis.Y) {
         top = context.getHorizontalDirection().getOpposite();
      } else {
         top = Direction.UP;
      }

      return (BlockState)this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(front, top));
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new JigsawBlockEntity(worldPosition, blockState);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof JigsawBlockEntity && player.canUseGameMasterBlocks()) {
         player.openJigsawBlock((JigsawBlockEntity)blockEntity);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static boolean canAttach(final StructureTemplate.JigsawBlockInfo source, final StructureTemplate.JigsawBlockInfo target) {
      Direction sourceFront = getFrontFacing(source.info().state());
      Direction targetFront = getFrontFacing(target.info().state());
      Direction sourceTop = getTopFacing(source.info().state());
      Direction targetTop = getTopFacing(target.info().state());
      JigsawBlockEntity.JointType jointType = source.jointType();
      boolean rollable = jointType == JigsawBlockEntity.JointType.ROLLABLE;
      return sourceFront == targetFront.getOpposite() && (rollable || sourceTop == targetTop) && source.target().equals(target.name());
   }

   public static Direction getFrontFacing(final BlockState state) {
      return ((FrontAndTop)state.getValue(ORIENTATION)).front();
   }

   public static Direction getTopFacing(final BlockState state) {
      return ((FrontAndTop)state.getValue(ORIENTATION)).top();
   }

   static {
      ORIENTATION = BlockStateProperties.ORIENTATION;
   }
}
