package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S05PacketSpawnPosition extends Packet {
   private int field_149364_a;
   private int field_149362_b;
   private int field_149363_c;

   public S05PacketSpawnPosition() {
      super();
   }

   public S05PacketSpawnPosition(int var1, int var2, int var3) {
      super();
      this.field_149364_a = var1;
      this.field_149362_b = var2;
      this.field_149363_c = var3;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149364_a = var1.readInt();
      this.field_149362_b = var1.readInt();
      this.field_149363_c = var1.readInt();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149364_a);
      var1.writeInt(this.field_149362_b);
      var1.writeInt(this.field_149363_c);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147271_a(this);
   }

   @Override
   public boolean func_148836_a() {
      return false;
   }

   @Override
   public String func_148835_b() {
      return String.format("x=%d, y=%d, z=%d", this.field_149364_a, this.field_149362_b, this.field_149363_c);
   }

   public int func_149360_c() {
      return this.field_149364_a;
   }

   public int func_149359_d() {
      return this.field_149362_b;
   }

   public int func_149358_e() {
      return this.field_149363_c;
   }
}
