package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
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
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class ShulkerBoxBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
   private static final int[] SLOTS = IntStream.range(0, 27).toArray();
   private NonNullList<ItemStack> itemStacks;
   private int openCount;
   private ShulkerBoxBlockEntity.AnimationStatus animationStatus;
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
      updateAnimation(var0, var1, var2, var3);
      if (var3.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.OPENING || var3.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSING) {
         moveCollidedEntities(var0, var1, var2, var3.getProgress(1.0F));
      }

   }

   private static void updateAnimation(Level var0, BlockPos var1, BlockState var2, ShulkerBoxBlockEntity var3) {
      var3.progressOld = var3.progress;
      switch(var3.animationStatus) {
      case CLOSED:
         var3.progress = 0.0F;
         break;
      case OPENING:
         var3.progress += 0.1F;
         if (var3.progress >= 1.0F) {
            moveCollidedEntities(var0, var1, var2, var3.getProgress(1.0F));
            var3.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
            var3.progress = 1.0F;
            doNeighborUpdates(var0, var1, var2);
         }
         break;
      case CLOSING:
         var3.progress -= 0.1F;
         if (var3.progress <= 0.0F) {
            var3.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
            var3.progress = 0.0F;
            doNeighborUpdates(var0, var1, var2);
         }
         break;
      case OPENED:
         var3.progress = 1.0F;
      }

   }

   public ShulkerBoxBlockEntity.AnimationStatus getAnimationStatus() {
      return this.animationStatus;
   }

   public AABB getBoundingBox(BlockState var1) {
      return getBoundingBox((Direction)var1.getValue(ShulkerBoxBlock.FACING), this.getProgress(1.0F));
   }

   public static AABB getBoundingBox(Direction var0, float var1) {
      return Shapes.block().bounds().expandTowards((double)(0.5F * var1 * (float)var0.getStepX()), (double)(0.5F * var1 * (float)var0.getStepY()), (double)(0.5F * var1 * (float)var0.getStepZ()));
   }

   private static AABB getTopBoundingBox(Direction var0, float var1) {
      Direction var2 = var0.getOpposite();
      return getBoundingBox(var0, var1).contract((double)var2.getStepX(), (double)var2.getStepY(), (double)var2.getStepZ());
   }

   private static void moveCollidedEntities(Level var0, BlockPos var1, BlockState var2, float var3) {
      if (var2.getBlock() instanceof ShulkerBoxBlock) {
         Direction var4 = (Direction)var2.getValue(ShulkerBoxBlock.FACING);
         AABB var5 = getTopBoundingBox(var4, var3).move(var1);
         List var6 = var0.getEntities((Entity)null, var5);
         if (!var6.isEmpty()) {
            for(int var7 = 0; var7 < var6.size(); ++var7) {
               Entity var8 = (Entity)var6.get(var7);
               if (var8.getPistonPushReaction() != PushReaction.IGNORE) {
                  double var9 = 0.0D;
                  double var11 = 0.0D;
                  double var13 = 0.0D;
                  AABB var15 = var8.getBoundingBox();
                  switch(var4.getAxis()) {
                  case X:
                     if (var4.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        var9 = var5.maxX - var15.minX;
                     } else {
                        var9 = var15.maxX - var5.minX;
                     }

                     var9 += 0.01D;
                     break;
                  case Y:
                     if (var4.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        var11 = var5.maxY - var15.minY;
                     } else {
                        var11 = var15.maxY - var5.minY;
                     }

                     var11 += 0.01D;
                     break;
                  case Z:
                     if (var4.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        var13 = var5.maxZ - var15.minZ;
                     } else {
                        var13 = var15.maxZ - var5.minZ;
                     }

                     var13 += 0.01D;
                  }

                  var8.move(MoverType.SHULKER_BOX, new Vec3(var9 * (double)var4.getStepX(), var11 * (double)var4.getStepY(), var13 * (double)var4.getStepZ()));
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
            doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
         }

         if (var2 == 1) {
            this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
            doNeighborUpdates(this.getLevel(), this.worldPosition, this.getBlockState());
         }

         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   private static void doNeighborUpdates(Level var0, BlockPos var1, BlockState var2) {
      var2.updateNeighbourShapes(var0, var1, 3);
   }

   public void startOpen(Player var1) {
      if (!var1.isSpectator()) {
         if (this.openCount < 0) {
            this.openCount = 0;
         }

         ++this.openCount;
         this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount == 1) {
            this.level.playSound((Player)null, (BlockPos)this.worldPosition, SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   public void stopOpen(Player var1) {
      if (!var1.isSpectator()) {
         --this.openCount;
         this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.openCount);
         if (this.openCount <= 0) {
            this.level.playSound((Player)null, (BlockPos)this.worldPosition, SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
         }
      }

   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.shulkerBox");
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.loadFromTag(var1);
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      return this.saveToTag(var1);
   }

   public void loadFromTag(CompoundTag var1) {
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1) && var1.contains("Items", 9)) {
         ContainerHelper.loadAllItems(var1, this.itemStacks);
      }

   }

   public CompoundTag saveToTag(CompoundTag var1) {
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.itemStacks, false);
      }

      return var1;
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
   }
}
