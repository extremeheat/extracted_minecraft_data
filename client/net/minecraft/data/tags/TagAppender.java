package net.minecraft.data.tags;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;

public interface TagAppender<E, T> {
   TagAppender<E, T> add(E var1);

   default TagAppender<E, T> add(E... var1) {
      return this.addAll(Arrays.stream(var1));
   }

   default TagAppender<E, T> addAll(Collection<E> var1) {
      var1.forEach(this::add);
      return this;
   }

   default TagAppender<E, T> addAll(Stream<E> var1) {
      var1.forEach(this::add);
      return this;
   }

   TagAppender<E, T> addOptional(E var1);

   TagAppender<E, T> addTag(TagKey<T> var1);

   TagAppender<E, T> addOptionalTag(TagKey<T> var1);

   static <T> TagAppender<ResourceKey<T>, T> forBuilder(final TagBuilder var0) {
      return new TagAppender<ResourceKey<T>, T>() {
         public TagAppender<ResourceKey<T>, T> add(ResourceKey<T> var1) {
            var0.addElement(var1.identifier());
            return this;
         }

         public TagAppender<ResourceKey<T>, T> addOptional(ResourceKey<T> var1) {
            var0.addOptionalElement(var1.identifier());
            return this;
         }

         public TagAppender<ResourceKey<T>, T> addTag(TagKey<T> var1) {
            var0.addTag(var1.location());
            return this;
         }

         public TagAppender<ResourceKey<T>, T> addOptionalTag(TagKey<T> var1) {
            var0.addOptionalTag(var1.location());
            return this;
         }
      };
   }

   default <U> TagAppender<U, T> map(final Function<U, E> var1) {
      return new TagAppender<U, T>() {
         public TagAppender<U, T> add(U var1x) {
            TagAppender.this.add(var1.apply(var1x));
            return this;
         }

         public TagAppender<U, T> addOptional(U var1x) {
            TagAppender.this.add(var1.apply(var1x));
            return this;
         }

         public TagAppender<U, T> addTag(TagKey<T> var1x) {
            TagAppender.this.addTag(var1x);
            return this;
         }

         public TagAppender<U, T> addOptionalTag(TagKey<T> var1x) {
            TagAppender.this.addOptionalTag(var1x);
            return this;
         }
      };
   }
}
