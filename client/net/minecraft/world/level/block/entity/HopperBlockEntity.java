package net.minecraft.world.level.block.entity;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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

public class HopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper, TickableBlockEntity {
   private NonNullList<ItemStack> items;
   private int cooldownTime;
   private long tickedGameTime;

   public HopperBlockEntity() {
      super(BlockEntityType.HOPPER);
      this.items = NonNullList.withSize(5, ItemStack.EMPTY);
      this.cooldownTime = -1;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1)) {
         ContainerHelper.loadAllItems(var1, this.items);
      }

      this.cooldownTime = var1.getInt("TransferCooldown");
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items);
      }

      var1.putInt("TransferCooldown", this.cooldownTime);
      return var1;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public ItemStack removeItem(int var1, int var2) {
      this.unpackLootTable((Player)null);
      return ContainerHelper.removeItem(this.getItems(), var1, var2);
   }

   public void setItem(int var1, ItemStack var2) {
      this.unpackLootTable((Player)null);
      this.getItems().set(var1, var2);
      if (var2.getCount() > this.getMaxStackSize()) {
         var2.setCount(this.getMaxStackSize());
      }

   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.hopper", new Object[0]);
   }

   public void tick() {
      if (this.level != null && !this.level.isClientSide) {
         --this.cooldownTime;
         this.tickedGameTime = this.level.getGameTime();
         if (!this.isOnCooldown()) {
            this.setCooldown(0);
            this.tryMoveItems(() -> {
               return suckInItems(this);
            });
         }

      }
   }

   private boolean tryMoveItems(Supplier<Boolean> var1) {
      if (this.level != null && !this.level.isClientSide) {
         if (!this.isOnCooldown() && (Boolean)this.getBlockState().getValue(HopperBlock.ENABLED)) {
            boolean var2 = false;
            if (!this.inventoryEmpty()) {
               var2 = this.ejectItems();
            }

            if (!this.inventoryFull()) {
               var2 |= (Boolean)var1.get();
            }

            if (var2) {
               this.setCooldown(8);
               this.setChanged();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean inventoryEmpty() {
      Iterator var1 = this.items.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.isEmpty());

      return false;
   }

   public boolean isEmpty() {
      return this.inventoryEmpty();
   }

   private boolean inventoryFull() {
      Iterator var1 = this.items.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(!var2.isEmpty() && var2.getCount() == var2.getMaxStackSize());

      return false;
   }

   private boolean ejectItems() {
      Container var1 = this.getAttachedContainer();
      if (var1 == null) {
         return false;
      } else {
         Direction var2 = ((Direction)this.getBlockState().getValue(HopperBlock.FACING)).getOpposite();
         if (this.isFullContainer(var1, var2)) {
            return false;
         } else {
            for(int var3 = 0; var3 < this.getContainerSize(); ++var3) {
               if (!this.getItem(var3).isEmpty()) {
                  ItemStack var4 = this.getItem(var3).copy();
                  ItemStack var5 = addItem(this, var1, this.removeItem(var3, 1), var2);
                  if (var5.isEmpty()) {
                     var1.setChanged();
                     return true;
                  }

                  this.setItem(var3, var4);
               }
            }

            return false;
         }
      }
   }

   private static IntStream getSlots(Container var0, Direction var1) {
      return var0 instanceof WorldlyContainer ? IntStream.of(((WorldlyContainer)var0).getSlotsForFace(var1)) : IntStream.range(0, var0.getContainerSize());
   }

   private boolean isFullContainer(Container var1, Direction var2) {
      return getSlots(var1, var2).allMatch((var1x) -> {
         ItemStack var2 = var1.getItem(var1x);
         return var2.getCount() >= var2.getMaxStackSize();
      });
   }

   private static boolean isEmptyContainer(Container var0, Direction var1) {
      return getSlots(var0, var1).allMatch((var1x) -> {
         return var0.getItem(var1x).isEmpty();
      });
   }

   public static boolean suckInItems(Hopper var0) {
      Container var1 = getSourceContainer(var0);
      if (var1 != null) {
         Direction var4 = Direction.DOWN;
         return isEmptyContainer(var1, var4) ? false : getSlots(var1, var4).anyMatch((var3x) -> {
            return tryTakeInItemFromSlot(var0, var1, var3x, var4);
         });
      } else {
         Iterator var2 = getItemsAtAndAbove(var0).iterator();

         ItemEntity var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (ItemEntity)var2.next();
         } while(!addItem(var0, var3));

         return true;
      }
   }

   private static boolean tryTakeInItemFromSlot(Hopper var0, Container var1, int var2, Direction var3) {
      ItemStack var4 = var1.getItem(var2);
      if (!var4.isEmpty() && canTakeItemFromContainer(var1, var4, var2, var3)) {
         ItemStack var5 = var4.copy();
         ItemStack var6 = addItem(var1, var0, var1.removeItem(var2, 1), (Direction)null);
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
      ItemStack var4 = addItem((Container)null, var0, var3, (Direction)null);
      if (var4.isEmpty()) {
         var2 = true;
         var1.remove();
      } else {
         var1.setItem(var4);
      }

      return var2;
   }

   public static ItemStack addItem(@Nullable Container var0, Container var1, ItemStack var2, @Nullable Direction var3) {
      if (var1 instanceof WorldlyContainer && var3 != null) {
         WorldlyContainer var7 = (WorldlyContainer)var1;
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
            if (var7 && var1 instanceof HopperBlockEntity) {
               HopperBlockEntity var11 = (HopperBlockEntity)var1;
               if (!var11.isOnCustomCooldown()) {
                  byte var12 = 0;
                  if (var0 instanceof HopperBlockEntity) {
                     HopperBlockEntity var10 = (HopperBlockEntity)var0;
                     if (var11.tickedGameTime >= var10.tickedGameTime) {
                        var12 = 1;
                     }
                  }

                  var11.setCooldown(8 - var12);
               }
            }

            var1.setChanged();
         }
      }

      return var2;
   }

   @Nullable
   private Container getAttachedContainer() {
      Direction var1 = (Direction)this.getBlockState().getValue(HopperBlock.FACING);
      return getContainerAt(this.getLevel(), this.worldPosition.relative(var1));
   }

   @Nullable
   public static Container getSourceContainer(Hopper var0) {
      return getContainerAt(var0.getLevel(), var0.getLevelX(), var0.getLevelY() + 1.0D, var0.getLevelZ());
   }

   public static List<ItemEntity> getItemsAtAndAbove(Hopper var0) {
      return (List)var0.getSuckShape().toAabbs().stream().flatMap((var1) -> {
         return var0.getLevel().getEntitiesOfClass(ItemEntity.class, var1.move(var0.getLevelX() - 0.5D, var0.getLevelY() - 0.5D, var0.getLevelZ() - 0.5D), EntitySelector.ENTITY_STILL_ALIVE).stream();
      }).collect(Collectors.toList());
   }

   @Nullable
   public static Container getContainerAt(Level var0, BlockPos var1) {
      return getContainerAt(var0, (double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D);
   }

   @Nullable
   public static Container getContainerAt(Level var0, double var1, double var3, double var5) {
      Object var7 = null;
      BlockPos var8 = new BlockPos(var1, var3, var5);
      BlockState var9 = var0.getBlockState(var8);
      Block var10 = var9.getBlock();
      if (var10 instanceof WorldlyContainerHolder) {
         var7 = ((WorldlyContainerHolder)var10).getContainer(var9, var0, var8);
      } else if (var10.isEntityBlock()) {
         BlockEntity var11 = var0.getBlockEntity(var8);
         if (var11 instanceof Container) {
            var7 = (Container)var11;
            if (var7 instanceof ChestBlockEntity && var10 instanceof ChestBlock) {
               var7 = ChestBlock.getContainer(var9, var0, var8, true);
            }
         }
      }

      if (var7 == null) {
         List var12 = var0.getEntities((Entity)null, new AABB(var1 - 0.5D, var3 - 0.5D, var5 - 0.5D, var1 + 0.5D, var3 + 0.5D, var5 + 0.5D), EntitySelector.CONTAINER_ENTITY_SELECTOR);
         if (!var12.isEmpty()) {
            var7 = (Container)var12.get(var0.random.nextInt(var12.size()));
         }
      }

      return (Container)var7;
   }

   private static boolean canMergeItems(ItemStack var0, ItemStack var1) {
      if (var0.getItem() != var1.getItem()) {
         return false;
      } else if (var0.getDamageValue() != var1.getDamageValue()) {
         return false;
      } else if (var0.getCount() > var0.getMaxStackSize()) {
         return false;
      } else {
         return ItemStack.tagMatches(var0, var1);
      }
   }

   public double getLevelX() {
      return (double)this.worldPosition.getX() + 0.5D;
   }

   public double getLevelY() {
      return (double)this.worldPosition.getY() + 0.5D;
   }

   public double getLevelZ() {
      return (double)this.worldPosition.getZ() + 0.5D;
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

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   public void entityInside(Entity var1) {
      if (var1 instanceof ItemEntity) {
         BlockPos var2 = this.getBlockPos();
         if (Shapes.joinIsNotEmpty(Shapes.create(var1.getBoundingBox().move((double)(-var2.getX()), (double)(-var2.getY()), (double)(-var2.getZ()))), this.getSuckShape(), BooleanOp.AND)) {
            this.tryMoveItems(() -> {
               return addItem(this, (ItemEntity)var1);
            });
         }
      }

   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new HopperMenu(var1, var2, this);
   }
}
