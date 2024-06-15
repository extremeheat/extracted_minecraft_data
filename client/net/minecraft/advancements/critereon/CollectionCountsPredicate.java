package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionCountsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<T>> {
   List<CollectionCountsPredicate.Entry<T, P>> unpack();

   static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate<T, P>> codec(Codec<P> var0) {
      return CollectionCountsPredicate.Entry.codec(var0).listOf().xmap(CollectionCountsPredicate::of, CollectionCountsPredicate::unpack);
   }

   @SafeVarargs
   static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> of(CollectionCountsPredicate.Entry<T, P>... var0) {
      return of(List.of(var0));
   }

   static <T, P extends Predicate<T>> CollectionCountsPredicate<T, P> of(List<CollectionCountsPredicate.Entry<T, P>> var0) {
      return (CollectionCountsPredicate<T, P>)(switch (var0.size()) {
         case 0 -> new CollectionCountsPredicate.Zero();
         case 1 -> new CollectionCountsPredicate.Single((CollectionCountsPredicate.Entry<T, P>)var0.getFirst());
         default -> new CollectionCountsPredicate.Multiple(var0);
      });
   }

   public static record Entry<T, P extends Predicate<T>>(P test, MinMaxBounds.Ints count) {
      public Entry(P test, MinMaxBounds.Ints count) {
         super();
         this.test = (P)test;
         this.count = count;
      }

      public static <T, P extends Predicate<T>> Codec<CollectionCountsPredicate.Entry<T, P>> codec(Codec<P> var0) {
         return RecordCodecBuilder.create(
            var1 -> var1.group(
                     var0.fieldOf("test").forGetter(CollectionCountsPredicate.Entry::test),
                     MinMaxBounds.Ints.CODEC.fieldOf("count").forGetter(CollectionCountsPredicate.Entry::count)
                  )
                  .apply(var1, CollectionCountsPredicate.Entry::new)
         );
      }

      public boolean test(Iterable<T> var1) {
         int var2 = 0;

         for (Object var4 : var1) {
            if (this.test.test((T)var4)) {
               var2++;
            }
         }

         return this.count.matches(var2);
      }
   }

   public static record Multiple<T, P extends Predicate<T>>(List<CollectionCountsPredicate.Entry<T, P>> entries) implements CollectionCountsPredicate<T, P> {
      public Multiple(List<CollectionCountsPredicate.Entry<T, P>> entries) {
         super();
         this.entries = entries;
      }

      public boolean test(Iterable<T> var1) {
         for (CollectionCountsPredicate.Entry var3 : this.entries) {
            if (!var3.test(var1)) {
               return false;
            }
         }

         return true;
      }

      @Override
      public List<CollectionCountsPredicate.Entry<T, P>> unpack() {
         return this.entries;
      }
   }

   public static record Single<T, P extends Predicate<T>>(CollectionCountsPredicate.Entry<T, P> entry) implements CollectionCountsPredicate<T, P> {
      public Single(CollectionCountsPredicate.Entry<T, P> entry) {
         super();
         this.entry = entry;
      }

      public boolean test(Iterable<T> var1) {
         return this.entry.test(var1);
      }

      @Override
      public List<CollectionCountsPredicate.Entry<T, P>> unpack() {
         return List.of(this.entry);
      }
   }

   public static class Zero<T, P extends Predicate<T>> implements CollectionCountsPredicate<T, P> {
      public Zero() {
         super();
      }

      public boolean test(Iterable<T> var1) {
         return true;
      }

      @Override
      public List<CollectionCountsPredicate.Entry<T, P>> unpack() {
         return List.of();
      }
   }
}
