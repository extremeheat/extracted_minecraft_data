package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BrewingStandBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
   private static final int[] SLOTS_FOR_UP = new int[]{3};
   private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
   private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
   private NonNullList<ItemStack> items;
   private int brewTime;
   private boolean[] lastPotionCount;
   private Item ingredient;
   private int fuel;
   protected final ContainerData dataAccess;

   public BrewingStandBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BREWING_STAND, var1, var2);
      this.items = NonNullList.withSize(5, ItemStack.EMPTY);
      this.dataAccess = new ContainerData() {
         public int get(int var1) {
            switch(var1) {
            case 0:
               return BrewingStandBlockEntity.this.brewTime;
            case 1:
               return BrewingStandBlockEntity.this.fuel;
            default:
               return 0;
            }
         }

         public void set(int var1, int var2) {
            switch(var1) {
            case 0:
               BrewingStandBlockEntity.this.brewTime = var2;
               break;
            case 1:
               BrewingStandBlockEntity.this.fuel = var2;
            }

         }

         public int getCount() {
            return 2;
         }
      };
   }

   protected Component getDefaultName() {
      return new TranslatableComponent("container.brewing");
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
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

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, BrewingStandBlockEntity var3) {
      ItemStack var4 = (ItemStack)var3.items.get(4);
      if (var3.fuel <= 0 && var4.is(Items.BLAZE_POWDER)) {
         var3.fuel = 20;
         var4.shrink(1);
         setChanged(var0, var1, var2);
      }

      boolean var5 = isBrewable(var3.items);
      boolean var6 = var3.brewTime > 0;
      ItemStack var7 = (ItemStack)var3.items.get(3);
      if (var6) {
         --var3.brewTime;
         boolean var8 = var3.brewTime == 0;
         if (var8 && var5) {
            doBrew(var0, var1, var3.items);
            setChanged(var0, var1, var2);
         } else if (!var5 || !var7.is(var3.ingredient)) {
            var3.brewTime = 0;
            setChanged(var0, var1, var2);
         }
      } else if (var5 && var3.fuel > 0) {
         --var3.fuel;
         var3.brewTime = 400;
         var3.ingredient = var7.getItem();
         setChanged(var0, var1, var2);
      }

      boolean[] var11 = var3.getPotionBits();
      if (!Arrays.equals(var11, var3.lastPotionCount)) {
         var3.lastPotionCount = var11;
         BlockState var9 = var2;
         if (!(var2.getBlock() instanceof BrewingStandBlock)) {
            return;
         }

         for(int var10 = 0; var10 < BrewingStandBlock.HAS_BOTTLE.length; ++var10) {
            var9 = (BlockState)var9.setValue(BrewingStandBlock.HAS_BOTTLE[var10], var11[var10]);
         }

         var0.setBlock(var1, var9, 2);
      }

   }

   private boolean[] getPotionBits() {
      boolean[] var1 = new boolean[3];

      for(int var2 = 0; var2 < 3; ++var2) {
         if (!((ItemStack)this.items.get(var2)).isEmpty()) {
            var1[var2] = true;
         }
      }

      return var1;
   }

   private static boolean isBrewable(NonNullList<ItemStack> var0) {
      ItemStack var1 = (ItemStack)var0.get(3);
      if (var1.isEmpty()) {
         return false;
      } else if (!PotionBrewing.isIngredient(var1)) {
         return false;
      } else {
         for(int var2 = 0; var2 < 3; ++var2) {
            ItemStack var3 = (ItemStack)var0.get(var2);
            if (!var3.isEmpty() && PotionBrewing.hasMix(var3, var1)) {
               return true;
            }
         }

         return false;
      }
   }

   private static void doBrew(Level var0, BlockPos var1, NonNullList<ItemStack> var2) {
      ItemStack var3 = (ItemStack)var2.get(3);

      for(int var4 = 0; var4 < 3; ++var4) {
         var2.set(var4, PotionBrewing.mix(var3, (ItemStack)var2.get(var4)));
      }

      var3.shrink(1);
      if (var3.getItem().hasCraftingRemainingItem()) {
         ItemStack var5 = new ItemStack(var3.getItem().getCraftingRemainingItem());
         if (var3.isEmpty()) {
            var3 = var5;
         } else {
            Containers.dropItemStack(var0, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), var5);
         }
      }

      var2.set(3, var3);
      var0.levelEvent(1035, var1, 0);
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var1, this.items);
      this.brewTime = var1.getShort("BrewTime");
      this.fuel = var1.getByte("Fuel");
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putShort("BrewTime", (short)this.brewTime);
      ContainerHelper.saveAllItems(var1, this.items);
      var1.putByte("Fuel", (byte)this.fuel);
      return var1;
   }

   public ItemStack getItem(int var1) {
      return var1 >= 0 && var1 < this.items.size() ? (ItemStack)this.items.get(var1) : ItemStack.EMPTY;
   }

   public ItemStack removeItem(int var1, int var2) {
      return ContainerHelper.removeItem(this.items, var1, var2);
   }

   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.items, var1);
   }

   public void setItem(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.items.size()) {
         this.items.set(var1, var2);
      }

   }

   public boolean stillValid(Player var1) {
      if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         return var1.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
      }
   }

   public boolean canPlaceItem(int var1, ItemStack var2) {
      if (var1 == 3) {
         return PotionBrewing.isIngredient(var2);
      } else if (var1 == 4) {
         return var2.is(Items.BLAZE_POWDER);
      } else {
         return (var2.is(Items.POTION) || var2.is(Items.SPLASH_POTION) || var2.is(Items.LINGERING_POTION) || var2.is(Items.GLASS_BOTTLE)) && this.getItem(var1).isEmpty();
      }
   }

   public int[] getSlotsForFace(Direction var1) {
      if (var1 == Direction.UP) {
         return SLOTS_FOR_UP;
      } else {
         return var1 == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_SIDES;
      }
   }

   public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
      return this.canPlaceItem(var1, var2);
   }

   public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
      return var1 == 3 ? var2.is(Items.GLASS_BOTTLE) : true;
   }

   public void clearContent() {
      this.items.clear();
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new BrewingStandMenu(var1, var2, this, this.dataAccess);
   }
}
