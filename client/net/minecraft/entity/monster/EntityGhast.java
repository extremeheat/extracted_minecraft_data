package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob {
   public int field_70797_a;
   public double field_70795_b;
   public double field_70796_c;
   public double field_70793_d;
   private Entity field_70792_g;
   private int field_70798_h;
   public int field_70794_e;
   public int field_70791_f;
   private int field_92014_j = 1;

   public EntityGhast(World var1) {
      super(var1);
      this.func_70105_a(4.0F, 4.0F);
      this.field_70178_ae = true;
      this.field_70728_aV = 5;
   }

   public boolean func_110182_bF() {
      return this.field_70180_af.func_75683_a(16) != 0;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else if ("fireball".equals(var1.func_76355_l()) && var1.func_76346_g() instanceof EntityPlayer) {
         super.func_70097_a(var1, 1000.0F);
         ((EntityPlayer)var1.func_76346_g()).func_71029_a(AchievementList.field_76028_y);
         return true;
      } else {
         return super.func_70097_a(var1, var2);
      }
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, (byte)0);
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(10.0);
   }

   @Override
   protected void func_70626_be() {
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.field_73013_u == EnumDifficulty.PEACEFUL) {
         this.func_70106_y();
      }

      this.func_70623_bb();
      this.field_70794_e = this.field_70791_f;
      double var1 = this.field_70795_b - this.field_70165_t;
      double var3 = this.field_70796_c - this.field_70163_u;
      double var5 = this.field_70793_d - this.field_70161_v;
      double var7 = var1 * var1 + var3 * var3 + var5 * var5;
      if (var7 < 1.0 || var7 > 3600.0) {
         this.field_70795_b = this.field_70165_t + (double)((this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.field_70796_c = this.field_70163_u + (double)((this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.field_70793_d = this.field_70161_v + (double)((this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * 16.0F);
      }

      if (this.field_70797_a-- <= 0) {
         this.field_70797_a += this.field_70146_Z.nextInt(5) + 2;
         var7 = (double)MathHelper.func_76133_a(var7);
         if (this.func_70790_a(this.field_70795_b, this.field_70796_c, this.field_70793_d, var7)) {
            this.field_70159_w += var1 / var7 * 0.1;
            this.field_70181_x += var3 / var7 * 0.1;
            this.field_70179_y += var5 / var7 * 0.1;
         } else {
            this.field_70795_b = this.field_70165_t;
            this.field_70796_c = this.field_70163_u;
            this.field_70793_d = this.field_70161_v;
         }
      }

      if (this.field_70792_g != null && this.field_70792_g.field_70128_L) {
         this.field_70792_g = null;
      }

      if (this.field_70792_g == null || this.field_70798_h-- <= 0) {
         this.field_70792_g = this.field_70170_p.func_72856_b(this, 100.0);
         if (this.field_70792_g != null) {
            this.field_70798_h = 20;
         }
      }

      double var9 = 64.0;
      if (this.field_70792_g != null && this.field_70792_g.func_70068_e(this) < var9 * var9) {
         double var11 = this.field_70792_g.field_70165_t - this.field_70165_t;
         double var13 = this.field_70792_g.field_70121_D.field_72338_b
            + (double)(this.field_70792_g.field_70131_O / 2.0F)
            - (this.field_70163_u + (double)(this.field_70131_O / 2.0F));
         double var15 = this.field_70792_g.field_70161_v - this.field_70161_v;
         this.field_70761_aq = this.field_70177_z = -((float)Math.atan2(var11, var15)) * 180.0F / 3.1415927F;
         if (this.func_70685_l(this.field_70792_g)) {
            if (this.field_70791_f == 10) {
               this.field_70170_p.func_72889_a(null, 1007, (int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 0);
            }

            ++this.field_70791_f;
            if (this.field_70791_f == 20) {
               this.field_70170_p.func_72889_a(null, 1008, (int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 0);
               EntityLargeFireball var17 = new EntityLargeFireball(this.field_70170_p, this, var11, var13, var15);
               var17.field_92057_e = this.field_92014_j;
               double var18 = 4.0;
               Vec3 var20 = this.func_70676_i(1.0F);
               var17.field_70165_t = this.field_70165_t + var20.field_72450_a * var18;
               var17.field_70163_u = this.field_70163_u + (double)(this.field_70131_O / 2.0F) + 0.5;
               var17.field_70161_v = this.field_70161_v + var20.field_72449_c * var18;
               this.field_70170_p.func_72838_d(var17);
               this.field_70791_f = -40;
            }
         } else if (this.field_70791_f > 0) {
            --this.field_70791_f;
         }
      } else {
         this.field_70761_aq = this.field_70177_z = -((float)Math.atan2(this.field_70159_w, this.field_70179_y)) * 180.0F / 3.1415927F;
         if (this.field_70791_f > 0) {
            --this.field_70791_f;
         }
      }

      if (!this.field_70170_p.field_72995_K) {
         byte var22 = this.field_70180_af.func_75683_a(16);
         byte var12 = (byte)(this.field_70791_f > 10 ? 1 : 0);
         if (var22 != var12) {
            this.field_70180_af.func_75692_b(16, var12);
         }
      }
   }

   private boolean func_70790_a(double var1, double var3, double var5, double var7) {
      double var9 = (this.field_70795_b - this.field_70165_t) / var7;
      double var11 = (this.field_70796_c - this.field_70163_u) / var7;
      double var13 = (this.field_70793_d - this.field_70161_v) / var7;
      AxisAlignedBB var15 = this.field_70121_D.func_72329_c();

      for(int var16 = 1; (double)var16 < var7; ++var16) {
         var15.func_72317_d(var9, var11, var13);
         if (!this.field_70170_p.func_72945_a(this, var15).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.ghast.moan";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.ghast.scream";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.ghast.death";
   }

   @Override
   protected Item func_146068_u() {
      return Items.field_151016_H;
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(2) + this.field_70146_Z.nextInt(1 + var2);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.func_145779_a(Items.field_151073_bk, 1);
      }

      var3 = this.field_70146_Z.nextInt(3) + this.field_70146_Z.nextInt(1 + var2);

      for(int var6 = 0; var6 < var3; ++var6) {
         this.func_145779_a(Items.field_151016_H, 1);
      }
   }

   @Override
   protected float func_70599_aP() {
      return 10.0F;
   }

   @Override
   public boolean func_70601_bi() {
      return this.field_70146_Z.nextInt(20) == 0 && super.func_70601_bi() && this.field_70170_p.field_73013_u != EnumDifficulty.PEACEFUL;
   }

   @Override
   public int func_70641_bl() {
      return 1;
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("ExplosionPower", this.field_92014_j);
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("ExplosionPower", 99)) {
         this.field_92014_j = var1.func_74762_e("ExplosionPower");
      }
   }
}
