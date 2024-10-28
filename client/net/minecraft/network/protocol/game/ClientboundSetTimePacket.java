package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetTimePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetTimePacket> STREAM_CODEC = Packet.codec(ClientboundSetTimePacket::write, ClientboundSetTimePacket::new);
   private final long gameTime;
   private final long dayTime;

   public ClientboundSetTimePacket(long var1, long var3, boolean var5) {
      super();
      this.gameTime = var1;
      long var6 = var3;
      if (!var5) {
         var6 = -var3;
         if (var6 == 0L) {
            var6 = -1L;
         }
      }

      this.dayTime = var6;
   }

   private ClientboundSetTimePacket(FriendlyByteBuf var1) {
      super();
      this.gameTime = var1.readLong();
      this.dayTime = var1.readLong();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeLong(this.gameTime);
      var1.writeLong(this.dayTime);
   }

   public PacketType<ClientboundSetTimePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_TIME;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetTime(this);
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }
}
