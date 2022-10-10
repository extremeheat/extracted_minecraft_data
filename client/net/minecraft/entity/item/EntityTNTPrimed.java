package net.minecraft.entity.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityTNTPrimed extends Entity {
   private static final DataParameter<Integer> field_184537_a;
   @Nullable
   private EntityLivingBase field_94084_b;
   private int field_70516_a;

   public EntityTNTPrimed(World var1) {
      super(EntityType.field_200735_aa, var1);
      this.field_70516_a = 80;
      this.field_70156_m = true;
      this.field_70178_ae = true;
      this.func_70105_a(0.98F, 0.98F);
   }

   public EntityTNTPrimed(World var1, double var2, double var4, double var6, @Nullable EntityLivingBase var8) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
      float var9 = (float)(Math.random() * 6.2831854820251465D);
      this.field_70159_w = (double)(-((float)Math.sin((double)var9)) * 0.02F);
      this.field_70181_x = 0.20000000298023224D;
      this.field_70179_y = (double)(-((float)Math.cos((double)var9)) * 0.02F);
      this.func_184534_a(80);
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
      this.field_94084_b = var8;
   }

   protected void func_70088_a() {
      this.field_70180_af.func_187214_a(field_184537_a, 80);
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
      if (!this.func_189652_ae()) {
         this.field_70181_x -= 0.03999999910593033D;
      }

      this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9800000190734863D;
      this.field_70181_x *= 0.9800000190734863D;
      this.field_70179_y *= 0.9800000190734863D;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071D;
         this.field_70179_y *= 0.699999988079071D;
         this.field_70181_x *= -0.5D;
      }

      --this.field_70516_a;
      if (this.field_70516_a <= 0) {
         this.func_70106_y();
         if (!this.field_70170_p.field_72995_K) {
            this.func_70515_d();
         }
      } else {
         this.func_70072_I();
         this.field_70170_p.func_195594_a(Particles.field_197601_L, this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v, 0.0D, 0.0D, 0.0D);
      }

   }

   private void func_70515_d() {
      float var1 = 4.0F;
      this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u + (double)(this.field_70131_O / 16.0F), this.field_70161_v, 4.0F, true);
   }

   protected void func_70014_b(NBTTagCompound var1) {
      var1.func_74777_a("Fuse", (short)this.func_184536_l());
   }

   protected void func_70037_a(NBTTagCompound var1) {
      this.func_184534_a(var1.func_74765_d("Fuse"));
   }

   @Nullable
   public EntityLivingBase func_94083_c() {
      return this.field_94084_b;
   }

   public float func_70047_e() {
      return 0.0F;
   }

   public void func_184534_a(int var1) {
      this.field_70180_af.func_187227_b(field_184537_a, var1);
      this.field_70516_a = var1;
   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_184537_a.equals(var1)) {
         this.field_70516_a = this.func_184535_k();
      }

   }

   public int func_184535_k() {
      return (Integer)this.field_70180_af.func_187225_a(field_184537_a);
   }

   public int func_184536_l() {
      return this.field_70516_a;
   }

   static {
      field_184537_a = EntityDataManager.func_187226_a(EntityTNTPrimed.class, DataSerializers.field_187192_b);
   }
}
