package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.registry.IRegistry;

public class SPacketSpawnMob implements Packet<INetHandlerPlayClient> {
   private int field_149042_a;
   private UUID field_186894_b;
   private int field_149040_b;
   private double field_149041_c;
   private double field_149038_d;
   private double field_149039_e;
   private int field_149036_f;
   private int field_149037_g;
   private int field_149047_h;
   private byte field_149048_i;
   private byte field_149045_j;
   private byte field_149046_k;
   private EntityDataManager field_149043_l;
   private List<EntityDataManager.DataEntry<?>> field_149044_m;

   public SPacketSpawnMob() {
      super();
   }

   public SPacketSpawnMob(EntityLivingBase var1) {
      super();
      this.field_149042_a = var1.func_145782_y();
      this.field_186894_b = var1.func_110124_au();
      this.field_149040_b = IRegistry.field_212629_r.func_148757_b(var1.func_200600_R());
      this.field_149041_c = var1.field_70165_t;
      this.field_149038_d = var1.field_70163_u;
      this.field_149039_e = var1.field_70161_v;
      this.field_149048_i = (byte)((int)(var1.field_70177_z * 256.0F / 360.0F));
      this.field_149045_j = (byte)((int)(var1.field_70125_A * 256.0F / 360.0F));
      this.field_149046_k = (byte)((int)(var1.field_70759_as * 256.0F / 360.0F));
      double var2 = 3.9D;
      double var4 = var1.field_70159_w;
      double var6 = var1.field_70181_x;
      double var8 = var1.field_70179_y;
      if (var4 < -3.9D) {
         var4 = -3.9D;
      }

      if (var6 < -3.9D) {
         var6 = -3.9D;
      }

      if (var8 < -3.9D) {
         var8 = -3.9D;
      }

      if (var4 > 3.9D) {
         var4 = 3.9D;
      }

      if (var6 > 3.9D) {
         var6 = 3.9D;
      }

      if (var8 > 3.9D) {
         var8 = 3.9D;
      }

      this.field_149036_f = (int)(var4 * 8000.0D);
      this.field_149037_g = (int)(var6 * 8000.0D);
      this.field_149047_h = (int)(var8 * 8000.0D);
      this.field_149043_l = var1.func_184212_Q();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149042_a = var1.func_150792_a();
      this.field_186894_b = var1.func_179253_g();
      this.field_149040_b = var1.func_150792_a();
      this.field_149041_c = var1.readDouble();
      this.field_149038_d = var1.readDouble();
      this.field_149039_e = var1.readDouble();
      this.field_149048_i = var1.readByte();
      this.field_149045_j = var1.readByte();
      this.field_149046_k = var1.readByte();
      this.field_149036_f = var1.readShort();
      this.field_149037_g = var1.readShort();
      this.field_149047_h = var1.readShort();
      this.field_149044_m = EntityDataManager.func_187215_b(var1);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149042_a);
      var1.func_179252_a(this.field_186894_b);
      var1.func_150787_b(this.field_149040_b);
      var1.writeDouble(this.field_149041_c);
      var1.writeDouble(this.field_149038_d);
      var1.writeDouble(this.field_149039_e);
      var1.writeByte(this.field_149048_i);
      var1.writeByte(this.field_149045_j);
      var1.writeByte(this.field_149046_k);
      var1.writeShort(this.field_149036_f);
      var1.writeShort(this.field_149037_g);
      var1.writeShort(this.field_149047_h);
      this.field_149043_l.func_187216_a(var1);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147281_a(this);
   }

   @Nullable
   public List<EntityDataManager.DataEntry<?>> func_149027_c() {
      return this.field_149044_m;
   }

   public int func_149024_d() {
      return this.field_149042_a;
   }

   public UUID func_186890_c() {
      return this.field_186894_b;
   }

   public int func_149025_e() {
      return this.field_149040_b;
   }

   public double func_186891_e() {
      return this.field_149041_c;
   }

   public double func_186892_f() {
      return this.field_149038_d;
   }

   public double func_186893_g() {
      return this.field_149039_e;
   }

   public int func_149026_i() {
      return this.field_149036_f;
   }

   public int func_149033_j() {
      return this.field_149037_g;
   }

   public int func_149031_k() {
      return this.field_149047_h;
   }

   public byte func_149028_l() {
      return this.field_149048_i;
   }

   public byte func_149030_m() {
      return this.field_149045_j;
   }

   public byte func_149032_n() {
      return this.field_149046_k;
   }
}
