package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

public class ChiseledBookShelfBlock extends BaseEntityBlock implements SelectableSlotContainer {
   public static final MapCodec<ChiseledBookShelfBlock> CODEC = simpleCodec(ChiseledBookShelfBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final BooleanProperty SLOT_0_OCCUPIED;
   public static final BooleanProperty SLOT_1_OCCUPIED;
   public static final BooleanProperty SLOT_2_OCCUPIED;
   public static final BooleanProperty SLOT_3_OCCUPIED;
   public static final BooleanProperty SLOT_4_OCCUPIED;
   public static final BooleanProperty SLOT_5_OCCUPIED;
   private static final int MAX_BOOKS_IN_STORAGE = 6;
   private static final int BOOKS_PER_ROW = 3;
   public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES;

   public MapCodec<ChiseledBookShelfBlock> codec() {
      return CODEC;
   }

   public int getRows() {
      return 2;
   }

   public int getColumns() {
      return 3;
   }

   public ChiseledBookShelfBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      BlockState defaultState = (BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH);

      for(BooleanProperty property : SLOT_OCCUPIED_PROPERTIES) {
         defaultState = (BlockState)defaultState.setValue(property, false);
      }

      this.registerDefaultState(defaultState);
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      BlockEntity var9 = level.getBlockEntity(pos);
      if (var9 instanceof ChiseledBookShelfBlockEntity bookshelfBlock) {
         if (!itemStack.is(ItemTags.BOOKSHELF_BOOKS)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         } else {
            OptionalInt hitSlot = this.getHitSlot(hitResult, (Direction)state.getValue(FACING));
            if (hitSlot.isEmpty()) {
               return InteractionResult.PASS;
            } else if ((Boolean)state.getValue((Property)SLOT_OCCUPIED_PROPERTIES.get(hitSlot.getAsInt()))) {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            } else {
               addBook(level, pos, player, bookshelfBlock, itemStack, hitSlot.getAsInt());
               return InteractionResult.SUCCESS;
            }
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      BlockEntity var7 = level.getBlockEntity(pos);
      if (var7 instanceof ChiseledBookShelfBlockEntity bookshelfBlock) {
         OptionalInt hitSlot = this.getHitSlot(hitResult, (Direction)state.getValue(FACING));
         if (hitSlot.isEmpty()) {
            return InteractionResult.PASS;
         } else if (!(Boolean)state.getValue((Property)SLOT_OCCUPIED_PROPERTIES.get(hitSlot.getAsInt()))) {
            return InteractionResult.CONSUME;
         } else {
            removeBook(level, pos, player, bookshelfBlock, hitSlot.getAsInt());
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private static void addBook(final Level level, final BlockPos pos, final Player player, final ChiseledBookShelfBlockEntity bookshelfBlock, final ItemStack itemStack, final int slot) {
      if (!level.isClientSide()) {
         player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
         SoundEvent soundEvent = itemStack.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
         bookshelfBlock.setItem(slot, itemStack.consumeAndReturn(1, player));
         level.playSound((Entity)null, (BlockPos)pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   private static void removeBook(final Level level, final BlockPos pos, final Player player, final ChiseledBookShelfBlockEntity bookshelfBlock, final int slot) {
      if (!level.isClientSide()) {
         ItemStack retrievedBook = bookshelfBlock.removeItem(slot, 1);
         SoundEvent soundEvent = retrievedBook.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
         level.playSound((Entity)null, (BlockPos)pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!player.getInventory().add(retrievedBook)) {
            player.drop(retrievedBook, false);
         }

         level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
      }
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new ChiseledBookShelfBlockEntity(worldPosition, blockState);
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING);
      List var10000 = SLOT_OCCUPIED_PROPERTIES;
      Objects.requireNonNull(builder);
      var10000.forEach((xva$0) -> builder.add(xva$0));
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      Containers.updateNeighboursAfterDestroy(state, level, pos);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected boolean hasAnalogOutputSignal(final BlockState state) {
      return true;
   }

   protected int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos, final Direction direction) {
      if (level.isClientSide()) {
         return 0;
      } else {
         BlockEntity var6 = level.getBlockEntity(pos);
         if (var6 instanceof ChiseledBookShelfBlockEntity) {
            ChiseledBookShelfBlockEntity blockEntity = (ChiseledBookShelfBlockEntity)var6;
            return blockEntity.getLastInteractedSlot() + 1;
         } else {
            return 0;
         }
      }
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      SLOT_0_OCCUPIED = BlockStateProperties.SLOT_0_OCCUPIED;
      SLOT_1_OCCUPIED = BlockStateProperties.SLOT_1_OCCUPIED;
      SLOT_2_OCCUPIED = BlockStateProperties.SLOT_2_OCCUPIED;
      SLOT_3_OCCUPIED = BlockStateProperties.SLOT_3_OCCUPIED;
      SLOT_4_OCCUPIED = BlockStateProperties.SLOT_4_OCCUPIED;
      SLOT_5_OCCUPIED = BlockStateProperties.SLOT_5_OCCUPIED;
      SLOT_OCCUPIED_PROPERTIES = List.of(SLOT_0_OCCUPIED, SLOT_1_OCCUPIED, SLOT_2_OCCUPIED, SLOT_3_OCCUPIED, SLOT_4_OCCUPIED, SLOT_5_OCCUPIED);
   }
}
