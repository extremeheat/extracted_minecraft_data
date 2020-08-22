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
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BrewingStandBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, TickableBlockEntity {
   private static final int[] SLOTS_FOR_UP = new int[]{3};
   private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
   private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
   private NonNullList items;
   private int brewTime;
   private boolean[] lastPotionCount;
   private Item ingredient;
   private int fuel;
   protected final ContainerData dataAccess;

   public BrewingStandBlockEntity() {
      super(BlockEntityType.BREWING_STAND);
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
      return new TranslatableComponent("container.brewing", new Object[0]);
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

   public void tick() {
      ItemStack var1 = (ItemStack)this.items.get(4);
      if (this.fuel <= 0 && var1.getItem() == Items.BLAZE_POWDER) {
         this.fuel = 20;
         var1.shrink(1);
         this.setChanged();
      }

      boolean var2 = this.isBrewable();
      boolean var3 = this.brewTime > 0;
      ItemStack var4 = (ItemStack)this.items.get(3);
      if (var3) {
         --this.brewTime;
         boolean var5 = this.brewTime == 0;
         if (var5 && var2) {
            this.doBrew();
            this.setChanged();
         } else if (!var2) {
            this.brewTime = 0;
            this.setChanged();
         } else if (this.ingredient != var4.getItem()) {
            this.brewTime = 0;
            this.setChanged();
         }
      } else if (var2 && this.fuel > 0) {
         --this.fuel;
         this.brewTime = 400;
         this.ingredient = var4.getItem();
         this.setChanged();
      }

      if (!this.level.isClientSide) {
         boolean[] var8 = this.getPotionBits();
         if (!Arrays.equals(var8, this.lastPotionCount)) {
            this.lastPotionCount = var8;
            BlockState var6 = this.level.getBlockState(this.getBlockPos());
            if (!(var6.getBlock() instanceof BrewingStandBlock)) {
               return;
            }

            for(int var7 = 0; var7 < BrewingStandBlock.HAS_BOTTLE.length; ++var7) {
               var6 = (BlockState)var6.setValue(BrewingStandBlock.HAS_BOTTLE[var7], var8[var7]);
            }

            this.level.setBlock(this.worldPosition, var6, 2);
         }
      }

   }

   public boolean[] getPotionBits() {
      boolean[] var1 = new boolean[3];

      for(int var2 = 0; var2 < 3; ++var2) {
         if (!((ItemStack)this.items.get(var2)).isEmpty()) {
            var1[var2] = true;
         }
      }

      return var1;
   }

   private boolean isBrewable() {
      ItemStack var1 = (ItemStack)this.items.get(3);
      if (var1.isEmpty()) {
         return false;
      } else if (!PotionBrewing.isIngredient(var1)) {
         return false;
      } else {
         for(int var2 = 0; var2 < 3; ++var2) {
            ItemStack var3 = (ItemStack)this.items.get(var2);
            if (!var3.isEmpty() && PotionBrewing.hasMix(var3, var1)) {
               return true;
            }
         }

         return false;
      }
   }

   private void doBrew() {
      ItemStack var1 = (ItemStack)this.items.get(3);

      for(int var2 = 0; var2 < 3; ++var2) {
         this.items.set(var2, PotionBrewing.mix(var1, (ItemStack)this.items.get(var2)));
      }

      var1.shrink(1);
      BlockPos var4 = this.getBlockPos();
      if (var1.getItem().hasCraftingRemainingItem()) {
         ItemStack var3 = new ItemStack(var1.getItem().getCraftingRemainingItem());
         if (var1.isEmpty()) {
            var1 = var3;
         } else if (!this.level.isClientSide) {
            Containers.dropItemStack(this.level, (double)var4.getX(), (double)var4.getY(), (double)var4.getZ(), var3);
         }
      }

      this.items.set(3, var1);
      this.level.levelEvent(1035, var4, 0);
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
      } else {
         Item var3 = var2.getItem();
         if (var1 == 4) {
            return var3 == Items.BLAZE_POWDER;
         } else {
            return (var3 == Items.POTION || var3 == Items.SPLASH_POTION || var3 == Items.LINGERING_POTION || var3 == Items.GLASS_BOTTLE) && this.getItem(var1).isEmpty();
         }
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
      if (var1 == 3) {
         return var2.getItem() == Items.GLASS_BOTTLE;
      } else {
         return true;
      }
   }

   public void clearContent() {
      this.items.clear();
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new BrewingStandMenu(var1, var2, this, this.dataAccess);
   }
}
