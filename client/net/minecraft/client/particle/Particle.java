package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public class Particle {
   private static final AxisAlignedBB field_187121_a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   protected World field_187122_b;
   protected double field_187123_c;
   protected double field_187124_d;
   protected double field_187125_e;
   protected double field_187126_f;
   protected double field_187127_g;
   protected double field_187128_h;
   protected double field_187129_i;
   protected double field_187130_j;
   protected double field_187131_k;
   private AxisAlignedBB field_187120_G;
   protected boolean field_187132_l;
   protected boolean field_190017_n;
   protected boolean field_187133_m;
   protected float field_187134_n;
   protected float field_187135_o;
   protected Random field_187136_p;
   protected int field_94054_b;
   protected int field_94055_c;
   protected float field_70548_b;
   protected float field_70549_c;
   protected int field_70546_d;
   protected int field_70547_e;
   protected float field_70544_f;
   protected float field_70545_g;
   protected float field_70552_h;
   protected float field_70553_i;
   protected float field_70551_j;
   protected float field_82339_as;
   protected TextureAtlasSprite field_187119_C;
   protected float field_190014_F;
   protected float field_190015_G;
   public static double field_70556_an;
   public static double field_70554_ao;
   public static double field_70555_ap;
   public static Vec3d field_190016_K;

   protected Particle(World var1, double var2, double var4, double var6) {
      super();
      this.field_187120_G = field_187121_a;
      this.field_187134_n = 0.6F;
      this.field_187135_o = 1.8F;
      this.field_187136_p = new Random();
      this.field_82339_as = 1.0F;
      this.field_187122_b = var1;
      this.func_187115_a(0.2F, 0.2F);
      this.func_187109_b(var2, var4, var6);
      this.field_187123_c = var2;
      this.field_187124_d = var4;
      this.field_187125_e = var6;
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.field_70548_b = this.field_187136_p.nextFloat() * 3.0F;
      this.field_70549_c = this.field_187136_p.nextFloat() * 3.0F;
      this.field_70544_f = (this.field_187136_p.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.field_70547_e = (int)(4.0F / (this.field_187136_p.nextFloat() * 0.9F + 0.1F));
      this.field_70546_d = 0;
      this.field_190017_n = true;
   }

   public Particle(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6);
      this.field_187129_i = var8 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.field_187130_j = var10 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.field_187131_k = var12 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      float var14 = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
      float var15 = MathHelper.func_76133_a(this.field_187129_i * this.field_187129_i + this.field_187130_j * this.field_187130_j + this.field_187131_k * this.field_187131_k);
      this.field_187129_i = this.field_187129_i / (double)var15 * (double)var14 * 0.4000000059604645D;
      this.field_187130_j = this.field_187130_j / (double)var15 * (double)var14 * 0.4000000059604645D + 0.10000000149011612D;
      this.field_187131_k = this.field_187131_k / (double)var15 * (double)var14 * 0.4000000059604645D;
   }

   public Particle func_70543_e(float var1) {
      this.field_187129_i *= (double)var1;
      this.field_187130_j = (this.field_187130_j - 0.10000000149011612D) * (double)var1 + 0.10000000149011612D;
      this.field_187131_k *= (double)var1;
      return this;
   }

   public Particle func_70541_f(float var1) {
      this.func_187115_a(0.2F * var1, 0.2F * var1);
      this.field_70544_f *= var1;
      return this;
   }

   public void func_70538_b(float var1, float var2, float var3) {
      this.field_70552_h = var1;
      this.field_70553_i = var2;
      this.field_70551_j = var3;
   }

   public void func_82338_g(float var1) {
      this.field_82339_as = var1;
   }

   public boolean func_187111_c() {
      return false;
   }

   public float func_70534_d() {
      return this.field_70552_h;
   }

   public float func_70542_f() {
      return this.field_70553_i;
   }

   public float func_70535_g() {
      return this.field_70551_j;
   }

   public void func_187114_a(int var1) {
      this.field_70547_e = var1;
   }

   public int func_206254_h() {
      return this.field_70547_e;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.field_187130_j -= 0.04D * (double)this.field_70545_g;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.9800000190734863D;
      this.field_187130_j *= 0.9800000190734863D;
      this.field_187131_k *= 0.9800000190734863D;
      if (this.field_187132_l) {
         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = (float)this.field_94054_b / 32.0F;
      float var10 = var9 + 0.03121875F;
      float var11 = (float)this.field_94055_c / 32.0F;
      float var12 = var11 + 0.03121875F;
      float var13 = 0.1F * this.field_70544_f;
      if (this.field_187119_C != null) {
         var9 = this.field_187119_C.func_94209_e();
         var10 = this.field_187119_C.func_94212_f();
         var11 = this.field_187119_C.func_94206_g();
         var12 = this.field_187119_C.func_94210_h();
      }

      float var14 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)var3 - field_70556_an);
      float var15 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)var3 - field_70554_ao);
      float var16 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)var3 - field_70555_ap);
      int var17 = this.func_189214_a(var3);
      int var18 = var17 >> 16 & '\uffff';
      int var19 = var17 & '\uffff';
      Vec3d[] var20 = new Vec3d[]{new Vec3d((double)(-var4 * var13 - var7 * var13), (double)(-var5 * var13), (double)(-var6 * var13 - var8 * var13)), new Vec3d((double)(-var4 * var13 + var7 * var13), (double)(var5 * var13), (double)(-var6 * var13 + var8 * var13)), new Vec3d((double)(var4 * var13 + var7 * var13), (double)(var5 * var13), (double)(var6 * var13 + var8 * var13)), new Vec3d((double)(var4 * var13 - var7 * var13), (double)(-var5 * var13), (double)(var6 * var13 - var8 * var13))};
      if (this.field_190014_F != 0.0F) {
         float var21 = this.field_190014_F + (this.field_190014_F - this.field_190015_G) * var3;
         float var22 = MathHelper.func_76134_b(var21 * 0.5F);
         float var23 = MathHelper.func_76126_a(var21 * 0.5F) * (float)field_190016_K.field_72450_a;
         float var24 = MathHelper.func_76126_a(var21 * 0.5F) * (float)field_190016_K.field_72448_b;
         float var25 = MathHelper.func_76126_a(var21 * 0.5F) * (float)field_190016_K.field_72449_c;
         Vec3d var26 = new Vec3d((double)var23, (double)var24, (double)var25);

         for(int var27 = 0; var27 < 4; ++var27) {
            var20[var27] = var26.func_186678_a(2.0D * var20[var27].func_72430_b(var26)).func_178787_e(var20[var27].func_186678_a((double)(var22 * var22) - var26.func_72430_b(var26))).func_178787_e(var26.func_72431_c(var20[var27]).func_186678_a((double)(2.0F * var22)));
         }
      }

      var1.func_181662_b((double)var14 + var20[0].field_72450_a, (double)var15 + var20[0].field_72448_b, (double)var16 + var20[0].field_72449_c).func_187315_a((double)var10, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)var14 + var20[1].field_72450_a, (double)var15 + var20[1].field_72448_b, (double)var16 + var20[1].field_72449_c).func_187315_a((double)var10, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)var14 + var20[2].field_72450_a, (double)var15 + var20[2].field_72448_b, (double)var16 + var20[2].field_72449_c).func_187315_a((double)var9, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)var14 + var20[3].field_72450_a, (double)var15 + var20[3].field_72448_b, (double)var16 + var20[3].field_72449_c).func_187315_a((double)var9, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
   }

   public int func_70537_b() {
      return 0;
   }

   public void func_187117_a(TextureAtlasSprite var1) {
      int var2 = this.func_70537_b();
      if (var2 == 1) {
         this.field_187119_C = var1;
      } else {
         throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
      }
   }

   public void func_70536_a(int var1) {
      if (this.func_70537_b() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.field_94054_b = var1 % 16;
         this.field_94055_c = var1 / 16;
      }
   }

   public void func_94053_h() {
      ++this.field_94054_b;
   }

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.field_187126_f + "," + this.field_187127_g + "," + this.field_187128_h + "), RGBA (" + this.field_70552_h + "," + this.field_70553_i + "," + this.field_70551_j + "," + this.field_82339_as + "), Age " + this.field_70546_d;
   }

   public void func_187112_i() {
      this.field_187133_m = true;
   }

   protected void func_187115_a(float var1, float var2) {
      if (var1 != this.field_187134_n || var2 != this.field_187135_o) {
         this.field_187134_n = var1;
         this.field_187135_o = var2;
         AxisAlignedBB var3 = this.func_187116_l();
         double var4 = (var3.field_72340_a + var3.field_72336_d - (double)var1) / 2.0D;
         double var6 = (var3.field_72339_c + var3.field_72334_f - (double)var1) / 2.0D;
         this.func_187108_a(new AxisAlignedBB(var4, var3.field_72338_b, var6, var4 + (double)this.field_187134_n, var3.field_72338_b + (double)this.field_187135_o, var6 + (double)this.field_187134_n));
      }

   }

   public void func_187109_b(double var1, double var3, double var5) {
      this.field_187126_f = var1;
      this.field_187127_g = var3;
      this.field_187128_h = var5;
      float var7 = this.field_187134_n / 2.0F;
      float var8 = this.field_187135_o;
      this.func_187108_a(new AxisAlignedBB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
   }

   public void func_187110_a(double var1, double var3, double var5) {
      double var9 = var3;
      if (this.field_190017_n && (var1 != 0.0D || var3 != 0.0D || var5 != 0.0D)) {
         ReuseableStream var13 = new ReuseableStream(this.field_187122_b.func_199406_a((Entity)null, this.func_187116_l(), var1, var3, var5));
         var3 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, this.func_187116_l(), var13.func_212761_a(), var3);
         this.func_187108_a(this.func_187116_l().func_72317_d(0.0D, var3, 0.0D));
         var1 = VoxelShapes.func_212437_a(EnumFacing.Axis.X, this.func_187116_l(), var13.func_212761_a(), var1);
         if (var1 != 0.0D) {
            this.func_187108_a(this.func_187116_l().func_72317_d(var1, 0.0D, 0.0D));
         }

         var5 = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, this.func_187116_l(), var13.func_212761_a(), var5);
         if (var5 != 0.0D) {
            this.func_187108_a(this.func_187116_l().func_72317_d(0.0D, 0.0D, var5));
         }
      } else {
         this.func_187108_a(this.func_187116_l().func_72317_d(var1, var3, var5));
      }

      this.func_187118_j();
      this.field_187132_l = var3 != var3 && var9 < 0.0D;
      if (var1 != var1) {
         this.field_187129_i = 0.0D;
      }

      if (var5 != var5) {
         this.field_187131_k = 0.0D;
      }

   }

   protected void func_187118_j() {
      AxisAlignedBB var1 = this.func_187116_l();
      this.field_187126_f = (var1.field_72340_a + var1.field_72336_d) / 2.0D;
      this.field_187127_g = var1.field_72338_b;
      this.field_187128_h = (var1.field_72339_c + var1.field_72334_f) / 2.0D;
   }

   public int func_189214_a(float var1) {
      BlockPos var2 = new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h);
      return this.field_187122_b.func_175667_e(var2) ? this.field_187122_b.func_175626_b(var2, 0) : 0;
   }

   public boolean func_187113_k() {
      return !this.field_187133_m;
   }

   public AxisAlignedBB func_187116_l() {
      return this.field_187120_G;
   }

   public void func_187108_a(AxisAlignedBB var1) {
      this.field_187120_G = var1;
   }
}
