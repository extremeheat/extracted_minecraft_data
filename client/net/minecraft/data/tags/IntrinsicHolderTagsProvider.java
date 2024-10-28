package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
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

   protected IntrinsicTagAppender<T> tag(TagKey<T> var1) {
      TagBuilder var2 = this.getOrCreateRawBuilder(var1);
      return new IntrinsicTagAppender(var2, this.keyExtractor);
   }

   // $FF: synthetic method
   protected TagsProvider.TagAppender tag(TagKey var1) {
      return this.tag(var1);
   }

   protected static class IntrinsicTagAppender<T> extends TagsProvider.TagAppender<T> {
      private final Function<T, ResourceKey<T>> keyExtractor;

      IntrinsicTagAppender(TagBuilder var1, Function<T, ResourceKey<T>> var2) {
         super(var1);
         this.keyExtractor = var2;
      }

      public IntrinsicTagAppender<T> addTag(TagKey<T> var1) {
         super.addTag(var1);
         return this;
      }

      public final IntrinsicTagAppender<T> add(T var1) {
         this.add((ResourceKey)((ResourceKey)this.keyExtractor.apply(var1)));
         return this;
      }

      @SafeVarargs
      public final IntrinsicTagAppender<T> add(T... var1) {
         Stream.of(var1).map(this.keyExtractor).forEach(this::add);
         return this;
      }

      // $FF: synthetic method
      public TagsProvider.TagAppender addTag(TagKey var1) {
         return this.addTag(var1);
      }
   }
}
