package net.minecraft.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record TagKey<T>(ResourceKey<? extends Registry<T>> registry, ResourceLocation location) {
   private static final Interner<TagKey<?>> VALUES = Interners.newWeakInterner();

   /** @deprecated */
   @Deprecated
   public TagKey(ResourceKey<? extends Registry<T>> var1, ResourceLocation var2) {
      super();
      this.registry = var1;
      this.location = var2;
   }

   public static <T> Codec<TagKey<T>> codec(ResourceKey<? extends Registry<T>> var0) {
      return ResourceLocation.CODEC.xmap((var1) -> {
         return create(var0, var1);
      }, TagKey::location);
   }

   public static <T> Codec<TagKey<T>> hashedCodec(ResourceKey<? extends Registry<T>> var0) {
      return Codec.STRING.comapFlatMap((var1) -> {
         return var1.startsWith("#") ? ResourceLocation.read(var1.substring(1)).map((var1x) -> {
            return create(var0, var1x);
         }) : DataResult.error(() -> {
            return "Not a tag id";
         });
      }, (var0x) -> {
         return "#" + String.valueOf(var0x.location);
      });
   }

   public static <T> StreamCodec<ByteBuf, TagKey<T>> streamCodec(ResourceKey<? extends Registry<T>> var0) {
      return ResourceLocation.STREAM_CODEC.map((var1) -> {
         return create(var0, var1);
      }, TagKey::location);
   }

   public static <T> TagKey<T> create(ResourceKey<? extends Registry<T>> var0, ResourceLocation var1) {
      return (TagKey)VALUES.intern(new TagKey(var0, var1));
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> var1) {
      return this.registry == var1;
   }

   public <E> Optional<TagKey<E>> cast(ResourceKey<? extends Registry<E>> var1) {
      return this.isFor(var1) ? Optional.of(this) : Optional.empty();
   }

   public String toString() {
      String var10000 = String.valueOf(this.registry.location());
      return "TagKey[" + var10000 + " / " + String.valueOf(this.location) + "]";
   }

   public ResourceKey<? extends Registry<T>> registry() {
      return this.registry;
   }

   public ResourceLocation location() {
      return this.location;
   }
}
