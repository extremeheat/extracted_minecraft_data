package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C00PacketKeepAlive extends Packet {
   private int field_149461_a;

   public C00PacketKeepAlive() {
      super();
   }

   public C00PacketKeepAlive(int var1) {
      super();
      this.field_149461_a = var1;
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147353_a(this);
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149461_a = var1.readInt();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149461_a);
   }

   @Override
   public boolean func_148836_a() {
      return true;
   }

   public int func_149460_c() {
      return this.field_149461_a;
   }
}
