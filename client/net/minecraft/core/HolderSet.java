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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public interface HolderSet<T> extends Iterable<Holder<T>> {
   Stream<Holder<T>> stream();

   int size();

   Either<TagKey<T>, List<Holder<T>>> unwrap();

   Optional<Holder<T>> getRandomElement(RandomSource var1);

   Holder<T> get(int var1);

   boolean contains(Holder<T> var1);

   boolean canSerializeIn(HolderOwner<T> var1);

   Optional<TagKey<T>> unwrapKey();

   @Deprecated
   @VisibleForTesting
   static <T> HolderSet.Named<T> emptyNamed(HolderOwner<T> var0, TagKey<T> var1) {
      return new HolderSet.Named<>(var0, var1);
   }

   @SafeVarargs
   static <T> HolderSet.Direct<T> direct(Holder<T>... var0) {
      return new HolderSet.Direct<>(List.of(var0));
   }

   static <T> HolderSet.Direct<T> direct(List<? extends Holder<T>> var0) {
      return new HolderSet.Direct<>(List.copyOf(var0));
   }

   @SafeVarargs
   static <E, T> HolderSet.Direct<T> direct(Function<E, Holder<T>> var0, E... var1) {
      return direct(Stream.of(var1).map(var0).toList());
   }

   static <E, T> HolderSet.Direct<T> direct(Function<E, Holder<T>> var0, List<E> var1) {
      return direct(var1.stream().map(var0).toList());
   }

   public static class Direct<T> extends HolderSet.ListBacked<T> {
      private final List<Holder<T>> contents;
      @Nullable
      private Set<Holder<T>> contentsSet;

      Direct(List<Holder<T>> var1) {
         super();
         this.contents = var1;
      }

      @Override
      protected List<Holder<T>> contents() {
         return this.contents;
      }

      @Override
      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.right(this.contents);
      }

      @Override
      public Optional<TagKey<T>> unwrapKey() {
         return Optional.empty();
      }

      @Override
      public boolean contains(Holder<T> var1) {
         if (this.contentsSet == null) {
            this.contentsSet = Set.copyOf(this.contents);
         }

         return this.contentsSet.contains(var1);
      }

      @Override
      public String toString() {
         return "DirectSet[" + this.contents + "]";
      }
   }

   public abstract static class ListBacked<T> implements HolderSet<T> {
      public ListBacked() {
         super();
      }

      protected abstract List<Holder<T>> contents();

      @Override
      public int size() {
         return this.contents().size();
      }

      @Override
      public Spliterator<Holder<T>> spliterator() {
         return this.contents().spliterator();
      }

      @Override
      public Iterator<Holder<T>> iterator() {
         return this.contents().iterator();
      }

      @Override
      public Stream<Holder<T>> stream() {
         return this.contents().stream();
      }

      @Override
      public Optional<Holder<T>> getRandomElement(RandomSource var1) {
         return Util.getRandomSafe(this.contents(), var1);
      }

      @Override
      public Holder<T> get(int var1) {
         return this.contents().get(var1);
      }

      @Override
      public boolean canSerializeIn(HolderOwner<T> var1) {
         return true;
      }
   }

   public static class Named<T> extends HolderSet.ListBacked<T> {
      private final HolderOwner<T> owner;
      private final TagKey<T> key;
      private List<Holder<T>> contents = List.of();

      Named(HolderOwner<T> var1, TagKey<T> var2) {
         super();
         this.owner = var1;
         this.key = var2;
      }

      void bind(List<Holder<T>> var1) {
         this.contents = List.copyOf(var1);
      }

      public TagKey<T> key() {
         return this.key;
      }

      @Override
      protected List<Holder<T>> contents() {
         return this.contents;
      }

      @Override
      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.left(this.key);
      }

      @Override
      public Optional<TagKey<T>> unwrapKey() {
         return Optional.of(this.key);
      }

      @Override
      public boolean contains(Holder<T> var1) {
         return var1.is(this.key);
      }

      @Override
      public String toString() {
         return "NamedSet(" + this.key + ")[" + this.contents + "]";
      }

      @Override
      public boolean canSerializeIn(HolderOwner<T> var1) {
         return this.owner.canSerializeIn(var1);
      }
   }
}
