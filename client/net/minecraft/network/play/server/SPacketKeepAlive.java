package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketKeepAlive implements Packet<INetHandlerPlayClient> {
   private long field_149136_a;

   public SPacketKeepAlive() {
      super();
   }

   public SPacketKeepAlive(long var1) {
      super();
      this.field_149136_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147272_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149136_a = var1.readLong();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeLong(this.field_149136_a);
   }

   public long func_149134_c() {
      return this.field_149136_a;
   }
}
