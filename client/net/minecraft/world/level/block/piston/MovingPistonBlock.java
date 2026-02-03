package net.minecraft.world.level.block.piston;

import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class MovingPistonBlock extends BaseEntityBlock {
   public static final MapCodec<MovingPistonBlock> CODEC = simpleCodec(MovingPistonBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final EnumProperty<PistonType> TYPE;

   public MapCodec<MovingPistonBlock> codec() {
      return CODEC;
   }

   public MovingPistonBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT));
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return null;
   }

   public static BlockEntity newMovingBlockEntity(final BlockPos position, final BlockState blockState, final BlockState movedState, final Direction direction, final boolean extending, final boolean isSourcePiston) {
      return new PistonMovingBlockEntity(position, blockState, movedState, direction, extending, isSourcePiston);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return createTickerHelper(type, BlockEntityType.PISTON, PistonMovingBlockEntity::tick);
   }

   public void destroy(final LevelAccessor level, final BlockPos pos, final BlockState state) {
      BlockPos relative = pos.relative(((Direction)state.getValue(FACING)).getOpposite());
      BlockState blockState = level.getBlockState(relative);
      if (blockState.getBlock() instanceof PistonBaseBlock && (Boolean)blockState.getValue(PistonBaseBlock.EXTENDED)) {
         level.removeBlock(relative, false);
      }

   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!level.isClientSide() && level.getBlockEntity(pos) == null) {
         level.removeBlock(pos, false);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }

   protected List<ItemStack> getDrops(final BlockState state, final LootParams.Builder params) {
      PistonMovingBlockEntity entity = this.getBlockEntity(params.getLevel(), BlockPos.containing((Position)params.getParameter(LootContextParams.ORIGIN)));
      return entity == null ? Collections.emptyList() : entity.getMovedState().getDrops(params);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return Shapes.empty();
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      PistonMovingBlockEntity blockEntity = this.getBlockEntity(level, pos);
      return blockEntity != null ? blockEntity.getCollisionShape(level, pos) : Shapes.empty();
   }

   private @Nullable PistonMovingBlockEntity getBlockEntity(final BlockGetter level, final BlockPos pos) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      return blockEntity instanceof PistonMovingBlockEntity ? (PistonMovingBlockEntity)blockEntity : null;
   }

   protected RenderShape getRenderShape(final BlockState state) {
      return RenderShape.INVISIBLE;
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return ItemStack.EMPTY;
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, TYPE);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   static {
      FACING = PistonHeadBlock.FACING;
      TYPE = PistonHeadBlock.TYPE;
   }
}
