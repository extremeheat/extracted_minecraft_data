package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S00PacketKeepAlive extends Packet {
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

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149136_a = var1.readInt();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149136_a);
   }

   @Override
   public boolean func_148836_a() {
      return true;
   }

   public int func_149134_c() {
      return this.field_149136_a;
   }
}
