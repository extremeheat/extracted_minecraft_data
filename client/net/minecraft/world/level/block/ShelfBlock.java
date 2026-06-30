package net.minecraft.world.level.block;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SideChainPart;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class ShelfBlock extends BaseEntityBlock implements SelectableSlotContainer, SideChainPartBlock, SimpleWaterloggedBlock {
   public static final BooleanProperty POWERED;
   public static final EnumProperty<Direction> FACING;
   public static final EnumProperty<SideChainPart> SIDE_CHAIN_PART;
   public static final BooleanProperty WATERLOGGED;
   private static final Map<Direction, VoxelShape> SHAPES;

   public ShelfBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED)).setValue(WATERLOGGED, false));
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)SHAPES.get(state.getValue(FACING));
   }

   protected boolean useShapeForLightOcclusion(final BlockState state) {
      return true;
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return type == PathComputationType.WATER && state.getFluidState().is(FluidTags.WATER);
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new ShelfBlockEntity(worldPosition, blockState);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED, SIDE_CHAIN_PART, WATERLOGGED);
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      Containers.updateNeighboursAfterDestroy(state, level, pos);
      this.updateNeighborsAfterPoweringDown(level, pos, state);
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (!level.isClientSide()) {
         boolean signal = level.hasNeighborSignal(pos);
         if ((Boolean)state.getValue(POWERED) != signal) {
            BlockState newState = (BlockState)state.setValue(POWERED, signal);
            if (!signal) {
               newState = (BlockState)newState.setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED);
            }

            level.setBlockAndUpdate(pos, newState);
            this.playSound(level, pos, signal ? SoundEvents.SHELF_ACTIVATE : SoundEvents.SHELF_DEACTIVATE);
            level.gameEvent(signal ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Context.of(newState));
         }

      }
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))).setValue(WATERLOGGED, replacedFluidState.is(Fluids.WATER));
   }

   public BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public int getRows() {
      return 1;
   }

   public int getColumns() {
      return 3;
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      BlockEntity var9 = level.getBlockEntity(pos);
      if (var9 instanceof ShelfBlockEntity shelfBlockEntity) {
         if (!hand.equals(InteractionHand.OFF_HAND)) {
            OptionalInt hitSlot = this.getHitSlot(hitResult, (Direction)state.getValue(FACING));
            if (hitSlot.isEmpty()) {
               return InteractionResult.PASS;
            }

            Inventory inventory = player.getInventory();
            if (level.isClientSide()) {
               return (InteractionResult)(inventory.getSelectedItem().isEmpty() ? InteractionResult.PASS : InteractionResult.SUCCESS);
            }

            if (!(Boolean)state.getValue(POWERED)) {
               boolean itemRemoved = swapSingleItem(itemStack, player, shelfBlockEntity, hitSlot.getAsInt(), inventory);
               if (itemRemoved) {
                  this.playSound(level, pos, itemStack.isEmpty() ? SoundEvents.SHELF_TAKE_ITEM : SoundEvents.SHELF_SINGLE_SWAP);
               } else {
                  if (itemStack.isEmpty()) {
                     return InteractionResult.PASS;
                  }

                  this.playSound(level, pos, SoundEvents.SHELF_PLACE_ITEM);
               }

               return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
            }

            ItemStack previousItem = inventory.getSelectedItem();
            boolean anySwapped = this.swapHotbar(level, pos, inventory);
            if (!anySwapped) {
               return InteractionResult.CONSUME;
            }

            this.playSound(level, pos, SoundEvents.SHELF_MULTI_SWAP);
            if (previousItem == inventory.getSelectedItem()) {
               return InteractionResult.SUCCESS;
            }

            return InteractionResult.SUCCESS.heldItemTransformedTo(inventory.getSelectedItem());
         }
      }

      return InteractionResult.PASS;
   }

   private static boolean swapSingleItem(final ItemStack itemStack, final Player player, final ShelfBlockEntity shelfBlockEntity, final int hitSlot, final Inventory inventory) {
      ItemStack removedItem = shelfBlockEntity.swapItemNoUpdate(hitSlot, itemStack);
      ItemStack newInventoryItem = player.hasInfiniteMaterials() && removedItem.isEmpty() ? itemStack.copy() : removedItem;
      inventory.setItem(inventory.getSelectedSlot(), newInventoryItem);
      inventory.setChanged();
      shelfBlockEntity.setChanged(newInventoryItem.has(DataComponents.USE_EFFECTS) && !((UseEffects)newInventoryItem.get(DataComponents.USE_EFFECTS)).interactVibrations() ? null : GameEvent.ITEM_INTERACT_FINISH);
      return !removedItem.isEmpty();
   }

   private boolean swapHotbar(final Level level, final BlockPos pos, final Inventory inventory) {
      List<BlockPos> connectedBlocks = this.getAllBlocksConnectedTo(level, pos);
      if (connectedBlocks.isEmpty()) {
         return false;
      } else {
         boolean anySwapped = false;

         for(int shelfPartIndex = 0; shelfPartIndex < connectedBlocks.size(); ++shelfPartIndex) {
            ShelfBlockEntity shelfPart = (ShelfBlockEntity)level.getBlockEntity((BlockPos)connectedBlocks.get(shelfPartIndex));
            if (shelfPart != null) {
               for(int slot = 0; slot < shelfPart.getContainerSize(); ++slot) {
                  int inventorySlot = 9 - (connectedBlocks.size() - shelfPartIndex) * shelfPart.getContainerSize() + slot;
                  if (inventorySlot >= 0 && inventorySlot <= inventory.getContainerSize()) {
                     ItemStack placedInventoryItem = inventory.removeItemNoUpdate(inventorySlot);
                     ItemStack removedShelfItem = shelfPart.swapItemNoUpdate(slot, placedInventoryItem);
                     if (!placedInventoryItem.isEmpty() || !removedShelfItem.isEmpty()) {
                        inventory.setItem(inventorySlot, removedShelfItem);
                        anySwapped = true;
                     }
                  }
               }

               inventory.setChanged();
               shelfPart.setChanged(GameEvent.ENTITY_INTERACT);
            }
         }

         return anySwapped;
      }
   }

   public SideChainPart getSideChainPart(final BlockState state) {
      return (SideChainPart)state.getValue(SIDE_CHAIN_PART);
   }

   public BlockState setSideChainPart(final BlockState state, final SideChainPart newPart) {
      return (BlockState)state.setValue(SIDE_CHAIN_PART, newPart);
   }

   public Direction getFacing(final BlockState state) {
      return (Direction)state.getValue(FACING);
   }

   public boolean isConnectable(final BlockState state) {
      return state.is(BlockTags.WOODEN_SHELVES) && state.hasProperty(POWERED) && (Boolean)state.getValue(POWERED);
   }

   public int getMaxChainLength() {
      return 3;
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if ((Boolean)state.getValue(POWERED)) {
         this.updateSelfAndNeighborsOnPoweringUp(level, pos, state, oldState);
      } else {
         this.updateNeighborsAfterPoweringDown(level, pos, state);
      }

   }

   private void playSound(final LevelAccessor level, final BlockPos pos, final SoundEvent sound) {
      level.playSound((Entity)null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      if (level.isClientSide()) {
         return 0;
      } else if (direction != ((Direction)state.getValue(FACING)).getOpposite()) {
         return 0;
      } else {
         BlockEntity var6 = level.getBlockEntity(pos);
         if (var6 instanceof ShelfBlockEntity) {
            ShelfBlockEntity blockEntity = (ShelfBlockEntity)var6;
            int item1Bit = blockEntity.getItem(0).isEmpty() ? 0 : 1;
            int item2Bit = blockEntity.getItem(1).isEmpty() ? 0 : 1;
            int item3Bit = blockEntity.getItem(2).isEmpty() ? 0 : 1;
            return item1Bit | item2Bit << 1 | item3Bit << 2;
         } else {
            return 0;
         }
      }
   }

   static {
      POWERED = BlockStateProperties.POWERED;
      FACING = BlockStateProperties.HORIZONTAL_FACING;
      SIDE_CHAIN_PART = BlockStateProperties.SIDE_CHAIN_PART;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPES = Shapes.rotateHorizontal(Shapes.or(Block.box(0.0, 12.0, 11.0, 16.0, 16.0, 13.0), Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0), Block.box(0.0, 0.0, 11.0, 16.0, 4.0, 13.0)));
   }
}
