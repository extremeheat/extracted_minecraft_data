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

   /** @deprecated */
   @Deprecated
   boolean is(Holder<T> var1);

   Stream<TagKey<T>> tags();

   Either<ResourceKey<T>, T> unwrap();

   Optional<ResourceKey<T>> unwrapKey();

   Kind kind();

   boolean canSerializeIn(HolderOwner<T> var1);

   default String getRegisteredName() {
      return (String)this.unwrapKey().map((var0) -> {
         return var0.location().toString();
      }).orElse("[unregistered]");
   }

   static <T> Holder<T> direct(T var0) {
      return new Direct(var0);
   }

   public static record Direct<T>(T value) implements Holder<T> {
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

      public boolean is(Holder<T> var1) {
         return this.value.equals(var1.value());
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
         return "Direct{" + String.valueOf(this.value) + "}";
      }

      public boolean canSerializeIn(HolderOwner<T> var1) {
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
      private final HolderOwner<T> owner;
      @Nullable
      private Set<TagKey<T>> tags;
      private final Type type;
      @Nullable
      private ResourceKey<T> key;
      @Nullable
      private T value;

      protected Reference(Type var1, HolderOwner<T> var2, @Nullable ResourceKey<T> var3, @Nullable T var4) {
         super();
         this.owner = var2;
         this.type = var1;
         this.key = var3;
         this.value = var4;
      }

      public static <T> Reference<T> createStandAlone(HolderOwner<T> var0, ResourceKey<T> var1) {
         return new Reference(Holder.Reference.Type.STAND_ALONE, var0, var1, (Object)null);
      }

      /** @deprecated */
      @Deprecated
      public static <T> Reference<T> createIntrusive(HolderOwner<T> var0, @Nullable T var1) {
         return new Reference(Holder.Reference.Type.INTRUSIVE, var0, (ResourceKey)null, var1);
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

      public boolean is(ResourceLocation var1) {
         return this.key().location().equals(var1);
      }

      public boolean is(ResourceKey<T> var1) {
         return this.key() == var1;
      }

      private Set<TagKey<T>> boundTags() {
         if (this.tags == null) {
            throw new IllegalStateException("Tags not bound");
         } else {
            return this.tags;
         }
      }

      public boolean is(TagKey<T> var1) {
         return this.boundTags().contains(var1);
      }

      public boolean is(Holder<T> var1) {
         return var1.is(this.key());
      }

      public boolean is(Predicate<ResourceKey<T>> var1) {
         return var1.test(this.key());
      }

      public boolean canSerializeIn(HolderOwner<T> var1) {
         return this.owner.canSerializeIn(var1);
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

      void bindKey(ResourceKey<T> var1) {
         if (this.key != null && var1 != this.key) {
            String var10002 = String.valueOf(this.key);
            throw new IllegalStateException("Can't change holder key: existing=" + var10002 + ", new=" + String.valueOf(var1));
         } else {
            this.key = var1;
         }
      }

      protected void bindValue(T var1) {
         if (this.type == Holder.Reference.Type.INTRUSIVE && this.value != var1) {
            String var10002 = String.valueOf(this.key);
            throw new IllegalStateException("Can't change holder " + var10002 + " value: existing=" + String.valueOf(this.value) + ", new=" + String.valueOf(var1));
         } else {
            this.value = var1;
         }
      }

      void bindTags(Collection<TagKey<T>> var1) {
         this.tags = Set.copyOf(var1);
      }

      public Stream<TagKey<T>> tags() {
         return this.boundTags().stream();
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
