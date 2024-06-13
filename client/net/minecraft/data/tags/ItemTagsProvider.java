package net.minecraft.data.tags;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class ItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
   private final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags;
   private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

   public ItemTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2, CompletableFuture<TagsProvider.TagLookup<Block>> var3) {
      super(var1, Registries.ITEM, var2, var0 -> var0.builtInRegistryHolder().key());
      this.blockTags = var3;
   }

   public ItemTagsProvider(
      PackOutput var1,
      CompletableFuture<HolderLookup.Provider> var2,
      CompletableFuture<TagsProvider.TagLookup<Item>> var3,
      CompletableFuture<TagsProvider.TagLookup<Block>> var4
   ) {
      super(var1, Registries.ITEM, var2, var3, var0 -> var0.builtInRegistryHolder().key());
      this.blockTags = var4;
   }

   protected void copy(TagKey<Block> var1, TagKey<Item> var2) {
      this.tagsToCopy.put(var1, var2);
   }

   @Override
   protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
      return super.createContentsProvider().thenCombine(this.blockTags, (var1, var2) -> {
         this.tagsToCopy.forEach((var2x, var3) -> {
            TagBuilder var4 = this.getOrCreateRawBuilder((TagKey<Item>)var3);
            Optional var5 = var2.apply((TagKey<? super TagKey<Block>>)var2x);
            ((TagBuilder)var5.orElseThrow(() -> new IllegalStateException("Missing block tag " + var3.location()))).build().forEach(var4::add);
         });
         return (HolderLookup.Provider)var1;
      });
   }
}
