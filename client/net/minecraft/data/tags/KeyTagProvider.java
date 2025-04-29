package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public abstract class KeyTagProvider<T> extends TagsProvider<T> {
   protected KeyTagProvider(PackOutput var1, ResourceKey<? extends Registry<T>> var2, CompletableFuture<HolderLookup.Provider> var3) {
      super(var1, var2, var3);
   }

   protected TagAppender<ResourceKey<T>, T> tag(TagKey<T> var1) {
      TagBuilder var2 = this.getOrCreateRawBuilder(var1);
      return TagAppender.forBuilder(var2);
   }
}
