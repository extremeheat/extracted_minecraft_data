package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity {
   public int field_70516_a;
   private EntityLivingBase field_94084_b;

   public EntityTNTPrimed(World var1) {
      super(var1);
      this.field_70156_m = true;
      this.func_70105_a(0.98F, 0.98F);
   }

   public EntityTNTPrimed(World var1, double var2, double var4, double var6, EntityLivingBase var8) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
      float var9 = (float)(Math.random() * 3.1415927410125732D * 2.0D);
      this.field_70159_w = (double)(-((float)Math.sin((double)var9)) * 0.02F);
      this.field_70181_x = 0.20000000298023224D;
      this.field_70179_y = (double)(-((float)Math.cos((double)var9)) * 0.02F);
      this.field_70516_a = 80;
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
      this.field_94084_b = var8;
   }

   protected void func_70088_a() {
   }

   protected boolean func_70041_e_() {
      return false;
   }

   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.field_70181_x -= 0.03999999910593033D;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9800000190734863D;
      this.field_70181_x *= 0.9800000190734863D;
      this.field_70179_y *= 0.9800000190734863D;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071D;
         this.field_70179_y *= 0.699999988079071D;
         this.field_70181_x *= -0.5D;
      }

      if (this.field_70516_a-- <= 0) {
         this.func_70106_y();
         if (!this.field_70170_p.field_72995_K) {
            this.func_70515_d();
         }
      } else {
         this.func_70072_I();
         this.field_70170_p.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v, 0.0D, 0.0D, 0.0D);
      }

   }

   private void func_70515_d() {
      float var1 = 4.0F;
      this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u + (double)(this.field_70131_O / 16.0F), this.field_70161_v, var1, true);
   }

   protected void func_70014_b(NBTTagCompound var1) {
      var1.func_74774_a("Fuse", (byte)this.field_70516_a);
   }

   protected void func_70037_a(NBTTagCompound var1) {
      this.field_70516_a = var1.func_74771_c("Fuse");
   }

   public EntityLivingBase func_94083_c() {
      return this.field_94084_b;
   }

   public float func_70047_e() {
      return 0.0F;
   }
}
