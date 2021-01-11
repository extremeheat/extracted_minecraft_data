package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;

public class S00PacketDisconnect implements Packet<INetHandlerLoginClient> {
   private IChatComponent field_149605_a;

   public S00PacketDisconnect() {
      super();
   }

   public S00PacketDisconnect(IChatComponent var1) {
      super();
      this.field_149605_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149605_a = var1.func_179258_d();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179256_a(this.field_149605_a);
   }

   public void func_148833_a(INetHandlerLoginClient var1) {
      var1.func_147388_a(this);
   }

   public IChatComponent func_149603_c() {
      return this.field_149605_a;
   }
}
