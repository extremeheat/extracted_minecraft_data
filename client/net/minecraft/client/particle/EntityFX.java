package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
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
   protected float field_82339_as = 1.0F;
   protected IIcon field_70550_a;
   public static double field_70556_an;
   public static double field_70554_ao;
   public static double field_70555_ap;

   protected EntityFX(World var1, double var2, double var4, double var6) {
      super(var1);
      this.func_70105_a(0.2F, 0.2F);
      this.field_70129_M = this.field_70131_O / 2.0F;
      this.func_70107_b(var2, var4, var6);
      this.field_70142_S = var2;
      this.field_70137_T = var4;
      this.field_70136_U = var6;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 1.0F;
      this.field_70548_b = this.field_70146_Z.nextFloat() * 3.0F;
      this.field_70549_c = this.field_70146_Z.nextFloat() * 3.0F;
      this.field_70544_f = (this.field_70146_Z.nextFloat() * 0.5F + 0.5F) * 2.0F;
      this.field_70547_e = (int)(4.0F / (this.field_70146_Z.nextFloat() * 0.9F + 0.1F));
      this.field_70546_d = 0;
   }

   public EntityFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6);
      this.field_70159_w = var8 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.4F);
      this.field_70181_x = var10 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.4F);
      this.field_70179_y = var12 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.4F);
      float var14 = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
      float var15 = MathHelper.func_76133_a(
         this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y
      );
      this.field_70159_w = this.field_70159_w / (double)var15 * (double)var14 * 0.4000000059604645;
      this.field_70181_x = this.field_70181_x / (double)var15 * (double)var14 * 0.4000000059604645 + 0.10000000149011612;
      this.field_70179_y = this.field_70179_y / (double)var15 * (double)var14 * 0.4000000059604645;
   }

   public EntityFX func_70543_e(float var1) {
      this.field_70159_w *= (double)var1;
      this.field_70181_x = (this.field_70181_x - 0.10000000149011612) * (double)var1 + 0.10000000149011612;
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

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   protected void func_70088_a() {
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      this.field_70181_x -= 0.04 * (double)this.field_70545_g;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9800000190734863;
      this.field_70181_x *= 0.9800000190734863;
      this.field_70179_y *= 0.9800000190734863;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071;
         this.field_70179_y *= 0.699999988079071;
      }
   }

   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = (float)this.field_94054_b / 16.0F;
      float var9 = var8 + 0.0624375F;
      float var10 = (float)this.field_94055_c / 16.0F;
      float var11 = var10 + 0.0624375F;
      float var12 = 0.1F * this.field_70544_f;
      if (this.field_70550_a != null) {
         var8 = this.field_70550_a.func_94209_e();
         var9 = this.field_70550_a.func_94212_f();
         var10 = this.field_70550_a.func_94206_g();
         var11 = this.field_70550_a.func_94210_h();
      }

      float var13 = (float)(this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var2 - field_70556_an);
      float var14 = (float)(this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var2 - field_70554_ao);
      float var15 = (float)(this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var2 - field_70555_ap);
      var1.func_78369_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as);
      var1.func_78374_a(
         (double)(var13 - var3 * var12 - var6 * var12),
         (double)(var14 - var4 * var12),
         (double)(var15 - var5 * var12 - var7 * var12),
         (double)var9,
         (double)var11
      );
      var1.func_78374_a(
         (double)(var13 - var3 * var12 + var6 * var12),
         (double)(var14 + var4 * var12),
         (double)(var15 - var5 * var12 + var7 * var12),
         (double)var9,
         (double)var10
      );
      var1.func_78374_a(
         (double)(var13 + var3 * var12 + var6 * var12),
         (double)(var14 + var4 * var12),
         (double)(var15 + var5 * var12 + var7 * var12),
         (double)var8,
         (double)var10
      );
      var1.func_78374_a(
         (double)(var13 + var3 * var12 - var6 * var12),
         (double)(var14 - var4 * var12),
         (double)(var15 + var5 * var12 - var7 * var12),
         (double)var8,
         (double)var11
      );
   }

   public int func_70537_b() {
      return 0;
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
   }

   public void func_110125_a(IIcon var1) {
      if (this.func_70537_b() == 1) {
         this.field_70550_a = var1;
      } else {
         if (this.func_70537_b() != 2) {
            throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
         }

         this.field_70550_a = var1;
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

   @Override
   public boolean func_70075_an() {
      return false;
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName()
         + ", Pos ("
         + this.field_70165_t
         + ","
         + this.field_70163_u
         + ","
         + this.field_70161_v
         + "), RGBA ("
         + this.field_70552_h
         + ","
         + this.field_70553_i
         + ","
         + this.field_70551_j
         + ","
         + this.field_82339_as
         + "), Age "
         + this.field_70546_d;
   }
}
