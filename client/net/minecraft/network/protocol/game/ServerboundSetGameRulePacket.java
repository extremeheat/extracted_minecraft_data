package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gamerules.GameRule;

public record ServerboundSetGameRulePacket(List<Entry> entries) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundSetGameRulePacket> STREAM_CODEC;

   public ServerboundSetGameRulePacket {
      super();
   }

   public PacketType<ServerboundSetGameRulePacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_GAME_RULE;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleSetGameRule(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ServerboundSetGameRulePacket.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()), ServerboundSetGameRulePacket::entries, ServerboundSetGameRulePacket::new);
   }

   public static record Entry(ResourceKey<GameRule<?>> gameRuleKey, String value) {
      public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC;

      public Entry {
         super();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ResourceKey.streamCodec(Registries.GAME_RULE), Entry::gameRuleKey, ByteBufCodecs.STRING_UTF8, Entry::value, Entry::new);
      }
   }
}
