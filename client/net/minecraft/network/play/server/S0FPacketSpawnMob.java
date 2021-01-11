package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0FPacketSpawnMob implements Packet<INetHandlerPlayClient> {
   private int field_149042_a;
   private int field_149040_b;
   private int field_149041_c;
   private int field_149038_d;
   private int field_149039_e;
   private int field_149036_f;
   private int field_149037_g;
   private int field_149047_h;
   private byte field_149048_i;
   private byte field_149045_j;
   private byte field_149046_k;
   private DataWatcher field_149043_l;
   private List<DataWatcher.WatchableObject> field_149044_m;

   public S0FPacketSpawnMob() {
      super();
   }

   public S0FPacketSpawnMob(EntityLivingBase var1) {
      super();
      this.field_149042_a = var1.func_145782_y();
      this.field_149040_b = (byte)EntityList.func_75619_a(var1);
      this.field_149041_c = MathHelper.func_76128_c(var1.field_70165_t * 32.0D);
      this.field_149038_d = MathHelper.func_76128_c(var1.field_70163_u * 32.0D);
      this.field_149039_e = MathHelper.func_76128_c(var1.field_70161_v * 32.0D);
      this.field_149048_i = (byte)((int)(var1.field_70177_z * 256.0F / 360.0F));
      this.field_149045_j = (byte)((int)(var1.field_70125_A * 256.0F / 360.0F));
      this.field_149046_k = (byte)((int)(var1.field_70759_as * 256.0F / 360.0F));
      double var2 = 3.9D;
      double var4 = var1.field_70159_w;
      double var6 = var1.field_70181_x;
      double var8 = var1.field_70179_y;
      if (var4 < -var2) {
         var4 = -var2;
      }

      if (var6 < -var2) {
         var6 = -var2;
      }

      if (var8 < -var2) {
         var8 = -var2;
      }

      if (var4 > var2) {
         var4 = var2;
      }

      if (var6 > var2) {
         var6 = var2;
      }

      if (var8 > var2) {
         var8 = var2;
      }

      this.field_149036_f = (int)(var4 * 8000.0D);
      this.field_149037_g = (int)(var6 * 8000.0D);
      this.field_149047_h = (int)(var8 * 8000.0D);
      this.field_149043_l = var1.func_70096_w();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149042_a = var1.func_150792_a();
      this.field_149040_b = var1.readByte() & 255;
      this.field_149041_c = var1.readInt();
      this.field_149038_d = var1.readInt();
      this.field_149039_e = var1.readInt();
      this.field_149048_i = var1.readByte();
      this.field_149045_j = var1.readByte();
      this.field_149046_k = var1.readByte();
      this.field_149036_f = var1.readShort();
      this.field_149037_g = var1.readShort();
      this.field_149047_h = var1.readShort();
      this.field_149044_m = DataWatcher.func_151508_b(var1);
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149042_a);
      var1.writeByte(this.field_149040_b & 255);
      var1.writeInt(this.field_149041_c);
      var1.writeInt(this.field_149038_d);
      var1.writeInt(this.field_149039_e);
      var1.writeByte(this.field_149048_i);
      var1.writeByte(this.field_149045_j);
      var1.writeByte(this.field_149046_k);
      var1.writeShort(this.field_149036_f);
      var1.writeShort(this.field_149037_g);
      var1.writeShort(this.field_149047_h);
      this.field_149043_l.func_151509_a(var1);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147281_a(this);
   }

   public List<DataWatcher.WatchableObject> func_149027_c() {
      if (this.field_149044_m == null) {
         this.field_149044_m = this.field_149043_l.func_75685_c();
      }

      return this.field_149044_m;
   }

   public int func_149024_d() {
      return this.field_149042_a;
   }

   public int func_149025_e() {
      return this.field_149040_b;
   }

   public int func_149023_f() {
      return this.field_149041_c;
   }

   public int func_149034_g() {
      return this.field_149038_d;
   }

   public int func_149029_h() {
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
