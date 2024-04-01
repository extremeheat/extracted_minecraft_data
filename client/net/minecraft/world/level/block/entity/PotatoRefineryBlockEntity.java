package net.minecraft.world.level.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.SingleKeyCache;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.PotatoRefineryMenu;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.PotatoRefinementRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PotatoRefineryBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PotatoRefineryBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
   protected static final int SLOT_INPUT = 0;
   protected static final int SLOT_BOTTLE_INPUT = 2;
   protected static final int SLOT_FUEL = 1;
   protected static final int SLOT_RESULT = 3;
   public static final int DATA_LIT_TIME = 0;
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{3, 1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1, 2};
   public static final int DATA_LIT_DURATION = 1;
   public static final int DATA_COOKING_PROGRESS = 2;
   public static final int DATA_COOKING_TOTAL_TIME = 3;
   public static final int NUM_DATA_VALUES = 4;
   public static final int BURN_TIME_STANDARD = 200;
   public static final int BURN_COOL_SPEED = 2;
   protected NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
   int litTime;
   int litDuration;
   int cookingProgress;
   int cookingTotalTime;
   protected final ContainerData dataAccess = new ContainerData() {
      @Override
      public int get(int var1) {
         switch(var1) {
            case 0:
               return PotatoRefineryBlockEntity.this.litTime;
            case 1:
               return PotatoRefineryBlockEntity.this.litDuration;
            case 2:
               return PotatoRefineryBlockEntity.this.cookingProgress;
            case 3:
               return PotatoRefineryBlockEntity.this.cookingTotalTime;
            default:
               return 0;
         }
      }

      @Override
      public void set(int var1, int var2) {
         switch(var1) {
            case 0:
               PotatoRefineryBlockEntity.this.litTime = var2;
               break;
            case 1:
               PotatoRefineryBlockEntity.this.litDuration = var2;
               break;
            case 2:
               PotatoRefineryBlockEntity.this.cookingProgress = var2;
               break;
            case 3:
               PotatoRefineryBlockEntity.this.cookingTotalTime = var2;
         }
      }

      @Override
      public int getCount() {
         return 4;
      }
   };
   private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap();
   private float storedExperience;
   private final SingleKeyCache<PotatoRefineryBlockEntity.RecipeKey, PotatoRefineryBlockEntity.SomeKindOfRecipe> newQuickCheck;

   @Override
   protected Component getDefaultName() {
      return Component.translatable("container.potato_refinery");
   }

   @Override
   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new PotatoRefineryMenu(var1, var2, this, this.dataAccess);
   }

   public PotatoRefineryBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.POTATO_REFINERY, var1, var2);
      RecipeManager.CachedCheck var3 = RecipeManager.createCheck(RecipeType.POTATO_REFINEMENT);
      this.newQuickCheck = Util.singleKeyCache(var2x -> {
         boolean var3xx = !var2x.inputStack.isEmpty() && !var2x.bottleInputStack.isEmpty();
         if (var3xx) {
            ItemStack var4 = var2x.bottleInputStack;
            if (var2x.inputStack.is(Items.POTATO_OIL) && !var4.isEmpty()) {
               ItemStack var6 = var4.copyWithCount(1);
               LubricationComponent.lubricate(var6);
               return new PotatoRefineryBlockEntity.LubricationRecipe(var4.copyWithCount(1), var6);
            }

            RecipeHolder var5 = (RecipeHolder)var3.getRecipeFor(this, var2x.level).orElse(null);
            if (var5 != null) {
               return new PotatoRefineryBlockEntity.NormalRecipe(var5);
            }
         }

         return null;
      });
   }

   private PotatoRefineryBlockEntity.SomeKindOfRecipe quickCheckRecipe(Level var1) {
      return this.newQuickCheck.getValue(new PotatoRefineryBlockEntity.RecipeKey(var1, this.items.get(0), this.items.get(2)));
   }

   public static Map<Item, Integer> getFuel() {
      return FurnaceBlockEntity.getFuel();
   }

   public float harvestExperience() {
      float var1 = this.storedExperience;
      this.storedExperience = 0.0F;
      return var1;
   }

   private boolean isLit() {
      return this.litTime > 0;
   }

   @Override
   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      super.load(var1, var2);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var1, this.items, var2);
      this.litTime = var1.getShort("BurnTime");
      this.cookingProgress = var1.getShort("CookTime");
      this.cookingTotalTime = var1.getShort("CookTimeTotal");
      this.litDuration = this.getBurnDuration(this.items.get(1));
      CompoundTag var3 = var1.getCompound("RecipesUsed");

      for(String var5 : var3.getAllKeys()) {
         this.recipesUsed.put(new ResourceLocation(var5), var3.getInt(var5));
      }

      this.storedExperience = var1.contains("StoredExperience") ? var1.getFloat("StoredExperience") : 0.0F;
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putShort("BurnTime", (short)this.litTime);
      var1.putShort("CookTime", (short)this.cookingProgress);
      var1.putShort("CookTimeTotal", (short)this.cookingTotalTime);
      ContainerHelper.saveAllItems(var1, this.items, var2);
      CompoundTag var3 = new CompoundTag();
      this.recipesUsed.forEach((var1x, var2x) -> var3.putInt(var1x.toString(), var2x));
      var1.put("RecipesUsed", var3);
      var1.putFloat("StoredExperience", this.storedExperience);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void serverTick(Level var0, BlockPos var1, BlockState var2, PotatoRefineryBlockEntity var3) {
      boolean var4 = var3.isLit();
      boolean var5 = false;
      if (var3.isLit()) {
         --var3.litTime;
      }

      ItemStack var6 = var3.items.get(1);
      boolean var7 = var3.hasIngredients();
      boolean var8 = !var6.isEmpty();
      if (var3.isLit() || var8 && var7) {
         PotatoRefineryBlockEntity.SomeKindOfRecipe var9 = var3.quickCheckRecipe(var0);
         int var10 = var3.getMaxStackSize();
         if (!var3.isLit() && canRefine(var0.registryAccess(), var9, var3.items, var10)) {
            var3.litTime = var3.getBurnDuration(var6);
            var3.litDuration = var3.litTime;
            if (var3.isLit()) {
               var5 = true;
               if (var8) {
                  Item var11 = var6.getItem();
                  var6.shrink(1);
                  if (var6.isEmpty()) {
                     Item var12 = var11.getCraftingRemainingItem();
                     var3.items.set(1, var12 == null ? ItemStack.EMPTY : new ItemStack(var12));
                  }
               }
            }
         }

         if (var3.isLit() && canRefine(var0.registryAccess(), var9, var3.items, var10)) {
            ++var3.cookingProgress;
            if (var3.cookingProgress == var3.cookingTotalTime) {
               var3.cookingProgress = 0;
               var3.cookingTotalTime = getTotalRefinementTime(var0, var3);
               if (refine(var0.registryAccess(), var9, var3.items, var10) && var9 instanceof PotatoRefineryBlockEntity.NormalRecipe var13) {
                  var3.setRecipeUsed(var13.recipeHolder);
               }

               var5 = true;
            }
         } else {
            var3.cookingProgress = 0;
         }
      } else if (!var3.isLit() && var3.cookingProgress > 0) {
         var3.cookingProgress = Mth.clamp(var3.cookingProgress - 2, 0, var3.cookingTotalTime);
      }

      if (var4 != var3.isLit()) {
         var5 = true;
         var2 = var2.setValue(PotatoRefineryBlock.LIT, Boolean.valueOf(var3.isLit()));
         var0.setBlock(var1, var2, 3);
      }

      if (var5) {
         setChanged(var0, var1, var2);
      }
   }

   private boolean hasIngredients() {
      return !this.items.get(0).isEmpty() && !this.items.get(2).isEmpty();
   }

   private static boolean canRefine(RegistryAccess var0, @Nullable PotatoRefineryBlockEntity.SomeKindOfRecipe var1, NonNullList<ItemStack> var2, int var3) {
      if (!((ItemStack)var2.get(0)).isEmpty() && !((ItemStack)var2.get(2)).isEmpty() && var1 != null) {
         ItemStack var4 = var1.getResultItem(var0);
         if (var4.isEmpty()) {
            return false;
         } else {
            ItemStack var5 = (ItemStack)var2.get(3);
            if (var5.isEmpty()) {
               return true;
            } else if (!ItemStack.isSameItemSameComponents(var5, var4)) {
               return false;
            } else if (var5.getCount() < var3 && var5.getCount() < var5.getMaxStackSize()) {
               return true;
            } else {
               return var5.getCount() < var4.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private static boolean refine(RegistryAccess var0, @Nullable PotatoRefineryBlockEntity.SomeKindOfRecipe var1, NonNullList<ItemStack> var2, int var3) {
      if (var1 != null && canRefine(var0, var1, var2, var3)) {
         ItemStack var4 = (ItemStack)var2.get(0);
         ItemStack var5 = (ItemStack)var2.get(2);
         ItemStack var6 = var1.getResultItem(var0);
         ItemStack var7 = (ItemStack)var2.get(3);
         if (var7.isEmpty()) {
            var2.set(3, var6.copy());
         } else if (ItemStack.isSameItemSameComponents(var7, var6)) {
            var7.grow(1);
         }

         var4.shrink(1);
         var5.shrink(1);
         return true;
      } else {
         return false;
      }
   }

   protected int getBurnDuration(ItemStack var1) {
      if (var1.isEmpty()) {
         return 0;
      } else {
         Item var2 = var1.getItem();
         return getFuel().getOrDefault(var2, 0);
      }
   }

   private static int getTotalRefinementTime(Level var0, PotatoRefineryBlockEntity var1) {
      PotatoRefineryBlockEntity.SomeKindOfRecipe var2 = var1.quickCheckRecipe(var0);
      return var2 != null ? var2.getTotalRefinementTime() : 20;
   }

   public static boolean isFuel(ItemStack var0) {
      return getFuel().containsKey(var0.getItem());
   }

   @Override
   public int[] getSlotsForFace(Direction var1) {
      if (var1 == Direction.DOWN) {
         return SLOTS_FOR_DOWN;
      } else {
         return var1 == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
      }
   }

   @Override
   public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
      return this.canPlaceItem(var1, var2);
   }

   @Override
   public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
      if (var3 == Direction.DOWN && var1 == 1) {
         return var2.is(Items.WATER_BUCKET) || var2.is(Items.BUCKET);
      } else {
         return true;
      }
   }

   @Override
   public int getContainerSize() {
      return this.items.size();
   }

   @Override
   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   @Override
   public void setItem(int var1, ItemStack var2) {
      ItemStack var3 = this.items.get(var1);
      boolean var4 = var2.isEmpty() || !ItemStack.isSameItemSameComponents(var3, var2);
      this.items.set(var1, var2);
      if (var2.getCount() > this.getMaxStackSize()) {
         var2.setCount(this.getMaxStackSize());
      }

      if ((var1 == 0 || var1 == 2) && var4) {
         this.cookingTotalTime = getTotalRefinementTime(this.level, this);
         this.cookingProgress = 0;
         this.setChanged();
      }
   }

   @Override
   public boolean canPlaceItem(int var1, ItemStack var2) {
      if (var1 == 3) {
         return false;
      } else if (var1 != 1) {
         return true;
      } else {
         ItemStack var3 = this.items.get(1);
         return isFuel(var2) || var2.is(Items.BUCKET) && !var3.is(Items.BUCKET);
      }
   }

   @Override
   public void setRecipeUsed(@Nullable RecipeHolder<?> var1) {
      if (var1 != null) {
         ResourceLocation var2 = var1.id();
         this.recipesUsed.addTo(var2, 1);
      }
   }

   @Nullable
   @Override
   public RecipeHolder<?> getRecipeUsed() {
      return null;
   }

   @Override
   public void awardUsedRecipes(Player var1, List<ItemStack> var2) {
   }

   @Override
   public void fillStackedContents(StackedContents var1) {
      for(ItemStack var3 : this.items) {
         var1.accountStack(var3);
      }
   }

   static record LubricationRecipe(ItemStack a, ItemStack b) implements PotatoRefineryBlockEntity.SomeKindOfRecipe {
      private final ItemStack itemStack;
      private final ItemStack result;

      LubricationRecipe(ItemStack var1, ItemStack var2) {
         super();
         this.itemStack = var1;
         this.result = var2;
      }

      @Override
      public ItemStack getResultItem(RegistryAccess var1) {
         return this.result;
      }

      @Override
      public int getTotalRefinementTime() {
         return 20;
      }
   }

   static record NormalRecipe(RecipeHolder<PotatoRefinementRecipe> a) implements PotatoRefineryBlockEntity.SomeKindOfRecipe {
      final RecipeHolder<PotatoRefinementRecipe> recipeHolder;

      NormalRecipe(RecipeHolder<PotatoRefinementRecipe> var1) {
         super();
         this.recipeHolder = var1;
      }

      @Override
      public ItemStack getResultItem(RegistryAccess var1) {
         return this.recipeHolder.value().getResultItem(var1);
      }

      @Override
      public int getTotalRefinementTime() {
         return this.recipeHolder.value().getRefinementTime();
      }
   }

   static record RecipeKey(Level a, ItemStack b, ItemStack c) {
      final Level level;
      final ItemStack inputStack;
      final ItemStack bottleInputStack;

      RecipeKey(Level var1, ItemStack var2, ItemStack var3) {
         super();
         this.level = var1;
         this.inputStack = var2;
         this.bottleInputStack = var3;
      }
   }

   interface SomeKindOfRecipe {
      ItemStack getResultItem(RegistryAccess var1);

      int getTotalRefinementTime();
   }
}
