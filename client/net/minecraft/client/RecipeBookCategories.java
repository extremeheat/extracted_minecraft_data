package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public enum RecipeBookCategories {
   CRAFTING_SEARCH(new ItemStack[]{new ItemStack(Items.COMPASS)}),
   CRAFTING_BUILDING_BLOCKS(new ItemStack[]{new ItemStack(Blocks.BRICKS)}),
   CRAFTING_REDSTONE(new ItemStack[]{new ItemStack(Items.REDSTONE)}),
   CRAFTING_EQUIPMENT(new ItemStack[]{new ItemStack(Items.IRON_AXE), new ItemStack(Items.GOLDEN_SWORD)}),
   CRAFTING_MISC(new ItemStack[]{new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.APPLE)}),
   FURNACE_SEARCH(new ItemStack[]{new ItemStack(Items.COMPASS)}),
   FURNACE_FOOD(new ItemStack[]{new ItemStack(Items.PORKCHOP)}),
   FURNACE_BLOCKS(new ItemStack[]{new ItemStack(Blocks.STONE)}),
   FURNACE_MISC(new ItemStack[]{new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.EMERALD)}),
   BLAST_FURNACE_SEARCH(new ItemStack[]{new ItemStack(Items.COMPASS)}),
   BLAST_FURNACE_BLOCKS(new ItemStack[]{new ItemStack(Blocks.REDSTONE_ORE)}),
   BLAST_FURNACE_MISC(new ItemStack[]{new ItemStack(Items.IRON_SHOVEL), new ItemStack(Items.GOLDEN_LEGGINGS)}),
   SMOKER_SEARCH(new ItemStack[]{new ItemStack(Items.COMPASS)}),
   SMOKER_FOOD(new ItemStack[]{new ItemStack(Items.PORKCHOP)}),
   STONECUTTER(new ItemStack[]{new ItemStack(Items.CHISELED_STONE_BRICKS)}),
   SMITHING(new ItemStack[]{new ItemStack(Items.NETHERITE_CHESTPLATE)}),
   CAMPFIRE(new ItemStack[]{new ItemStack(Items.PORKCHOP)}),
   UNKNOWN(new ItemStack[]{new ItemStack(Items.BARRIER)});

   public static final List<RecipeBookCategories> SMOKER_CATEGORIES = ImmutableList.of(SMOKER_SEARCH, SMOKER_FOOD);
   public static final List<RecipeBookCategories> BLAST_FURNACE_CATEGORIES = ImmutableList.of(BLAST_FURNACE_SEARCH, BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC);
   public static final List<RecipeBookCategories> FURNACE_CATEGORIES = ImmutableList.of(FURNACE_SEARCH, FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC);
   public static final List<RecipeBookCategories> CRAFTING_CATEGORIES = ImmutableList.of(CRAFTING_SEARCH, CRAFTING_EQUIPMENT, CRAFTING_BUILDING_BLOCKS, CRAFTING_MISC, CRAFTING_REDSTONE);
   public static final Map<RecipeBookCategories, List<RecipeBookCategories>> AGGREGATE_CATEGORIES = ImmutableMap.of(CRAFTING_SEARCH, ImmutableList.of(CRAFTING_EQUIPMENT, CRAFTING_BUILDING_BLOCKS, CRAFTING_MISC, CRAFTING_REDSTONE), FURNACE_SEARCH, ImmutableList.of(FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC), BLAST_FURNACE_SEARCH, ImmutableList.of(BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC), SMOKER_SEARCH, ImmutableList.of(SMOKER_FOOD));
   private final List<ItemStack> itemIcons;

   private RecipeBookCategories(ItemStack... var3) {
      this.itemIcons = ImmutableList.copyOf(var3);
   }

   public static List<RecipeBookCategories> getCategories(RecipeBookType var0) {
      switch(var0) {
      case CRAFTING:
         return CRAFTING_CATEGORIES;
      case FURNACE:
         return FURNACE_CATEGORIES;
      case BLAST_FURNACE:
         return BLAST_FURNACE_CATEGORIES;
      case SMOKER:
         return SMOKER_CATEGORIES;
      default:
         return ImmutableList.of();
      }
   }

   public List<ItemStack> getIconItems() {
      return this.itemIcons;
   }

   // $FF: synthetic method
   private static RecipeBookCategories[] $values() {
      return new RecipeBookCategories[]{CRAFTING_SEARCH, CRAFTING_BUILDING_BLOCKS, CRAFTING_REDSTONE, CRAFTING_EQUIPMENT, CRAFTING_MISC, FURNACE_SEARCH, FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC, BLAST_FURNACE_SEARCH, BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC, SMOKER_SEARCH, SMOKER_FOOD, STONECUTTER, SMITHING, CAMPFIRE, UNKNOWN};
   }
}
