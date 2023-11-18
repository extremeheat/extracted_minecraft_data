package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import org.slf4j.Logger;

public class DispenserBlock extends BaseEntityBlock {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
   private static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY = Util.make(
      new Object2ObjectOpenHashMap(), var0 -> var0.defaultReturnValue(new DefaultDispenseItemBehavior())
   );
   private static final int TRIGGER_DURATION = 4;

   public static void registerBehavior(ItemLike var0, DispenseItemBehavior var1) {
      DISPENSER_REGISTRY.put(var0.asItem(), var1);
   }

   protected DispenserBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.valueOf(false)));
   }

   @Override
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

   protected void dispenseFrom(ServerLevel var1, BlockState var2, BlockPos var3) {
      DispenserBlockEntity var4 = var1.getBlockEntity(var3, BlockEntityType.DISPENSER).orElse(null);
      if (var4 == null) {
         LOGGER.warn("Ignoring dispensing attempt for Dispenser without matching block entity at {}", var3);
      } else {
         BlockSource var5 = new BlockSource(var1, var3, var2, var4);
         int var6 = var4.getRandomSlot(var1.random);
         if (var6 < 0) {
            var1.levelEvent(1001, var3, 0);
            var1.gameEvent(GameEvent.BLOCK_ACTIVATE, var3, GameEvent.Context.of(var4.getBlockState()));
         } else {
            ItemStack var7 = var4.getItem(var6);
            DispenseItemBehavior var8 = this.getDispenseMethod(var7);
            if (var8 != DispenseItemBehavior.NOOP) {
               var4.setItem(var6, var8.dispense(var5, var7));
            }
         }
      }
   }

   protected DispenseItemBehavior getDispenseMethod(ItemStack var1) {
      return DISPENSER_REGISTRY.get(var1.getItem());
   }

   @Override
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3) || var2.hasNeighborSignal(var3.above());
      boolean var8 = var1.getValue(TRIGGERED);
      if (var7 && !var8) {
         var2.scheduleTick(var3, this, 4);
         var2.setBlock(var3, var1.setValue(TRIGGERED, Boolean.valueOf(true)), 2);
      } else if (!var7 && var8) {
         var2.setBlock(var3, var1.setValue(TRIGGERED, Boolean.valueOf(false)), 2);
      }
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.dispenseFrom(var2, var1, var3);
   }

   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new DispenserBlockEntity(var1, var2);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getNearestLookingDirection().getOpposite());
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var6 = var1.getBlockEntity(var2);
         if (var6 instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)var6).setCustomName(var5.getHoverName());
         }
      }
   }

   @Override
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof DispenserBlockEntity) {
            Containers.dropContents(var2, var3, (DispenserBlockEntity)var6);
            var2.updateNeighbourForOutputSignal(var3, this);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public static Position getDispensePosition(BlockSource var0) {
      Direction var1 = var0.state().getValue(FACING);
      return var0.center().add(0.7 * (double)var1.getStepX(), 0.7 * (double)var1.getStepY(), 0.7 * (double)var1.getStepZ());
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TRIGGERED);
   }
}
