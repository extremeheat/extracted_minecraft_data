package net.minecraft.network.login.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent$Serializer;

public class S00PacketDisconnect extends Packet {
   private IChatComponent field_149605_a;

   public S00PacketDisconnect() {
      super();
   }

   public S00PacketDisconnect(IChatComponent var1) {
      super();
      this.field_149605_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149605_a = IChatComponent$Serializer.func_150699_a(var1.func_150789_c(32767));
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(IChatComponent$Serializer.func_150696_a(this.field_149605_a));
   }

   public void func_148833_a(INetHandlerLoginClient var1) {
      var1.func_147388_a(this);
   }

   @Override
   public boolean func_148836_a() {
      return true;
   }

   public IChatComponent func_149603_c() {
      return this.field_149605_a;
   }
}
