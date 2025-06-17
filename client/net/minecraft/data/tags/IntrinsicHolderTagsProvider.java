package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public abstract class IntrinsicHolderTagsProvider<T> extends TagsProvider<T> {
   private final Function<T, ResourceKey<T>> keyExtractor;

   public IntrinsicHolderTagsProvider(PackOutput var1, ResourceKey<? extends Registry<T>> var2, CompletableFuture<HolderLookup.Provider> var3, Function<T, ResourceKey<T>> var4) {
      super(var1, var2, var3);
      this.keyExtractor = var4;
   }

   public IntrinsicHolderTagsProvider(PackOutput var1, ResourceKey<? extends Registry<T>> var2, CompletableFuture<HolderLookup.Provider> var3, CompletableFuture<TagsProvider.TagLookup<T>> var4, Function<T, ResourceKey<T>> var5) {
      super(var1, var2, var3, var4);
      this.keyExtractor = var5;
   }

   protected TagAppender<T, T> tag(TagKey<T> var1) {
      TagBuilder var2 = this.getOrCreateRawBuilder(var1);
      return TagAppender.forBuilder(var2).map(this.keyExtractor);
   }
}
