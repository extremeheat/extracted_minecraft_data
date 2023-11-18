package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
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

   public ChiseledBookShelfBlock(BlockBehaviour.Properties var1) {
      super(var1);
      BlockState var2 = this.stateDefinition.any().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);

      for(BooleanProperty var4 : SLOT_OCCUPIED_PROPERTIES) {
         var2 = var2.setValue(var4, Boolean.valueOf(false));
      }

      this.registerDefaultState(var2);
   }

   @Override
   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockEntity var8 = var2.getBlockEntity(var3);
      if (var8 instanceof ChiseledBookShelfBlockEntity var7) {
         Optional var11 = getRelativeHitCoordinatesForBlockFace(var6, var1.getValue(HorizontalDirectionalBlock.FACING));
         if (var11.isEmpty()) {
            return InteractionResult.PASS;
         } else {
            int var9 = getHitSlot((Vec2)var11.get());
            if (var1.getValue(SLOT_OCCUPIED_PROPERTIES.get(var9))) {
               removeBook(var2, var3, var4, (ChiseledBookShelfBlockEntity)var7, var9);
               return InteractionResult.sidedSuccess(var2.isClientSide);
            } else {
               ItemStack var10 = var4.getItemInHand(var5);
               if (var10.is(ItemTags.BOOKSHELF_BOOKS)) {
                  addBook(var2, var3, var4, (ChiseledBookShelfBlockEntity)var7, var10, var9);
                  return InteractionResult.sidedSuccess(var2.isClientSide);
               } else {
                  return InteractionResult.CONSUME;
               }
            }
         }
      } else {
         return InteractionResult.PASS;
      }
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

         return switch(var2) {
            case NORTH -> Optional.of(new Vec2((float)(1.0 - var5), (float)var7));
            case SOUTH -> Optional.of(new Vec2((float)var5, (float)var7));
            case WEST -> Optional.of(new Vec2((float)var9, (float)var7));
            case EAST -> Optional.of(new Vec2((float)(1.0 - var9), (float)var7));
            case DOWN, UP -> Optional.empty();
         };
      }
   }

   private static int getHitSlot(Vec2 var0) {
      int var1 = var0.y >= 0.5F ? 0 : 1;
      int var2 = getSection(var0.x);
      return var2 + var1 * 3;
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
         var3.setItem(var5, var4.split(1));
         var0.playSound(null, var1, var6, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (var2.isCreative()) {
            var4.grow(1);
         }
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

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof ChiseledBookShelfBlockEntity var7 && !var7.isEmpty()) {
            for(int var8 = 0; var8 < 6; ++var8) {
               ItemStack var9 = var7.getItem(var8);
               if (!var9.isEmpty()) {
                  Containers.dropItemStack(var2, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), var9);
               }
            }

            var7.clearContent();
            var2.updateNeighbourForOutputSignal(var3, this);
         }

         super.onRemove(var1, var2, var3, var4, var5);
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
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      if (var2.isClientSide()) {
         return 0;
      } else {
         BlockEntity var5 = var2.getBlockEntity(var3);
         return var5 instanceof ChiseledBookShelfBlockEntity var4 ? var4.getLastInteractedSlot() + 1 : 0;
      }
   }
}
