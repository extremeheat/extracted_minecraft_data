package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
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
   private static final int INGREDIENT_SLOT = 3;
   private static final int FUEL_SLOT = 4;
   private static final int[] SLOTS_FOR_UP = new int[]{3};
   private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
   private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
   public static final int FUEL_USES = 20;
   public static final int DATA_BREW_TIME = 0;
   public static final int DATA_FUEL_USES = 1;
   public static final int NUM_DATA_VALUES = 2;
   private NonNullList<ItemStack> items;
   int brewTime;
   private boolean[] lastPotionCount;
   private Item ingredient;
   int fuel;
   protected final ContainerData dataAccess;

   public BrewingStandBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BREWING_STAND, var1, var2);
      this.items = NonNullList.<ItemStack>withSize(5, ItemStack.EMPTY);
      this.dataAccess = new ContainerData() {
         public int get(int var1) {
            int var10000;
            switch (var1) {
               case 0 -> var10000 = BrewingStandBlockEntity.this.brewTime;
               case 1 -> var10000 = BrewingStandBlockEntity.this.fuel;
               default -> var10000 = 0;
            }

            return var10000;
         }

         public void set(int var1, int var2) {
            switch (var1) {
               case 0 -> BrewingStandBlockEntity.this.brewTime = var2;
               case 1 -> BrewingStandBlockEntity.this.fuel = var2;
            }

         }

         public int getCount() {
            return 2;
         }
      };
   }

   protected Component getDefaultName() {
      return Component.translatable("container.brewing");
   }

   public int getContainerSize() {
      return this.items.size();
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, BrewingStandBlockEntity var3) {
      ItemStack var4 = var3.items.get(4);
      if (var3.fuel <= 0 && var4.is(ItemTags.BREWING_FUEL)) {
         var3.fuel = 20;
         var4.shrink(1);
         setChanged(var0, var1, var2);
      }

      boolean var5 = isBrewable(var0.potionBrewing(), var3.items);
      boolean var6 = var3.brewTime > 0;
      ItemStack var7 = var3.items.get(3);
      if (var6) {
         --var3.brewTime;
         boolean var8 = var3.brewTime == 0;
         if (var8 && var5) {
            doBrew(var0, var1, var3.items);
         } else if (!var5 || !var7.is(var3.ingredient)) {
            var3.brewTime = 0;
         }

         setChanged(var0, var1, var2);
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

   private static boolean isBrewable(PotionBrewing var0, NonNullList<ItemStack> var1) {
      ItemStack var2 = (ItemStack)var1.get(3);
      if (var2.isEmpty()) {
         return false;
      } else if (!var0.isIngredient(var2)) {
         return false;
      } else {
         for(int var3 = 0; var3 < 3; ++var3) {
            ItemStack var4 = (ItemStack)var1.get(var3);
            if (!var4.isEmpty() && var0.hasMix(var4, var2)) {
               return true;
            }
         }

         return false;
      }
   }

   private static void doBrew(Level var0, BlockPos var1, NonNullList<ItemStack> var2) {
      ItemStack var3 = (ItemStack)var2.get(3);
      PotionBrewing var4 = var0.potionBrewing();

      for(int var5 = 0; var5 < 3; ++var5) {
         var2.set(var5, var4.mix(var3, (ItemStack)var2.get(var5)));
      }

      var3.shrink(1);
      ItemStack var6 = var3.getItem().getCraftingRemainder();
      if (!var6.isEmpty()) {
         if (var3.isEmpty()) {
            var3 = var6;
         } else {
            Containers.dropItemStack(var0, (double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), var6);
         }
      }

      var2.set(3, var3);
      var0.levelEvent(1035, var1, 0);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var1, this.items, var2);
      this.brewTime = var1.getShort("BrewTime");
      if (this.brewTime > 0) {
         this.ingredient = ((ItemStack)this.items.get(3)).getItem();
      }

      this.fuel = var1.getByte("Fuel");
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putShort("BrewTime", (short)this.brewTime);
      ContainerHelper.saveAllItems(var1, this.items, var2);
      var1.putByte("Fuel", (byte)this.fuel);
   }

   public boolean canPlaceItem(int var1, ItemStack var2) {
      if (var1 == 3) {
         PotionBrewing var3 = this.level != null ? this.level.potionBrewing() : PotionBrewing.EMPTY;
         return var3.isIngredient(var2);
      } else if (var1 == 4) {
         return var2.is(ItemTags.BREWING_FUEL);
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

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new BrewingStandMenu(var1, var2, this, this.dataAccess);
   }
}
