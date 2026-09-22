package net.minecraft.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record PropertyMap(Map<Identifier, String> properties) {
   public static final int MAX_PROPERTY_COUNT = 32;
   public static final StreamCodec<ByteBuf, PropertyMap> STREAM_CODEC;

   public PropertyMap {
      super();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.stringUtf8(128), 32), PropertyMap::properties, PropertyMap::new);
   }
}
