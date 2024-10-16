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

   @Deprecated
   boolean is(Holder<T> var1);

   Stream<TagKey<T>> tags();

   Either<ResourceKey<T>, T> unwrap();

   Optional<ResourceKey<T>> unwrapKey();

   Holder.Kind kind();

   boolean canSerializeIn(HolderOwner<T> var1);

   default String getRegisteredName() {
      return this.unwrapKey().map(var0 -> var0.location().toString()).orElse("[unregistered]");
   }

   static <T> Holder<T> direct(T var0) {
      return new Holder.Direct<>((T)var0);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   public static enum Kind {
      REFERENCE,
      DIRECT;

      private Kind() {
      }
   }

   public static class Reference<T> implements Holder<T> {
      private final HolderOwner<T> owner;
      @Nullable
      private Set<TagKey<T>> tags;
      private final Holder.Reference.Type type;
      @Nullable
      private ResourceKey<T> key;
      @Nullable
      private T value;

      protected Reference(Holder.Reference.Type var1, HolderOwner<T> var2, @Nullable ResourceKey<T> var3, @Nullable T var4) {
         super();
         this.owner = var2;
         this.type = var1;
         this.key = var3;
         this.value = (T)var4;
      }

      public static <T> Holder.Reference<T> createStandAlone(HolderOwner<T> var0, ResourceKey<T> var1) {
         return new Holder.Reference<>(Holder.Reference.Type.STAND_ALONE, var0, var1, null);
      }

      @Deprecated
      public static <T> Holder.Reference<T> createIntrusive(HolderOwner<T> var0, @Nullable T var1) {
         return new Holder.Reference<>(Holder.Reference.Type.INTRUSIVE, var0, null, (T)var1);
      }

      public ResourceKey<T> key() {
         if (this.key == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.owner);
         } else {
            return this.key;
         }
      }

      @Override
      public T value() {
         if (this.value == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.key + "' from registry " + this.owner);
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

      private Set<TagKey<T>> boundTags() {
         if (this.tags == null) {
            throw new IllegalStateException("Tags not bound");
         } else {
            return this.tags;
         }
      }

      @Override
      public boolean is(TagKey<T> var1) {
         return this.boundTags().contains(var1);
      }

      @Override
      public boolean is(Holder<T> var1) {
         return var1.is(this.key());
      }

      @Override
      public boolean is(Predicate<ResourceKey<T>> var1) {
         return var1.test(this.key());
      }

      @Override
      public boolean canSerializeIn(HolderOwner<T> var1) {
         return this.owner.canSerializeIn(var1);
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

      void bindKey(ResourceKey<T> var1) {
         if (this.key != null && var1 != this.key) {
            throw new IllegalStateException("Can't change holder key: existing=" + this.key + ", new=" + var1);
         } else {
            this.key = var1;
         }
      }

      protected void bindValue(T var1) {
         if (this.type == Holder.Reference.Type.INTRUSIVE && this.value != var1) {
            throw new IllegalStateException("Can't change holder " + this.key + " value: existing=" + this.value + ", new=" + var1);
         } else {
            this.value = (T)var1;
         }
      }

      void bindTags(Collection<TagKey<T>> var1) {
         this.tags = Set.copyOf(var1);
      }

      @Override
      public Stream<TagKey<T>> tags() {
         return this.boundTags().stream();
      }

      @Override
      public String toString() {
         return "Reference{" + this.key + "=" + this.value + "}";
      }

      protected static enum Type {
         STAND_ALONE,
         INTRUSIVE;

         private Type() {
         }
      }
   }
}
