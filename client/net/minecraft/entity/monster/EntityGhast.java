package net.minecraft.entity.monster;

import java.util.Random;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGhast extends EntityFlying implements IMob {
   private int field_92014_j = 1;

   public EntityGhast(World var1) {
      super(var1);
      this.func_70105_a(4.0F, 4.0F);
      this.field_70178_ae = true;
      this.field_70728_aV = 5;
      this.field_70765_h = new EntityGhast.GhastMoveHelper(this);
      this.field_70714_bg.func_75776_a(5, new EntityGhast.AIRandomFly(this));
      this.field_70714_bg.func_75776_a(7, new EntityGhast.AILookAround(this));
      this.field_70714_bg.func_75776_a(7, new EntityGhast.AIFireballAttack(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIFindEntityNearestPlayer(this));
   }

   public boolean func_110182_bF() {
      return this.field_70180_af.func_75683_a(16) != 0;
   }

   public void func_175454_a(boolean var1) {
      this.field_70180_af.func_75692_b(16, Byte.valueOf((byte)(var1 ? 1 : 0)));
   }

   public int func_175453_cd() {
      return this.field_92014_j;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
         this.func_70106_y();
      }

   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if ("fireball".equals(var1.func_76355_l()) && var1.func_76346_g() instanceof EntityPlayer) {
         super.func_70097_a(var1, 1000.0F);
         ((EntityPlayer)var1.func_76346_g()).func_71029_a(AchievementList.field_76028_y);
         return true;
      } else {
         return super.func_70097_a(var1, var2);
      }
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, (byte)0);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(10.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(100.0D);
   }

   protected String func_70639_aQ() {
      return "mob.ghast.moan";
   }

   protected String func_70621_aR() {
      return "mob.ghast.scream";
   }

   protected String func_70673_aS() {
      return "mob.ghast.death";
   }

   protected Item func_146068_u() {
      return Items.field_151016_H;
   }

   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(2) + this.field_70146_Z.nextInt(1 + var2);

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         this.func_145779_a(Items.field_151073_bk, 1);
      }

      var3 = this.field_70146_Z.nextInt(3) + this.field_70146_Z.nextInt(1 + var2);

      for(var4 = 0; var4 < var3; ++var4) {
         this.func_145779_a(Items.field_151016_H, 1);
      }

   }

   protected float func_70599_aP() {
      return 10.0F;
   }

   public boolean func_70601_bi() {
      return this.field_70146_Z.nextInt(20) == 0 && super.func_70601_bi() && this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL;
   }

   public int func_70641_bl() {
      return 1;
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("ExplosionPower", this.field_92014_j);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("ExplosionPower", 99)) {
         this.field_92014_j = var1.func_74762_e("ExplosionPower");
      }

   }

   public float func_70047_e() {
      return 2.6F;
   }

   static class AIFireballAttack extends EntityAIBase {
      private EntityGhast field_179470_b;
      public int field_179471_a;

      public AIFireballAttack(EntityGhast var1) {
         super();
         this.field_179470_b = var1;
      }

      public boolean func_75250_a() {
         return this.field_179470_b.func_70638_az() != null;
      }

      public void func_75249_e() {
         this.field_179471_a = 0;
      }

      public void func_75251_c() {
         this.field_179470_b.func_175454_a(false);
      }

      public void func_75246_d() {
         EntityLivingBase var1 = this.field_179470_b.func_70638_az();
         double var2 = 64.0D;
         if (var1.func_70068_e(this.field_179470_b) < var2 * var2 && this.field_179470_b.func_70685_l(var1)) {
            World var4 = this.field_179470_b.field_70170_p;
            ++this.field_179471_a;
            if (this.field_179471_a == 10) {
               var4.func_180498_a((EntityPlayer)null, 1007, new BlockPos(this.field_179470_b), 0);
            }

            if (this.field_179471_a == 20) {
               double var5 = 4.0D;
               Vec3 var7 = this.field_179470_b.func_70676_i(1.0F);
               double var8 = var1.field_70165_t - (this.field_179470_b.field_70165_t + var7.field_72450_a * var5);
               double var10 = var1.func_174813_aQ().field_72338_b + (double)(var1.field_70131_O / 2.0F) - (0.5D + this.field_179470_b.field_70163_u + (double)(this.field_179470_b.field_70131_O / 2.0F));
               double var12 = var1.field_70161_v - (this.field_179470_b.field_70161_v + var7.field_72449_c * var5);
               var4.func_180498_a((EntityPlayer)null, 1008, new BlockPos(this.field_179470_b), 0);
               EntityLargeFireball var14 = new EntityLargeFireball(var4, this.field_179470_b, var8, var10, var12);
               var14.field_92057_e = this.field_179470_b.func_175453_cd();
               var14.field_70165_t = this.field_179470_b.field_70165_t + var7.field_72450_a * var5;
               var14.field_70163_u = this.field_179470_b.field_70163_u + (double)(this.field_179470_b.field_70131_O / 2.0F) + 0.5D;
               var14.field_70161_v = this.field_179470_b.field_70161_v + var7.field_72449_c * var5;
               var4.func_72838_d(var14);
               this.field_179471_a = -40;
            }
         } else if (this.field_179471_a > 0) {
            --this.field_179471_a;
         }

         this.field_179470_b.func_175454_a(this.field_179471_a > 10);
      }
   }

   static class AILookAround extends EntityAIBase {
      private EntityGhast field_179472_a;

      public AILookAround(EntityGhast var1) {
         super();
         this.field_179472_a = var1;
         this.func_75248_a(2);
      }

      public boolean func_75250_a() {
         return true;
      }

      public void func_75246_d() {
         if (this.field_179472_a.func_70638_az() == null) {
            this.field_179472_a.field_70761_aq = this.field_179472_a.field_70177_z = -((float)MathHelper.func_181159_b(this.field_179472_a.field_70159_w, this.field_179472_a.field_70179_y)) * 180.0F / 3.1415927F;
         } else {
            EntityLivingBase var1 = this.field_179472_a.func_70638_az();
            double var2 = 64.0D;
            if (var1.func_70068_e(this.field_179472_a) < var2 * var2) {
               double var4 = var1.field_70165_t - this.field_179472_a.field_70165_t;
               double var6 = var1.field_70161_v - this.field_179472_a.field_70161_v;
               this.field_179472_a.field_70761_aq = this.field_179472_a.field_70177_z = -((float)MathHelper.func_181159_b(var4, var6)) * 180.0F / 3.1415927F;
            }
         }

      }
   }

   static class AIRandomFly extends EntityAIBase {
      private EntityGhast field_179454_a;

      public AIRandomFly(EntityGhast var1) {
         super();
         this.field_179454_a = var1;
         this.func_75248_a(1);
      }

      public boolean func_75250_a() {
         EntityMoveHelper var1 = this.field_179454_a.func_70605_aq();
         if (!var1.func_75640_a()) {
            return true;
         } else {
            double var2 = var1.func_179917_d() - this.field_179454_a.field_70165_t;
            double var4 = var1.func_179919_e() - this.field_179454_a.field_70163_u;
            double var6 = var1.func_179918_f() - this.field_179454_a.field_70161_v;
            double var8 = var2 * var2 + var4 * var4 + var6 * var6;
            return var8 < 1.0D || var8 > 3600.0D;
         }
      }

      public boolean func_75253_b() {
         return false;
      }

      public void func_75249_e() {
         Random var1 = this.field_179454_a.func_70681_au();
         double var2 = this.field_179454_a.field_70165_t + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var4 = this.field_179454_a.field_70163_u + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var6 = this.field_179454_a.field_70161_v + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.field_179454_a.func_70605_aq().func_75642_a(var2, var4, var6, 1.0D);
      }
   }

   static class GhastMoveHelper extends EntityMoveHelper {
      private EntityGhast field_179927_g;
      private int field_179928_h;

      public GhastMoveHelper(EntityGhast var1) {
         super(var1);
         this.field_179927_g = var1;
      }

      public void func_75641_c() {
         if (this.field_75643_f) {
            double var1 = this.field_75646_b - this.field_179927_g.field_70165_t;
            double var3 = this.field_75647_c - this.field_179927_g.field_70163_u;
            double var5 = this.field_75644_d - this.field_179927_g.field_70161_v;
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            if (this.field_179928_h-- <= 0) {
               this.field_179928_h += this.field_179927_g.func_70681_au().nextInt(5) + 2;
               var7 = (double)MathHelper.func_76133_a(var7);
               if (this.func_179926_b(this.field_75646_b, this.field_75647_c, this.field_75644_d, var7)) {
                  EntityGhast var10000 = this.field_179927_g;
                  var10000.field_70159_w += var1 / var7 * 0.1D;
                  var10000 = this.field_179927_g;
                  var10000.field_70181_x += var3 / var7 * 0.1D;
                  var10000 = this.field_179927_g;
                  var10000.field_70179_y += var5 / var7 * 0.1D;
               } else {
                  this.field_75643_f = false;
               }
            }

         }
      }

      private boolean func_179926_b(double var1, double var3, double var5, double var7) {
         double var9 = (var1 - this.field_179927_g.field_70165_t) / var7;
         double var11 = (var3 - this.field_179927_g.field_70163_u) / var7;
         double var13 = (var5 - this.field_179927_g.field_70161_v) / var7;
         AxisAlignedBB var15 = this.field_179927_g.func_174813_aQ();

         for(int var16 = 1; (double)var16 < var7; ++var16) {
            var15 = var15.func_72317_d(var9, var11, var13);
            if (!this.field_179927_g.field_70170_p.func_72945_a(this.field_179927_g, var15).isEmpty()) {
               return false;
            }
         }

         return true;
      }
   }
}
