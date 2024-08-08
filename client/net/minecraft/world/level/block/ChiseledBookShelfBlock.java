package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class ChiseledBookShelfBlock extends BaseEntityBlock {
   public static final MapCodec<ChiseledBookShelfBlock> CODEC = simpleCodec(ChiseledBookShelfBlock::new);
   private static final int MAX_BOOKS_IN_STORAGE = 6;
   public static final int BOOKS_PER_ROW = 3;
   public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of(
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED,
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED,
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED,
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED,
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED,
      BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED
   );

   @Override
   public MapCodec<ChiseledBookShelfBlock> codec() {
      return CODEC;
   }

   public ChiseledBookShelfBlock(BlockBehaviour.Properties var1) {
      super(var1);
      BlockState var2 = this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);

      for (BooleanProperty var4 : SLOT_OCCUPIED_PROPERTIES) {
         var2 = var2.setValue(var4, Boolean.valueOf(false));
      }

      this.registerDefaultState(var2);
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var3.getBlockEntity(var4) instanceof ChiseledBookShelfBlockEntity var8) {
         if (!var1.is(ItemTags.BOOKSHELF_BOOKS)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         } else {
            OptionalInt var10 = this.getHitSlot(var7, var2);
            if (var10.isEmpty()) {
               return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
            } else if (var2.getValue(SLOT_OCCUPIED_PROPERTIES.get(var10.getAsInt()))) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else {
               addBook(var3, var4, var5, var8, var1, var10.getAsInt());
               return ItemInteractionResult.sidedSuccess(var3.isClientSide);
            }
         }
      } else {
         return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.getBlockEntity(var3) instanceof ChiseledBookShelfBlockEntity var6) {
         OptionalInt var8 = this.getHitSlot(var5, var1);
         if (var8.isEmpty()) {
            return InteractionResult.PASS;
         } else if (!var1.getValue(SLOT_OCCUPIED_PROPERTIES.get(var8.getAsInt()))) {
            return InteractionResult.CONSUME;
         } else {
            removeBook(var2, var3, var4, var6, var8.getAsInt());
            return InteractionResult.sidedSuccess(var2.isClientSide);
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private OptionalInt getHitSlot(BlockHitResult var1, BlockState var2) {
      return getRelativeHitCoordinatesForBlockFace(var1, var2.getValue(HorizontalDirectionalBlock.FACING)).map(var0 -> {
         int var1x = var0.y >= 0.5F ? 0 : 1;
         int var2x = getSection(var0.x);
         return OptionalInt.of(var2x + var1x * 3);
      }).orElseGet(OptionalInt::empty);
   }

   private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult var0, Direction var1) {
      Direction var2 = var0.getDirection();
      if (var1 != var2) {
         return Optional.empty();
      } else {
         BlockPos var3 = var0.getBlockPos().relative(var2);
         Vec3 var4 = var0.getLocation().subtract((double)var3.getX(), (double)var3.getY(), (double)var3.getZ());
         double var5 = var4.x();
         double var7 = var4.y();
         double var9 = var4.z();

         return switch (var2) {
            case NORTH -> Optional.of(new Vec2((float)(1.0 - var5), (float)var7));
            case SOUTH -> Optional.of(new Vec2((float)var5, (float)var7));
            case WEST -> Optional.of(new Vec2((float)var9, (float)var7));
            case EAST -> Optional.of(new Vec2((float)(1.0 - var9), (float)var7));
            case DOWN, UP -> Optional.empty();
         };
      }
   }

   private static int getSection(float var0) {
      float var1 = 0.0625F;
      float var2 = 0.375F;
      if (var0 < 0.375F) {
         return 0;
      } else {
         float var3 = 0.6875F;
         return var0 < 0.6875F ? 1 : 2;
      }
   }

   private static void addBook(Level var0, BlockPos var1, Player var2, ChiseledBookShelfBlockEntity var3, ItemStack var4, int var5) {
      if (!var0.isClientSide) {
         var2.awardStat(Stats.ITEM_USED.get(var4.getItem()));
         SoundEvent var6 = var4.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
         var3.setItem(var5, var4.consumeAndReturn(1, var2));
         var0.playSound(null, var1, var6, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   private static void removeBook(Level var0, BlockPos var1, Player var2, ChiseledBookShelfBlockEntity var3, int var4) {
      if (!var0.isClientSide) {
         ItemStack var5 = var3.removeItem(var4, 1);
         SoundEvent var6 = var5.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
         var0.playSound(null, var1, var6, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!var2.getInventory().add(var5)) {
            var2.drop(var5, false);
         }

         var0.gameEvent(var2, GameEvent.BLOCK_CHANGE, var1);
      }
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new ChiseledBookShelfBlockEntity(var1, var2);
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HorizontalDirectionalBlock.FACING);
      SLOT_OCCUPIED_PROPERTIES.forEach(var1x -> var1.add(var1x));
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         boolean var6;
         label32: {
            if (var2.getBlockEntity(var3) instanceof ChiseledBookShelfBlockEntity var8 && !var8.isEmpty()) {
               for (int var9 = 0; var9 < 6; var9++) {
                  ItemStack var10 = var8.getItem(var9);
                  if (!var10.isEmpty()) {
                     Containers.dropItemStack(var2, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), var10);
                  }
               }

               var8.clearContent();
               var6 = true;
               break label32;
            }

            var6 = false;
         }

         super.onRemove(var1, var2, var3, var4, var5);
         if (var6) {
            var2.updateNeighbourForOutputSignal(var3, this);
         }
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, var1.getHorizontalDirection().getOpposite());
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(HorizontalDirectionalBlock.FACING, var2.rotate(var1.getValue(HorizontalDirectionalBlock.FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(HorizontalDirectionalBlock.FACING)));
   }

   @Override
   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      if (var2.isClientSide()) {
         return 0;
      } else {
         return var2.getBlockEntity(var3) instanceof ChiseledBookShelfBlockEntity var4 ? var4.getLastInteractedSlot() + 1 : 0;
      }
   }
}
