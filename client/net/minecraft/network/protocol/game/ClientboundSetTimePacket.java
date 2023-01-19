package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTimePacket implements Packet<ClientGamePacketListener> {
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

   public ClientboundSetTimePacket(FriendlyByteBuf var1) {
      super();
      this.gameTime = var1.readLong();
      this.dayTime = var1.readLong();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeLong(this.gameTime);
      var1.writeLong(this.dayTime);
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
