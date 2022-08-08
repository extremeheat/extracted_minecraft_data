package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HolderSet<T> extends Iterable<Holder<T>> {
   Stream<Holder<T>> stream();

   int size();

   Either<TagKey<T>, List<Holder<T>>> unwrap();

   Optional<Holder<T>> getRandomElement(RandomSource var1);

   Holder<T> get(int var1);

   boolean contains(Holder<T> var1);

   boolean isValidInRegistry(Registry<T> var1);

   @SafeVarargs
   static <T> Direct<T> direct(Holder<T>... var0) {
      return new Direct(List.of(var0));
   }

   static <T> Direct<T> direct(List<? extends Holder<T>> var0) {
      return new Direct(List.copyOf(var0));
   }

   @SafeVarargs
   static <E, T> Direct<T> direct(Function<E, Holder<T>> var0, E... var1) {
      return direct(Stream.of(var1).map(var0).toList());
   }

   static <E, T> Direct<T> direct(Function<E, Holder<T>> var0, List<E> var1) {
      return direct(var1.stream().map(var0).toList());
   }

   public static class Direct<T> extends ListBacked<T> {
      private final List<Holder<T>> contents;
      private @Nullable Set<Holder<T>> contentsSet;

      Direct(List<Holder<T>> var1) {
         super();
         this.contents = var1;
      }

      protected List<Holder<T>> contents() {
         return this.contents;
      }

      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.right(this.contents);
      }

      public boolean contains(Holder<T> var1) {
         if (this.contentsSet == null) {
            this.contentsSet = Set.copyOf(this.contents);
         }

         return this.contentsSet.contains(var1);
      }

      public String toString() {
         return "DirectSet[" + this.contents + "]";
      }
   }

   public static class Named<T> extends ListBacked<T> {
      private final Registry<T> registry;
      private final TagKey<T> key;
      private List<Holder<T>> contents = List.of();

      Named(Registry<T> var1, TagKey<T> var2) {
         super();
         this.registry = var1;
         this.key = var2;
      }

      void bind(List<Holder<T>> var1) {
         this.contents = List.copyOf(var1);
      }

      public TagKey<T> key() {
         return this.key;
      }

      protected List<Holder<T>> contents() {
         return this.contents;
      }

      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.left(this.key);
      }

      public boolean contains(Holder<T> var1) {
         return var1.is(this.key);
      }

      public String toString() {
         return "NamedSet(" + this.key + ")[" + this.contents + "]";
      }

      public boolean isValidInRegistry(Registry<T> var1) {
         return this.registry == var1;
      }
   }

   public abstract static class ListBacked<T> implements HolderSet<T> {
      public ListBacked() {
         super();
      }

      protected abstract List<Holder<T>> contents();

      public int size() {
         return this.contents().size();
      }

      public Spliterator<Holder<T>> spliterator() {
         return this.contents().spliterator();
      }

      public @NotNull Iterator<Holder<T>> iterator() {
         return this.contents().iterator();
      }

      public Stream<Holder<T>> stream() {
         return this.contents().stream();
      }

      public Optional<Holder<T>> getRandomElement(RandomSource var1) {
         return Util.getRandomSafe(this.contents(), var1);
      }

      public Holder<T> get(int var1) {
         return (Holder)this.contents().get(var1);
      }

      public boolean isValidInRegistry(Registry<T> var1) {
         return true;
      }
   }
}
