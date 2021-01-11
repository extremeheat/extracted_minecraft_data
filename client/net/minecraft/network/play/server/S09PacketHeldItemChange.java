package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S09PacketHeldItemChange implements Packet<INetHandlerPlayClient> {
   private int field_149387_a;

   public S09PacketHeldItemChange() {
      super();
   }

   public S09PacketHeldItemChange(int var1) {
      super();
      this.field_149387_a = var1;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149387_a = var1.readByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149387_a);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147257_a(this);
   }

   public int func_149385_c() {
      return this.field_149387_a;
   }
}
