package net.minecraft.entity.passive;

import java.util.Calendar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBat extends EntityAmbientCreature {
   private ChunkCoordinates field_82237_a;

   public EntityBat(World var1) {
      super(var1);
      this.func_70105_a(0.5F, 0.9F);
      this.func_82236_f(true);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, new Byte((byte)0));
   }

   @Override
   protected float func_70599_aP() {
      return 0.1F;
   }

   @Override
   protected float func_70647_i() {
      return super.func_70647_i() * 0.95F;
   }

   @Override
   protected String func_70639_aQ() {
      return this.func_82235_h() && this.field_70146_Z.nextInt(4) != 0 ? null : "mob.bat.idle";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.bat.hurt";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.bat.death";
   }

   @Override
   public boolean func_70104_M() {
      return false;
   }

   @Override
   protected void func_82167_n(Entity var1) {
   }

   @Override
   protected void func_85033_bc() {
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(6.0);
   }

   public boolean func_82235_h() {
      return (this.field_70180_af.func_75683_a(16) & 1) != 0;
   }

   public void func_82236_f(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)(var2 | 1));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(var2 & -2));
      }
   }

   @Override
   protected boolean func_70650_aV() {
      return true;
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.func_82235_h()) {
         this.field_70159_w = this.field_70181_x = this.field_70179_y = 0.0;
         this.field_70163_u = (double)MathHelper.func_76128_c(this.field_70163_u) + 1.0 - (double)this.field_70131_O;
      } else {
         this.field_70181_x *= 0.6000000238418579;
      }
   }

   @Override
   protected void func_70619_bc() {
      super.func_70619_bc();
      if (this.func_82235_h()) {
         if (!this.field_70170_p
            .func_147439_a(MathHelper.func_76128_c(this.field_70165_t), (int)this.field_70163_u + 1, MathHelper.func_76128_c(this.field_70161_v))
            .func_149721_r()) {
            this.func_82236_f(false);
            this.field_70170_p.func_72889_a(null, 1015, (int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 0);
         } else {
            if (this.field_70146_Z.nextInt(200) == 0) {
               this.field_70759_as = (float)this.field_70146_Z.nextInt(360);
            }

            if (this.field_70170_p.func_72890_a(this, 4.0) != null) {
               this.func_82236_f(false);
               this.field_70170_p.func_72889_a(null, 1015, (int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 0);
            }
         }
      } else {
         if (this.field_82237_a != null
            && (
               !this.field_70170_p.func_147437_c(this.field_82237_a.field_71574_a, this.field_82237_a.field_71572_b, this.field_82237_a.field_71573_c)
                  || this.field_82237_a.field_71572_b < 1
            )) {
            this.field_82237_a = null;
         }

         if (this.field_82237_a == null
            || this.field_70146_Z.nextInt(30) == 0
            || this.field_82237_a.func_71569_e((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v) < 4.0F) {
            this.field_82237_a = new ChunkCoordinates(
               (int)this.field_70165_t + this.field_70146_Z.nextInt(7) - this.field_70146_Z.nextInt(7),
               (int)this.field_70163_u + this.field_70146_Z.nextInt(6) - 2,
               (int)this.field_70161_v + this.field_70146_Z.nextInt(7) - this.field_70146_Z.nextInt(7)
            );
         }

         double var1 = (double)this.field_82237_a.field_71574_a + 0.5 - this.field_70165_t;
         double var3 = (double)this.field_82237_a.field_71572_b + 0.1 - this.field_70163_u;
         double var5 = (double)this.field_82237_a.field_71573_c + 0.5 - this.field_70161_v;
         this.field_70159_w += (Math.signum(var1) * 0.5 - this.field_70159_w) * 0.10000000149011612;
         this.field_70181_x += (Math.signum(var3) * 0.699999988079071 - this.field_70181_x) * 0.10000000149011612;
         this.field_70179_y += (Math.signum(var5) * 0.5 - this.field_70179_y) * 0.10000000149011612;
         float var7 = (float)(Math.atan2(this.field_70179_y, this.field_70159_w) * 180.0 / 3.1415927410125732) - 90.0F;
         float var8 = MathHelper.func_76142_g(var7 - this.field_70177_z);
         this.field_70701_bs = 0.5F;
         this.field_70177_z += var8;
         if (this.field_70146_Z.nextInt(100) == 0
            && this.field_70170_p
               .func_147439_a(MathHelper.func_76128_c(this.field_70165_t), (int)this.field_70163_u + 1, MathHelper.func_76128_c(this.field_70161_v))
               .func_149721_r()) {
            this.func_82236_f(true);
         }
      }
   }

   @Override
   protected boolean func_70041_e_() {
      return false;
   }

   @Override
   protected void func_70069_a(float var1) {
   }

   @Override
   protected void func_70064_a(double var1, boolean var3) {
   }

   @Override
   public boolean func_145773_az() {
      return true;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else {
         if (!this.field_70170_p.field_72995_K && this.func_82235_h()) {
            this.func_82236_f(false);
         }

         return super.func_70097_a(var1, var2);
      }
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70180_af.func_75692_b(16, var1.func_74771_c("BatFlags"));
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74774_a("BatFlags", this.field_70180_af.func_75683_a(16));
   }

   @Override
   public boolean func_70601_bi() {
      int var1 = MathHelper.func_76128_c(this.field_70121_D.field_72338_b);
      if (var1 >= 63) {
         return false;
      } else {
         int var2 = MathHelper.func_76128_c(this.field_70165_t);
         int var3 = MathHelper.func_76128_c(this.field_70161_v);
         int var4 = this.field_70170_p.func_72957_l(var2, var1, var3);
         byte var5 = 4;
         Calendar var6 = this.field_70170_p.func_83015_S();
         if ((var6.get(2) + 1 != 10 || var6.get(5) < 20) && (var6.get(2) + 1 != 11 || var6.get(5) > 3)) {
            if (this.field_70146_Z.nextBoolean()) {
               return false;
            }
         } else {
            var5 = 7;
         }

         return var4 > this.field_70146_Z.nextInt(var5) ? false : super.func_70601_bi();
      }
   }
}
