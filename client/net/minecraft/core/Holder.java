package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;

public sealed interface Holder<T> {
   T value();

   boolean isBound();

   boolean areComponentsBound();

   boolean is(Identifier key);

   boolean is(ResourceKey<T> key);

   boolean is(Predicate<ResourceKey<T>> predicate);

   boolean is(TagKey<T> tag);

   /** @deprecated */
   @Deprecated
   boolean is(Holder<T> holder);

   Stream<TagKey<T>> tags();

   DataComponentMap components();

   Either<ResourceKey<T>, T> unwrap();

   Optional<ResourceKey<T>> unwrapKey();

   Kind kind();

   boolean canSerializeIn(HolderOwner<T> registry);

   default Optional<String> getRegisteredNameIfPresent() {
      return this.unwrapKey().map((key) -> key.identifier().toString());
   }

   default String getRegisteredName() {
      return (String)this.getRegisteredNameIfPresent().orElse("[unregistered]");
   }

   static <T> Holder<T> direct(final T value) {
      return new Direct<T>(value, DataComponentMap.EMPTY);
   }

   static <T> Holder<T> direct(final T value, final DataComponentMap components) {
      return new Direct<T>(value, components);
   }

   public static enum Kind {
      REFERENCE,
      DIRECT;

      private Kind() {
      }

      // $FF: synthetic method
      private static Kind[] $values() {
         return new Kind[]{REFERENCE, DIRECT};
      }
   }

   public static record Direct<T>(T value, DataComponentMap components) implements Holder<T> {
      public Direct {
         super();
      }

      public boolean isBound() {
         return true;
      }

      public boolean areComponentsBound() {
         return true;
      }

      public boolean is(final Identifier key) {
         return false;
      }

      public boolean is(final ResourceKey<T> key) {
         return false;
      }

      public boolean is(final TagKey<T> tag) {
         return false;
      }

      public boolean is(final Holder<T> holder) {
         return this.value.equals(holder.value());
      }

      public boolean is(final Predicate<ResourceKey<T>> predicate) {
         return false;
      }

      public Either<ResourceKey<T>, T> unwrap() {
         return Either.right(this.value);
      }

      public Optional<ResourceKey<T>> unwrapKey() {
         return Optional.empty();
      }

      public Kind kind() {
         return Holder.Kind.DIRECT;
      }

      public String toString() {
         return "Direct{" + String.valueOf(this.value) + "}";
      }

      public boolean canSerializeIn(final HolderOwner<T> registry) {
         return true;
      }

      public Stream<TagKey<T>> tags() {
         return Stream.of();
      }
   }

   public static non-sealed class Reference<T> implements Holder<T> {
      private final HolderOwner<T> owner;
      private @Nullable Set<TagKey<T>> tags;
      private @Nullable DataComponentMap components;
      private final Type type;
      private @Nullable ResourceKey<T> key;
      private @Nullable T value;

      protected Reference(final Type type, final HolderOwner<T> owner, final @Nullable ResourceKey<T> key, final @Nullable T value) {
         super();
         this.owner = owner;
         this.type = type;
         this.key = key;
         this.value = value;
      }

      public static <T> Reference<T> createStandAlone(final HolderOwner<T> owner, final ResourceKey<T> key) {
         return new Reference<T>(Holder.Reference.Type.STAND_ALONE, owner, key, (Object)null);
      }

      /** @deprecated */
      @Deprecated
      public static <T> Reference<T> createIntrusive(final HolderOwner<T> owner, final @Nullable T value) {
         return new Reference<T>(Holder.Reference.Type.INTRUSIVE, owner, (ResourceKey)null, value);
      }

      public ResourceKey<T> key() {
         if (this.key == null) {
            String var10002 = String.valueOf(this.value);
            throw new IllegalStateException("Trying to access unbound value '" + var10002 + "' from registry " + String.valueOf(this.owner));
         } else {
            return this.key;
         }
      }

      public T value() {
         if (this.value == null) {
            String var10002 = String.valueOf(this.key);
            throw new IllegalStateException("Trying to access unbound value '" + var10002 + "' from registry " + String.valueOf(this.owner));
         } else {
            return this.value;
         }
      }

      public boolean is(final Identifier key) {
         return this.key().identifier().equals(key);
      }

      public boolean is(final ResourceKey<T> key) {
         return this.key() == key;
      }

      private Set<TagKey<T>> boundTags() {
         if (this.tags == null) {
            throw new IllegalStateException("Tags not bound");
         } else {
            return this.tags;
         }
      }

      public boolean is(final TagKey<T> tag) {
         return this.boundTags().contains(tag);
      }

      public boolean is(final Holder<T> holder) {
         return holder.is(this.key());
      }

      public boolean is(final Predicate<ResourceKey<T>> predicate) {
         return predicate.test(this.key());
      }

      public boolean canSerializeIn(final HolderOwner<T> context) {
         return context.canSerialize(this.owner);
      }

      public Either<ResourceKey<T>, T> unwrap() {
         return Either.left(this.key());
      }

      public Optional<ResourceKey<T>> unwrapKey() {
         return Optional.of(this.key());
      }

      public Kind kind() {
         return Holder.Kind.REFERENCE;
      }

      public boolean isBound() {
         return this.key != null && this.value != null;
      }

      public boolean areComponentsBound() {
         return this.components != null;
      }

      void bindKey(final ResourceKey<T> key) {
         if (this.key != null && key != this.key) {
            String var10002 = String.valueOf(this.key);
            throw new IllegalStateException("Can't change holder key: existing=" + var10002 + ", new=" + String.valueOf(key));
         } else {
            this.key = key;
         }
      }

      protected void bindValue(final T value) {
         if (this.type == Holder.Reference.Type.INTRUSIVE && this.value != value) {
            String var10002 = String.valueOf(this.key);
            throw new IllegalStateException("Can't change holder " + var10002 + " value: existing=" + String.valueOf(this.value) + ", new=" + String.valueOf(value));
         } else {
            this.value = value;
         }
      }

      void bindTags(final Collection<TagKey<T>> tags) {
         this.tags = Set.copyOf(tags);
      }

      public void bindComponents(final DataComponentMap components) {
         this.components = components;
      }

      public Stream<TagKey<T>> tags() {
         return this.boundTags().stream();
      }

      public DataComponentMap components() {
         return (DataComponentMap)Objects.requireNonNull(this.components, "Components not bound yet");
      }

      public String toString() {
         String var10000 = String.valueOf(this.key);
         return "Reference{" + var10000 + "=" + String.valueOf(this.value) + "}";
      }

      protected static enum Type {
         STAND_ALONE,
         INTRUSIVE;

         private Type() {
         }

         // $FF: synthetic method
         private static Type[] $values() {
            return new Type[]{STAND_ALONE, INTRUSIVE};
         }
      }
   }
}
