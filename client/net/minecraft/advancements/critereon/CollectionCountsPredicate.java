package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionCountsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<T>> {
   List<Entry<T, P>> unpack();

   static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate<T, P>> codec(Codec<P> var0) {
      return CollectionCountsPredicate.Entry.codec(var0).listOf().xmap(CollectionCountsPredicate::of, CollectionCountsPredicate::unpack);
   }

   @SafeVarargs
   static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> of(Entry<T, P>... var0) {
      return of(List.of(var0));
   }

   static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> of(List<Entry<T, P>> var0) {
      Object var10000;
      switch (var0.size()) {
         case 0 -> var10000 = new Zero();
         case 1 -> var10000 = new Single((Entry)var0.getFirst());
         default -> var10000 = new Multiple(var0);
      }

      return (CollectionCountsPredicate<T, P>)var10000;
   }

   public static class Zero<T, P extends Predicate<T>> implements CollectionCountsPredicate<T, P> {
      public Zero() {
         super();
      }

      public boolean test(Iterable<T> var1) {
         return true;
      }

      public List<Entry<T, P>> unpack() {
         return List.of();
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Iterable)var1);
      }
   }

   public static record Single<T, P extends Predicate<T>>(Entry<T, P> entry) implements CollectionCountsPredicate<T, P> {
      public Single(Entry<T, P> var1) {
         super();
         this.entry = var1;
      }

      public boolean test(Iterable<T> var1) {
         return this.entry.test(var1);
      }

      public List<Entry<T, P>> unpack() {
         return List.of(this.entry);
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Iterable)var1);
      }
   }

   public static record Multiple<T, P extends Predicate<T>>(List<Entry<T, P>> entries) implements CollectionCountsPredicate<T, P> {
      public Multiple(List<Entry<T, P>> var1) {
         super();
         this.entries = var1;
      }

      public boolean test(Iterable<T> var1) {
         for(Entry var3 : this.entries) {
            if (!var3.test(var1)) {
               return false;
            }
         }

         return true;
      }

      public List<Entry<T, P>> unpack() {
         return this.entries;
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Iterable)var1);
      }
   }

   public static record Entry<T, P extends Predicate<T>>(P test, MinMaxBounds.Ints count) {
      public Entry(P var1, MinMaxBounds.Ints var2) {
         super();
         this.test = var1;
         this.count = var2;
      }

      public static <T, P extends Predicate<T>> Codec<Entry<T, P>> codec(Codec<P> var0) {
         return RecordCodecBuilder.create((var1) -> var1.group(var0.fieldOf("test").forGetter(Entry::test), MinMaxBounds.Ints.CODEC.fieldOf("count").forGetter(Entry::count)).apply(var1, Entry::new));
      }

      public boolean test(Iterable<T> var1) {
         int var2 = 0;

         for(Object var4 : var1) {
            if (this.test.test(var4)) {
               ++var2;
            }
         }

         return this.count.matches(var2);
      }
   }
}
