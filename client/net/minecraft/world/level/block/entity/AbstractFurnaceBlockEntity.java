package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
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
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

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
   private final Map<ResourceLocation, Integer> recipesUsed;
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
      this.recipesUsed = Maps.newHashMap();
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
      add(var0, (Tag)ItemTags.BOATS, 200);
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
      add(var0, (ItemLike)Blocks.SCAFFOLDING, 50);
      add(var0, (ItemLike)Blocks.LOOM, 300);
      add(var0, (ItemLike)Blocks.BARREL, 300);
      add(var0, (ItemLike)Blocks.CARTOGRAPHY_TABLE, 300);
      add(var0, (ItemLike)Blocks.FLETCHING_TABLE, 300);
      add(var0, (ItemLike)Blocks.SMITHING_TABLE, 300);
      add(var0, (ItemLike)Blocks.COMPOSTER, 300);
      return var0;
   }

   private static void add(Map<Item, Integer> var0, Tag<Item> var1, int var2) {
      Iterator var3 = var1.getValues().iterator();

      while(var3.hasNext()) {
         Item var4 = (Item)var3.next();
         var0.put(var4, var2);
      }

   }

   private static void add(Map<Item, Integer> var0, ItemLike var1, int var2) {
      var0.put(var1.asItem(), var2);
   }

   private boolean isLit() {
      return this.litTime > 0;
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var1, this.items);
      this.litTime = var1.getShort("BurnTime");
      this.cookingProgress = var1.getShort("CookTime");
      this.cookingTotalTime = var1.getShort("CookTimeTotal");
      this.litDuration = this.getBurnDuration((ItemStack)this.items.get(1));
      short var2 = var1.getShort("RecipesUsedSize");

      for(int var3 = 0; var3 < var2; ++var3) {
         ResourceLocation var4 = new ResourceLocation(var1.getString("RecipeLocation" + var3));
         int var5 = var1.getInt("RecipeAmount" + var3);
         this.recipesUsed.put(var4, var5);
      }

   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.putShort("BurnTime", (short)this.litTime);
      var1.putShort("CookTime", (short)this.cookingProgress);
      var1.putShort("CookTimeTotal", (short)this.cookingTotalTime);
      ContainerHelper.saveAllItems(var1, this.items);
      var1.putShort("RecipesUsedSize", (short)this.recipesUsed.size());
      int var2 = 0;

      for(Iterator var3 = this.recipesUsed.entrySet().iterator(); var3.hasNext(); ++var2) {
         Entry var4 = (Entry)var3.next();
         var1.putString("RecipeLocation" + var2, ((ResourceLocation)var4.getKey()).toString());
         var1.putInt("RecipeAmount" + var2, (Integer)var4.getValue());
      }

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
         this.recipesUsed.compute(var1.getId(), (var0, var1x) -> {
            return 1 + (var1x == null ? 0 : var1x);
         });
      }

   }

   @Nullable
   public Recipe<?> getRecipeUsed() {
      return null;
   }

   public void awardAndReset(Player var1) {
   }

   public void awardResetAndExperience(Player var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.recipesUsed.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var1.level.getRecipeManager().byKey((ResourceLocation)var4.getKey()).ifPresent((var3x) -> {
            var2.add(var3x);
            createExperience(var1, (Integer)var4.getValue(), ((AbstractCookingRecipe)var3x).getExperience());
         });
      }

      var1.awardRecipes(var2);
      this.recipesUsed.clear();
   }

   private static void createExperience(Player var0, int var1, float var2) {
      int var3;
      if (var2 == 0.0F) {
         var1 = 0;
      } else if (var2 < 1.0F) {
         var3 = Mth.floor((float)var1 * var2);
         if (var3 < Mth.ceil((float)var1 * var2) && Math.random() < (double)((float)var1 * var2 - (float)var3)) {
            ++var3;
         }

         var1 = var3;
      }

      while(var1 > 0) {
         var3 = ExperienceOrb.getExperienceValue(var1);
         var1 -= var3;
         var0.level.addFreshEntity(new ExperienceOrb(var0.level, var0.x, var0.y + 0.5D, var0.z + 0.5D, var3));
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
