package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketUpdateBeacon implements Packet<INetHandlerPlayServer> {
   private int field_210357_a;
   private int field_210358_b;

   public CPacketUpdateBeacon() {
      super();
   }

   public CPacketUpdateBeacon(int var1, int var2) {
      super();
      this.field_210357_a = var1;
      this.field_210358_b = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_210357_a = var1.func_150792_a();
      this.field_210358_b = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_210357_a);
      var1.func_150787_b(this.field_210358_b);
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_210154_a(this);
   }

   public int func_210355_a() {
      return this.field_210357_a;
   }

   public int func_210356_b() {
      return this.field_210358_b;
   }
}
