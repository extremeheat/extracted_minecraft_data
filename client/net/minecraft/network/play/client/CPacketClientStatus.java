package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketClientStatus implements Packet<INetHandlerPlayServer> {
   private CPacketClientStatus.State field_149437_a;

   public CPacketClientStatus() {
      super();
   }

   public CPacketClientStatus(CPacketClientStatus.State var1) {
      super();
      this.field_149437_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149437_a = (CPacketClientStatus.State)var1.func_179257_a(CPacketClientStatus.State.class);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_149437_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147342_a(this);
   }

   public CPacketClientStatus.State func_149435_c() {
      return this.field_149437_a;
   }

   public static enum State {
      PERFORM_RESPAWN,
      REQUEST_STATS;

      private State() {
      }
   }
}
