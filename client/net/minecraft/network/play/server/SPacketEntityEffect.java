package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class SPacketEntityEffect implements Packet<INetHandlerPlayClient> {
   private int field_149434_a;
   private byte field_149432_b;
   private byte field_149433_c;
   private int field_149431_d;
   private byte field_186985_e;

   public SPacketEntityEffect() {
      super();
   }

   public SPacketEntityEffect(int var1, PotionEffect var2) {
      super();
      this.field_149434_a = var1;
      this.field_149432_b = (byte)(Potion.func_188409_a(var2.func_188419_a()) & 255);
      this.field_149433_c = (byte)(var2.func_76458_c() & 255);
      if (var2.func_76459_b() > 32767) {
         this.field_149431_d = 32767;
      } else {
         this.field_149431_d = var2.func_76459_b();
      }

      this.field_186985_e = 0;
      if (var2.func_82720_e()) {
         this.field_186985_e = (byte)(this.field_186985_e | 1);
      }

      if (var2.func_188418_e()) {
         this.field_186985_e = (byte)(this.field_186985_e | 2);
      }

      if (var2.func_205348_f()) {
         this.field_186985_e = (byte)(this.field_186985_e | 4);
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149434_a = var1.func_150792_a();
      this.field_149432_b = var1.readByte();
      this.field_149433_c = var1.readByte();
      this.field_149431_d = var1.func_150792_a();
      this.field_186985_e = var1.readByte();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149434_a);
      var1.writeByte(this.field_149432_b);
      var1.writeByte(this.field_149433_c);
      var1.func_150787_b(this.field_149431_d);
      var1.writeByte(this.field_186985_e);
   }

   public boolean func_149429_c() {
      return this.field_149431_d == 32767;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147260_a(this);
   }

   public int func_149426_d() {
      return this.field_149434_a;
   }

   public byte func_149427_e() {
      return this.field_149432_b;
   }

   public byte func_149428_f() {
      return this.field_149433_c;
   }

   public int func_180755_e() {
      return this.field_149431_d;
   }

   public boolean func_179707_f() {
      return (this.field_186985_e & 2) == 2;
   }

   public boolean func_186984_g() {
      return (this.field_186985_e & 1) == 1;
   }

   public boolean func_205527_h() {
      return (this.field_186985_e & 4) == 4;
   }
}
