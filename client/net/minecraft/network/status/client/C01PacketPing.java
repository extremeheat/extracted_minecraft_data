package net.minecraft.network.status.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C01PacketPing implements Packet<INetHandlerStatusServer> {
   private long field_149290_a;

   public C01PacketPing() {
      super();
   }

   public C01PacketPing(long var1) {
      super();
      this.field_149290_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149290_a = var1.readLong();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeLong(this.field_149290_a);
   }

   public void func_148833_a(INetHandlerStatusServer var1) {
      var1.func_147311_a(this);
   }

   public long func_149289_c() {
      return this.field_149290_a;
   }
}
