package net.minecraft.world.item;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BlockItem extends Item {
   /** @deprecated */
   @Deprecated
   private final Block block;

   public BlockItem(Block var1, Item.Properties var2) {
      super(var2);
      this.block = var1;
   }

   public InteractionResult useOn(UseOnContext var1) {
      InteractionResult var2 = this.place(new BlockPlaceContext(var1));
      if (!var2.consumesAction() && var1.getItemInHand().has(DataComponents.FOOD)) {
         InteractionResult var3 = super.use(var1.getLevel(), var1.getPlayer(), var1.getHand()).getResult();
         return var3 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : var3;
      } else {
         return var2;
      }
   }

   public InteractionResult place(BlockPlaceContext var1) {
      if (!this.getBlock().isEnabled(var1.getLevel().enabledFeatures())) {
         return InteractionResult.FAIL;
      } else if (!var1.canPlace()) {
         return InteractionResult.FAIL;
      } else {
         BlockPlaceContext var2 = this.updatePlacementContext(var1);
         if (var2 == null) {
            return InteractionResult.FAIL;
         } else {
            BlockState var3 = this.getPlacementState(var2);
            if (var3 == null) {
               return InteractionResult.FAIL;
            } else if (!this.placeBlock(var2, var3)) {
               return InteractionResult.FAIL;
            } else {
               BlockPos var4 = var2.getClickedPos();
               Level var5 = var2.getLevel();
               Player var6 = var2.getPlayer();
               ItemStack var7 = var2.getItemInHand();
               BlockState var8 = var5.getBlockState(var4);
               if (var8.is(var3.getBlock())) {
                  var8 = this.updateBlockStateFromTag(var4, var5, var7, var8);
                  this.updateCustomBlockEntityTag(var4, var5, var6, var7, var8);
                  updateBlockEntityComponents(var5, var4, var7);
                  var8.getBlock().setPlacedBy(var5, var4, var8, var6, var7);
                  if (var6 instanceof ServerPlayer) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var6, var4, var7);
                  }
               }

               SoundType var9 = var8.getSoundType();
               var5.playSound(var6, var4, this.getPlaceSound(var8), SoundSource.BLOCKS, (var9.getVolume() + 1.0F) / 2.0F, var9.getPitch() * 0.8F);
               var5.gameEvent(GameEvent.BLOCK_PLACE, var4, GameEvent.Context.of(var6, var8));
               var7.consume(1, var6);
               return InteractionResult.sidedSuccess(var5.isClientSide);
            }
         }
      }
   }

   protected SoundEvent getPlaceSound(BlockState var1) {
      return var1.getSoundType().getPlaceSound();
   }

   @Nullable
   public BlockPlaceContext updatePlacementContext(BlockPlaceContext var1) {
      return var1;
   }

   private static void updateBlockEntityComponents(Level var0, BlockPos var1, ItemStack var2) {
      BlockEntity var3 = var0.getBlockEntity(var1);
      if (var3 != null) {
         var3.applyComponentsFromItemStack(var2);
         var3.setChanged();
      }

   }

   protected boolean updateCustomBlockEntityTag(BlockPos var1, Level var2, @Nullable Player var3, ItemStack var4, BlockState var5) {
      return updateCustomBlockEntityTag(var2, var3, var1, var4);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext var1) {
      BlockState var2 = this.getBlock().getStateForPlacement(var1);
      return var2 != null && this.canPlace(var1, var2) ? var2 : null;
   }

   private BlockState updateBlockStateFromTag(BlockPos var1, Level var2, ItemStack var3, BlockState var4) {
      BlockItemStateProperties var5 = (BlockItemStateProperties)var3.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY);
      if (var5.isEmpty()) {
         return var4;
      } else {
         BlockState var6 = var5.apply(var4);
         if (var6 != var4) {
            var2.setBlock(var1, var6, 2);
         }

         return var6;
      }
   }

   protected boolean canPlace(BlockPlaceContext var1, BlockState var2) {
      Player var3 = var1.getPlayer();
      CollisionContext var4 = var3 == null ? CollisionContext.empty() : CollisionContext.of(var3);
      return (!this.mustSurvive() || var2.canSurvive(var1.getLevel(), var1.getClickedPos())) && var1.getLevel().isUnobstructed(var2, var1.getClickedPos(), var4);
   }

   protected boolean mustSurvive() {
      return true;
   }

   protected boolean placeBlock(BlockPlaceContext var1, BlockState var2) {
      return var1.getLevel().setBlock(var1.getClickedPos(), var2, 11);
   }

   public static boolean updateCustomBlockEntityTag(Level var0, @Nullable Player var1, BlockPos var2, ItemStack var3) {
      MinecraftServer var4 = var0.getServer();
      if (var4 == null) {
         return false;
      } else {
         CustomData var5 = (CustomData)var3.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
         if (!var5.isEmpty()) {
            BlockEntity var6 = var0.getBlockEntity(var2);
            if (var6 != null) {
               if (var0.isClientSide || !var6.onlyOpCanSetNbt() || var1 != null && var1.canUseGameMasterBlocks()) {
                  return var5.loadInto(var6, var0.registryAccess());
               }

               return false;
            }
         }

         return false;
      }
   }

   public String getDescriptionId() {
      return this.getBlock().getDescriptionId();
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      this.getBlock().appendHoverText(var1, var2, var3, var4);
   }

   public Block getBlock() {
      return this.block;
   }

   public void registerBlocks(Map<Block, Item> var1, Item var2) {
      var1.put(this.getBlock(), var2);
   }

   public boolean canFitInsideContainerItems() {
      return !(this.getBlock() instanceof ShulkerBoxBlock);
   }

   public void onDestroyed(ItemEntity var1) {
      ItemContainerContents var2 = (ItemContainerContents)var1.getItem().set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
      if (var2 != null) {
         ItemUtils.onContainerDestroyed(var1, var2.nonEmptyItemsCopy());
      }

   }

   public static void setBlockEntityData(ItemStack var0, BlockEntityType<?> var1, CompoundTag var2) {
      var2.remove("id");
      if (var2.isEmpty()) {
         var0.remove(DataComponents.BLOCK_ENTITY_DATA);
      } else {
         BlockEntity.addEntityType(var2, var1);
         var0.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(var2));
      }

   }

   public FeatureFlagSet requiredFeatures() {
      return this.getBlock().requiredFeatures();
   }
}
