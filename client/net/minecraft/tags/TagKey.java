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
   public TagKey(ResourceKey<? extends Registry<T>> registry, Identifier location) {
      super();
      this.registry = registry;
      this.location = location;
   }

   public static <T> Codec<TagKey<T>> codec(final ResourceKey<? extends Registry<T>> registryName) {
      return Identifier.CODEC.xmap((name) -> create(registryName, name), TagKey::location);
   }

   public static <T> Codec<TagKey<T>> hashedCodec(final ResourceKey<? extends Registry<T>> registryName) {
      return Codec.STRING.comapFlatMap((name) -> name.startsWith("#") ? Identifier.read(name.substring(1)).map((id) -> create(registryName, id)) : DataResult.error(() -> "Not a tag id"), (e) -> "#" + String.valueOf(e.location));
   }

   public static <T> StreamCodec<ByteBuf, TagKey<T>> streamCodec(final ResourceKey<? extends Registry<T>> registryName) {
      return Identifier.STREAM_CODEC.map((location) -> create(registryName, location), TagKey::location);
   }

   public static <T> TagKey<T> create(final ResourceKey<? extends Registry<T>> registry, final Identifier location) {
      return (TagKey)VALUES.intern(new TagKey(registry, location));
   }

   public boolean isFor(final ResourceKey<? extends Registry<?>> registry) {
      return this.registry == registry;
   }

   public <E> Optional<TagKey<E>> cast(final ResourceKey<? extends Registry<E>> registry) {
      return this.isFor(registry) ? Optional.of(this) : Optional.empty();
   }

   public String toString() {
      String var10000 = String.valueOf(this.registry.identifier());
      return "TagKey[" + var10000 + " / " + String.valueOf(this.location) + "]";
   }
}
