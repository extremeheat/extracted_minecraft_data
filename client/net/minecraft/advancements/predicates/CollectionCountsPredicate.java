package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionCountsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<? extends T>> {
   List<Entry<T, P>> unpack();

   static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate<T, P>> codec(final Codec<P> elementCodec) {
      return CollectionCountsPredicate.Entry.codec(elementCodec).listOf().xmap(CollectionCountsPredicate::of, CollectionCountsPredicate::unpack);
   }

   @SafeVarargs
   static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> of(final Entry<T, P>... predicates) {
      return of(List.of(predicates));
   }

   static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> of(final List<Entry<T, P>> predicates) {
      Object var10000;
      switch (predicates.size()) {
         case 0 -> var10000 = new Zero();
         case 1 -> var10000 = new Single((Entry)predicates.getFirst());
         default -> var10000 = new Multiple(predicates);
      }

      return (CollectionCountsPredicate<T, P>)var10000;
   }

   public static class Zero<T, P extends Predicate<T>> implements CollectionCountsPredicate<T, P> {
      public Zero() {
         super();
      }

      public boolean test(final Iterable<? extends T> values) {
         return true;
      }

      public List<Entry<T, P>> unpack() {
         return List.of();
      }
   }

   public static record Single<T, P extends Predicate<T>>(Entry<T, P> entry) implements CollectionCountsPredicate<T, P> {
      public Single {
         super();
      }

      public boolean test(final Iterable<? extends T> values) {
         return this.entry.test(values);
      }

      public List<Entry<T, P>> unpack() {
         return List.of(this.entry);
      }
   }

   public static record Multiple<T, P extends Predicate<T>>(List<Entry<T, P>> entries) implements CollectionCountsPredicate<T, P> {
      public Multiple {
         super();
      }

      public boolean test(final Iterable<? extends T> values) {
         for(Entry<T, P> entry : this.entries) {
            if (!entry.test(values)) {
               return false;
            }
         }

         return true;
      }

      public List<Entry<T, P>> unpack() {
         return this.entries;
      }
   }

   public static record Entry<T, P extends Predicate<T>>(P test, MinMaxBounds.Ints count) {
      public Entry {
         super();
      }

      public static <T, P extends Predicate<T>> Codec<Entry<T, P>> codec(final Codec<P> elementCodec) {
         return RecordCodecBuilder.create((i) -> i.group(elementCodec.fieldOf("test").forGetter(Entry::test), MinMaxBounds.Ints.CODEC.fieldOf("count").forGetter(Entry::count)).apply(i, Entry::new));
      }

      public boolean test(final Iterable<? extends T> values) {
         int count = 0;

         for(T value : values) {
            if (this.test.test(value)) {
               ++count;
            }
         }

         return this.count.matches(count);
      }
   }
}
