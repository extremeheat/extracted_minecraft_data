package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketKeepAlive implements Packet<INetHandlerPlayServer> {
   private long field_149461_a;

   public CPacketKeepAlive() {
      super();
   }

   public CPacketKeepAlive(long var1) {
      super();
      this.field_149461_a = var1;
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147353_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149461_a = var1.readLong();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeLong(this.field_149461_a);
   }

   public long func_149460_c() {
      return this.field_149461_a;
   }
}
