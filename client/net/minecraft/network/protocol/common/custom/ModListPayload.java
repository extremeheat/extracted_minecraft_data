package net.minecraft.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ModListPayload(Map<Identifier, PropertyMap> entries) implements CustomPacketPayload {
   public static final int MAX_MOD_ENTRY_COUNT = 8192;
   public static final StreamCodec<ByteBuf, ModListPayload> STREAM_CODEC;
   public static final CustomPacketPayload.Type<ModListPayload> TYPE;

   public ModListPayload {
      super();
   }

   public CustomPacketPayload.Type<ModListPayload> type() {
      return TYPE;
   }

   public static ModListPayload createClient() {
      return new ModListPayload(Map.of());
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, PropertyMap.STREAM_CODEC, 8192), ModListPayload::entries, ModListPayload::new);
      TYPE = CustomPacketPayload.<ModListPayload>createType("mod_list");
   }
}
