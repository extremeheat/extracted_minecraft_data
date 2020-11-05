package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible, TickableBlockEntity {
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1};
   protected NonNullList<ItemStack> items;
   private int litTime;
   private int litDuration;
   private int cookingProgress;
   private int cookingTotalTime;
   protected final ContainerData dataAccess;
   private final Object2IntOpenHashMap<ResourceLocation> recipesUsed;
   protected final RecipeType<? extends AbstractCookingRecipe> recipeType;

   protected AbstractFurnaceBlockEntity(BlockEntityType<?> var1, RecipeType<? extends AbstractCookingRecipe> var2) {
      super(var1);
      this.items = NonNullList.withSize(3, ItemStack.EMPTY);
      this.dataAccess = new ContainerData() {
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

         public int getCount() {
            return 4;
         }
      };
      this.recipesUsed = new Object2IntOpenHashMap();
      this.recipeType = var2;
   }

   public static Map<Item, Integer> getFuel() {
      LinkedHashMap var0 = Maps.newLinkedHashMap();
      add(var0, (ItemLike)Items.LAVA_BUCKET, 20000);
      add(var0, (ItemLike)Blocks.COAL_BLOCK, 16000);
      add(var0, (ItemLike)Items.BLAZE_ROD, 2400);
      add(var0, (ItemLike)Items.COAL, 1600);
      add(var0, (ItemLike)Items.CHARCOAL, 1600);
      add(var0, (Tag)ItemTags.LOGS, 300);
      add(var0, (Tag)ItemTags.PLANKS, 300);
      add(var0, (Tag)ItemTags.WOODEN_STAIRS, 300);
      add(var0, (Tag)ItemTags.WOODEN_SLABS, 150);
      add(var0, (Tag)ItemTags.WOODEN_TRAPDOORS, 300);
      add(var0, (Tag)ItemTags.WOODEN_PRESSURE_PLATES, 300);
      add(var0, (ItemLike)Blocks.OAK_FENCE, 300);
      add(var0, (ItemLike)Blocks.BIRCH_FENCE, 300);
      add(var0, (ItemLike)Blocks.SPRUCE_FENCE, 300);
      add(var0, (ItemLike)Blocks.JUNGLE_FENCE, 300);
      add(var0, (ItemLike)Blocks.DARK_OAK_FENCE, 300);
      add(var0, (ItemLike)Blocks.ACACIA_FENCE, 300);
      add(var0, (ItemLike)Blocks.OAK_FENCE_GATE, 300);
      add(var0, (ItemLike)Blocks.BIRCH_FENCE_GATE, 300);
      add(var0, (ItemLike)Blocks.SPRUCE_FENCE_GATE, 300);
      add(var0, (ItemLike)Blocks.JUNGLE_FENCE_GATE, 300);
      add(var0, (ItemLike)Blocks.DARK_OAK_FENCE_GATE, 300);
      add(var0, (ItemLike)Blocks.ACACIA_FENCE_GATE, 300);
      add(var0, (ItemLike)Blocks.NOTE_BLOCK, 300);
      add(var0, (ItemLike)Blocks.BOOKSHELF, 300);
      add(var0, (ItemLike)Blocks.LECTERN, 300);
      add(var0, (ItemLike)Blocks.JUKEBOX, 300);
      add(var0, (ItemLike)Blocks.CHEST, 300);
      add(var0, (ItemLike)Blocks.TRAPPED_CHEST, 300);
      add(var0, (ItemLike)Blocks.CRAFTING_TABLE, 300);
      add(var0, (ItemLike)Blocks.DAYLIGHT_DETECTOR, 300);
      add(var0, (Tag)ItemTags.BANNERS, 300);
      add(var0, (ItemLike)Items.BOW, 300);
      add(var0, (ItemLike)Items.FISHING_ROD, 300);
      add(var0, (ItemLike)Blocks.LADDER, 300);
      add(var0, (Tag)ItemTags.SIGNS, 200);
      add(var0, (ItemLike)Items.WOODEN_SHOVEL, 200);
      add(var0, (ItemLike)Items.WOODEN_SWORD, 200);
      add(var0, (ItemLike)Items.WOODEN_HOE, 200);
      add(var0, (ItemLike)Items.WOODEN_AXE, 200);
      add(var0, (ItemLike)Items.WOODEN_PICKAXE, 200);
      add(var0, (Tag)ItemTags.WOODEN_DOORS, 200);
      add(var0, (Tag)ItemTags.BOATS, 1200);
      add(var0, (Tag)ItemTags.WOOL, 100);
      add(var0, (Tag)ItemTags.WOODEN_BUTTONS, 100);
      add(var0, (ItemLike)Items.STICK, 100);
      add(var0, (Tag)ItemTags.SAPLINGS, 100);
      add(var0, (ItemLike)Items.BOWL, 100);
      add(var0, (Tag)ItemTags.CARPETS, 67);
      add(var0, (ItemLike)Blocks.DRIED_KELP_BLOCK, 4001);
      add(var0, (ItemLike)Items.CROSSBOW, 300);
      add(var0, (ItemLike)Blocks.BAMBOO, 50);
      add(var0, (ItemLike)Blocks.DEAD_BUSH, 100);
      add(var0, (ItemLike)Blocks.SCAFFOLDING, 400);
      add(var0, (ItemLike)Blocks.LOOM, 300);
      add(var0, (ItemLike)Blocks.BARREL, 300);
      add(var0, (ItemLike)Blocks.CARTOGRAPHY_TABLE, 300);
      add(var0, (ItemLike)Blocks.FLETCHING_TABLE, 300);
      add(var0, (ItemLike)Blocks.SMITHING_TABLE, 300);
      add(var0, (ItemLike)Blocks.COMPOSTER, 300);
      return var0;
   }

   private static boolean isNeverAFurnaceFuel(Item var0) {
      return ItemTags.NON_FLAMMABLE_WOOD.contains(var0);
   }

   private static void add(Map<Item, Integer> var0, Tag<Item> var1, int var2) {
      Iterator var3 = var1.getValues().iterator();

      while(var3.hasNext()) {
         Item var4 = (Item)var3.next();
         if (!isNeverAFurnaceFuel(var4)) {
            var0.put(var4, var2);
         }
      }

   }

   private static void add(Map<Item, Integer> var0, ItemLike var1, int var2) {
      Item var3 = var1.asItem();
      if (isNeverAFurnaceFuel(var3)) {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("A developer tried to explicitly make fire resistant item " + var3.getName((ItemStack)null).getString() + " a furnace fuel. That will not work!"));
         }
      } else {
         var0.put(var3, var2);
      }
   }

   private boolean isLit() {
      return this.litTime > 0;
   }

   public void load(BlockState var1, CompoundTag var2) {
      super.load(var1, var2);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var2, this.items);
      this.litTime = var2.getShort("BurnTime");
      this.cookingProgress = var2.getShort("CookTime");
      this.cookingTotalTime = var2.getShort("CookTimeTotal");
      this.litDuration = this.getBurnDuration((ItemStack)this.items.get(1));
      CompoundTag var3 = var2.getCompound("RecipesUsed");
      Iterator var4 = var3.getAllKeys().iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         this.recipesUsed.put(new ResourceLocation(var5), var3.getInt(var5));
      }

   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putShort("BurnTime", (short)this.litTime);
      var1.putShort("CookTime", (short)this.cookingProgress);
      var1.putShort("CookTimeTotal", (short)this.cookingTotalTime);
      ContainerHelper.saveAllItems(var1, this.items);
      CompoundTag var2 = new CompoundTag();
      this.recipesUsed.forEach((var1x, var2x) -> {
         var2.putInt(var1x.toString(), var2x);
      });
      var1.put("RecipesUsed", var2);
      return var1;
   }

   public void tick() {
      boolean var1 = this.isLit();
      boolean var2 = false;
      if (this.isLit()) {
         --this.litTime;
      }

      if (!this.level.isClientSide) {
         ItemStack var3 = (ItemStack)this.items.get(1);
         if (!this.isLit() && (var3.isEmpty() || ((ItemStack)this.items.get(0)).isEmpty())) {
            if (!this.isLit() && this.cookingProgress > 0) {
               this.cookingProgress = Mth.clamp(this.cookingProgress - 2, 0, this.cookingTotalTime);
            }
         } else {
            Recipe var4 = (Recipe)this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level).orElse((Object)null);
            if (!this.isLit() && this.canBurn(var4)) {
               this.litTime = this.getBurnDuration(var3);
               this.litDuration = this.litTime;
               if (this.isLit()) {
                  var2 = true;
                  if (!var3.isEmpty()) {
                     Item var5 = var3.getItem();
                     var3.shrink(1);
                     if (var3.isEmpty()) {
                        Item var6 = var5.getCraftingRemainingItem();
                        this.items.set(1, var6 == null ? ItemStack.EMPTY : new ItemStack(var6));
                     }
                  }
               }
            }

            if (this.isLit() && this.canBurn(var4)) {
               ++this.cookingProgress;
               if (this.cookingProgress == this.cookingTotalTime) {
                  this.cookingProgress = 0;
                  this.cookingTotalTime = this.getTotalCookTime();
                  this.burn(var4);
                  var2 = true;
               }
            } else {
               this.cookingProgress = 0;
            }
         }

         if (var1 != this.isLit()) {
            var2 = true;
            this.level.setBlock(this.worldPosition, (BlockState)this.level.getBlockState(this.worldPosition).setValue(AbstractFurnaceBlock.LIT, this.isLit()), 3);
         }
      }

      if (var2) {
         this.setChanged();
      }

   }

   protected boolean canBurn(@Nullable Recipe<?> var1) {
      if (!((ItemStack)this.items.get(0)).isEmpty() && var1 != null) {
         ItemStack var2 = var1.getResultItem();
         if (var2.isEmpty()) {
            return false;
         } else {
            ItemStack var3 = (ItemStack)this.items.get(2);
            if (var3.isEmpty()) {
               return true;
            } else if (!var3.sameItem(var2)) {
               return false;
            } else if (var3.getCount() < this.getMaxStackSize() && var3.getCount() < var3.getMaxStackSize()) {
               return true;
            } else {
               return var3.getCount() < var2.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private void burn(@Nullable Recipe<?> var1) {
      if (var1 != null && this.canBurn(var1)) {
         ItemStack var2 = (ItemStack)this.items.get(0);
         ItemStack var3 = var1.getResultItem();
         ItemStack var4 = (ItemStack)this.items.get(2);
         if (var4.isEmpty()) {
            this.items.set(2, var3.copy());
         } else if (var4.getItem() == var3.getItem()) {
            var4.grow(1);
         }

         if (!this.level.isClientSide) {
            this.setRecipeUsed(var1);
         }

         if (var2.getItem() == Blocks.WET_SPONGE.asItem() && !((ItemStack)this.items.get(1)).isEmpty() && ((ItemStack)this.items.get(1)).getItem() == Items.BUCKET) {
            this.items.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         var2.shrink(1);
      }
   }

   protected int getBurnDuration(ItemStack var1) {
      if (var1.isEmpty()) {
         return 0;
      } else {
         Item var2 = var1.getItem();
         return (Integer)getFuel().getOrDefault(var2, 0);
      }
   }

   protected int getTotalCookTime() {
      return (Integer)this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level).map(AbstractCookingRecipe::getCookingTime).orElse(200);
   }

   public static boolean isFuel(ItemStack var0) {
      return getFuel().containsKey(var0.getItem());
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
         Item var4 = var2.getItem();
         if (var4 != Items.WATER_BUCKET && var4 != Items.BUCKET) {
            return false;
         }
      }

      return true;
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

   public ItemStack getItem(int var1) {
      return (ItemStack)this.items.get(var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      return ContainerHelper.removeItem(this.items, var1, var2);
   }

   public ItemStack removeItemNoUpdate(int var1) {
      return ContainerHelper.takeItem(this.items, var1);
   }

   public void setItem(int var1, ItemStack var2) {
      ItemStack var3 = (ItemStack)this.items.get(var1);
      boolean var4 = !var2.isEmpty() && var2.sameItem(var3) && ItemStack.tagMatches(var2, var3);
      this.items.set(var1, var2);
      if (var2.getCount() > this.getMaxStackSize()) {
         var2.setCount(this.getMaxStackSize());
      }

      if (var1 == 0 && !var4) {
         this.cookingTotalTime = this.getTotalCookTime();
         this.cookingProgress = 0;
         this.setChanged();
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
      if (var1 == 2) {
         return false;
      } else if (var1 != 1) {
         return true;
      } else {
         ItemStack var3 = (ItemStack)this.items.get(1);
         return isFuel(var2) || var2.getItem() == Items.BUCKET && var3.getItem() != Items.BUCKET;
      }
   }

   public void clearContent() {
      this.items.clear();
   }

   public void setRecipeUsed(@Nullable Recipe<?> var1) {
      if (var1 != null) {
         ResourceLocation var2 = var1.getId();
         this.recipesUsed.addTo(var2, 1);
      }

   }

   @Nullable
   public Recipe<?> getRecipeUsed() {
      return null;
   }

   public void awardUsedRecipes(Player var1) {
   }

   public void awardUsedRecipesAndPopExperience(Player var1) {
      List var2 = this.getRecipesToAwardAndPopExperience(var1.level, var1.position());
      var1.awardRecipes(var2);
      this.recipesUsed.clear();
   }

   public List<Recipe<?>> getRecipesToAwardAndPopExperience(Level var1, Vec3 var2) {
      ArrayList var3 = Lists.newArrayList();
      ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         var1.getRecipeManager().byKey((ResourceLocation)var5.getKey()).ifPresent((var4x) -> {
            var3.add(var4x);
            createExperience(var1, var2, var5.getIntValue(), ((AbstractCookingRecipe)var4x).getExperience());
         });
      }

      return var3;
   }

   private static void createExperience(Level var0, Vec3 var1, int var2, float var3) {
      int var4 = Mth.floor((float)var2 * var3);
      float var5 = Mth.frac((float)var2 * var3);
      if (var5 != 0.0F && Math.random() < (double)var5) {
         ++var4;
      }

      while(var4 > 0) {
         int var6 = ExperienceOrb.getExperienceValue(var4);
         var4 -= var6;
         var0.addFreshEntity(new ExperienceOrb(var0, var1.x, var1.y, var1.z, var6));
      }

   }

   public void fillStackedContents(StackedContents var1) {
      Iterator var2 = this.items.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.accountStack(var3);
      }

   }
}
