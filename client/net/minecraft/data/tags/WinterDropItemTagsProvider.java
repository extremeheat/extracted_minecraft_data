package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class WinterDropItemTagsProvider extends ItemTagsProvider {
   public WinterDropItemTagsProvider(
      PackOutput var1,
      CompletableFuture<HolderLookup.Provider> var2,
      CompletableFuture<TagsProvider.TagLookup<Item>> var3,
      CompletableFuture<TagsProvider.TagLookup<Block>> var4
   ) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected void addTags(HolderLookup.Provider var1) {
      this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
      this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
      this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
      this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
      this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
      this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
      this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
      this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
      this.copy(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
      this.copy(BlockTags.PALE_OAK_LOGS, ItemTags.PALE_OAK_LOGS);
      this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
      this.copy(BlockTags.LEAVES, ItemTags.LEAVES);
      this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
      this.copy(BlockTags.DIRT, ItemTags.DIRT);
      this.tag(ItemTags.BOATS).add(Items.PALE_OAK_BOAT);
      this.tag(ItemTags.CHEST_BOATS).add(Items.PALE_OAK_CHEST_BOAT);
      this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
      this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
   }
}
