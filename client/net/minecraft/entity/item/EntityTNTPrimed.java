package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity {
   public int field_70516_a;
   private EntityLivingBase field_94084_b;

   public EntityTNTPrimed(World var1) {
      super(var1);
      this.field_70156_m = true;
      this.func_70105_a(0.98F, 0.98F);
      this.field_70129_M = this.field_70131_O / 2.0F;
   }

   public EntityTNTPrimed(World var1, double var2, double var4, double var6, EntityLivingBase var8) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
      float var9 = (float)(Math.random() * 3.1415927410125732 * 2.0);
      this.field_70159_w = (double)(-((float)Math.sin((double)var9)) * 0.02F);
      this.field_70181_x = 0.20000000298023224;
      this.field_70179_y = (double)(-((float)Math.cos((double)var9)) * 0.02F);
      this.field_70516_a = 80;
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
      this.field_94084_b = var8;
   }

   @Override
   protected void func_70088_a() {
   }

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.field_70181_x -= 0.03999999910593033;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9800000190734863;
      this.field_70181_x *= 0.9800000190734863;
      this.field_70179_y *= 0.9800000190734863;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071;
         this.field_70179_y *= 0.699999988079071;
         this.field_70181_x *= -0.5;
      }

      if (this.field_70516_a-- <= 0) {
         this.func_70106_y();
         if (!this.field_70170_p.field_72995_K) {
            this.func_70515_d();
         }
      } else {
         this.field_70170_p.func_72869_a("smoke", this.field_70165_t, this.field_70163_u + 0.5, this.field_70161_v, 0.0, 0.0, 0.0);
      }
   }

   private void func_70515_d() {
      float var1 = 4.0F;
      this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, var1, true);
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
      var1.func_74774_a("Fuse", (byte)this.field_70516_a);
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
      this.field_70516_a = var1.func_74771_c("Fuse");
   }

   @Override
   public float func_70053_R() {
      return 0.0F;
   }

   public EntityLivingBase func_94083_c() {
      return this.field_94084_b;
   }
}
