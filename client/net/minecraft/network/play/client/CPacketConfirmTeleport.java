package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketConfirmTeleport implements Packet<INetHandlerPlayServer> {
   private int field_186988_a;

   public CPacketConfirmTeleport() {
      super();
   }

   public CPacketConfirmTeleport(int var1) {
      super();
      this.field_186988_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_186988_a = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_186988_a);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_184339_a(this);
   }

   public int func_186987_a() {
      return this.field_186988_a;
   }
}
