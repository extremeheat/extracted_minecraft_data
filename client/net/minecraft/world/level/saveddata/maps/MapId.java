package net.minecraft.world.level.saveddata.maps;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MapId(int id) {
   public static final Codec<MapId> CODEC;
   public static final StreamCodec<ByteBuf, MapId> STREAM_CODEC;

   public MapId(int var1) {
      super();
      this.id = var1;
   }

   public String key() {
      return "map_" + this.id;
   }

   static {
      CODEC = Codec.INT.xmap(MapId::new, MapId::id);
      STREAM_CODEC = ByteBufCodecs.VAR_INT.map(MapId::new, MapId::id);
   }
}
