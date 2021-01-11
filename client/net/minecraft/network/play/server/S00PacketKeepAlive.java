package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S00PacketKeepAlive implements Packet<INetHandlerPlayClient> {
   private int field_149136_a;

   public S00PacketKeepAlive() {
      super();
   }

   public S00PacketKeepAlive(int var1) {
      super();
      this.field_149136_a = var1;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147272_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149136_a = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149136_a);
   }

   public int func_149134_c() {
      return this.field_149136_a;
   }
}
