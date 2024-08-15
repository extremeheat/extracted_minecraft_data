package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DecoratedPotBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<DecoratedPotBlock> CODEC = simpleCodec(DecoratedPotBlock::new);
   public static final ResourceLocation SHERDS_DYNAMIC_DROP_ID = ResourceLocation.withDefaultNamespace("sherds");
   private static final VoxelShape BOUNDING_BOX = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
   private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty CRACKED = BlockStateProperties.CRACKED;
   private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

   @Override
   public MapCodec<DecoratedPotBlock> codec() {
      return CODEC;
   }

   protected DecoratedPotBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(HORIZONTAL_FACING, Direction.NORTH)
            .setValue(WATERLOGGED, Boolean.valueOf(false))
            .setValue(CRACKED, Boolean.valueOf(false))
      );
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return this.defaultBlockState()
         .setValue(HORIZONTAL_FACING, var1.getHorizontalDirection())
         .setValue(WATERLOGGED, Boolean.valueOf(var2.getType() == Fluids.WATER))
         .setValue(CRACKED, Boolean.valueOf(false));
   }

   @Override
   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var3.getBlockEntity(var4) instanceof DecoratedPotBlockEntity var8) {
         if (var3.isClientSide) {
            return InteractionResult.SUCCESS;
         } else {
            ItemStack var13 = var8.getTheItem();
            if (!var1.isEmpty() && (var13.isEmpty() || ItemStack.isSameItemSameComponents(var13, var1) && var13.getCount() < var13.getMaxStackSize())) {
               var8.wobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
               var5.awardStat(Stats.ITEM_USED.get(var1.getItem()));
               ItemStack var10 = var1.consumeAndReturn(1, var5);
               float var11;
               if (var8.isEmpty()) {
                  var8.setTheItem(var10);
                  var11 = (float)var10.getCount() / (float)var10.getMaxStackSize();
               } else {
                  var13.grow(1);
                  var11 = (float)var13.getCount() / (float)var13.getMaxStackSize();
               }

               var3.playSound(null, var4, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * var11);
               if (var3 instanceof ServerLevel var12) {
                  var12.sendParticles(
                     ParticleTypes.DUST_PLUME, (double)var4.getX() + 0.5, (double)var4.getY() + 1.2, (double)var4.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0
                  );
               }

               var8.setChanged();
               var3.gameEvent(var5, GameEvent.BLOCK_CHANGE, var4);
               return InteractionResult.SUCCESS;
            } else {
               return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2.getBlockEntity(var3) instanceof DecoratedPotBlockEntity var6) {
         var2.playSound(null, var3, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F);
         var6.wobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
         var2.gameEvent(var4, GameEvent.BLOCK_CHANGE, var3);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return BOUNDING_BOX;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HORIZONTAL_FACING, WATERLOGGED, CRACKED);
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new DecoratedPotBlockEntity(var1, var2);
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   @Override
   protected List<ItemStack> getDrops(BlockState var1, LootParams.Builder var2) {
      BlockEntity var3 = var2.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (var3 instanceof DecoratedPotBlockEntity var4) {
         var2.withDynamicDrop(SHERDS_DYNAMIC_DROP_ID, var1x -> {
            for (Item var3x : var4.getDecorations().ordered()) {
               var1x.accept(var3x.getDefaultInstance());
            }
         });
      }

      return super.getDrops(var1, var2);
   }

   @Override
   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      ItemStack var5 = var4.getMainHandItem();
      BlockState var6 = var3;
      if (var5.is(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasTag(var5, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING)) {
         var6 = var3.setValue(CRACKED, Boolean.valueOf(true));
         var1.setBlock(var2, var6, 4);
      }

      return super.playerWillDestroy(var1, var2, var6, var4);
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   protected SoundType getSoundType(BlockState var1) {
      return var1.getValue(CRACKED) ? SoundType.DECORATED_POT_CRACKED : SoundType.DECORATED_POT;
   }

   @Override
   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      PotDecorations var5 = var1.getOrDefault(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY);
      if (!var5.equals(PotDecorations.EMPTY)) {
         var3.add(CommonComponents.EMPTY);
         Stream.of(var5.front(), var5.left(), var5.right(), var5.back())
            .forEach(var1x -> var3.add(new ItemStack(var1x.orElse(Items.BRICK), 1).getHoverName().plainCopy().withStyle(ChatFormatting.GRAY)));
      }
   }

   @Override
   protected void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      BlockPos var5 = var3.getBlockPos();
      if (!var1.isClientSide && var4.mayInteract(var1, var5) && var4.mayBreak(var1)) {
         var1.setBlock(var5, var2.setValue(CRACKED, Boolean.valueOf(true)), 4);
         var1.destroyBlock(var5, true, var4);
      }
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockEntity(var2) instanceof DecoratedPotBlockEntity var4 ? var4.getPotAsItem() : super.getCloneItemStack(var1, var2, var3);
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
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(HORIZONTAL_FACING, var2.rotate(var1.getValue(HORIZONTAL_FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(HORIZONTAL_FACING)));
   }
}
