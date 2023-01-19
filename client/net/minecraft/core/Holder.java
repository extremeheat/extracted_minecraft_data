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

   Holder.Kind kind();

   boolean isValidInRegistry(Registry<T> var1);

   static <T> Holder<T> direct(T var0) {
      return new Holder.Direct<>((T)var0);
   }

   static <T> Holder<T> hackyErase(Holder<? extends T> var0) {
      return var0;
   }

   public static record Direct<T>(T a) implements Holder<T> {
      private final T value;

      public Direct(T var1) {
         super();
         this.value = (T)var1;
      }

      @Override
      public boolean isBound() {
         return true;
      }

      @Override
      public boolean is(ResourceLocation var1) {
         return false;
      }

      @Override
      public boolean is(ResourceKey<T> var1) {
         return false;
      }

      @Override
      public boolean is(TagKey<T> var1) {
         return false;
      }

      @Override
      public boolean is(Predicate<ResourceKey<T>> var1) {
         return false;
      }

      @Override
      public Either<ResourceKey<T>, T> unwrap() {
         return Either.right(this.value);
      }

      @Override
      public Optional<ResourceKey<T>> unwrapKey() {
         return Optional.empty();
      }

      @Override
      public Holder.Kind kind() {
         return Holder.Kind.DIRECT;
      }

      @Override
      public String toString() {
         return "Direct{" + this.value + "}";
      }

      @Override
      public boolean isValidInRegistry(Registry<T> var1) {
         return true;
      }

      @Override
      public Stream<TagKey<T>> tags() {
         return Stream.of();
      }
   }

   public static enum Kind {
      REFERENCE,
      DIRECT;

      private Kind() {
      }
   }

   public static class Reference<T> implements Holder<T> {
      private final Registry<T> registry;
      private Set<TagKey<T>> tags = Set.of();
      private final Holder.Reference.Type type;
      @Nullable
      private ResourceKey<T> key;
      @Nullable
      private T value;

      private Reference(Holder.Reference.Type var1, Registry<T> var2, @Nullable ResourceKey<T> var3, @Nullable T var4) {
         super();
         this.registry = var2;
         this.type = var1;
         this.key = var3;
         this.value = (T)var4;
      }

      public static <T> Holder.Reference<T> createStandAlone(Registry<T> var0, ResourceKey<T> var1) {
         return new Holder.Reference<>(Holder.Reference.Type.STAND_ALONE, var0, var1, (T)null);
      }

      @Deprecated
      public static <T> Holder.Reference<T> createIntrusive(Registry<T> var0, @Nullable T var1) {
         return new Holder.Reference<>(Holder.Reference.Type.INTRUSIVE, var0, null, (T)var1);
      }

      public ResourceKey<T> key() {
         if (this.key == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.registry);
         } else {
            return this.key;
         }
      }

      @Override
      public T value() {
         if (this.value == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.key + "' from registry " + this.registry);
         } else {
            return this.value;
         }
      }

      @Override
      public boolean is(ResourceLocation var1) {
         return this.key().location().equals(var1);
      }

      @Override
      public boolean is(ResourceKey<T> var1) {
         return this.key() == var1;
      }

      @Override
      public boolean is(TagKey<T> var1) {
         return this.tags.contains(var1);
      }

      @Override
      public boolean is(Predicate<ResourceKey<T>> var1) {
         return var1.test(this.key());
      }

      @Override
      public boolean isValidInRegistry(Registry<T> var1) {
         return this.registry == var1;
      }

      @Override
      public Either<ResourceKey<T>, T> unwrap() {
         return Either.left(this.key());
      }

      @Override
      public Optional<ResourceKey<T>> unwrapKey() {
         return Optional.of(this.key());
      }

      @Override
      public Holder.Kind kind() {
         return Holder.Kind.REFERENCE;
      }

      @Override
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
            this.value = (T)var2;
         }
      }

      void bindTags(Collection<TagKey<T>> var1) {
         this.tags = Set.copyOf(var1);
      }

      @Override
      public Stream<TagKey<T>> tags() {
         return this.tags.stream();
      }

      @Override
      public String toString() {
         return "Reference{" + this.key + "=" + this.value + "}";
      }

      static enum Type {
         STAND_ALONE,
         INTRUSIVE;

         private Type() {
         }
      }
   }
}
