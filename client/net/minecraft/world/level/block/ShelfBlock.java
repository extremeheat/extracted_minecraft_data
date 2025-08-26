package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public class ShelfBlock extends BaseEntityBlock implements SelectableSlotContainer, SideChainPartBlock, SimpleWaterloggedBlock {
   public static final MapCodec<ShelfBlock> CODEC = simpleCodec(ShelfBlock::new);
   public static final BooleanProperty POWERED;
   public static final EnumProperty<Direction> FACING;
   public static final EnumProperty<SideChainPart> SIDE_CHAIN_PART;
   public static final BooleanProperty WATERLOGGED;
   private static final Map<Direction, VoxelShape> SHAPES;

   public MapCodec<ShelfBlock> codec() {
      return CODEC;
   }

   public ShelfBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED)).setValue(WATERLOGGED, false));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPES.get(var1.getValue(FACING));
   }

   protected boolean useShapeForLightOcclusion(BlockState var1) {
      return true;
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return var2 == PathComputationType.WATER && var1.getFluidState().is(FluidTags.WATER);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new ShelfBlockEntity(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, SIDE_CHAIN_PART, WATERLOGGED);
   }

   protected void affectNeighborsAfterRemoval(BlockState var1, ServerLevel var2, BlockPos var3, boolean var4) {
      Containers.updateNeighboursAfterDestroy(var1, var2, var3);
      this.updateNeighborsAfterPoweringDown(var2, var3, var1);
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (!var2.isClientSide()) {
         boolean var7 = var2.hasNeighborSignal(var3);
         if ((Boolean)var1.getValue(POWERED) != var7) {
            BlockState var8 = (BlockState)var1.setValue(POWERED, var7);
            if (!var7) {
               var8 = (BlockState)var8.setValue(SIDE_CHAIN_PART, SideChainPart.UNCONNECTED);
            }

            var2.setBlock(var3, var8, 3);
            this.playSound(var2, var3, var7 ? SoundEvents.SHELF_ACTIVATE : SoundEvents.SHELF_DEACTIVATE);
            var2.gameEvent(var7 ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, var3, GameEvent.Context.of(var8));
         }

      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite())).setValue(POWERED, var1.getLevel().hasNeighborSignal(var1.getClickedPos()))).setValue(WATERLOGGED, var2.getType() == Fluids.WATER);
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   public int getRows() {
      return 1;
   }

   public int getColumns() {
      return 3;
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      BlockEntity var9 = var3.getBlockEntity(var4);
      if (var9 instanceof ShelfBlockEntity var8) {
         if (!var6.equals(InteractionHand.OFF_HAND)) {
            OptionalInt var12 = this.getHitSlot(var7, (Direction)var2.getValue(FACING));
            if (var12.isEmpty()) {
               return InteractionResult.PASS;
            }

            Inventory var10 = var5.getInventory();
            if (!(Boolean)var2.getValue(POWERED)) {
               boolean var13 = swapSingleItem(var1, var5, var8, var12.getAsInt(), var10);
               if (var13) {
                  this.playSound(var3, var4, var1.isEmpty() ? SoundEvents.SHELF_TAKE_ITEM : SoundEvents.SHELF_SINGLE_SWAP);
               } else if (!var1.isEmpty()) {
                  this.playSound(var3, var4, SoundEvents.SHELF_PLACE_ITEM);
               }

               return InteractionResult.SUCCESS.heldItemTransformedTo(var1);
            }

            ItemStack var11 = var10.getSelectedItem();
            this.swapHotbar(var3, var4, var10);
            this.playSound(var3, var4, SoundEvents.SHELF_MULTI_SWAP);
            return var11 == var10.getSelectedItem() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS.heldItemTransformedTo(var10.getSelectedItem());
         }
      }

      return InteractionResult.PASS;
   }

   private static boolean swapSingleItem(ItemStack var0, Player var1, ShelfBlockEntity var2, int var3, Inventory var4) {
      ItemStack var5 = var2.swapItemNoUpdate(var3, var0);
      ItemStack var6 = var1.hasInfiniteMaterials() && var5.isEmpty() ? var0.copy() : var5;
      var4.setItem(var4.getSelectedSlot(), var6);
      var4.setChanged();
      var2.setChanged(GameEvent.ITEM_INTERACT_FINISH);
      return !var5.isEmpty();
   }

   private void swapHotbar(Level var1, BlockPos var2, Inventory var3) {
      List var4 = this.getAllBlocksConnectedTo(var1, var2);
      if (!var4.isEmpty()) {
         for(int var5 = 0; var5 < var4.size(); ++var5) {
            ShelfBlockEntity var6 = (ShelfBlockEntity)var1.getBlockEntity((BlockPos)var4.get(var5));
            if (var6 != null) {
               for(int var7 = 0; var7 < var6.getContainerSize(); ++var7) {
                  int var8 = 9 - (var4.size() - var5) * var6.getContainerSize() + var7;
                  if (var8 >= 0 && var8 <= var3.getContainerSize()) {
                     ItemStack var9 = var6.swapItemNoUpdate(var7, var3.removeItemNoUpdate(var8));
                     var3.setItem(var8, var9);
                  }
               }

               var3.setChanged();
               var6.setChanged(GameEvent.ENTITY_INTERACT);
            }
         }

      }
   }

   public SideChainPart getSideChainPart(BlockState var1) {
      return (SideChainPart)var1.getValue(SIDE_CHAIN_PART);
   }

   public BlockState setSideChainPart(BlockState var1, SideChainPart var2) {
      return (BlockState)var1.setValue(SIDE_CHAIN_PART, var2);
   }

   public Direction getFacing(BlockState var1) {
      return (Direction)var1.getValue(FACING);
   }

   public boolean isConnectable(BlockState var1) {
      return var1.is(BlockTags.WOODEN_SHELVES) && (Boolean)var1.getValue(POWERED);
   }

   public int getMaxChainLength() {
      return 3;
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if ((Boolean)var1.getValue(POWERED)) {
         this.updateSelfAndNeighborsOnPoweringUp(var2, var3, var1, var4);
      } else {
         this.updateNeighborsAfterPoweringDown(var2, var3, var1);
      }

   }

   private void playSound(LevelAccessor var1, BlockPos var2, SoundEvent var3) {
      var1.playSound((Entity)null, var2, var3, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3, Direction var4) {
      if (var2.isClientSide()) {
         return 0;
      } else if (var4 != ((Direction)var1.getValue(FACING)).getOpposite()) {
         return 0;
      } else {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof ShelfBlockEntity) {
            ShelfBlockEntity var5 = (ShelfBlockEntity)var6;
            int var9 = var5.getItem(0).isEmpty() ? 0 : 1;
            int var7 = var5.getItem(1).isEmpty() ? 0 : 1;
            int var8 = var5.getItem(2).isEmpty() ? 0 : 1;
            return var9 | var7 << 1 | var8 << 2;
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
