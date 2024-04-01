package net.minecraft.world.inventory;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PotatoRefinementRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.PotatoRefineryBlockEntity;
import net.minecraft.world.phys.Vec3;

public class PotatoRefineryMenu extends RecipeBookMenu<Container> {
   public static final int INGREDIENT_SLOT = 0;
   public static final int FUEL_SLOT = 1;
   public static final int BOTTLE_SLOT = 2;
   public static final int RESULT_SLOT = 3;
   public static final int SLOT_COUNT = 4;
   public static final int DATA_COUNT = 4;
   private static final int INV_SLOT_START = 4;
   private static final int INV_SLOT_END = 31;
   private static final int USE_ROW_SLOT_START = 31;
   private static final int USE_ROW_SLOT_END = 40;
   private final Container container;
   private final ContainerData data;
   protected final Level level;
   private final RecipeType<PotatoRefinementRecipe> recipeType;
   private final RecipeBookType recipeBookType;

   public PotatoRefineryMenu(int var1, Inventory var2) {
      this(var1, var2, new SimpleContainer(4), new SimpleContainerData(4));
   }

   public PotatoRefineryMenu(int var1, Inventory var2, Container var3, ContainerData var4) {
      this(MenuType.POTATO_REFINERY, RecipeType.POTATO_REFINEMENT, RecipeBookType.FURNACE, var1, var2, var3, var4);
   }

   private PotatoRefineryMenu(
      MenuType<?> var1, RecipeType<PotatoRefinementRecipe> var2, RecipeBookType var3, int var4, Inventory var5, Container var6, ContainerData var7
   ) {
      super(var1, var4);
      this.recipeType = var2;
      this.recipeBookType = var3;
      checkContainerSize(var6, 4);
      checkContainerDataCount(var7, 4);
      this.container = var6;
      this.data = var7;
      this.level = var5.player.level();
      this.addSlot(new Slot(var6, 0, 52, 33));
      this.addSlot(new Slot(var6, 2, 107, 36));
      this.addSlot(new PotatoRefineryMenu.FuelSlot(var6, 1, 52, 71));
      this.addSlot(new PotatoRefineryMenu.ResultSlot(var5.player, var6, 3, 107, 62));

      for(int var8 = 0; var8 < 3; ++var8) {
         for(int var9 = 0; var9 < 9; ++var9) {
            this.addSlot(new Slot(var5, var9 + var8 * 9 + 9, 8 + var9 * 18, 104 + var8 * 18));
         }
      }

      for(int var10 = 0; var10 < 9; ++var10) {
         this.addSlot(new Slot(var5, var10, 8 + var10 * 18, 162));
      }

      this.addDataSlots(var7);
   }

   @Override
   public void fillCraftSlotsStackedContents(StackedContents var1) {
      if (this.container instanceof StackedContentsCompatible) {
         ((StackedContentsCompatible)this.container).fillStackedContents(var1);
      }
   }

   @Override
   public void clearCraftingContent() {
      this.getSlot(0).set(ItemStack.EMPTY);
      this.getSlot(2).set(ItemStack.EMPTY);
      this.getSlot(3).set(ItemStack.EMPTY);
   }

   @Override
   public boolean recipeMatches(RecipeHolder<? extends Recipe<Container>> var1) {
      return var1.value().matches(this.container, this.level);
   }

   @Override
   public int getResultSlotIndex() {
      return 3;
   }

   @Override
   public int getGridWidth() {
      return 1;
   }

   @Override
   public int getGridHeight() {
      return 1;
   }

   @Override
   public int getSize() {
      return 3;
   }

   @Override
   public boolean stillValid(Player var1) {
      return this.container.stillValid(var1);
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 3) {
            if (!this.moveItemStackTo(var5, 4, 40, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 != 1 && var2 != 0) {
            if (this.canRefine(var5)) {
               if (!this.moveItemStackTo(var5, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.isFuel(var5)) {
               if (!this.moveItemStackTo(var5, 1, 2, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 4 && var2 < 31) {
               if (!this.moveItemStackTo(var5, 31, 40, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 31 && var2 < 40 && !this.moveItemStackTo(var5, 4, 31, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 4, 40, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }

   protected boolean canRefine(ItemStack var1) {
      return this.level.getRecipeManager().getRecipeFor(this.recipeType, new SimpleContainer(var1), this.level).isPresent();
   }

   protected boolean isFuel(ItemStack var1) {
      return PotatoRefineryBlockEntity.isFuel(var1);
   }

   public float getBurnProgress() {
      int var1 = this.data.get(2);
      int var2 = this.data.get(3);
      return var2 != 0 && var1 != 0 ? Mth.clamp((float)var1 / (float)var2, 0.0F, 1.0F) : 0.0F;
   }

   public float getLitProgress() {
      int var1 = this.data.get(1);
      if (var1 == 0) {
         var1 = 200;
      }

      return Mth.clamp((float)this.data.get(0) / (float)var1, 0.0F, 1.0F);
   }

   public boolean isLit() {
      return this.data.get(0) > 0;
   }

   @Override
   public RecipeBookType getRecipeBookType() {
      return this.recipeBookType;
   }

   @Override
   public boolean shouldMoveToInventory(int var1) {
      return var1 != 1;
   }

   class FuelSlot extends Slot {
      public FuelSlot(Container var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
      }

      @Override
      public boolean mayPlace(ItemStack var1) {
         return PotatoRefineryMenu.this.isFuel(var1) || isBucket(var1);
      }

      @Override
      public int getMaxStackSize(ItemStack var1) {
         return isBucket(var1) ? 1 : super.getMaxStackSize(var1);
      }

      public static boolean isBucket(ItemStack var0) {
         return var0.is(Items.BUCKET);
      }
   }

   static class ResultSlot extends Slot {
      private final Player player;
      private int removeCount;

      public ResultSlot(Player var1, Container var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
         this.player = var1;
      }

      @Override
      public boolean mayPlace(ItemStack var1) {
         return false;
      }

      @Override
      public ItemStack remove(int var1) {
         if (this.hasItem()) {
            this.removeCount += Math.min(var1, this.getItem().getCount());
         }

         return super.remove(var1);
      }

      @Override
      public void onTake(Player var1, ItemStack var2) {
         this.checkTakeAchievements(var2);
         super.onTake(var1, var2);
      }

      @Override
      protected void onQuickCraft(ItemStack var1, int var2) {
         this.removeCount += var2;
         this.checkTakeAchievements(var1);
      }

      // $VF: Could not properly define all variable types!
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      protected void checkTakeAchievements(ItemStack var1) {
         var1.onCraftedBy(this.player.level(), this.player, this.removeCount);
         this.removeCount = 0;
         Player var4 = this.player;
         if (var4 instanceof ServerPlayer var2) {
            Container var5 = this.container;
            if (var5 instanceof PotatoRefineryBlockEntity var3) {
               CriteriaTriggers.POTATO_REFINED.trigger((ServerPlayer)var2, var1);
               float var6 = var3.harvestExperience();
               if (var6 > 0.0F) {
                  createExperience(((ServerPlayer)var2).serverLevel(), ((ServerPlayer)var2).position(), 1, var6);
               }
            }
         }
      }

      private static void createExperience(ServerLevel var0, Vec3 var1, int var2, float var3) {
         int var4 = Mth.floor((float)var2 * var3);
         float var5 = Mth.frac((float)var2 * var3);
         if (var5 != 0.0F && Math.random() < (double)var5) {
            ++var4;
         }

         ExperienceOrb.award(var0, var1, var4);
      }
   }
}
