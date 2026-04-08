package net.minecraft.world.level.block;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.dispenser.SpawnEggItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DispenserBlock extends BaseEntityBlock {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<DispenserBlock> CODEC = simpleCodec(DispenserBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final BooleanProperty TRIGGERED;
   private static final DefaultDispenseItemBehavior DEFAULT_BEHAVIOR;
   public static final Map<Item, DispenseItemBehavior> DISPENSER_REGISTRY;
   private static final int TRIGGER_DURATION = 4;

   public MapCodec<? extends DispenserBlock> codec() {
      return CODEC;
   }

   public static void registerBehavior(final ItemLike item, final DispenseItemBehavior behavior) {
      DISPENSER_REGISTRY.put(item.asItem(), behavior);
   }

   public static void registerProjectileBehavior(final ItemLike item) {
      DISPENSER_REGISTRY.put(item.asItem(), new ProjectileDispenseBehavior(item.asItem()));
   }

   protected DispenserBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TRIGGERED, false));
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!level.isClientSide()) {
         BlockEntity var7 = level.getBlockEntity(pos);
         if (var7 instanceof DispenserBlockEntity) {
            DispenserBlockEntity dispenser = (DispenserBlockEntity)var7;
            player.openMenu(dispenser);
            player.awardStat(dispenser instanceof DropperBlockEntity ? Stats.INSPECT_DROPPER : Stats.INSPECT_DISPENSER);
         }
      }

      return InteractionResult.SUCCESS;
   }

   protected void dispenseFrom(final ServerLevel level, final BlockState state, final BlockPos pos) {
      DispenserBlockEntity blockEntity = (DispenserBlockEntity)level.getBlockEntity(pos, BlockEntityType.DISPENSER).orElse((Object)null);
      if (blockEntity == null) {
         LOGGER.warn("Ignoring dispensing attempt for Dispenser without matching block entity at {}", pos);
      } else {
         BlockSource source = new BlockSource(level, pos, state, blockEntity);
         int slot = blockEntity.getRandomSlot(level.getRandom());
         if (slot < 0) {
            level.levelEvent(1001, pos, 0);
            level.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(blockEntity.getBlockState()));
         } else {
            ItemStack itemStack = blockEntity.getItem(slot);
            DispenseItemBehavior behavior = this.getDispenseMethod(level, itemStack);
            if (behavior != DispenseItemBehavior.NOOP) {
               blockEntity.setItem(slot, behavior.dispense(source, itemStack));
            }

         }
      }
   }

   protected DispenseItemBehavior getDispenseMethod(final Level level, final ItemStack itemStack) {
      if (!itemStack.isItemEnabled(level.enabledFeatures())) {
         return DEFAULT_BEHAVIOR;
      } else {
         DispenseItemBehavior behavior = (DispenseItemBehavior)DISPENSER_REGISTRY.get(itemStack.getItem());
         return behavior != null ? behavior : getDefaultDispenseMethod(itemStack);
      }
   }

   private static DispenseItemBehavior getDefaultDispenseMethod(final ItemStack itemStack) {
      if (itemStack.has(DataComponents.EQUIPPABLE)) {
         return EquipmentDispenseItemBehavior.INSTANCE;
      } else {
         return (DispenseItemBehavior)(itemStack.getItem() instanceof SpawnEggItem && itemStack.has(DataComponents.ENTITY_DATA) ? SpawnEggItemBehavior.INSTANCE : DEFAULT_BEHAVIOR);
      }
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      boolean shouldTrigger = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
      boolean isTriggered = (Boolean)state.getValue(TRIGGERED);
      if (shouldTrigger && !isTriggered) {
         level.scheduleTick(pos, this, 4);
         level.setBlock(pos, (BlockState)state.setValue(TRIGGERED, true), 2);
      } else if (!shouldTrigger && isTriggered) {
         level.setBlock(pos, (BlockState)state.setValue(TRIGGERED, false), 2);
      }

   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      this.dispenseFrom(level, state, pos);
   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new DispenserBlockEntity(worldPosition, blockState);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      Containers.updateNeighboursAfterDestroy(state, level, pos);
   }

   public static Position getDispensePosition(final BlockSource source) {
      return getDispensePosition(source, 0.7, Vec3.ZERO);
   }

   public static Position getDispensePosition(final BlockSource source, final double scale, final Vec3 offset) {
      Direction direction = (Direction)source.state().getValue(FACING);
      return source.center().add(scale * (double)direction.getStepX() + offset.x(), scale * (double)direction.getStepY() + offset.y(), scale * (double)direction.getStepZ() + offset.z());
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, TRIGGERED);
   }

   static {
      FACING = DirectionalBlock.FACING;
      TRIGGERED = BlockStateProperties.TRIGGERED;
      DEFAULT_BEHAVIOR = new DefaultDispenseItemBehavior();
      DISPENSER_REGISTRY = new IdentityHashMap();
   }
}
