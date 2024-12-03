package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface CollectionContentsPredicate<T, P extends Predicate<T>> extends Predicate<Iterable<T>> {
   List<P> unpack();

   static <T, P extends Predicate<T>> Codec<CollectionContentsPredicate<T, P>> codec(Codec<P> var0) {
      return var0.listOf().xmap(CollectionContentsPredicate::of, CollectionContentsPredicate::unpack);
   }

   @SafeVarargs
   static <T, P extends Predicate<T>> CollectionContentsPredicate<T, P> of(P... var0) {
      return of(List.of(var0));
   }

   static <T, P extends Predicate<T>> CollectionContentsPredicate<T, P> of(List<P> var0) {
      Object var10000;
      switch (var0.size()) {
         case 0 -> var10000 = new Zero();
         case 1 -> var10000 = new Single((Predicate)var0.getFirst());
         default -> var10000 = new Multiple(var0);
      }

      return (CollectionContentsPredicate<T, P>)var10000;
   }

   public static class Zero<T, P extends Predicate<T>> implements CollectionContentsPredicate<T, P> {
      public Zero() {
         super();
      }

      public boolean test(Iterable<T> var1) {
         return true;
      }

      public List<P> unpack() {
         return List.of();
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Iterable)var1);
      }
   }

   public static record Single<T, P extends Predicate<T>>(P test) implements CollectionContentsPredicate<T, P> {
      public Single(P var1) {
         super();
         this.test = var1;
      }

      public boolean test(Iterable<T> var1) {
         for(Object var3 : var1) {
            if (this.test.test(var3)) {
               return true;
            }
         }

         return false;
      }

      public List<P> unpack() {
         return List.of(this.test);
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Iterable)var1);
      }
   }

   public static record Multiple<T, P extends Predicate<T>>(List<P> tests) implements CollectionContentsPredicate<T, P> {
      public Multiple(List<P> var1) {
         super();
         this.tests = var1;
      }

      public boolean test(Iterable<T> var1) {
         ArrayList var2 = new ArrayList(this.tests);

         for(Object var4 : var1) {
            var2.removeIf((var1x) -> var1x.test(var4));
            if (var2.isEmpty()) {
               return true;
            }
         }

         return false;
      }

      public List<P> unpack() {
         return this.tests;
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Iterable)var1);
      }
   }
}
