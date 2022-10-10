package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketResourcePackStatus implements Packet<INetHandlerPlayServer> {
   private CPacketResourcePackStatus.Action field_179719_b;

   public CPacketResourcePackStatus() {
      super();
   }

   public CPacketResourcePackStatus(CPacketResourcePackStatus.Action var1) {
      super();
      this.field_179719_b = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179719_b = (CPacketResourcePackStatus.Action)var1.func_179257_a(CPacketResourcePackStatus.Action.class);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_179719_b);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_175086_a(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;

      private Action() {
      }
   }
}
