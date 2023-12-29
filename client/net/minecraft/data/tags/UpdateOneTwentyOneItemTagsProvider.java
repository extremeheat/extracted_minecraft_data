package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class UpdateOneTwentyOneItemTagsProvider extends ItemTagsProvider {
   public UpdateOneTwentyOneItemTagsProvider(
      PackOutput var1,
      CompletableFuture<HolderLookup.Provider> var2,
      CompletableFuture<TagsProvider.TagLookup<Item>> var3,
      CompletableFuture<TagsProvider.TagLookup<Block>> var4
   ) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected void addTags(HolderLookup.Provider var1) {
      this.tag(ItemTags.STAIRS).add(Items.TUFF_STAIRS, Items.POLISHED_TUFF_STAIRS, Items.TUFF_BRICK_STAIRS);
      this.tag(ItemTags.SLABS).add(Items.TUFF_SLAB, Items.POLISHED_TUFF_SLAB, Items.TUFF_BRICK_SLAB);
      this.tag(ItemTags.WALLS).add(Items.TUFF_WALL, Items.POLISHED_TUFF_WALL, Items.TUFF_BRICK_WALL);
   }
}
