package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public abstract class HolderTagProvider<T> extends TagsProvider<T> {
   protected HolderTagProvider(final PackOutput output, final ResourceKey<? extends Registry<T>> registryKey, final CompletableFuture<HolderLookup.Provider> lookupProvider) {
      super(output, registryKey, lookupProvider);
   }

   protected TagAppender<Holder.Reference<T>, T> tag(final TagKey<T> tag) {
      TagBuilder builder = this.getOrCreateRawBuilder(tag);
      return TagAppender.forBuilder(builder).map(Holder.Reference::key);
   }
}
