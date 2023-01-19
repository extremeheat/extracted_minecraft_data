package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class ItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
   private final Function<TagKey<Block>, TagBuilder> blockTags;

   public ItemTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2, TagsProvider<Block> var3) {
      super(var1, Registries.ITEM, var2, var0 -> var0.builtInRegistryHolder().key());
      this.blockTags = var3::getOrCreateRawBuilder;
   }

   protected void copy(TagKey<Block> var1, TagKey<Item> var2) {
      TagBuilder var3 = this.getOrCreateRawBuilder(var2);
      TagBuilder var4 = this.blockTags.apply(var1);
      var4.build().forEach(var3::add);
   }
}
