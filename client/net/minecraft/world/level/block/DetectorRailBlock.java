package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;

public class DetectorRailBlock extends BaseRailBlock {
   public static final MapCodec<DetectorRailBlock> CODEC = simpleCodec(DetectorRailBlock::new);
   public static final EnumProperty<RailShape> SHAPE;
   public static final BooleanProperty POWERED;
   private static final int PRESSED_CHECK_PERIOD = 20;

   public MapCodec<DetectorRailBlock> codec() {
      return CODEC;
   }

   public DetectorRailBlock(final BlockBehaviour.Properties properties) {
      super(true, properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false)).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, false));
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   protected void entityInside(final BlockState state, final Level level, final BlockPos pos, final Entity entity, final InsideBlockEffectApplier effectApplier, final boolean isPrecise) {
      if (!level.isClientSide()) {
         if (!(Boolean)state.getValue(POWERED)) {
            this.checkPressed(level, pos, state);
         }
      }
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if ((Boolean)state.getValue(POWERED)) {
         this.checkPressed(level, pos, state);
      }
   }

   protected int getSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return (Boolean)state.getValue(POWERED) ? 15 : 0;
   }

   protected int getDirectSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      if (!(Boolean)state.getValue(POWERED)) {
         return 0;
      } else {
         return direction == Direction.UP ? 15 : 0;
      }
   }

   private void checkPressed(final Level level, final BlockPos pos, final BlockState state) {
      if (this.canSurvive(state, level, pos)) {
         boolean wasPressed = (Boolean)state.getValue(POWERED);
         boolean shouldBePressed = false;
         List<AbstractMinecart> entities = this.<AbstractMinecart>getInteractingMinecartOfType(level, pos, AbstractMinecart.class, (e) -> true);
         if (!entities.isEmpty()) {
            shouldBePressed = true;
         }

         if (shouldBePressed && !wasPressed) {
            BlockState newState = (BlockState)state.setValue(POWERED, true);
            level.setBlock(pos, newState, 3);
            this.updatePowerToConnected(level, pos, newState, true);
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.below(), this);
            level.setBlocksDirty(pos, state, newState);
         }

         if (!shouldBePressed && wasPressed) {
            BlockState newState = (BlockState)state.setValue(POWERED, false);
            level.setBlock(pos, newState, 3);
            this.updatePowerToConnected(level, pos, newState, false);
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.below(), this);
            level.setBlocksDirty(pos, state, newState);
         }

         if (shouldBePressed) {
            level.scheduleTick(pos, this, 20);
         }

         level.updateNeighbourForOutputSignal(pos, this);
      }
   }

   protected void updatePowerToConnected(final Level level, final BlockPos pos, final BlockState state, final boolean powered) {
      RailState rail = new RailState(level, pos, state);

      for(BlockPos connectionPos : rail.getConnections()) {
         BlockState connectionState = level.getBlockState(connectionPos);
         level.neighborChanged(connectionState, connectionPos, connectionState.getBlock(), (Orientation)null, false);
      }

   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (!oldState.is(state.getBlock())) {
         BlockState updatedState = this.updateState(state, level, pos, movedByPiston);
         this.checkPressed(level, pos, updatedState);
      }
   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      if ((Boolean)state.getValue(POWERED)) {
         List<MinecartCommandBlock> commandBlocks = this.<MinecartCommandBlock>getInteractingMinecartOfType(level, pos, MinecartCommandBlock.class, (e) -> true);
         if (!commandBlocks.isEmpty()) {
            return ((MinecartCommandBlock)commandBlocks.get(0)).getCommandBlock().getSuccessCount();
         }

         List<AbstractMinecart> entities = this.<AbstractMinecart>getInteractingMinecartOfType(level, pos, AbstractMinecart.class, EntitySelector.CONTAINER_ENTITY_SELECTOR);
         if (!entities.isEmpty()) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)entities.get(0));
         }
      }

      return 0;
   }

   private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(final Level level, final BlockPos pos, final Class<T> type, final Predicate<Entity> containerEntitySelector) {
      return level.getEntitiesOfClass(type, this.getSearchBB(pos), containerEntitySelector);
   }

   private AABB getSearchBB(final BlockPos pos) {
      double b = 0.2;
      return new AABB((double)pos.getX() + 0.2, (double)pos.getY(), (double)pos.getZ() + 0.2, (double)(pos.getX() + 1) - 0.2, (double)(pos.getY() + 1) - 0.2, (double)(pos.getZ() + 1) - 0.2);
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      RailShape currentShape = (RailShape)state.getValue(SHAPE);
      RailShape newShape = this.rotate(currentShape, rotation);
      return (BlockState)state.setValue(SHAPE, newShape);
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      RailShape currentShape = (RailShape)state.getValue(SHAPE);
      RailShape newShape = this.mirror(currentShape, mirror);
      return (BlockState)state.setValue(SHAPE, newShape);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(SHAPE, POWERED, WATERLOGGED);
   }

   static {
      SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
      POWERED = BlockStateProperties.POWERED;
   }
}
