package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class DispenserBlock extends BaseEntityBlock {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<DispenserBlock> CODEC = simpleCodec(DispenserBlock::new);
   public static final DirectionProperty FACING = DirectionalBlock.FACING;
   public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
   private static final DefaultDispenseItemBehavior DEFAULT_BEHAVIOR = new DefaultDispenseItemBehavior();
   public static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY = Util.make(
      new Object2ObjectOpenHashMap(), var0 -> var0.defaultReturnValue(DEFAULT_BEHAVIOR)
   );
   private static final int TRIGGER_DURATION = 4;

   @Override
   public MapCodec<? extends DispenserBlock> codec() {
      return CODEC;
   }

   public static void registerBehavior(ItemLike var0, DispenseItemBehavior var1) {
      DISPENSER_REGISTRY.put(var0.asItem(), var1);
   }

   public static void registerProjectileBehavior(ItemLike var0) {
      DISPENSER_REGISTRY.put(var0.asItem(), new ProjectileDispenseBehavior(var0.asItem()));
   }

   protected DispenserBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.valueOf(false)));
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var2.isClientSide && var2.getBlockEntity(var3) instanceof DispenserBlockEntity var6) {
         var4.openMenu(var6);
         var4.awardStat(var6 instanceof DropperBlockEntity ? Stats.INSPECT_DROPPER : Stats.INSPECT_DISPENSER);
      }

      return InteractionResult.SUCCESS;
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
            DispenseItemBehavior var8 = this.getDispenseMethod(var1, var7);
            if (var8 != DispenseItemBehavior.NOOP) {
               var4.setItem(var6, var8.dispense(var5, var7));
            }
         }
      }
   }

   protected DispenseItemBehavior getDispenseMethod(Level var1, ItemStack var2) {
      return (DispenseItemBehavior)(!var2.isItemEnabled(var1.enabledFeatures()) ? DEFAULT_BEHAVIOR : DISPENSER_REGISTRY.get(var2.getItem()));
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
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
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
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
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   public static Position getDispensePosition(BlockSource var0) {
      return getDispensePosition(var0, 0.7, Vec3.ZERO);
   }

   public static Position getDispensePosition(BlockSource var0, double var1, Vec3 var3) {
      Direction var4 = var0.state().getValue(FACING);
      return var0.center().add(var1 * (double)var4.getStepX() + var3.x(), var1 * (double)var4.getStepY() + var3.y(), var1 * (double)var4.getStepZ() + var3.z());
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TRIGGERED);
   }
}
