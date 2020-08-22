package net.minecraft.data.tags;

import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemTagsProvider extends TagsProvider {
   private static final Logger LOGGER = LogManager.getLogger();

   public ItemTagsProvider(DataGenerator var1) {
      super(var1, Registry.ITEM);
   }

   protected void addTags() {
      this.copy(BlockTags.WOOL, ItemTags.WOOL);
      this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
      this.copy(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
      this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
      this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
      this.copy(BlockTags.CARPETS, ItemTags.CARPETS);
      this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
      this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
      this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
      this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
      this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
      this.copy(BlockTags.DOORS, ItemTags.DOORS);
      this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
      this.copy(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
      this.copy(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
      this.copy(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
      this.copy(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
      this.copy(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
      this.copy(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
      this.copy(BlockTags.LOGS, ItemTags.LOGS);
      this.copy(BlockTags.SAND, ItemTags.SAND);
      this.copy(BlockTags.SLABS, ItemTags.SLABS);
      this.copy(BlockTags.WALLS, ItemTags.WALLS);
      this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
      this.copy(BlockTags.ANVIL, ItemTags.ANVIL);
      this.copy(BlockTags.RAILS, ItemTags.RAILS);
      this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
      this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
      this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
      this.copy(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
      this.copy(BlockTags.BEDS, ItemTags.BEDS);
      this.copy(BlockTags.FENCES, ItemTags.FENCES);
      this.copy(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
      this.copy(BlockTags.FLOWERS, ItemTags.FLOWERS);
      this.tag(ItemTags.BANNERS).add((Object[])(Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER));
      this.tag(ItemTags.BOATS).add((Object[])(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT));
      this.tag(ItemTags.FISHES).add((Object[])(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH));
      this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
      this.tag(ItemTags.MUSIC_DISCS).add((Object[])(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT));
      this.tag(ItemTags.COALS).add((Object[])(Items.COAL, Items.CHARCOAL));
      this.tag(ItemTags.ARROWS).add((Object[])(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW));
      this.tag(ItemTags.LECTERN_BOOKS).add((Object[])(Items.WRITTEN_BOOK, Items.WRITABLE_BOOK));
   }

   protected void copy(Tag var1, Tag var2) {
      Tag.Builder var3 = this.tag(var2);
      Iterator var4 = var1.getSource().iterator();

      while(var4.hasNext()) {
         Tag.Entry var5 = (Tag.Entry)var4.next();
         Tag.Entry var6 = this.copy(var5);
         var3.add(var6);
      }

   }

   private Tag.Entry copy(Tag.Entry var1) {
      if (var1 instanceof Tag.TagEntry) {
         return new Tag.TagEntry(((Tag.TagEntry)var1).getId());
      } else if (var1 instanceof Tag.ValuesEntry) {
         ArrayList var2 = Lists.newArrayList();
         Iterator var3 = ((Tag.ValuesEntry)var1).getValues().iterator();

         while(var3.hasNext()) {
            Block var4 = (Block)var3.next();
            Item var5 = var4.asItem();
            if (var5 == Items.AIR) {
               LOGGER.warn("Itemless block copied to item tag: {}", Registry.BLOCK.getKey(var4));
            } else {
               var2.add(var5);
            }
         }

         return new Tag.ValuesEntry(var2);
      } else {
         throw new UnsupportedOperationException("Unknown tag entry " + var1);
      }
   }

   protected Path getPath(ResourceLocation var1) {
      return this.generator.getOutputFolder().resolve("data/" + var1.getNamespace() + "/tags/items/" + var1.getPath() + ".json");
   }

   public String getName() {
      return "Item Tags";
   }

   protected void useTags(TagCollection var1) {
      ItemTags.reset(var1);
   }
}
