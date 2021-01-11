package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S40PacketDisconnect implements Packet<INetHandlerPlayClient> {
   private IChatComponent field_149167_a;

   public S40PacketDisconnect() {
      super();
   }

   public S40PacketDisconnect(IChatComponent var1) {
      super();
      this.field_149167_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149167_a = var1.func_179258_d();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179256_a(this.field_149167_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147253_a(this);
   }

   public IChatComponent func_149165_c() {
      return this.field_149167_a;
   }
}
