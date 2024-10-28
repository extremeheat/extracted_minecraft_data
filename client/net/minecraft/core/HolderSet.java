package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
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

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   static <T> Named<T> emptyNamed(HolderOwner<T> var0, TagKey<T> var1) {
      return new Named<T>(var0, var1) {
         protected List<Holder<T>> contents() {
            throw new UnsupportedOperationException("Tag " + String.valueOf(this.key()) + " can't be dereferenced during construction");
         }
      };
   }

   static <T> HolderSet<T> empty() {
      return HolderSet.Direct.EMPTY;
   }

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

   static <E, T> Direct<T> direct(Function<E, Holder<T>> var0, Collection<E> var1) {
      return direct(var1.stream().map(var0).toList());
   }

   public static final class Direct<T> extends ListBacked<T> {
      static final Direct<?> EMPTY = new Direct(List.of());
      private final List<Holder<T>> contents;
      @Nullable
      private Set<Holder<T>> contentsSet;

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

      public Optional<TagKey<T>> unwrapKey() {
         return Optional.empty();
      }

      public boolean contains(Holder<T> var1) {
         if (this.contentsSet == null) {
            this.contentsSet = Set.copyOf(this.contents);
         }

         return this.contentsSet.contains(var1);
      }

      public String toString() {
         return "DirectSet[" + String.valueOf(this.contents) + "]";
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            boolean var10000;
            if (var1 instanceof Direct) {
               Direct var2 = (Direct)var1;
               if (this.contents.equals(var2.contents)) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         }
      }

      public int hashCode() {
         return this.contents.hashCode();
      }
   }

   public static class Named<T> extends ListBacked<T> {
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

      protected List<Holder<T>> contents() {
         return this.contents;
      }

      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.left(this.key);
      }

      public Optional<TagKey<T>> unwrapKey() {
         return Optional.of(this.key);
      }

      public boolean contains(Holder<T> var1) {
         return var1.is(this.key);
      }

      public String toString() {
         String var10000 = String.valueOf(this.key);
         return "NamedSet(" + var10000 + ")[" + String.valueOf(this.contents) + "]";
      }

      public boolean canSerializeIn(HolderOwner<T> var1) {
         return this.owner.canSerializeIn(var1);
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

      public Iterator<Holder<T>> iterator() {
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

      public boolean canSerializeIn(HolderOwner<T> var1) {
         return true;
      }
   }
}
