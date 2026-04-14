package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionContentsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<? extends T>> {
   List<P> unpack();

   static <T, P extends Predicate<T>> Codec<CollectionContentsPredicate<T, P>> codec(final Codec<P> elementCodec) {
      return elementCodec.listOf().xmap(CollectionContentsPredicate::of, CollectionContentsPredicate::unpack);
   }

   @SafeVarargs
   static <T, P extends Predicate<T>> CollectionContentsPredicate<T, P> of(final P... predicates) {
      return of(List.of(predicates));
   }

   static <T, P extends Predicate<T>> CollectionContentsPredicate<T, P> of(final List<P> predicates) {
      Object var10000;
      switch (predicates.size()) {
         case 0 -> var10000 = new Zero();
         case 1 -> var10000 = new Single((Predicate)predicates.getFirst());
         default -> var10000 = new Multiple(predicates);
      }

      return (CollectionContentsPredicate<T, P>)var10000;
   }

   public static class Zero<T, P extends Predicate<T>> implements CollectionContentsPredicate<T, P> {
      public Zero() {
         super();
      }

      public boolean test(final Iterable<? extends T> values) {
         return true;
      }

      public List<P> unpack() {
         return List.of();
      }
   }

   public static record Single<T, P extends Predicate<T>>(P test) implements CollectionContentsPredicate<T, P> {
      public Single {
         super();
      }

      public boolean test(final Iterable<? extends T> values) {
         for(T value : values) {
            if (this.test.test(value)) {
               return true;
            }
         }

         return false;
      }

      public List<P> unpack() {
         return List.of(this.test);
      }
   }

   public static record Multiple<T, P extends Predicate<T>>(List<P> tests) implements CollectionContentsPredicate<T, P> {
      public Multiple {
         super();
      }

      public boolean test(final Iterable<? extends T> values) {
         List<Predicate<T>> testsToMatch = new ArrayList(this.tests);

         for(T value : values) {
            testsToMatch.removeIf((p) -> p.test(value));
            if (testsToMatch.isEmpty()) {
               return true;
            }
         }

         return false;
      }

      public List<P> unpack() {
         return this.tests;
      }
   }
}
