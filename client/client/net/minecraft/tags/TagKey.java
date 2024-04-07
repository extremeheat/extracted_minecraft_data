package net.minecraft.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record TagKey<T>(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
   private static final Interner<TagKey<?>> VALUES = Interners.newWeakInterner();

   @Deprecated
   public TagKey(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
      super();
      this.registry = registry;
      this.location = location;
   }

   public static <T> Codec<TagKey<T>> codec(ResourceKey<? extends Registry<T>> var0) {
      return ResourceLocation.CODEC.xmap(var1 -> create(var0, var1), TagKey::location);
   }

   public static <T> Codec<TagKey<T>> hashedCodec(ResourceKey<? extends Registry<T>> var0) {
      return Codec.STRING
         .comapFlatMap(
            var1 -> var1.startsWith("#") ? ResourceLocation.read(var1.substring(1)).map(var1x -> create(var0, var1x)) : DataResult.error(() -> "Not a tag id"),
            var0x -> "#" + var0x.location
         );
   }

   public static <T> TagKey<T> create(ResourceKey<? extends Registry<T>> var0, ResourceLocation var1) {
      return (TagKey<T>)VALUES.intern(new TagKey(var0, var1));
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> var1) {
      return this.registry == var1;
   }

   public <E> Optional<TagKey<E>> cast(ResourceKey<? extends Registry<E>> var1) {
      return this.isFor(var1) ? Optional.of((TagKey<E>)this) : Optional.empty();
   }

   public String toString() {
      return "TagKey[" + this.registry.location() + " / " + this.location + "]";
   }
}
