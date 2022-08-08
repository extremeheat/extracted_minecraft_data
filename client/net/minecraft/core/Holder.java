package net.minecraft.core;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface Holder<T> {
   T value();

   boolean isBound();

   boolean is(ResourceLocation var1);

   boolean is(ResourceKey<T> var1);

   boolean is(Predicate<ResourceKey<T>> var1);

   boolean is(TagKey<T> var1);

   Stream<TagKey<T>> tags();

   Either<ResourceKey<T>, T> unwrap();

   Optional<ResourceKey<T>> unwrapKey();

   Kind kind();

   boolean isValidInRegistry(Registry<T> var1);

   static <T> Holder<T> direct(T var0) {
      return new Direct(var0);
   }

   static <T> Holder<T> hackyErase(Holder<? extends T> var0) {
      return var0;
   }

   public static record Direct<T>(T a) implements Holder<T> {
      private final T value;

      public Direct(T var1) {
         super();
         this.value = var1;
      }

      public boolean isBound() {
         return true;
      }

      public boolean is(ResourceLocation var1) {
         return false;
      }

      public boolean is(ResourceKey<T> var1) {
         return false;
      }

      public boolean is(TagKey<T> var1) {
         return false;
      }

      public boolean is(Predicate<ResourceKey<T>> var1) {
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
         return "Direct{" + this.value + "}";
      }

      public boolean isValidInRegistry(Registry<T> var1) {
         return true;
      }

      public Stream<TagKey<T>> tags() {
         return Stream.of();
      }

      public T value() {
         return this.value;
      }
   }

   public static class Reference<T> implements Holder<T> {
      private final Registry<T> registry;
      private Set<TagKey<T>> tags = Set.of();
      private final Type type;
      @Nullable
      private ResourceKey<T> key;
      @Nullable
      private T value;

      private Reference(Type var1, Registry<T> var2, @Nullable ResourceKey<T> var3, @Nullable T var4) {
         super();
         this.registry = var2;
         this.type = var1;
         this.key = var3;
         this.value = var4;
      }

      public static <T> Reference<T> createStandAlone(Registry<T> var0, ResourceKey<T> var1) {
         return new Reference(Holder.Reference.Type.STAND_ALONE, var0, var1, (Object)null);
      }

      /** @deprecated */
      @Deprecated
      public static <T> Reference<T> createIntrusive(Registry<T> var0, @Nullable T var1) {
         return new Reference(Holder.Reference.Type.INTRUSIVE, var0, (ResourceKey)null, var1);
      }

      public ResourceKey<T> key() {
         if (this.key == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.registry);
         } else {
            return this.key;
         }
      }

      public T value() {
         if (this.value == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.key + "' from registry " + this.registry);
         } else {
            return this.value;
         }
      }

      public boolean is(ResourceLocation var1) {
         return this.key().location().equals(var1);
      }

      public boolean is(ResourceKey<T> var1) {
         return this.key() == var1;
      }

      public boolean is(TagKey<T> var1) {
         return this.tags.contains(var1);
      }

      public boolean is(Predicate<ResourceKey<T>> var1) {
         return var1.test(this.key());
      }

      public boolean isValidInRegistry(Registry<T> var1) {
         return this.registry == var1;
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

      void bind(ResourceKey<T> var1, T var2) {
         if (this.key != null && var1 != this.key) {
            throw new IllegalStateException("Can't change holder key: existing=" + this.key + ", new=" + var1);
         } else if (this.type == Holder.Reference.Type.INTRUSIVE && this.value != var2) {
            throw new IllegalStateException("Can't change holder " + var1 + " value: existing=" + this.value + ", new=" + var2);
         } else {
            this.key = var1;
            this.value = var2;
         }
      }

      void bindTags(Collection<TagKey<T>> var1) {
         this.tags = Set.copyOf(var1);
      }

      public Stream<TagKey<T>> tags() {
         return this.tags.stream();
      }

      public String toString() {
         return "Reference{" + this.key + "=" + this.value + "}";
      }

      static enum Type {
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
}
