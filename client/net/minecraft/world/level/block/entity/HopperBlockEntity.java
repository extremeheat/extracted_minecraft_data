package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
   public static final int MOVE_ITEM_SPEED = 8;
   public static final int HOPPER_CONTAINER_SIZE = 5;
   private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
   private int cooldownTime = -1;
   private long tickedGameTime;

   public HopperBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.HOPPER, var1, var2);
   }

   @Override
   public void load(CompoundTag var1) {
      super.load(var1);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1)) {
         ContainerHelper.loadAllItems(var1, this.items);
      }

      this.cooldownTime = var1.getInt("TransferCooldown");
   }

   @Override
   protected void saveAdditional(CompoundTag var1) {
      super.saveAdditional(var1);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items);
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
      if (var2.getCount() > this.getMaxStackSize()) {
         var2.setCount(this.getMaxStackSize());
      }
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
               var5 = ejectItems(var0, var1, var2, var3);
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

   private static boolean ejectItems(Level var0, BlockPos var1, BlockState var2, Container var3) {
      Container var4 = getAttachedContainer(var0, var1, var2);
      if (var4 == null) {
         return false;
      } else {
         Direction var5 = var2.getValue(HopperBlock.FACING).getOpposite();
         if (isFullContainer(var4, var5)) {
            return false;
         } else {
            for(int var6 = 0; var6 < var3.getContainerSize(); ++var6) {
               if (!var3.getItem(var6).isEmpty()) {
                  ItemStack var7 = var3.getItem(var6).copy();
                  ItemStack var8 = addItem(var3, var4, var3.removeItem(var6, 1), var5);
                  if (var8.isEmpty()) {
                     var4.setChanged();
                     return true;
                  }

                  var3.setItem(var6, var7);
               }
            }

            return false;
         }
      }
   }

   private static IntStream getSlots(Container var0, Direction var1) {
      return var0 instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer)var0).getSlotsForFace(var1)) : IntStream.range(0, var0.getContainerSize());
   }

   private static boolean isFullContainer(Container var0, Direction var1) {
      return getSlots(var0, var1).allMatch(var1x -> {
         ItemStack var2 = var0.getItem(var1x);
         return var2.getCount() >= var2.getMaxStackSize();
      });
   }

   private static boolean isEmptyContainer(Container var0, Direction var1) {
      return getSlots(var0, var1).allMatch(var1x -> var0.getItem(var1x).isEmpty());
   }

   public static boolean suckInItems(Level var0, Hopper var1) {
      Container var2 = getSourceContainer(var0, var1);
      if (var2 != null) {
         Direction var5 = Direction.DOWN;
         return isEmptyContainer(var2, var5) ? false : getSlots(var2, var5).anyMatch(var3 -> tryTakeInItemFromSlot(var1, var2, var3, var5));
      } else {
         for(ItemEntity var4 : getItemsAtAndAbove(var0, var1)) {
            if (addItem(var1, var4)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean tryTakeInItemFromSlot(Hopper var0, Container var1, int var2, Direction var3) {
      ItemStack var4 = var1.getItem(var2);
      if (!var4.isEmpty() && canTakeItemFromContainer(var1, var4, var2, var3)) {
         ItemStack var5 = var4.copy();
         ItemStack var6 = addItem(var1, var0, var1.removeItem(var2, 1), null);
         if (var6.isEmpty()) {
            var1.setChanged();
            return true;
         }

         var1.setItem(var2, var5);
      }

      return false;
   }

   public static boolean addItem(Container var0, ItemEntity var1) {
      boolean var2 = false;
      ItemStack var3 = var1.getItem().copy();
      ItemStack var4 = addItem(null, var0, var3, null);
      if (var4.isEmpty()) {
         var2 = true;
         var1.discard();
      } else {
         var1.setItem(var4);
      }

      return var2;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static ItemStack addItem(@Nullable Container var0, Container var1, ItemStack var2, @Nullable Direction var3) {
      if (var1 instanceof WorldlyContainer var7 && var3 != null) {
         int[] var8 = var7.getSlotsForFace(var3);

         for(int var6 = 0; var6 < var8.length && !var2.isEmpty(); ++var6) {
            var2 = tryMoveInItem(var0, var1, var2, var8[var6], var3);
         }
      } else {
         int var4 = var1.getContainerSize();

         for(int var5 = 0; var5 < var4 && !var2.isEmpty(); ++var5) {
            var2 = tryMoveInItem(var0, var1, var2, var5, var3);
         }
      }

      return var2;
   }

   private static boolean canPlaceItemInContainer(Container var0, ItemStack var1, int var2, @Nullable Direction var3) {
      if (!var0.canPlaceItem(var2, var1)) {
         return false;
      } else {
         return !(var0 instanceof WorldlyContainer) || ((WorldlyContainer)var0).canPlaceItemThroughFace(var2, var1, var3);
      }
   }

   private static boolean canTakeItemFromContainer(Container var0, ItemStack var1, int var2, Direction var3) {
      return !(var0 instanceof WorldlyContainer) || ((WorldlyContainer)var0).canTakeItemThroughFace(var2, var1, var3);
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
   private static Container getAttachedContainer(Level var0, BlockPos var1, BlockState var2) {
      Direction var3 = var2.getValue(HopperBlock.FACING);
      return getContainerAt(var0, var1.relative(var3));
   }

   @Nullable
   private static Container getSourceContainer(Level var0, Hopper var1) {
      return getContainerAt(var0, var1.getLevelX(), var1.getLevelY() + 1.0, var1.getLevelZ());
   }

   public static List<ItemEntity> getItemsAtAndAbove(Level var0, Hopper var1) {
      return var1.getSuckShape()
         .toAabbs()
         .stream()
         .flatMap(
            var2 -> var0.getEntitiesOfClass(
                     ItemEntity.class, var2.move(var1.getLevelX() - 0.5, var1.getLevelY() - 0.5, var1.getLevelZ() - 0.5), EntitySelector.ENTITY_STILL_ALIVE
                  )
                  .stream()
         )
         .collect(Collectors.toList());
   }

   @Nullable
   public static Container getContainerAt(Level var0, BlockPos var1) {
      return getContainerAt(var0, (double)var1.getX() + 0.5, (double)var1.getY() + 0.5, (double)var1.getZ() + 0.5);
   }

   @Nullable
   private static Container getContainerAt(Level var0, double var1, double var3, double var5) {
      Object var7 = null;
      BlockPos var8 = new BlockPos(var1, var3, var5);
      BlockState var9 = var0.getBlockState(var8);
      Block var10 = var9.getBlock();
      if (var10 instanceof WorldlyContainerHolder) {
         var7 = ((WorldlyContainerHolder)var10).getContainer(var9, var0, var8);
      } else if (var9.hasBlockEntity()) {
         BlockEntity var11 = var0.getBlockEntity(var8);
         if (var11 instanceof Container) {
            var7 = (Container)var11;
            if (var7 instanceof ChestBlockEntity && var10 instanceof ChestBlock) {
               var7 = ChestBlock.getContainer((ChestBlock)var10, var9, var0, var8, true);
            }
         }
      }

      if (var7 == null) {
         List var12 = var0.getEntities(
            (Entity)null, new AABB(var1 - 0.5, var3 - 0.5, var5 - 0.5, var1 + 0.5, var3 + 0.5, var5 + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR
         );
         if (!var12.isEmpty()) {
            var7 = (Container)var12.get(var0.random.nextInt(var12.size()));
         }
      }

      return (Container)var7;
   }

   private static boolean canMergeItems(ItemStack var0, ItemStack var1) {
      if (!var0.is(var1.getItem())) {
         return false;
      } else if (var0.getDamageValue() != var1.getDamageValue()) {
         return false;
      } else if (var0.getCount() > var0.getMaxStackSize()) {
         return false;
      } else {
         return ItemStack.tagMatches(var0, var1);
      }
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
      if (var3 instanceof ItemEntity
         && Shapes.joinIsNotEmpty(
            Shapes.create(var3.getBoundingBox().move((double)(-var1.getX()), (double)(-var1.getY()), (double)(-var1.getZ()))),
            var4.getSuckShape(),
            BooleanOp.AND
         )) {
         tryMoveItems(var0, var1, var2, var4, () -> addItem(var4, (ItemEntity)var3));
      }
   }

   @Override
   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new HopperMenu(var1, var2, this);
   }
}
