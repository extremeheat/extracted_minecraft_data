package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
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
import net.minecraft.world.level.ItemLike;
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
   protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   int litTime;
   int litDuration;
   int cookingProgress;
   int cookingTotalTime;
   @Nullable
   private static volatile Map<Item, Integer> fuelCache;
   protected final ContainerData dataAccess = new ContainerData() {
      @Override
      public int get(int var1) {
         switch(var1) {
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
         switch(var1) {
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
   private final RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> quickCheck;

   protected AbstractFurnaceBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3, RecipeType<? extends AbstractCookingRecipe> var4) {
      super(var1, var2, var3);
      this.quickCheck = RecipeManager.createCheck(var4);
   }

   public static void invalidateCache() {
      fuelCache = null;
   }

   public static Map<Item, Integer> getFuel() {
      Map var0 = fuelCache;
      if (var0 != null) {
         return var0;
      } else {
         LinkedHashMap var1 = Maps.newLinkedHashMap();
         add(var1, Items.LAVA_BUCKET, 20000);
         add(var1, Blocks.COAL_BLOCK, 16000);
         add(var1, Items.BLAZE_ROD, 2400);
         add(var1, Items.COAL, 1600);
         add(var1, Items.CHARCOAL, 1600);
         add(var1, ItemTags.LOGS, 300);
         add(var1, ItemTags.BAMBOO_BLOCKS, 300);
         add(var1, ItemTags.PLANKS, 300);
         add(var1, Blocks.BAMBOO_MOSAIC, 300);
         add(var1, ItemTags.WOODEN_STAIRS, 300);
         add(var1, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
         add(var1, ItemTags.WOODEN_SLABS, 150);
         add(var1, Blocks.BAMBOO_MOSAIC_SLAB, 150);
         add(var1, ItemTags.WOODEN_TRAPDOORS, 300);
         add(var1, ItemTags.WOODEN_PRESSURE_PLATES, 300);
         add(var1, ItemTags.WOODEN_FENCES, 300);
         add(var1, ItemTags.FENCE_GATES, 300);
         add(var1, Blocks.NOTE_BLOCK, 300);
         add(var1, Blocks.BOOKSHELF, 300);
         add(var1, Blocks.CHISELED_BOOKSHELF, 300);
         add(var1, Blocks.LECTERN, 300);
         add(var1, Blocks.JUKEBOX, 300);
         add(var1, Blocks.CHEST, 300);
         add(var1, Blocks.TRAPPED_CHEST, 300);
         add(var1, Blocks.CRAFTING_TABLE, 300);
         add(var1, Blocks.DAYLIGHT_DETECTOR, 300);
         add(var1, ItemTags.BANNERS, 300);
         add(var1, Items.BOW, 300);
         add(var1, Items.FISHING_ROD, 300);
         add(var1, Blocks.LADDER, 300);
         add(var1, ItemTags.SIGNS, 200);
         add(var1, ItemTags.HANGING_SIGNS, 800);
         add(var1, Items.WOODEN_SHOVEL, 200);
         add(var1, Items.WOODEN_SWORD, 200);
         add(var1, Items.WOODEN_HOE, 200);
         add(var1, Items.WOODEN_AXE, 200);
         add(var1, Items.WOODEN_PICKAXE, 200);
         add(var1, ItemTags.WOODEN_DOORS, 200);
         add(var1, ItemTags.BOATS, 1200);
         add(var1, ItemTags.WOOL, 100);
         add(var1, ItemTags.WOODEN_BUTTONS, 100);
         add(var1, Items.STICK, 100);
         add(var1, ItemTags.SAPLINGS, 100);
         add(var1, Items.BOWL, 100);
         add(var1, ItemTags.WOOL_CARPETS, 67);
         add(var1, Blocks.DRIED_KELP_BLOCK, 4001);
         add(var1, Items.CROSSBOW, 300);
         add(var1, Blocks.BAMBOO, 50);
         add(var1, Blocks.DEAD_BUSH, 100);
         add(var1, Blocks.SCAFFOLDING, 50);
         add(var1, Blocks.LOOM, 300);
         add(var1, Blocks.BARREL, 300);
         add(var1, Blocks.CARTOGRAPHY_TABLE, 300);
         add(var1, Blocks.FLETCHING_TABLE, 300);
         add(var1, Blocks.SMITHING_TABLE, 300);
         add(var1, Blocks.COMPOSTER, 300);
         add(var1, Blocks.AZALEA, 100);
         add(var1, Blocks.FLOWERING_AZALEA, 100);
         add(var1, Blocks.MANGROVE_ROOTS, 300);
         fuelCache = var1;
         return var1;
      }
   }

   private static boolean isNeverAFurnaceFuel(Item var0) {
      return var0.builtInRegistryHolder().is(ItemTags.NON_FLAMMABLE_WOOD);
   }

   private static void add(Map<Item, Integer> var0, TagKey<Item> var1, int var2) {
      for(Holder var4 : BuiltInRegistries.ITEM.getTagOrEmpty(var1)) {
         if (!isNeverAFurnaceFuel((Item)var4.value())) {
            var0.put((Item)var4.value(), var2);
         }
      }
   }

   private static void add(Map<Item, Integer> var0, ItemLike var1, int var2) {
      Item var3 = var1.asItem();
      if (isNeverAFurnaceFuel(var3)) {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw (IllegalStateException)Util.pauseInIde(
               new IllegalStateException(
                  "A developer tried to explicitly make fire resistant item " + var3.getName(null).getString() + " a furnace fuel. That will not work!"
               )
            );
         }
      } else {
         var0.put(var3, var2);
      }
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
         --var3.litTime;
      }

      ItemStack var6 = var3.items.get(1);
      boolean var7 = !var3.items.get(0).isEmpty();
      boolean var8 = !var6.isEmpty();
      if (var3.isLit() || var8 && var7) {
         RecipeHolder var9;
         if (var7) {
            var9 = (RecipeHolder)var3.quickCheck.getRecipeFor(var3, var0).orElse(null);
         } else {
            var9 = null;
         }

         int var10 = var3.getMaxStackSize();
         if (!var3.isLit() && canBurn(var0.registryAccess(), var9, var3.items, var10)) {
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

         if (var3.isLit() && canBurn(var0.registryAccess(), var9, var3.items, var10)) {
            ++var3.cookingProgress;
            if (var3.cookingProgress == var3.cookingTotalTime) {
               var3.cookingProgress = 0;
               var3.cookingTotalTime = getTotalCookTime(var0, var3);
               if (burn(var0, var9, var3.items, var10)) {
                  var3.setRecipeUsed(var9);
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
            } else if (var5.is(Items.TOXIC_RESIN)) {
               return false;
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

   private static boolean burn(Level var0, @Nullable RecipeHolder<?> var1, NonNullList<ItemStack> var2, int var3) {
      RegistryAccess var4 = var0.registryAccess();
      if (var1 != null && canBurn(var4, var1, var2, var3)) {
         ItemStack var5 = (ItemStack)var2.get(0);
         ItemStack var6 = var1.value().getResultItem(var4);
         if (var6.is(Items.TOXIC_RESIN)) {
            var6.set(DataComponents.RESIN, new FletchingBlockEntity.Resin('a', FletchingBlockEntity.Resin.getRandomImpurities(var0.getRandom())));
         }

         ItemStack var7 = (ItemStack)var2.get(2);
         if (var7.isEmpty()) {
            var2.set(2, var6.copy());
         } else if (ItemStack.isSameItemSameComponents(var7, var6)) {
            var7.grow(1);
         }

         if (var5.is(Blocks.WET_SPONGE.asItem()) && !((ItemStack)var2.get(1)).isEmpty() && ((ItemStack)var2.get(1)).is(Items.BUCKET)) {
            var2.set(1, new ItemStack(Items.WATER_BUCKET));
         }

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

   private static int getTotalCookTime(Level var0, AbstractFurnaceBlockEntity var1) {
      return var1.quickCheck.getRecipeFor(var1, var0).map(var0x -> ((AbstractCookingRecipe)var0x.value()).getCookingTime()).orElse(200);
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
      ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

      while(var4.hasNext()) {
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
         ++var4;
      }

      ExperienceOrb.award(var0, var1, var4);
   }

   @Override
   public void fillStackedContents(StackedContents var1) {
      for(ItemStack var3 : this.items) {
         var1.accountStack(var3);
      }
   }
}
