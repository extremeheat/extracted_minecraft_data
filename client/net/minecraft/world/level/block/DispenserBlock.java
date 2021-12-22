package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class DispenserBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty TRIGGERED;
   private static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY;
   private static final int TRIGGER_DURATION = 4;

   public static void registerBehavior(ItemLike var0, DispenseItemBehavior var1) {
      DISPENSER_REGISTRY.put(var0.asItem(), var1);
   }

   protected DispenserBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TRIGGERED, false));
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof DispenserBlockEntity) {
            var4.openMenu((DispenserBlockEntity)var7);
            if (var7 instanceof DropperBlockEntity) {
               var4.awardStat(Stats.INSPECT_DROPPER);
            } else {
               var4.awardStat(Stats.INSPECT_DISPENSER);
            }
         }

         return InteractionResult.CONSUME;
      }
   }

   protected void dispenseFrom(ServerLevel var1, BlockPos var2) {
      BlockSourceImpl var3 = new BlockSourceImpl(var1, var2);
      DispenserBlockEntity var4 = (DispenserBlockEntity)var3.getEntity();
      int var5 = var4.getRandomSlot();
      if (var5 < 0) {
         var1.levelEvent(1001, var2, 0);
         var1.gameEvent(GameEvent.DISPENSE_FAIL, var2);
      } else {
         ItemStack var6 = var4.getItem(var5);
         DispenseItemBehavior var7 = this.getDispenseMethod(var6);
         if (var7 != DispenseItemBehavior.NOOP) {
            var4.setItem(var5, var7.dispense(var3, var6));
         }

      }
   }

   protected DispenseItemBehavior getDispenseMethod(ItemStack var1) {
      return (DispenseItemBehavior)DISPENSER_REGISTRY.get(var1.getItem());
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3) || var2.hasNeighborSignal(var3.above());
      boolean var8 = (Boolean)var1.getValue(TRIGGERED);
      if (var7 && !var8) {
         var2.scheduleTick(var3, this, 4);
         var2.setBlock(var3, (BlockState)var1.setValue(TRIGGERED, true), 4);
      } else if (!var7 && var8) {
         var2.setBlock(var3, (BlockState)var1.setValue(TRIGGERED, false), 4);
      }

   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      this.dispenseFrom(var2, var3);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new DispenserBlockEntity(var1, var2);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getNearestLookingDirection().getOpposite());
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var6 = var1.getBlockEntity(var2);
         if (var6 instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)var6).setCustomName(var5.getHoverName());
         }
      }

   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof DispenserBlockEntity) {
            Containers.dropContents(var2, (BlockPos)var3, (Container)((DispenserBlockEntity)var6));
            var2.updateNeighbourForOutputSignal(var3, this);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public static Position getDispensePosition(BlockSource var0) {
      Direction var1 = (Direction)var0.getBlockState().getValue(FACING);
      double var2 = var0.method_2() + 0.7D * (double)var1.getStepX();
      double var4 = var0.method_3() + 0.7D * (double)var1.getStepY();
      double var6 = var0.method_4() + 0.7D * (double)var1.getStepZ();
      return new PositionImpl(var2, var4, var6);
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TRIGGERED);
   }

   static {
      FACING = DirectionalBlock.FACING;
      TRIGGERED = BlockStateProperties.TRIGGERED;
      DISPENSER_REGISTRY = (Map)Util.make(new Object2ObjectOpenHashMap(), (var0) -> {
         var0.defaultReturnValue(new DefaultDispenseItemBehavior());
      });
   }
}
