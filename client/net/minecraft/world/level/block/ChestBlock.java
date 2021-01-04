package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING;
   public static final EnumProperty<ChestType> TYPE;
   public static final BooleanProperty WATERLOGGED;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape EAST_AABB;
   protected static final VoxelShape AABB;
   private static final ChestBlock.ChestSearchCallback<Container> CHEST_COMBINER;
   private static final ChestBlock.ChestSearchCallback<MenuProvider> MENU_PROVIDER_COMBINER;

   protected ChestBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, ChestType.SINGLE)).setValue(WATERLOGGED, false));
   }

   public boolean hasCustomBreakingProgress(BlockState var1) {
      return true;
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      if (var3.getBlock() == this && var2.getAxis().isHorizontal()) {
         ChestType var7 = (ChestType)var3.getValue(TYPE);
         if (var1.getValue(TYPE) == ChestType.SINGLE && var7 != ChestType.SINGLE && var1.getValue(FACING) == var3.getValue(FACING) && getConnectedDirection(var3) == var2.getOpposite()) {
            return (BlockState)var1.setValue(TYPE, var7.getOpposite());
         }
      } else if (getConnectedDirection(var1) == var2) {
         return (BlockState)var1.setValue(TYPE, ChestType.SINGLE);
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (var1.getValue(TYPE) == ChestType.SINGLE) {
         return AABB;
      } else {
         switch(getConnectedDirection(var1)) {
         case NORTH:
         default:
            return NORTH_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case WEST:
            return WEST_AABB;
         case EAST:
            return EAST_AABB;
         }
      }
   }

   public static Direction getConnectedDirection(BlockState var0) {
      Direction var1 = (Direction)var0.getValue(FACING);
      return var0.getValue(TYPE) == ChestType.LEFT ? var1.getClockWise() : var1.getCounterClockWise();
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      ChestType var2 = ChestType.SINGLE;
      Direction var3 = var1.getHorizontalDirection().getOpposite();
      FluidState var4 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var5 = var1.isSneaking();
      Direction var6 = var1.getClickedFace();
      if (var6.getAxis().isHorizontal() && var5) {
         Direction var7 = this.candidatePartnerFacing(var1, var6.getOpposite());
         if (var7 != null && var7.getAxis() != var6.getAxis()) {
            var3 = var7;
            var2 = var7.getCounterClockWise() == var6.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (var2 == ChestType.SINGLE && !var5) {
         if (var3 == this.candidatePartnerFacing(var1, var3.getClockWise())) {
            var2 = ChestType.LEFT;
         } else if (var3 == this.candidatePartnerFacing(var1, var3.getCounterClockWise())) {
            var2 = ChestType.RIGHT;
         }
      }

      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var3)).setValue(TYPE, var2)).setValue(WATERLOGGED, var4.getType() == Fluids.WATER);
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Nullable
   private Direction candidatePartnerFacing(BlockPlaceContext var1, Direction var2) {
      BlockState var3 = var1.getLevel().getBlockState(var1.getClickedPos().relative(var2));
      return var3.getBlock() == this && var3.getValue(TYPE) == ChestType.SINGLE ? (Direction)var3.getValue(FACING) : null;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var6 = var1.getBlockEntity(var2);
         if (var6 instanceof ChestBlockEntity) {
            ((ChestBlockEntity)var6).setCustomName(var5.getHoverName());
         }
      }

   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var1.getBlock() != var4.getBlock()) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof Container) {
            Containers.dropContents(var2, var3, (Container)var6);
            var2.updateNeighbourForOutputSignal(var3, this);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return true;
      } else {
         MenuProvider var7 = this.getMenuProvider(var1, var2, var3);
         if (var7 != null) {
            var4.openMenu(var7);
            var4.awardStat(this.getOpenChestStat());
         }

         return true;
      }
   }

   protected Stat<ResourceLocation> getOpenChestStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   @Nullable
   public static <T> T combineWithNeigbour(BlockState var0, LevelAccessor var1, BlockPos var2, boolean var3, ChestBlock.ChestSearchCallback<T> var4) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      if (!(var5 instanceof ChestBlockEntity)) {
         return null;
      } else if (!var3 && isChestBlockedAt(var1, var2)) {
         return null;
      } else {
         ChestBlockEntity var6 = (ChestBlockEntity)var5;
         ChestType var7 = (ChestType)var0.getValue(TYPE);
         if (var7 == ChestType.SINGLE) {
            return var4.acceptSingle(var6);
         } else {
            BlockPos var8 = var2.relative(getConnectedDirection(var0));
            BlockState var9 = var1.getBlockState(var8);
            if (var9.getBlock() == var0.getBlock()) {
               ChestType var10 = (ChestType)var9.getValue(TYPE);
               if (var10 != ChestType.SINGLE && var7 != var10 && var9.getValue(FACING) == var0.getValue(FACING)) {
                  if (!var3 && isChestBlockedAt(var1, var8)) {
                     return null;
                  }

                  BlockEntity var11 = var1.getBlockEntity(var8);
                  if (var11 instanceof ChestBlockEntity) {
                     ChestBlockEntity var12 = var7 == ChestType.RIGHT ? var6 : (ChestBlockEntity)var11;
                     ChestBlockEntity var13 = var7 == ChestType.RIGHT ? (ChestBlockEntity)var11 : var6;
                     return var4.acceptDouble(var12, var13);
                  }
               }
            }

            return var4.acceptSingle(var6);
         }
      }
   }

   @Nullable
   public static Container getContainer(BlockState var0, Level var1, BlockPos var2, boolean var3) {
      return (Container)combineWithNeigbour(var0, var1, var2, var3, CHEST_COMBINER);
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return (MenuProvider)combineWithNeigbour(var1, var2, var3, false, MENU_PROVIDER_COMBINER);
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new ChestBlockEntity();
   }

   private static boolean isChestBlockedAt(LevelAccessor var0, BlockPos var1) {
      return isBlockedChestByBlock(var0, var1) || isCatSittingOnChest(var0, var1);
   }

   private static boolean isBlockedChestByBlock(BlockGetter var0, BlockPos var1) {
      BlockPos var2 = var1.above();
      return var0.getBlockState(var2).isRedstoneConductor(var0, var2);
   }

   private static boolean isCatSittingOnChest(LevelAccessor var0, BlockPos var1) {
      List var2 = var0.getEntitiesOfClass(Cat.class, new AABB((double)var1.getX(), (double)(var1.getY() + 1), (double)var1.getZ(), (double)(var1.getX() + 1), (double)(var1.getY() + 2), (double)(var1.getZ() + 1)));
      if (!var2.isEmpty()) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Cat var4 = (Cat)var3.next();
            if (var4.isSitting()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromContainer(getContainer(var1, var2, var3, false));
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TYPE, WATERLOGGED);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      TYPE = BlockStateProperties.CHEST_TYPE;
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      NORTH_AABB = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
      SOUTH_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
      WEST_AABB = Block.box(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
      EAST_AABB = Block.box(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
      AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
      CHEST_COMBINER = new ChestBlock.ChestSearchCallback<Container>() {
         public Container acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
            return new CompoundContainer(var1, var2);
         }

         public Container acceptSingle(ChestBlockEntity var1) {
            return var1;
         }

         // $FF: synthetic method
         public Object acceptSingle(ChestBlockEntity var1) {
            return this.acceptSingle(var1);
         }

         // $FF: synthetic method
         public Object acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
            return this.acceptDouble(var1, var2);
         }
      };
      MENU_PROVIDER_COMBINER = new ChestBlock.ChestSearchCallback<MenuProvider>() {
         public MenuProvider acceptDouble(final ChestBlockEntity var1, final ChestBlockEntity var2) {
            final CompoundContainer var3 = new CompoundContainer(var1, var2);
            return new MenuProvider() {
               @Nullable
               public AbstractContainerMenu createMenu(int var1x, Inventory var2x, Player var3x) {
                  if (var1.canOpen(var3x) && var2.canOpen(var3x)) {
                     var1.unpackLootTable(var2x.player);
                     var2.unpackLootTable(var2x.player);
                     return ChestMenu.sixRows(var1x, var2x, var3);
                  } else {
                     return null;
                  }
               }

               public Component getDisplayName() {
                  if (var1.hasCustomName()) {
                     return var1.getDisplayName();
                  } else {
                     return (Component)(var2.hasCustomName() ? var2.getDisplayName() : new TranslatableComponent("container.chestDouble", new Object[0]));
                  }
               }
            };
         }

         public MenuProvider acceptSingle(ChestBlockEntity var1) {
            return var1;
         }

         // $FF: synthetic method
         public Object acceptSingle(ChestBlockEntity var1) {
            return this.acceptSingle(var1);
         }

         // $FF: synthetic method
         public Object acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2) {
            return this.acceptDouble(var1, var2);
         }
      };
   }

   interface ChestSearchCallback<T> {
      T acceptDouble(ChestBlockEntity var1, ChestBlockEntity var2);

      T acceptSingle(ChestBlockEntity var1);
   }
}
