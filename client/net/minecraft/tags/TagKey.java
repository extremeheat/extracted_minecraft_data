package net.minecraft.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record TagKey<T>(ResourceKey<? extends Registry<T>> registry, Identifier location) {
   private static final Interner<TagKey<?>> VALUES = Interners.newWeakInterner();

   /** @deprecated */
   @Deprecated
   public TagKey(ResourceKey<? extends Registry<T>> var1, Identifier var2) {
      super();
      this.registry = var1;
      this.location = var2;
   }

   public static <T> Codec<TagKey<T>> codec(ResourceKey<? extends Registry<T>> var0) {
      return Identifier.CODEC.xmap((var1) -> create(var0, var1), TagKey::location);
   }

   public static <T> Codec<TagKey<T>> hashedCodec(ResourceKey<? extends Registry<T>> var0) {
      return Codec.STRING.comapFlatMap((var1) -> var1.startsWith("#") ? Identifier.read(var1.substring(1)).map((var1x) -> create(var0, var1x)) : DataResult.error(() -> "Not a tag id"), (var0x) -> "#" + String.valueOf(var0x.location));
   }

   public static <T> StreamCodec<ByteBuf, TagKey<T>> streamCodec(ResourceKey<? extends Registry<T>> var0) {
      return Identifier.STREAM_CODEC.map((var1) -> create(var0, var1), TagKey::location);
   }

   public static <T> TagKey<T> create(ResourceKey<? extends Registry<T>> var0, Identifier var1) {
      return (TagKey)VALUES.intern(new TagKey(var0, var1));
   }

   public boolean isFor(ResourceKey<? extends Registry<?>> var1) {
      return this.registry == var1;
   }

   public <E> Optional<TagKey<E>> cast(ResourceKey<? extends Registry<E>> var1) {
      return this.isFor(var1) ? Optional.of(this) : Optional.empty();
   }

   public String toString() {
      String var10000 = String.valueOf(this.registry.identifier());
      return "TagKey[" + var10000 + " / " + String.valueOf(this.location) + "]";
   }
}
