package net.minecraft.network.status.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class S01PacketPong extends Packet {
   private long field_149293_a;

   public S01PacketPong() {
      super();
   }

   public S01PacketPong(long var1) {
      super();
      this.field_149293_a = var1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149293_a = var1.readLong();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeLong(this.field_149293_a);
   }

   public void func_148833_a(INetHandlerStatusClient var1) {
      var1.func_147398_a(this);
   }

   @Override
   public boolean func_148836_a() {
      return true;
   }

   public long func_149292_c() {
      return this.field_149293_a;
   }
}
