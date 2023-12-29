package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
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
   public static final ResourceLocation SHERDS_DYNAMIC_DROP_ID = new ResourceLocation("sherds");
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
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
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

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      BlockEntity var8 = var2.getBlockEntity(var3);
      if (var8 instanceof DecoratedPotBlockEntity var7) {
         if (var2.isClientSide) {
            return InteractionResult.CONSUME;
         } else {
            ItemStack var13 = var4.getItemInHand(var5);
            ItemStack var9 = var7.getTheItem();
            if (!var13.isEmpty() && (var9.isEmpty() || ItemStack.isSameItemSameTags(var9, var13) && var9.getCount() < var9.getMaxStackSize())) {
               var7.wobble(DecoratedPotBlockEntity.WobbleStyle.POSITIVE);
               var4.awardStat(Stats.ITEM_USED.get(var13.getItem()));
               ItemStack var10 = var4.isCreative() ? var13.copyWithCount(1) : var13.split(1);
               float var11;
               if (var7.isEmpty()) {
                  var7.setTheItem(var10);
                  var11 = (float)var10.getCount() / (float)var10.getMaxStackSize();
               } else {
                  var9.grow(1);
                  var11 = (float)var9.getCount() / (float)var9.getMaxStackSize();
               }

               var2.playSound(null, var3, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * var11);
               if (var2 instanceof ServerLevel var12) {
                  var12.sendParticles(
                     ParticleTypes.DUST_PLUME, (double)var3.getX() + 0.5, (double)var3.getY() + 1.2, (double)var3.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0
                  );
               }

               var2.updateNeighbourForOutputSignal(var3, this);
            } else {
               var2.playSound(null, var3, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1.0F, 1.0F);
               var7.wobble(DecoratedPotBlockEntity.WobbleStyle.NEGATIVE);
            }

            var2.gameEvent(var4, GameEvent.BLOCK_CHANGE, var3);
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      if (var1.isClientSide) {
         var1.getBlockEntity(var2, BlockEntityType.DECORATED_POT).ifPresent(var1x -> var1x.setFromItem(var5));
      }
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
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
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   @Override
   public List<ItemStack> getDrops(BlockState var1, LootParams.Builder var2) {
      BlockEntity var3 = var2.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (var3 instanceof DecoratedPotBlockEntity var4) {
         var2.withDynamicDrop(SHERDS_DYNAMIC_DROP_ID, var1x -> var4.getDecorations().sorted().map(Item::getDefaultInstance).forEach(var1x));
      }

      return super.getDrops(var1, var2);
   }

   @Override
   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      ItemStack var5 = var4.getMainHandItem();
      BlockState var6 = var3;
      if (var5.is(ItemTags.BREAKS_DECORATED_POTS) && !EnchantmentHelper.hasSilkTouch(var5)) {
         var6 = var3.setValue(CRACKED, Boolean.valueOf(true));
         var1.setBlock(var2, var6, 4);
      }

      return super.playerWillDestroy(var1, var2, var6, var4);
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public SoundType getSoundType(BlockState var1) {
      return var1.getValue(CRACKED) ? SoundType.DECORATED_POT_CRACKED : SoundType.DECORATED_POT;
   }

   @Override
   public void appendHoverText(ItemStack var1, @Nullable BlockGetter var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      DecoratedPotBlockEntity.Decorations var5 = DecoratedPotBlockEntity.Decorations.load(BlockItem.getBlockEntityData(var1));
      if (!var5.equals(DecoratedPotBlockEntity.Decorations.EMPTY)) {
         var3.add(CommonComponents.EMPTY);
         Stream.of(var5.front(), var5.left(), var5.right(), var5.back())
            .forEach(var1x -> var3.add(new ItemStack(var1x, 1).getHoverName().plainCopy().withStyle(ChatFormatting.GRAY)));
      }
   }

   @Override
   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      BlockPos var5 = var3.getBlockPos();
      if (!var1.isClientSide && var4.mayInteract(var1, var5) && var4.mayBreak(var1)) {
         var1.setBlock(var5, var2.setValue(CRACKED, Boolean.valueOf(true)), 4);
         var1.destroyBlock(var5, true, var4);
      }
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      BlockEntity var5 = var1.getBlockEntity(var2);
      return var5 instanceof DecoratedPotBlockEntity var4 ? var4.getPotAsItem() : super.getCloneItemStack(var1, var2, var3);
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   @Override
   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }
}
