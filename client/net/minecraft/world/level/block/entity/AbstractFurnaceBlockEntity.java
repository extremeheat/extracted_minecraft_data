package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.item.crafting.Recipe;
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
   protected NonNullList<ItemStack> items;
   int litTime;
   int litDuration;
   int cookingProgress;
   int cookingTotalTime;
   protected final ContainerData dataAccess;
   private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed;
   private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> quickCheck;

   protected AbstractFurnaceBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3, RecipeType<? extends AbstractCookingRecipe> var4) {
      super(var1, var2, var3);
      this.items = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
      this.litDuration = 0;
      this.dataAccess = new ContainerData() {
         public int get(int var1) {
            switch (var1) {
               case 0 -> {
                  return AbstractFurnaceBlockEntity.this.litTime;
               }
               case 1 -> {
                  return AbstractFurnaceBlockEntity.this.litDuration;
               }
               case 2 -> {
                  return AbstractFurnaceBlockEntity.this.cookingProgress;
               }
               case 3 -> {
                  return AbstractFurnaceBlockEntity.this.cookingTotalTime;
               }
               default -> {
                  return 0;
               }
            }
         }

         public void set(int var1, int var2) {
            switch (var1) {
               case 0 -> AbstractFurnaceBlockEntity.this.litTime = var2;
               case 1 -> AbstractFurnaceBlockEntity.this.litDuration = var2;
               case 2 -> AbstractFurnaceBlockEntity.this.cookingProgress = var2;
               case 3 -> AbstractFurnaceBlockEntity.this.cookingTotalTime = var2;
            }

         }

         public int getCount() {
            return 4;
         }
      };
      this.recipesUsed = new Reference2IntOpenHashMap();
      this.quickCheck = RecipeManager.<SingleRecipeInput, AbstractCookingRecipe>createCheck(var4);
   }

   private boolean isLit() {
      return this.litTime > 0;
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var1, this.items, var2);
      this.litTime = var1.getShort("BurnTime");
      this.cookingProgress = var1.getShort("CookTime");
      this.cookingTotalTime = var1.getShort("CookTimeTotal");
      this.litDuration = 0;
      CompoundTag var3 = var1.getCompound("RecipesUsed");

      for(String var5 : var3.getAllKeys()) {
         this.recipesUsed.put(ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(var5)), var3.getInt(var5));
      }

   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putShort("BurnTime", (short)this.litTime);
      var1.putShort("CookTime", (short)this.cookingProgress);
      var1.putShort("CookTimeTotal", (short)this.cookingTotalTime);
      ContainerHelper.saveAllItems(var1, this.items, var2);
      CompoundTag var3 = new CompoundTag();
      this.recipesUsed.forEach((var1x, var2x) -> var3.putInt(var1x.location().toString(), var2x));
      var1.put("RecipesUsed", var3);
   }

   public static void serverTick(ServerLevel var0, BlockPos var1, BlockState var2, AbstractFurnaceBlockEntity var3) {
      boolean var4 = var3.isLit();
      boolean var5 = false;
      if (var3.isLit()) {
         --var3.litTime;
      }

      ItemStack var6 = var3.items.get(1);
      ItemStack var7 = var3.items.get(0);
      boolean var8 = !var7.isEmpty();
      boolean var9 = !var6.isEmpty();
      if (var3.litDuration == 0) {
         var3.litDuration = var3.getBurnDuration(var0.fuelValues(), var6);
      }

      if (var3.isLit() || var9 && var8) {
         SingleRecipeInput var11 = new SingleRecipeInput(var7);
         RecipeHolder var10;
         if (var8) {
            var10 = (RecipeHolder)var3.quickCheck.getRecipeFor(var11, var0).orElse((Object)null);
         } else {
            var10 = null;
         }

         int var12 = var3.getMaxStackSize();
         if (!var3.isLit() && canBurn(var0.registryAccess(), var10, var11, var3.items, var12)) {
            var3.litTime = var3.getBurnDuration(var0.fuelValues(), var6);
            var3.litDuration = var3.litTime;
            if (var3.isLit()) {
               var5 = true;
               if (var9) {
                  Item var13 = var6.getItem();
                  var6.shrink(1);
                  if (var6.isEmpty()) {
                     var3.items.set(1, var13.getCraftingRemainder());
                  }
               }
            }
         }

         if (var3.isLit() && canBurn(var0.registryAccess(), var10, var11, var3.items, var12)) {
            ++var3.cookingProgress;
            if (var3.cookingProgress == var3.cookingTotalTime) {
               var3.cookingProgress = 0;
               var3.cookingTotalTime = getTotalCookTime(var0, var3);
               if (burn(var0.registryAccess(), var10, var11, var3.items, var12)) {
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
         var2 = (BlockState)var2.setValue(AbstractFurnaceBlock.LIT, var3.isLit());
         var0.setBlock(var1, var2, 3);
      }

      if (var5) {
         setChanged(var0, var1, var2);
      }

   }

   private static boolean canBurn(RegistryAccess var0, @Nullable RecipeHolder<? extends AbstractCookingRecipe> var1, SingleRecipeInput var2, NonNullList<ItemStack> var3, int var4) {
      if (!((ItemStack)var3.get(0)).isEmpty() && var1 != null) {
         ItemStack var5 = ((AbstractCookingRecipe)var1.value()).assemble(var2, var0);
         if (var5.isEmpty()) {
            return false;
         } else {
            ItemStack var6 = (ItemStack)var3.get(2);
            if (var6.isEmpty()) {
               return true;
            } else if (!ItemStack.isSameItemSameComponents(var6, var5)) {
               return false;
            } else if (var6.getCount() < var4 && var6.getCount() < var6.getMaxStackSize()) {
               return true;
            } else {
               return var6.getCount() < var5.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private static boolean burn(RegistryAccess var0, @Nullable RecipeHolder<? extends AbstractCookingRecipe> var1, SingleRecipeInput var2, NonNullList<ItemStack> var3, int var4) {
      if (var1 != null && canBurn(var0, var1, var2, var3, var4)) {
         ItemStack var5 = (ItemStack)var3.get(0);
         ItemStack var6 = ((AbstractCookingRecipe)var1.value()).assemble(var2, var0);
         ItemStack var7 = (ItemStack)var3.get(2);
         if (var7.isEmpty()) {
            var3.set(2, var6.copy());
         } else if (ItemStack.isSameItemSameComponents(var7, var6)) {
            var7.grow(1);
         }

         if (var5.is(Blocks.WET_SPONGE.asItem()) && !((ItemStack)var3.get(1)).isEmpty() && ((ItemStack)var3.get(1)).is(Items.BUCKET)) {
            var3.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         var5.shrink(1);
         return true;
      } else {
         return false;
      }
   }

   protected int getBurnDuration(FuelValues var1, ItemStack var2) {
      return var1.burnDuration(var2);
   }

   private static int getTotalCookTime(ServerLevel var0, AbstractFurnaceBlockEntity var1) {
      SingleRecipeInput var2 = new SingleRecipeInput(var1.getItem(0));
      return (Integer)var1.quickCheck.getRecipeFor(var2, var0).map((var0x) -> ((AbstractCookingRecipe)var0x.value()).cookingTime()).orElse(200);
   }

   public int[] getSlotsForFace(Direction var1) {
      if (var1 == Direction.DOWN) {
         return SLOTS_FOR_DOWN;
      } else {
         return var1 == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
      }
   }

   public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
      return this.canPlaceItem(var1, var2);
   }

   public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
      if (var3 == Direction.DOWN && var1 == 1) {
         return var2.is(Items.WATER_BUCKET) || var2.is(Items.BUCKET);
      } else {
         return true;
      }
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

   public void setItem(int var1, ItemStack var2) {
      ItemStack var3 = this.items.get(var1);
      boolean var4 = !var2.isEmpty() && ItemStack.isSameItemSameComponents(var3, var2);
      this.items.set(var1, var2);
      var2.limitSize(this.getMaxStackSize(var2));
      if (var1 == 0 && !var4) {
         Level var6 = this.level;
         if (var6 instanceof ServerLevel) {
            ServerLevel var5 = (ServerLevel)var6;
            this.cookingTotalTime = getTotalCookTime(var5, this);
            this.cookingProgress = 0;
            this.setChanged();
         }
      }

   }

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

   public void setRecipeUsed(@Nullable RecipeHolder<?> var1) {
      if (var1 != null) {
         ResourceKey var2 = var1.id();
         this.recipesUsed.addTo(var2, 1);
      }

   }

   @Nullable
   public RecipeHolder<?> getRecipeUsed() {
      return null;
   }

   public void awardUsedRecipes(Player var1, List<ItemStack> var2) {
   }

   public void awardUsedRecipesAndPopExperience(ServerPlayer var1) {
      List var2 = this.getRecipesToAwardAndPopExperience(var1.serverLevel(), var1.position());
      var1.awardRecipes(var2);

      for(RecipeHolder var4 : var2) {
         if (var4 != null) {
            var1.triggerRecipeCrafted(var4, this.items);
         }
      }

      this.recipesUsed.clear();
   }

   public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel var1, Vec3 var2) {
      ArrayList var3 = Lists.newArrayList();
      ObjectIterator var4 = this.recipesUsed.reference2IntEntrySet().iterator();

      while(var4.hasNext()) {
         Reference2IntMap.Entry var5 = (Reference2IntMap.Entry)var4.next();
         var1.recipeAccess().byKey((ResourceKey)var5.getKey()).ifPresent((var4x) -> {
            var3.add(var4x);
            createExperience(var1, var2, var5.getIntValue(), ((AbstractCookingRecipe)var4x.value()).experience());
         });
      }

      return var3;
   }

   private static void createExperience(ServerLevel var0, Vec3 var1, int var2, float var3) {
      int var4 = Mth.floor((float)var2 * var3);
      float var5 = Mth.frac((float)var2 * var3);
      if (var5 != 0.0F && Math.random() < (double)var5) {
         ++var4;
      }

      ExperienceOrb.award(var0, var1, var4);
   }

   public void fillStackedContents(StackedItemContents var1) {
      for(ItemStack var3 : this.items) {
         var1.accountStack(var3);
      }

   }
}
