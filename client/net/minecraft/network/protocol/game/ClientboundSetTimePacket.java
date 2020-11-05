package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTimePacket implements Packet<ClientGamePacketListener> {
   private long gameTime;
   private long dayTime;

   public ClientboundSetTimePacket() {
      super();
   }

   public ClientboundSetTimePacket(long var1, long var3, boolean var5) {
      super();
      this.gameTime = var1;
      this.dayTime = var3;
      if (!var5) {
         this.dayTime = -this.dayTime;
         if (this.dayTime == 0L) {
            this.dayTime = -1L;
         }
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.gameTime = var1.readLong();
      this.dayTime = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
