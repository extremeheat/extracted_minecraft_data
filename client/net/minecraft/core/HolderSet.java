package net.minecraft.core;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public interface HolderSet<T> extends Iterable<Holder<T>> {
   Stream<Holder<T>> stream();

   int size();

   boolean isBound();

   Either<TagKey<T>, List<Holder<T>>> unwrap();

   Optional<Holder<T>> getRandomElement(RandomSource random);

   Holder<T> get(int index);

   boolean contains(final Holder<T> value);

   boolean canSerializeIn(HolderOwner<T> owner);

   Optional<TagKey<T>> unwrapKey();

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   static <T> Named<T> emptyNamed(final HolderOwner<T> owner, final TagKey<T> key) {
      return new Named<T>(owner, key) {
         protected List<Holder<T>> contents() {
            throw new UnsupportedOperationException("Tag " + String.valueOf(this.key()) + " can't be dereferenced during construction");
         }
      };
   }

   static <T> HolderSet<T> empty() {
      return HolderSet.Direct.EMPTY;
   }

   @SafeVarargs
   static <T> Direct<T> direct(final Holder<T>... values) {
      return new Direct<T>(List.of(values));
   }

   static <T> Direct<T> direct(final List<? extends Holder<T>> values) {
      return new Direct<T>(List.copyOf(values));
   }

   @SafeVarargs
   static <E, T> Direct<T> direct(final Function<E, Holder<T>> holderGetter, final E... elements) {
      return direct(Stream.of(elements).map(holderGetter).toList());
   }

   static <E, T> Direct<T> direct(final Function<E, Holder<T>> holderGetter, final Collection<E> elements) {
      return direct(elements.stream().map(holderGetter).toList());
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

      public Optional<Holder<T>> getRandomElement(final RandomSource random) {
         return Util.<Holder<T>>getRandomSafe(this.contents(), random);
      }

      public Holder<T> get(final int index) {
         return (Holder)this.contents().get(index);
      }

      public boolean canSerializeIn(final HolderOwner<T> owner) {
         return true;
      }
   }

   public static final class Direct<T> extends ListBacked<T> {
      private static final Direct<?> EMPTY = new Direct(List.of());
      private final List<Holder<T>> contents;
      private @Nullable Set<Holder<T>> contentsSet;

      private Direct(final List<Holder<T>> contents) {
         super();
         this.contents = contents;
      }

      protected List<Holder<T>> contents() {
         return this.contents;
      }

      public boolean isBound() {
         return true;
      }

      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.right(this.contents);
      }

      public Optional<TagKey<T>> unwrapKey() {
         return Optional.empty();
      }

      public boolean contains(final Holder<T> value) {
         if (this.contentsSet == null) {
            this.contentsSet = Set.copyOf(this.contents);
         }

         return this.contentsSet.contains(value);
      }

      public String toString() {
         return "DirectSet[" + String.valueOf(this.contents) + "]";
      }

      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         } else {
            boolean var10000;
            if (obj instanceof Direct) {
               Direct<?> direct = (Direct)obj;
               if (this.contents.equals(direct.contents)) {
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
      private @Nullable List<Holder<T>> contents;

      Named(final HolderOwner<T> owner, final TagKey<T> key) {
         super();
         this.owner = owner;
         this.key = key;
      }

      void bind(final List<Holder<T>> contents) {
         this.contents = List.copyOf(contents);
      }

      public TagKey<T> key() {
         return this.key;
      }

      protected List<Holder<T>> contents() {
         if (this.contents == null) {
            String var10002 = String.valueOf(this.key);
            throw new IllegalStateException("Trying to access unbound tag '" + var10002 + "' from registry " + String.valueOf(this.owner));
         } else {
            return this.contents;
         }
      }

      public boolean isBound() {
         return this.contents != null;
      }

      public Either<TagKey<T>, List<Holder<T>>> unwrap() {
         return Either.left(this.key);
      }

      public Optional<TagKey<T>> unwrapKey() {
         return Optional.of(this.key);
      }

      public boolean contains(final Holder<T> value) {
         return value.is(this.key);
      }

      public String toString() {
         String var10000 = String.valueOf(this.key);
         return "NamedSet(" + var10000 + ")[" + String.valueOf(this.contents) + "]";
      }

      public boolean canSerializeIn(final HolderOwner<T> context) {
         return this.owner.canSerializeIn(context);
      }
   }
}
