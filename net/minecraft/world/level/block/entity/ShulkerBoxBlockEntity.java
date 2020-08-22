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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class ShulkerBoxBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, TickableBlockEntity {
   private static final int[] SLOTS = IntStream.range(0, 27).toArray();
   private NonNullList itemStacks;
   private int openCount;
   private ShulkerBoxBlockEntity.AnimationStatus animationStatus;
   private float progress;
   private float progressOld;
   @Nullable
   private DyeColor color;
   private boolean loadColorFromBlock;

   public ShulkerBoxBlockEntity(@Nullable DyeColor var1) {
      super(BlockEntityType.SHULKER_BOX);
      this.itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
      this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
      this.color = var1;
   }

   public ShulkerBoxBlockEntity() {
      this((DyeColor)null);
      this.loadColorFromBlock = true;
   }

   public void tick() {
      this.updateAnimation();
      if (this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.OPENING || this.animationStatus == ShulkerBoxBlockEntity.AnimationStatus.CLOSING) {
         this.moveCollidedEntities();
      }

   }

   protected void updateAnimation() {
      this.progressOld = this.progress;
      switch(this.animationStatus) {
      case CLOSED:
         this.progress = 0.0F;
         break;
      case OPENING:
         this.progress += 0.1F;
         if (this.progress >= 1.0F) {
            this.moveCollidedEntities();
            this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENED;
            this.progress = 1.0F;
            this.doNeighborUpdates();
         }
         break;
      case CLOSING:
         this.progress -= 0.1F;
         if (this.progress <= 0.0F) {
            this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.CLOSED;
            this.progress = 0.0F;
            this.doNeighborUpdates();
         }
         break;
      case OPENED:
         this.progress = 1.0F;
      }

   }

   public ShulkerBoxBlockEntity.AnimationStatus getAnimationStatus() {
      return this.animationStatus;
   }

   public AABB getBoundingBox(BlockState var1) {
      return this.getBoundingBox((Direction)var1.getValue(ShulkerBoxBlock.FACING));
   }

   public AABB getBoundingBox(Direction var1) {
      float var2 = this.getProgress(1.0F);
      return Shapes.block().bounds().expandTowards((double)(0.5F * var2 * (float)var1.getStepX()), (double)(0.5F * var2 * (float)var1.getStepY()), (double)(0.5F * var2 * (float)var1.getStepZ()));
   }

   private AABB getTopBoundingBox(Direction var1) {
      Direction var2 = var1.getOpposite();
      return this.getBoundingBox(var1).contract((double)var2.getStepX(), (double)var2.getStepY(), (double)var2.getStepZ());
   }

   private void moveCollidedEntities() {
      BlockState var1 = this.level.getBlockState(this.getBlockPos());
      if (var1.getBlock() instanceof ShulkerBoxBlock) {
         Direction var2 = (Direction)var1.getValue(ShulkerBoxBlock.FACING);
         AABB var3 = this.getTopBoundingBox(var2).move(this.worldPosition);
         List var4 = this.level.getEntities((Entity)null, var3);
         if (!var4.isEmpty()) {
            for(int var5 = 0; var5 < var4.size(); ++var5) {
               Entity var6 = (Entity)var4.get(var5);
               if (var6.getPistonPushReaction() != PushReaction.IGNORE) {
                  double var7 = 0.0D;
                  double var9 = 0.0D;
                  double var11 = 0.0D;
                  AABB var13 = var6.getBoundingBox();
                  switch(var2.getAxis()) {
                  case X:
                     if (var2.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        var7 = var3.maxX - var13.minX;
                     } else {
                        var7 = var13.maxX - var3.minX;
                     }

                     var7 += 0.01D;
                     break;
                  case Y:
                     if (var2.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        var9 = var3.maxY - var13.minY;
                     } else {
                        var9 = var13.maxY - var3.minY;
                     }

                     var9 += 0.01D;
                     break;
                  case Z:
                     if (var2.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        var11 = var3.maxZ - var13.minZ;
                     } else {
                        var11 = var13.maxZ - var3.minZ;
                     }

                     var11 += 0.01D;
                  }

                  var6.move(MoverType.SHULKER_BOX, new Vec3(var7 * (double)var2.getStepX(), var9 * (double)var2.getStepY(), var11 * (double)var2.getStepZ()));
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
            this.doNeighborUpdates();
         }

         if (var2 == 1) {
            this.animationStatus = ShulkerBoxBlockEntity.AnimationStatus.OPENING;
            this.doNeighborUpdates();
         }

         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   private void doNeighborUpdates() {
      this.getBlockState().updateNeighbourShapes(this.getLevel(), this.getBlockPos(), 3);
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
      return new TranslatableComponent("container.shulkerBox", new Object[0]);
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

   protected NonNullList getItems() {
      return this.itemStacks;
   }

   protected void setItems(NonNullList var1) {
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
      if (this.loadColorFromBlock) {
         this.color = ShulkerBoxBlock.getColorFromBlock(this.getBlockState().getBlock());
         this.loadColorFromBlock = false;
      }

      return this.color;
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new ShulkerBoxMenu(var1, var2, this);
   }

   public static enum AnimationStatus {
      CLOSED,
      OPENING,
      OPENED,
      CLOSING;
   }
}
