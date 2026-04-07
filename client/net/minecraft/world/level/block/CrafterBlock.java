package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeCache;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CrafterBlock extends BaseEntityBlock {
   public static final MapCodec<CrafterBlock> CODEC = simpleCodec(CrafterBlock::new);
   public static final BooleanProperty CRAFTING;
   public static final BooleanProperty TRIGGERED;
   private static final EnumProperty<FrontAndTop> ORIENTATION;
   private static final int MAX_CRAFTING_TICKS = 6;
   private static final int CRAFTING_TICK_DELAY = 4;
   private static final RecipeCache RECIPE_CACHE;
   private static final int CRAFTER_ADVANCEMENT_DIAMETER = 17;

   public CrafterBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(ORIENTATION, FrontAndTop.NORTH_UP)).setValue(TRIGGERED, false)).setValue(CRAFTING, false));
   }

   protected MapCodec<CrafterBlock> codec() {
      return CODEC;
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
         return crafterBlockEntity.getRedstoneSignal();
      } else {
         return 0;
      }
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      boolean shouldTrigger = level.hasNeighborSignal(pos);
      boolean isTriggered = (Boolean)state.getValue(TRIGGERED);
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (shouldTrigger && !isTriggered) {
         level.scheduleTick(pos, this, 4);
         level.setBlock(pos, (BlockState)state.setValue(TRIGGERED, true), 2);
         this.setBlockEntityTriggered(blockEntity, true);
      } else if (!shouldTrigger && isTriggered) {
         level.setBlock(pos, (BlockState)((BlockState)state.setValue(TRIGGERED, false)).setValue(CRAFTING, false), 2);
         this.setBlockEntityTriggered(blockEntity, false);
      }

   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      this.dispenseFrom(state, level, pos);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return level.isClientSide() ? null : createTickerHelper(type, BlockEntityType.CRAFTER, CrafterBlockEntity::serverTick);
   }

   private void setBlockEntityTriggered(final @Nullable BlockEntity blockEntity, final boolean triggered) {
      if (blockEntity instanceof CrafterBlockEntity crafterBlockEntity) {
         crafterBlockEntity.setTriggered(triggered);
      }

   }

   public BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      CrafterBlockEntity crafterBlockEntity = new CrafterBlockEntity(worldPosition, blockState);
      crafterBlockEntity.setTriggered(blockState.hasProperty(TRIGGERED) && (Boolean)blockState.getValue(TRIGGERED));
      return crafterBlockEntity;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      Direction nearestLookingDirection = context.getNearestLookingDirection().getOpposite();
      Direction var10000;
      switch (nearestLookingDirection) {
         case DOWN:
            var10000 = context.getHorizontalDirection().getOpposite();
            break;
         case UP:
            var10000 = context.getHorizontalDirection();
            break;
         case NORTH:
         case SOUTH:
         case WEST:
         case EAST:
            var10000 = Direction.UP;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Direction verticalDirection = var10000;
      return (BlockState)((BlockState)this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(nearestLookingDirection, verticalDirection))).setValue(TRIGGERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
   }

   public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
      if ((Boolean)state.getValue(TRIGGERED)) {
         level.scheduleTick(pos, this, 4);
      }

   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      Containers.updateNeighboursAfterDestroy(state, level, pos);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!level.isClientSide()) {
         BlockEntity var7 = level.getBlockEntity(pos);
         if (var7 instanceof CrafterBlockEntity) {
            CrafterBlockEntity crafter = (CrafterBlockEntity)var7;
            player.openMenu(crafter);
         }
      }

      return InteractionResult.SUCCESS;
   }

   protected void dispenseFrom(final BlockState state, final ServerLevel level, final BlockPos pos) {
      BlockEntity var5 = level.getBlockEntity(pos);
      if (var5 instanceof CrafterBlockEntity blockEntity) {
         CraftingInput var11 = blockEntity.asCraftInput();
         Optional<RecipeHolder<CraftingRecipe>> recipe = getPotentialResults(level, var11);
         if (recipe.isEmpty()) {
            level.levelEvent(1050, pos, 0);
         } else {
            RecipeHolder<CraftingRecipe> pickedRecipe = (RecipeHolder)recipe.get();
            ItemStack results = ((CraftingRecipe)pickedRecipe.value()).assemble(var11);
            if (results.isEmpty()) {
               level.levelEvent(1050, pos, 0);
            } else {
               blockEntity.setCraftingTicksRemaining(6);
               level.setBlock(pos, (BlockState)state.setValue(CRAFTING, true), 2);
               results.onCraftedBySystem(level);
               this.dispenseItem(level, pos, blockEntity, results, state, pickedRecipe);

               for(ItemStack remainingItem : (pickedRecipe.value()).getRemainingItems(var11)) {
                  if (!remainingItem.isEmpty()) {
                     this.dispenseItem(level, pos, blockEntity, remainingItem, state, pickedRecipe);
                  }
               }

               blockEntity.getItems().forEach((it) -> {
                  if (!it.isEmpty()) {
                     it.shrink(1);
                  }
               });
               blockEntity.setChanged();
            }
         }
      }
   }

   public static Optional<RecipeHolder<CraftingRecipe>> getPotentialResults(final ServerLevel level, final CraftingInput input) {
      return RECIPE_CACHE.get(level, input);
   }

   private void dispenseItem(final ServerLevel level, final BlockPos pos, final CrafterBlockEntity blockEntity, final ItemStack results, final BlockState blockState, final RecipeHolder<?> recipe) {
      Direction direction = ((FrontAndTop)blockState.getValue(ORIENTATION)).front();
      Container into = HopperBlockEntity.getContainerAt(level, pos.relative(direction));
      ItemStack remaining = results.copy();
      if (into != null && (into instanceof CrafterBlockEntity || results.getCount() > into.getMaxStackSize(results))) {
         while(!remaining.isEmpty()) {
            ItemStack copy = remaining.copyWithCount(1);
            ItemStack itemStack = HopperBlockEntity.addItem(blockEntity, into, copy, direction.getOpposite());
            if (!itemStack.isEmpty()) {
               break;
            }

            remaining.shrink(1);
         }
      } else if (into != null) {
         while(!remaining.isEmpty()) {
            int oldSize = remaining.getCount();
            remaining = HopperBlockEntity.addItem(blockEntity, into, remaining, direction.getOpposite());
            if (oldSize == remaining.getCount()) {
               break;
            }
         }
      }

      if (!remaining.isEmpty()) {
         Vec3 centerPos = Vec3.atCenterOf(pos);
         Vec3 itemSpawnOffset = centerPos.relative(direction, 0.7);
         DefaultDispenseItemBehavior.spawnItem(level, remaining, 6, direction, itemSpawnOffset);

         for(ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, AABB.ofSize(centerPos, 17.0, 17.0, 17.0))) {
            CriteriaTriggers.CRAFTER_RECIPE_CRAFTED.trigger(player, recipe.id(), blockEntity.getItems());
         }

         level.levelEvent(1049, pos, 0);
         level.levelEvent(2010, pos, direction.get3DDataValue());
      }

   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(ORIENTATION, rotation.rotation().rotate((FrontAndTop)state.getValue(ORIENTATION)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return (BlockState)state.setValue(ORIENTATION, mirror.rotation().rotate((FrontAndTop)state.getValue(ORIENTATION)));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(ORIENTATION, TRIGGERED, CRAFTING);
   }

   static {
      CRAFTING = BlockStateProperties.CRAFTING;
      TRIGGERED = BlockStateProperties.TRIGGERED;
      ORIENTATION = BlockStateProperties.ORIENTATION;
      RECIPE_CACHE = new RecipeCache(10);
   }
}
