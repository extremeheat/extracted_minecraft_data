package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import javax.annotation.Nullable;
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

   public ChiseledBookShelfBlock(BlockBehaviour.Properties var1) {
      super(var1);
      BlockState var2 = (BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH);

      for(BooleanProperty var4 : SLOT_OCCUPIED_PROPERTIES) {
         var2 = (BlockState)var2.setValue(var4, false);
      }

      this.registerDefaultState(var2);
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      BlockEntity var9 = var3.getBlockEntity(var4);
      if (var9 instanceof ChiseledBookShelfBlockEntity var8) {
         if (!var1.is(ItemTags.BOOKSHELF_BOOKS)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         } else {
            OptionalInt var10 = this.getHitSlot(var7, (Direction)var2.getValue(FACING));
            if (var10.isEmpty()) {
               return InteractionResult.PASS;
            } else if ((Boolean)var2.getValue((Property)SLOT_OCCUPIED_PROPERTIES.get(var10.getAsInt()))) {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            } else {
               addBook(var3, var4, var5, var8, var1, var10.getAsInt());
               return InteractionResult.SUCCESS;
            }
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      BlockEntity var7 = var2.getBlockEntity(var3);
      if (var7 instanceof ChiseledBookShelfBlockEntity var6) {
         OptionalInt var8 = this.getHitSlot(var5, (Direction)var1.getValue(FACING));
         if (var8.isEmpty()) {
            return InteractionResult.PASS;
         } else if (!(Boolean)var1.getValue((Property)SLOT_OCCUPIED_PROPERTIES.get(var8.getAsInt()))) {
            return InteractionResult.CONSUME;
         } else {
            removeBook(var2, var3, var4, var6, var8.getAsInt());
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private static void addBook(Level var0, BlockPos var1, Player var2, ChiseledBookShelfBlockEntity var3, ItemStack var4, int var5) {
      if (!var0.isClientSide()) {
         var2.awardStat(Stats.ITEM_USED.get(var4.getItem()));
         SoundEvent var6 = var4.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
         var3.setItem(var5, var4.consumeAndReturn(1, var2));
         var0.playSound((Entity)null, (BlockPos)var1, var6, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   private static void removeBook(Level var0, BlockPos var1, Player var2, ChiseledBookShelfBlockEntity var3, int var4) {
      if (!var0.isClientSide()) {
         ItemStack var5 = var3.removeItem(var4, 1);
         SoundEvent var6 = var5.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
         var0.playSound((Entity)null, (BlockPos)var1, var6, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!var2.getInventory().add(var5)) {
            var2.drop(var5, false);
         }

         var0.gameEvent(var2, GameEvent.BLOCK_CHANGE, var1);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new ChiseledBookShelfBlockEntity(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
      List var10000 = SLOT_OCCUPIED_PROPERTIES;
      Objects.requireNonNull(var1);
      var10000.forEach((var1x) -> var1.add(var1x));
   }

   protected void affectNeighborsAfterRemoval(BlockState var1, ServerLevel var2, BlockPos var3, boolean var4) {
      Containers.updateNeighboursAfterDestroy(var1, var2, var3);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3, Direction var4) {
      if (var2.isClientSide()) {
         return 0;
      } else {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof ChiseledBookShelfBlockEntity) {
            ChiseledBookShelfBlockEntity var5 = (ChiseledBookShelfBlockEntity)var6;
            return var5.getLastInteractedSlot() + 1;
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
