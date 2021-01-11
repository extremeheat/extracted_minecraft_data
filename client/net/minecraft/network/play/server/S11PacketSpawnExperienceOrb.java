package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S11PacketSpawnExperienceOrb implements Packet<INetHandlerPlayClient> {
   private int field_148992_a;
   private int field_148990_b;
   private int field_148991_c;
   private int field_148988_d;
   private int field_148989_e;

   public S11PacketSpawnExperienceOrb() {
      super();
   }

   public S11PacketSpawnExperienceOrb(EntityXPOrb var1) {
      super();
      this.field_148992_a = var1.func_145782_y();
      this.field_148990_b = MathHelper.func_76128_c(var1.field_70165_t * 32.0D);
      this.field_148991_c = MathHelper.func_76128_c(var1.field_70163_u * 32.0D);
      this.field_148988_d = MathHelper.func_76128_c(var1.field_70161_v * 32.0D);
      this.field_148989_e = var1.func_70526_d();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148992_a = var1.func_150792_a();
      this.field_148990_b = var1.readInt();
      this.field_148991_c = var1.readInt();
      this.field_148988_d = var1.readInt();
      this.field_148989_e = var1.readShort();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_148992_a);
      var1.writeInt(this.field_148990_b);
      var1.writeInt(this.field_148991_c);
      var1.writeInt(this.field_148988_d);
      var1.writeShort(this.field_148989_e);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147286_a(this);
   }

   public int func_148985_c() {
      return this.field_148992_a;
   }

   public int func_148984_d() {
      return this.field_148990_b;
   }

   public int func_148983_e() {
      return this.field_148991_c;
   }

   public int func_148982_f() {
      return this.field_148988_d;
   }

   public int func_148986_g() {
      return this.field_148989_e;
   }
}
