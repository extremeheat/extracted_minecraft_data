package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C00PacketKeepAlive implements Packet<INetHandlerPlayServer> {
   private int field_149461_a;

   public C00PacketKeepAlive() {
      super();
   }

   public C00PacketKeepAlive(int var1) {
      super();
      this.field_149461_a = var1;
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147353_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149461_a = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149461_a);
   }

   public int func_149460_c() {
      return this.field_149461_a;
   }
}
