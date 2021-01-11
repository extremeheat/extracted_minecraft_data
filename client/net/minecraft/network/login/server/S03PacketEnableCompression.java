package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S03PacketEnableCompression implements Packet<INetHandlerLoginClient> {
   private int field_179733_a;

   public S03PacketEnableCompression() {
      super();
   }

   public S03PacketEnableCompression(int var1) {
      super();
      this.field_179733_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_179733_a = var1.func_150792_a();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_179733_a);
   }

   public void func_148833_a(INetHandlerLoginClient var1) {
      var1.func_180464_a(this);
   }

   public int func_179731_a() {
      return this.field_179733_a;
   }
}
