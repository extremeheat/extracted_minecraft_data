package net.minecraft.network.status.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C01PacketPing extends Packet {
   private long field_149290_a;

   public C01PacketPing() {
      super();
   }

   public C01PacketPing(long var1) {
      super();
      this.field_149290_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149290_a = var1.readLong();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeLong(this.field_149290_a);
   }

   public void func_148833_a(INetHandlerStatusServer var1) {
      var1.func_147311_a(this);
   }

   @Override
   public boolean func_148836_a() {
      return true;
   }

   public long func_149289_c() {
      return this.field_149290_a;
   }
}
