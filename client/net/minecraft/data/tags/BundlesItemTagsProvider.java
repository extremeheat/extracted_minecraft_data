package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class BundlesItemTagsProvider extends ItemTagsProvider {
   public BundlesItemTagsProvider(
      PackOutput var1,
      CompletableFuture<HolderLookup.Provider> var2,
      CompletableFuture<TagsProvider.TagLookup<Item>> var3,
      CompletableFuture<TagsProvider.TagLookup<Block>> var4
   ) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected void addTags(HolderLookup.Provider var1) {
      this.tag(ItemTags.BUNDLES)
         .add(
            Items.BUNDLE,
            Items.BLACK_BUNDLE,
            Items.BLUE_BUNDLE,
            Items.BROWN_BUNDLE,
            Items.CYAN_BUNDLE,
            Items.GRAY_BUNDLE,
            Items.GREEN_BUNDLE,
            Items.LIGHT_BLUE_BUNDLE,
            Items.LIGHT_GRAY_BUNDLE,
            Items.LIME_BUNDLE,
            Items.MAGENTA_BUNDLE,
            Items.ORANGE_BUNDLE,
            Items.PINK_BUNDLE,
            Items.PURPLE_BUNDLE,
            Items.RED_BUNDLE,
            Items.YELLOW_BUNDLE,
            Items.WHITE_BUNDLE
         );
   }
}