package net.minecraft.world.level.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.SequencedSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class FuelValues {
   private final Object2IntSortedMap<Item> values;

   FuelValues(Object2IntSortedMap<Item> var1) {
      super();
      this.values = var1;
   }

   public boolean isFuel(ItemStack var1) {
      return this.values.containsKey(var1.getItem());
   }

   public SequencedSet<Item> fuelItems() {
      return Collections.unmodifiableSequencedSet(this.values.keySet());
   }

   public int burnDuration(ItemStack var1) {
      return var1.isEmpty() ? 0 : this.values.getInt(var1.getItem());
   }

   public static FuelValues vanillaBurnTimes(HolderLookup.Provider var0, FeatureFlagSet var1) {
      return vanillaBurnTimes(var0, var1, 200);
   }

   public static FuelValues vanillaBurnTimes(HolderLookup.Provider var0, FeatureFlagSet var1, int var2) {
      return (new Builder(var0, var1)).add((ItemLike)Items.LAVA_BUCKET, var2 * 100).add((ItemLike)Blocks.COAL_BLOCK, var2 * 8 * 10).add((ItemLike)Items.BLAZE_ROD, var2 * 12).add((ItemLike)Items.COAL, var2 * 8).add((ItemLike)Items.CHARCOAL, var2 * 8).add(ItemTags.LOGS, var2 * 3 / 2).add(ItemTags.BAMBOO_BLOCKS, var2 * 3 / 2).add(ItemTags.PLANKS, var2 * 3 / 2).add((ItemLike)Blocks.BAMBOO_MOSAIC, var2 * 3 / 2).add(ItemTags.WOODEN_STAIRS, var2 * 3 / 2).add((ItemLike)Blocks.BAMBOO_MOSAIC_STAIRS, var2 * 3 / 2).add(ItemTags.WOODEN_SLABS, var2 * 3 / 4).add((ItemLike)Blocks.BAMBOO_MOSAIC_SLAB, var2 * 3 / 4).add(ItemTags.WOODEN_TRAPDOORS, var2 * 3 / 2).add(ItemTags.WOODEN_PRESSURE_PLATES, var2 * 3 / 2).add(ItemTags.WOODEN_FENCES, var2 * 3 / 2).add(ItemTags.FENCE_GATES, var2 * 3 / 2).add((ItemLike)Blocks.NOTE_BLOCK, var2 * 3 / 2).add((ItemLike)Blocks.BOOKSHELF, var2 * 3 / 2).add((ItemLike)Blocks.CHISELED_BOOKSHELF, var2 * 3 / 2).add((ItemLike)Blocks.LECTERN, var2 * 3 / 2).add((ItemLike)Blocks.JUKEBOX, var2 * 3 / 2).add((ItemLike)Blocks.CHEST, var2 * 3 / 2).add((ItemLike)Blocks.TRAPPED_CHEST, var2 * 3 / 2).add((ItemLike)Blocks.CRAFTING_TABLE, var2 * 3 / 2).add((ItemLike)Blocks.DAYLIGHT_DETECTOR, var2 * 3 / 2).add(ItemTags.BANNERS, var2 * 3 / 2).add((ItemLike)Items.BOW, var2 * 3 / 2).add((ItemLike)Items.FISHING_ROD, var2 * 3 / 2).add((ItemLike)Blocks.LADDER, var2 * 3 / 2).add(ItemTags.SIGNS, var2).add(ItemTags.HANGING_SIGNS, var2 * 4).add((ItemLike)Items.WOODEN_SHOVEL, var2).add((ItemLike)Items.WOODEN_SWORD, var2).add((ItemLike)Items.WOODEN_HOE, var2).add((ItemLike)Items.WOODEN_AXE, var2).add((ItemLike)Items.WOODEN_PICKAXE, var2).add(ItemTags.WOODEN_DOORS, var2).add(ItemTags.BOATS, var2 * 6).add(ItemTags.WOOL, var2 / 2).add(ItemTags.WOODEN_BUTTONS, var2 / 2).add((ItemLike)Items.STICK, var2 / 2).add(ItemTags.SAPLINGS, var2 / 2).add((ItemLike)Items.BOWL, var2 / 2).add(ItemTags.WOOL_CARPETS, 1 + var2 / 3).add((ItemLike)Blocks.DRIED_KELP_BLOCK, 1 + var2 * 20).add((ItemLike)Items.CROSSBOW, var2 * 3 / 2).add((ItemLike)Blocks.BAMBOO, var2 / 4).add((ItemLike)Blocks.DEAD_BUSH, var2 / 2).add((ItemLike)Blocks.SCAFFOLDING, var2 / 4).add((ItemLike)Blocks.LOOM, var2 * 3 / 2).add((ItemLike)Blocks.BARREL, var2 * 3 / 2).add((ItemLike)Blocks.CARTOGRAPHY_TABLE, var2 * 3 / 2).add((ItemLike)Blocks.FLETCHING_TABLE, var2 * 3 / 2).add((ItemLike)Blocks.SMITHING_TABLE, var2 * 3 / 2).add((ItemLike)Blocks.COMPOSTER, var2 * 3 / 2).add((ItemLike)Blocks.AZALEA, var2 / 2).add((ItemLike)Blocks.FLOWERING_AZALEA, var2 / 2).add((ItemLike)Blocks.MANGROVE_ROOTS, var2 * 3 / 2).remove(ItemTags.NON_FLAMMABLE_WOOD).build();
   }

   public static class Builder {
      private final HolderLookup<Item> items;
      private final FeatureFlagSet enabledFeatures;
      private final Object2IntSortedMap<Item> values = new Object2IntLinkedOpenHashMap();

      public Builder(HolderLookup.Provider var1, FeatureFlagSet var2) {
         super();
         this.items = var1.lookupOrThrow(Registries.ITEM);
         this.enabledFeatures = var2;
      }

      public FuelValues build() {
         return new FuelValues(this.values);
      }

      public Builder remove(TagKey<Item> var1) {
         this.values.keySet().removeIf((var1x) -> {
            return var1x.builtInRegistryHolder().is(var1);
         });
         return this;
      }

      public Builder add(TagKey<Item> var1, int var2) {
         this.items.get(var1).ifPresent((var2x) -> {
            Iterator var3 = var2x.iterator();

            while(var3.hasNext()) {
               Holder var4 = (Holder)var3.next();
               this.putInternal(var2, (Item)var4.value());
            }

         });
         return this;
      }

      public Builder add(ItemLike var1, int var2) {
         Item var3 = var1.asItem();
         this.putInternal(var2, var3);
         return this;
      }

      private void putInternal(int var1, Item var2) {
         if (var2.isEnabled(this.enabledFeatures)) {
            this.values.put(var2, var1);
         }

      }
   }
}