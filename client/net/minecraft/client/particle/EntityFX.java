package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFX extends Entity {
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
   protected TextureAtlasSprite field_70550_a;
   public static double field_70556_an;
   public static double field_70554_ao;
   public static double field_70555_ap;

   protected EntityFX(World var1, double var2, double var4, double var6) {
      super(var1);
      this.field_82339_as = 1.0F;
      this.func_70105_a(0.2F, 0.2F);
      this.func_70107_b(var2, var4, var6);
      this.field_70142_S = this.field_70169_q = var2;
      this.field_70137_T = this.field_70167_r = var4;
      this.field_70136_U = this.field_70166_s = var6;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 1.0F;
      this.field_70548_b = this.field_70146_Z.nextFloat() * 3.0F;
      this.field_70549_c = this.field_70146_Z.nextFloat() * 3.0F;
      this.field_70544_f = (this.field_70146_Z.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.field_70547_e = (int)(4.0F / (this.field_70146_Z.nextFloat() * 0.9F + 0.1F));
      this.field_70546_d = 0;
   }

   public EntityFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6);
      this.field_70159_w = var8 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.field_70181_x = var10 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.field_70179_y = var12 + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
      float var14 = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
      float var15 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y);
      this.field_70159_w = this.field_70159_w / (double)var15 * (double)var14 * 0.4000000059604645D;
      this.field_70181_x = this.field_70181_x / (double)var15 * (double)var14 * 0.4000000059604645D + 0.10000000149011612D;
      this.field_70179_y = this.field_70179_y / (double)var15 * (double)var14 * 0.4000000059604645D;
   }

   public EntityFX func_70543_e(float var1) {
      this.field_70159_w *= (double)var1;
      this.field_70181_x = (this.field_70181_x - 0.10000000149011612D) * (double)var1 + 0.10000000149011612D;
      this.field_70179_y *= (double)var1;
      return this;
   }

   public EntityFX func_70541_f(float var1) {
      this.func_70105_a(0.2F * var1, 0.2F * var1);
      this.field_70544_f *= var1;
      return this;
   }

   public void func_70538_b(float var1, float var2, float var3) {
      this.field_70552_h = var1;
      this.field_70553_i = var2;
      this.field_70551_j = var3;
   }

   public void func_82338_g(float var1) {
      if (this.field_82339_as == 1.0F && var1 < 1.0F) {
         Minecraft.func_71410_x().field_71452_i.func_178928_b(this);
      } else if (this.field_82339_as < 1.0F && var1 == 1.0F) {
         Minecraft.func_71410_x().field_71452_i.func_178931_c(this);
      }

      this.field_82339_as = var1;
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

   public float func_174838_j() {
      return this.field_82339_as;
   }

   protected boolean func_70041_e_() {
      return false;
   }

   protected void func_70088_a() {
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      this.field_70181_x -= 0.04D * (double)this.field_70545_g;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9800000190734863D;
      this.field_70181_x *= 0.9800000190734863D;
      this.field_70179_y *= 0.9800000190734863D;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071D;
         this.field_70179_y *= 0.699999988079071D;
      }

   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = (float)this.field_94054_b / 16.0F;
      float var10 = var9 + 0.0624375F;
      float var11 = (float)this.field_94055_c / 16.0F;
      float var12 = var11 + 0.0624375F;
      float var13 = 0.1F * this.field_70544_f;
      if (this.field_70550_a != null) {
         var9 = this.field_70550_a.func_94209_e();
         var10 = this.field_70550_a.func_94212_f();
         var11 = this.field_70550_a.func_94206_g();
         var12 = this.field_70550_a.func_94210_h();
      }

      float var14 = (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var3 - field_70556_an);
      float var15 = (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var3 - field_70554_ao);
      float var16 = (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var3 - field_70555_ap);
      int var17 = this.func_70070_b(var3);
      int var18 = var17 >> 16 & '\uffff';
      int var19 = var17 & '\uffff';
      var1.func_181662_b((double)(var14 - var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 - var6 * var13 - var8 * var13)).func_181673_a((double)var10, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181671_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 - var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 - var6 * var13 + var8 * var13)).func_181673_a((double)var10, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181671_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 + var4 * var13 + var7 * var13), (double)(var15 + var5 * var13), (double)(var16 + var6 * var13 + var8 * var13)).func_181673_a((double)var9, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181671_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)(var14 + var4 * var13 - var7 * var13), (double)(var15 - var5 * var13), (double)(var16 + var6 * var13 - var8 * var13)).func_181673_a((double)var9, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_181671_a(var18, var19).func_181675_d();
   }

   public int func_70537_b() {
      return 0;
   }

   public void func_70014_b(NBTTagCompound var1) {
   }

   public void func_70037_a(NBTTagCompound var1) {
   }

   public void func_180435_a(TextureAtlasSprite var1) {
      int var2 = this.func_70537_b();
      if (var2 == 1) {
         this.field_70550_a = var1;
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

   public boolean func_70075_an() {
      return false;
   }

   public String toString() {
      return this.getClass().getSimpleName() + ", Pos (" + this.field_70165_t + "," + this.field_70163_u + "," + this.field_70161_v + "), RGBA (" + this.field_70552_h + "," + this.field_70553_i + "," + this.field_70551_j + "," + this.field_82339_as + "), Age " + this.field_70546_d;
   }
}
