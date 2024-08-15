package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeCraftingHolder, StackedContentsCompatible {
   protected static final int SLOT_INPUT = 0;
   protected static final int SLOT_FUEL = 1;
   protected static final int SLOT_RESULT = 2;
   public static final int DATA_LIT_TIME = 0;
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1};
   public static final int DATA_LIT_DURATION = 1;
   public static final int DATA_COOKING_PROGRESS = 2;
   public static final int DATA_COOKING_TOTAL_TIME = 3;
   public static final int NUM_DATA_VALUES = 4;
   public static final int BURN_TIME_STANDARD = 200;
   public static final int BURN_COOL_SPEED = 2;
   public static final int UNKNOWN_LIT_DURATION = 0;
   protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   int litTime;
   int litDuration = 0;
   int cookingProgress;
   int cookingTotalTime;
   protected final ContainerData dataAccess = new ContainerData() {
      @Override
      public int get(int var1) {
         switch (var1) {
            case 0:
               return AbstractFurnaceBlockEntity.this.litTime;
            case 1:
               return AbstractFurnaceBlockEntity.this.litDuration;
            case 2:
               return AbstractFurnaceBlockEntity.this.cookingProgress;
            case 3:
               return AbstractFurnaceBlockEntity.this.cookingTotalTime;
            default:
               return 0;
         }
      }

      @Override
      public void set(int var1, int var2) {
         switch (var1) {
            case 0:
               AbstractFurnaceBlockEntity.this.litTime = var2;
               break;
            case 1:
               AbstractFurnaceBlockEntity.this.litDuration = var2;
               break;
            case 2:
               AbstractFurnaceBlockEntity.this.cookingProgress = var2;
               break;
            case 3:
               AbstractFurnaceBlockEntity.this.cookingTotalTime = var2;
         }
      }

      @Override
      public int getCount() {
         return 4;
      }
   };
   private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap();
   private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

   protected AbstractFurnaceBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3, RecipeType<? extends AbstractCookingRecipe> var4) {
      super(var1, var2, var3);
      this.quickCheck = RecipeManager.createCheck(var4);
   }

   private boolean isLit() {
      return this.litTime > 0;
   }

   @Override
   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var1, this.items, var2);
      this.litTime = var1.getShort("BurnTime");
      this.cookingProgress = var1.getShort("CookTime");
      this.cookingTotalTime = var1.getShort("CookTimeTotal");
      this.litDuration = 0;
      CompoundTag var3 = var1.getCompound("RecipesUsed");

      for (String var5 : var3.getAllKeys()) {
         this.recipesUsed.put(ResourceLocation.parse(var5), var3.getInt(var5));
      }
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
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, AbstractFurnaceBlockEntity var3) {
      boolean var4 = var3.isLit();
      boolean var5 = false;
      if (var3.isLit()) {
         var3.litTime--;
      }

      ItemStack var6 = var3.items.get(1);
      ItemStack var7 = var3.items.get(0);
      boolean var8 = !var7.isEmpty();
      boolean var9 = !var6.isEmpty();
      if (var3.litDuration == 0) {
         var3.litDuration = var3.getBurnDuration(var0.fuelValues(), var6);
      }

      if (var3.isLit() || var9 && var8) {
         RecipeHolder var10;
         if (var8) {
            var10 = var3.quickCheck.getRecipeFor(new SingleRecipeInput(var7), var0).orElse(null);
         } else {
            var10 = null;
         }

         int var11 = var3.getMaxStackSize();
         if (!var3.isLit() && canBurn(var0.registryAccess(), var10, var3.items, var11)) {
            var3.litTime = var3.getBurnDuration(var0.fuelValues(), var6);
            var3.litDuration = var3.litTime;
            if (var3.isLit()) {
               var5 = true;
               if (var9) {
                  Item var12 = var6.getItem();
                  var6.shrink(1);
                  if (var6.isEmpty()) {
                     Item var13 = var12.getCraftingRemainingItem();
                     var3.items.set(1, var13 == null ? ItemStack.EMPTY : new ItemStack(var13));
                  }
               }
            }
         }

         if (var3.isLit() && canBurn(var0.registryAccess(), var10, var3.items, var11)) {
            var3.cookingProgress++;
            if (var3.cookingProgress == var3.cookingTotalTime) {
               var3.cookingProgress = 0;
               var3.cookingTotalTime = getTotalCookTime(var0, var3);
               if (burn(var0.registryAccess(), var10, var3.items, var11)) {
                  var3.setRecipeUsed(var10);
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
         var2 = var2.setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(var3.isLit()));
         var0.setBlock(var1, var2, 3);
      }

      if (var5) {
         setChanged(var0, var1, var2);
      }
   }

   private static boolean canBurn(RegistryAccess var0, @Nullable RecipeHolder<?> var1, NonNullList<ItemStack> var2, int var3) {
      if (!((ItemStack)var2.get(0)).isEmpty() && var1 != null) {
         ItemStack var4 = var1.value().getResultItem(var0);
         if (var4.isEmpty()) {
            return false;
         } else {
            ItemStack var5 = (ItemStack)var2.get(2);
            if (var5.isEmpty()) {
               return true;
            } else if (!ItemStack.isSameItemSameComponents(var5, var4)) {
               return false;
            } else {
               return var5.getCount() < var3 && var5.getCount() < var5.getMaxStackSize() ? true : var5.getCount() < var4.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private static boolean burn(RegistryAccess var0, @Nullable RecipeHolder<?> var1, NonNullList<ItemStack> var2, int var3) {
      if (var1 != null && canBurn(var0, var1, var2, var3)) {
         ItemStack var4 = (ItemStack)var2.get(0);
         ItemStack var5 = var1.value().getResultItem(var0);
         ItemStack var6 = (ItemStack)var2.get(2);
         if (var6.isEmpty()) {
            var2.set(2, var5.copy());
         } else if (ItemStack.isSameItemSameComponents(var6, var5)) {
            var6.grow(1);
         }

         if (var4.is(Blocks.WET_SPONGE.asItem()) && !((ItemStack)var2.get(1)).isEmpty() && ((ItemStack)var2.get(1)).is(Items.BUCKET)) {
            var2.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         var4.shrink(1);
         return true;
      } else {
         return false;
      }
   }

   protected int getBurnDuration(FuelValues var1, ItemStack var2) {
      return var1.burnDuration(var2);
   }

   private static int getTotalCookTime(Level var0, AbstractFurnaceBlockEntity var1) {
      SingleRecipeInput var2 = new SingleRecipeInput(var1.getItem(0));
      return var1.quickCheck.getRecipeFor(var2, var0).map(var0x -> var0x.value().getCookingTime()).orElse(200);
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
      return var3 == Direction.DOWN && var1 == 1 ? var2.is(Items.WATER_BUCKET) || var2.is(Items.BUCKET) : true;
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
      boolean var4 = !var2.isEmpty() && ItemStack.isSameItemSameComponents(var3, var2);
      this.items.set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
      if (var1 == 0 && !var4) {
         this.cookingTotalTime = getTotalCookTime(this.level, this);
         this.cookingProgress = 0;
         this.setChanged();
      }
   }

   @Override
   public boolean canPlaceItem(int var1, ItemStack var2) {
      if (var1 == 2) {
         return false;
      } else if (var1 != 1) {
         return true;
      } else {
         ItemStack var3 = this.items.get(1);
         return this.level.fuelValues().isFuel(var2) || var2.is(Items.BUCKET) && !var3.is(Items.BUCKET);
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

   public void awardUsedRecipesAndPopExperience(ServerPlayer var1) {
      List var2 = this.getRecipesToAwardAndPopExperience(var1.serverLevel(), var1.position());
      var1.awardRecipes(var2);

      for (RecipeHolder var4 : var2) {
         if (var4 != null) {
            var1.triggerRecipeCrafted(var4, this.items);
         }
      }

      this.recipesUsed.clear();
   }

   public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel var1, Vec3 var2) {
      ArrayList var3 = Lists.newArrayList();
      ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

      while (var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var1.getRecipeManager().byKey((ResourceLocation)var5.getKey()).ifPresent(var4x -> {
            var3.add(var4x);
            createExperience(var1, var2, var5.getIntValue(), ((AbstractCookingRecipe)var4x.value()).getExperience());
         });
      }

      return var3;
   }

   private static void createExperience(ServerLevel var0, Vec3 var1, int var2, float var3) {
      int var4 = Mth.floor((float)var2 * var3);
      float var5 = Mth.frac((float)var2 * var3);
      if (var5 != 0.0F && Math.random() < (double)var5) {
         var4++;
      }

      ExperienceOrb.award(var0, var1, var4);
   }

   @Override
   public void fillStackedContents(StackedItemContents var1) {
      for (ItemStack var3 : this.items) {
         var1.accountStack(var3);
      }
   }
}
