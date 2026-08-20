package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record MapDecorationType(Identifier assetId, boolean showOnItemFrame, boolean trackCount) {
   public static final Codec<Holder<MapDecorationType>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<MapDecorationType>> STREAM_CODEC;

   public MapDecorationType {
      super();
   }

   static {
      CODEC = BuiltInRegistries.MAP_DECORATION_TYPE.holderByNameCodec();
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.MAP_DECORATION_TYPE);
   }
}
