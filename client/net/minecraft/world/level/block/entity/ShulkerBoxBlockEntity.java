package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShulkerBoxBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
   public static final int COLUMNS = 9;
   public static final int ROWS = 3;
   public static final int CONTAINER_SIZE = 27;
   public static final int EVENT_SET_OPEN_COUNT = 1;
   public static final int OPENING_TICK_LENGTH = 10;
   public static final float MAX_LID_HEIGHT = 0.5F;
   public static final float MAX_LID_ROTATION = 270.0F;
   private static final int[] SLOTS = IntStream.range(0, 27).toArray();
   private NonNullList<ItemStack> itemStacks;
   private int openCount;
   private AnimationStatus animationStatus;
   private float progress;
   private float progressOld;
   @Nullable
   private final DyeColor color;

   public ShulkerBoxBlockEntity(@Nullable DyeColor var1, BlockPos var2, BlockState var3) {
      super(BlockEntityType.SHULKER_BOX, var2, var3);
      this.itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
      this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
      this.color = var1;
   }

   public ShulkerBoxBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.SHULKER_BOX, var1, var2);
      this.itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
      this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
      this.color = ShulkerBoxBlock.getColorFromBlock(var2.getBlock());
   }

   public static void tick(Level var0, BlockPos var1, BlockState var2, ShulkerBoxBlockEntity var3) {
      var3.updateAnimation(var0, var1, var2);
   }

   private void updateAnimation(Level var1, BlockPos var2, BlockState var3) {
      this.progressOld = this.progress;
      switch (this.animationStatus.ordinal()) {
         case 0:
            this.progress = 0.0F;
            break;
         case 1:
            this.progress += 0.1F;
            if (this.progressOld == 0.0F) {
               doNeighborUpdates(var1, var2, var3);
            }

            if (this.progress >= 1.0F) {
               this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
               this.progress = 1.0F;
               doNeighborUpdates(var1, var2, var3);
            }

            this.moveCollidedEntities(var1, var2, var3);
            break;
         case 2:
            this.progress = 1.0F;
            break;
         case 3:
            this.progress -= 0.1F;
            if (this.progressOld == 1.0F) {
               doNeighborUpdates(var1, var2, var3);
            }

            if (this.progress <= 0.0F) {
               this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
               this.progress = 0.0F;
               doNeighborUpdates(var1, var2, var3);
            }
      }

   }

   public AnimationStatus getAnimationStatus() {
      return this.animationStatus;
   }

   public AABB getBoundingBox(BlockState var1) {
      return Shulker.getProgressAabb(1.0F, (Direction)var1.getValue(ShulkerBoxBlock.FACING), 0.5F * this.getProgress(1.0F));
   }

   private void moveCollidedEntities(Level var1, BlockPos var2, BlockState var3) {
      if (var3.getBlock() instanceof ShulkerBoxBlock) {
         Direction var4 = (Direction)var3.getValue(ShulkerBoxBlock.FACING);
         AABB var5 = Shulker.getProgressDeltaAabb(1.0F, var4, this.progressOld, this.progress).move(var2);
         List var6 = var1.getEntities((Entity)null, var5);
         if (!var6.isEmpty()) {
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               Entity var8 = (Entity)var7.next();
               if (var8.getPistonPushReaction() != PushReaction.IGNORE) {
                  var8.move(MoverType.SHULKER_BOX, new Vec3((var5.getXsize() + 0.01) * (double)var4.getStepX(), (var5.getYsize() + 0.01) * (double)var4.getStepY(), (var5.getZsize() + 0.01) * (double)var4.getStepZ()));
               }
            }

         }
      }
   }

   public int getContainerSize() {
      return this.itemStacks.size();
   }

   public boolean triggerEvent(int var1, int var2) {
      if (var1 == 1) {
         this.openCount = var2;
         if (var2 == 0) {
            this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSING;
         }

         if (var2 == 1) {
            this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
         }

         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   private static void doNeighborUpdates(Level var0, BlockPos var1, BlockState var2) {
      var2.updateNeighbourShapes(var0, var1, 3);
      var0.updateNeighborsAt(var1, var2.getBlock());
   }

   public void startOpen(Player var1) {
      if (!this.remove && !var1.isSpectator()) {
         if (this.openCount < 0) {
            this.openCount = 0;
         }

         ++this.openCount;
         this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount == 1) {
            this.level.gameEvent(var1, GameEvent.CONTAINER_OPEN, this.worldPosition);
            this.level.playSound((Player)null, (BlockPos)this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public void stopOpen(Player var1) {
      if (!this.remove && !var1.isSpectator()) {
         --this.openCount;
         this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount <= 0) {
            this.level.gameEvent(var1, GameEvent.CONTAINER_CLOSE, this.worldPosition);
            this.level.playSound((Player)null, (BlockPos)this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   protected Component getDefaultName() {
      return Component.translatable("container.shulkerBox");
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.loadFromTag(var1, var2);
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.itemStacks, false, var2);
      }

   }

   public void loadFromTag(CompoundTag var1, HolderLookup.Provider var2) {
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1) && var1.contains("Items", 9)) {
         ContainerHelper.loadAllItems(var1, this.itemStacks, var2);
      }

   }

   protected NonNullList<ItemStack> getItems() {
      return this.itemStacks;
   }

   protected void setItems(NonNullList<ItemStack> var1) {
      this.itemStacks = var1;
   }

   public int[] getSlotsForFace(Direction var1) {
      return SLOTS;
   }

   public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
      return !(Block.byItem(var2.getItem()) instanceof ShulkerBoxBlock);
   }

   public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
      return true;
   }

   public float getProgress(float var1) {
      return Mth.lerp(var1, this.progressOld, this.progress);
   }

   @Nullable
   public DyeColor getColor() {
      return this.color;
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new ShulkerBoxMenu(var1, var2, this);
   }

   public boolean isClosed() {
      return this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
   }

   public static enum AnimationStatus {
      CLOSED,
      OPENING,
      OPENED,
      CLOSING;

      private AnimationStatus() {
      }

      // $FF: synthetic method
      private static AnimationStatus[] $values() {
         return new AnimationStatus[]{CLOSED, OPENING, OPENED, CLOSING};
      }
   }
}
