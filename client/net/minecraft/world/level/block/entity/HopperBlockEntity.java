package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
   public static final int MOVE_ITEM_SPEED = 8;
   public static final int HOPPER_CONTAINER_SIZE = 5;
   private static final int[][] CACHED_SLOTS = new int[54][];
   private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
   private int cooldownTime = -1;
   private long tickedGameTime;
   private Direction facing;

   public HopperBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.HOPPER, var1, var2);
      this.facing = var2.getValue(HopperBlock.FACING);
   }

   @Override
   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1)) {
         ContainerHelper.loadAllItems(var1, this.items, var2);
      }

      this.cooldownTime = var1.getInt("TransferCooldown");
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items, var2);
      }

      var1.putInt("TransferCooldown", this.cooldownTime);
   }

   @Override
   public int getContainerSize() {
      return this.items.size();
   }

   @Override
   public ItemStack removeItem(int var1, int var2) {
      this.unpackLootTable(null);
      return ContainerHelper.removeItem(this.getItems(), var1, var2);
   }

   @Override
   public void setItem(int var1, ItemStack var2) {
      this.unpackLootTable(null);
      this.getItems().set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
   }

   @Override
   public void setBlockState(BlockState var1) {
      super.setBlockState(var1);
      this.facing = var1.getValue(HopperBlock.FACING);
   }

   @Override
   protected Component getDefaultName() {
      return Component.translatable("container.hopper");
   }

   public static void pushItemsTick(Level var0, BlockPos var1, BlockState var2, HopperBlockEntity var3) {
      --var3.cooldownTime;
      var3.tickedGameTime = var0.getGameTime();
      if (!var3.isOnCooldown()) {
         var3.setCooldown(0);
         tryMoveItems(var0, var1, var2, var3, () -> suckInItems(var0, var3));
      }
   }

   private static boolean tryMoveItems(Level var0, BlockPos var1, BlockState var2, HopperBlockEntity var3, BooleanSupplier var4) {
      if (var0.isClientSide) {
         return false;
      } else {
         if (!var3.isOnCooldown() && var2.getValue(HopperBlock.ENABLED)) {
            boolean var5 = false;
            if (!var3.isEmpty()) {
               var5 = ejectItems(var0, var1, var3);
            }

            if (!var3.inventoryFull()) {
               var5 |= var4.getAsBoolean();
            }

            if (var5) {
               var3.setCooldown(8);
               setChanged(var0, var1, var2);
               return true;
            }
         }

         return false;
      }
   }

   private boolean inventoryFull() {
      for(ItemStack var2 : this.items) {
         if (var2.isEmpty() || var2.getCount() != var2.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   private static boolean ejectItems(Level var0, BlockPos var1, HopperBlockEntity var2) {
      Container var3 = getAttachedContainer(var0, var1, var2);
      if (var3 == null) {
         return false;
      } else {
         Direction var4 = var2.facing.getOpposite();
         if (isFullContainer(var3, var4)) {
            return false;
         } else {
            for(int var5 = 0; var5 < var2.getContainerSize(); ++var5) {
               ItemStack var6 = var2.getItem(var5);
               if (!var6.isEmpty()) {
                  int var7 = var6.getCount();
                  ItemStack var8 = addItem(var2, var3, var2.removeItem(var5, 1), var4);
                  if (var8.isEmpty()) {
                     var3.setChanged();
                     return true;
                  }

                  var6.setCount(var7);
                  if (var7 == 1) {
                     var2.setItem(var5, var6);
                  }
               }
            }

            return false;
         }
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static int[] getSlots(Container var0, Direction var1) {
      if (var0 instanceof WorldlyContainer var5) {
         return var5.getSlotsForFace(var1);
      } else {
         int var2 = var0.getContainerSize();
         if (var2 < CACHED_SLOTS.length) {
            int[] var3 = CACHED_SLOTS[var2];
            if (var3 != null) {
               return var3;
            } else {
               int[] var4 = createFlatSlots(var2);
               CACHED_SLOTS[var2] = var4;
               return var4;
            }
         } else {
            return createFlatSlots(var2);
         }
      }
   }

   private static int[] createFlatSlots(int var0) {
      int[] var1 = new int[var0];
      int var2 = 0;

      while(var2 < var1.length) {
         var1[var2] = var2++;
      }

      return var1;
   }

   private static boolean isFullContainer(Container var0, Direction var1) {
      int[] var2 = getSlots(var0, var1);

      for(int var6 : var2) {
         ItemStack var7 = var0.getItem(var6);
         if (var7.getCount() < var7.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   public static boolean suckInItems(Level var0, Hopper var1) {
      BlockPos var2 = BlockPos.containing(var1.getLevelX(), var1.getLevelY() + 1.0, var1.getLevelZ());
      BlockState var3 = var0.getBlockState(var2);
      Container var4 = getSourceContainer(var0, var1, var2, var3);
      if (var4 != null) {
         Direction var10 = Direction.DOWN;

         for(int var9 : getSlots(var4, var10)) {
            if (tryTakeInItemFromSlot(var1, var4, var9, var10)) {
               return true;
            }
         }

         return false;
      } else {
         boolean var5 = var1.isGridAligned() && var3.isCollisionShapeFullBlock(var0, var2) && !var3.is(BlockTags.DOES_NOT_BLOCK_HOPPERS);
         if (!var5) {
            for(ItemEntity var7 : getItemsAtAndAbove(var0, var1)) {
               if (addItem(var1, var7)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private static boolean tryTakeInItemFromSlot(Hopper var0, Container var1, int var2, Direction var3) {
      ItemStack var4 = var1.getItem(var2);
      if (!var4.isEmpty() && canTakeItemFromContainer(var0, var1, var4, var2, var3)) {
         int var5 = var4.getCount();
         ItemStack var6 = addItem(var1, var0, var1.removeItem(var2, 1), null);
         if (var6.isEmpty()) {
            var1.setChanged();
            return true;
         }

         var4.setCount(var5);
         if (var5 == 1) {
            var1.setItem(var2, var4);
         }
      }

      return false;
   }

   public static boolean addItem(Container var0, ItemEntity var1) {
      boolean var2 = false;
      ItemStack var3 = var1.getItem().copy();
      ItemStack var4 = addItem(null, var0, var3, null);
      if (var4.isEmpty()) {
         var2 = true;
         var1.setItem(ItemStack.EMPTY);
         var1.discard();
      } else {
         var1.setItem(var4);
      }

      return var2;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static ItemStack addItem(@Nullable Container var0, Container var1, ItemStack var2, @Nullable Direction var3) {
      if (var1 instanceof WorldlyContainer var4 && var3 != null) {
         int[] var7 = var4.getSlotsForFace(var3);

         for(int var8 = 0; var8 < var7.length && !var2.isEmpty(); ++var8) {
            var2 = tryMoveInItem(var0, var1, var2, var7[var8], var3);
         }

         return var2;
      }

      int var5 = var1.getContainerSize();

      for(int var6 = 0; var6 < var5 && !var2.isEmpty(); ++var6) {
         var2 = tryMoveInItem(var0, var1, var2, var6, var3);
      }

      return var2;
   }

   private static boolean canPlaceItemInContainer(Container var0, ItemStack var1, int var2, @Nullable Direction var3) {
      if (!var0.canPlaceItem(var2, var1)) {
         return false;
      } else {
         if (var0 instanceof WorldlyContainer var4 && !var4.canPlaceItemThroughFace(var2, var1, var3)) {
            return false;
         }

         return true;
      }
   }

   private static boolean canTakeItemFromContainer(Container var0, Container var1, ItemStack var2, int var3, Direction var4) {
      if (!var1.canTakeItem(var0, var3, var2)) {
         return false;
      } else {
         if (var1 instanceof WorldlyContainer var5 && !var5.canTakeItemThroughFace(var3, var2, var4)) {
            return false;
         }

         return true;
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static ItemStack tryMoveInItem(@Nullable Container var0, Container var1, ItemStack var2, int var3, @Nullable Direction var4) {
      ItemStack var5 = var1.getItem(var3);
      if (canPlaceItemInContainer(var1, var2, var3, var4)) {
         boolean var6 = false;
         boolean var7 = var1.isEmpty();
         if (var5.isEmpty()) {
            var1.setItem(var3, var2);
            var2 = ItemStack.EMPTY;
            var6 = true;
         } else if (canMergeItems(var5, var2)) {
            int var8 = var2.getMaxStackSize() - var5.getCount();
            int var9 = Math.min(var2.getCount(), var8);
            var2.shrink(var9);
            var5.grow(var9);
            var6 = var9 > 0;
         }

         if (var6) {
            if (var7 && var1 instanceof HopperBlockEntity var11 && !var11.isOnCustomCooldown()) {
               byte var12 = 0;
               if (var0 instanceof HopperBlockEntity var10 && var11.tickedGameTime >= var10.tickedGameTime) {
                  var12 = 1;
               }

               var11.setCooldown(8 - var12);
            }

            var1.setChanged();
         }
      }

      return var2;
   }

   @Nullable
   private static Container getAttachedContainer(Level var0, BlockPos var1, HopperBlockEntity var2) {
      return getContainerAt(var0, var1.relative(var2.facing));
   }

   @Nullable
   private static Container getSourceContainer(Level var0, Hopper var1, BlockPos var2, BlockState var3) {
      return getContainerAt(var0, var2, var3, var1.getLevelX(), var1.getLevelY() + 1.0, var1.getLevelZ());
   }

   public static List<ItemEntity> getItemsAtAndAbove(Level var0, Hopper var1) {
      AABB var2 = var1.getSuckAabb().move(var1.getLevelX() - 0.5, var1.getLevelY() - 0.5, var1.getLevelZ() - 0.5);
      return var0.getEntitiesOfClass(ItemEntity.class, var2, EntitySelector.ENTITY_STILL_ALIVE);
   }

   @Nullable
   public static Container getContainerAt(Level var0, BlockPos var1) {
      return getContainerAt(var0, var1, var0.getBlockState(var1), (double)var1.getX() + 0.5, (double)var1.getY() + 0.5, (double)var1.getZ() + 0.5);
   }

   @Nullable
   private static Container getContainerAt(Level var0, BlockPos var1, BlockState var2, double var3, double var5, double var7) {
      Container var9 = getBlockContainer(var0, var1, var2);
      if (var9 == null) {
         var9 = getEntityContainer(var0, var3, var5, var7);
      }

      return var9;
   }

   @Nullable
   private static Container getBlockContainer(Level var0, BlockPos var1, BlockState var2) {
      Block var3 = var2.getBlock();
      if (var3 instanceof WorldlyContainerHolder) {
         return ((WorldlyContainerHolder)var3).getContainer(var2, var0, var1);
      } else {
         if (var2.hasBlockEntity()) {
            BlockEntity var4 = var0.getBlockEntity(var1);
            if (var4 instanceof Container var5) {
               if (var5 instanceof ChestBlockEntity && var3 instanceof ChestBlock) {
                  var5 = ChestBlock.getContainer((ChestBlock)var3, var2, var0, var1, true);
               }

               return var5;
            }
         }

         return null;
      }
   }

   @Nullable
   private static Container getEntityContainer(Level var0, double var1, double var3, double var5) {
      List var7 = var0.getEntities(
         (Entity)null, new AABB(var1 - 0.5, var3 - 0.5, var5 - 0.5, var1 + 0.5, var3 + 0.5, var5 + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR
      );
      return !var7.isEmpty() ? (Container)var7.get(var0.random.nextInt(var7.size())) : null;
   }

   private static boolean canMergeItems(ItemStack var0, ItemStack var1) {
      return var0.getCount() <= var0.getMaxStackSize() && ItemStack.isSameItemSameComponents(var0, var1);
   }

   @Override
   public double getLevelX() {
      return (double)this.worldPosition.getX() + 0.5;
   }

   @Override
   public double getLevelY() {
      return (double)this.worldPosition.getY() + 0.5;
   }

   @Override
   public double getLevelZ() {
      return (double)this.worldPosition.getZ() + 0.5;
   }

   @Override
   public boolean isGridAligned() {
      return true;
   }

   private void setCooldown(int var1) {
      this.cooldownTime = var1;
   }

   private boolean isOnCooldown() {
      return this.cooldownTime > 0;
   }

   private boolean isOnCustomCooldown() {
      return this.cooldownTime > 8;
   }

   @Override
   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   public static void entityInside(Level var0, BlockPos var1, BlockState var2, Entity var3, HopperBlockEntity var4) {
      if (var3 instanceof ItemEntity var5
         && !((ItemEntity)var5).getItem().isEmpty()
         && var3.getBoundingBox().move((double)(-var1.getX()), (double)(-var1.getY()), (double)(-var1.getZ())).intersects(var4.getSuckAabb())) {
         tryMoveItems(var0, var1, var2, var4, () -> addItem(var4, var5));
      }
   }

   @Override
   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new HopperMenu(var1, var2, this);
   }
}
