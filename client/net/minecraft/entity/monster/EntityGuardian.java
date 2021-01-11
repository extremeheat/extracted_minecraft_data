package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityGuardian extends EntityMob {
   private float field_175482_b;
   private float field_175484_c;
   private float field_175483_bk;
   private float field_175485_bl;
   private float field_175486_bm;
   private EntityLivingBase field_175478_bn;
   private int field_175479_bo;
   private boolean field_175480_bp;
   private EntityAIWander field_175481_bq;

   public EntityGuardian(World var1) {
      super(var1);
      this.field_70728_aV = 10;
      this.func_70105_a(0.85F, 0.85F);
      this.field_70714_bg.func_75776_a(4, new EntityGuardian.AIGuardianAttack(this));
      EntityAIMoveTowardsRestriction var2;
      this.field_70714_bg.func_75776_a(5, var2 = new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.field_70714_bg.func_75776_a(7, this.field_175481_bq = new EntityAIWander(this, 1.0D, 80));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityGuardian.class, 12.0F, 0.01F));
      this.field_70714_bg.func_75776_a(9, new EntityAILookIdle(this));
      this.field_175481_bq.func_75248_a(3);
      var2.func_75248_a(3);
      this.field_70715_bh.func_75776_a(1, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 10, true, false, new EntityGuardian.GuardianTargetSelector(this)));
      this.field_70765_h = new EntityGuardian.GuardianMoveHelper(this);
      this.field_175484_c = this.field_175482_b = this.field_70146_Z.nextFloat();
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(6.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(16.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(30.0D);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_175467_a(var1.func_74767_n("Elder"));
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("Elder", this.func_175461_cl());
   }

   protected PathNavigate func_175447_b(World var1) {
      return new PathNavigateSwimmer(this, var1);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, 0);
      this.field_70180_af.func_75682_a(17, 0);
   }

   private boolean func_175468_a(int var1) {
      return (this.field_70180_af.func_75679_c(16) & var1) != 0;
   }

   private void func_175473_a(int var1, boolean var2) {
      int var3 = this.field_70180_af.func_75679_c(16);
      if (var2) {
         this.field_70180_af.func_75692_b(16, var3 | var1);
      } else {
         this.field_70180_af.func_75692_b(16, var3 & ~var1);
      }

   }

   public boolean func_175472_n() {
      return this.func_175468_a(2);
   }

   private void func_175476_l(boolean var1) {
      this.func_175473_a(2, var1);
   }

   public int func_175464_ck() {
      return this.func_175461_cl() ? 60 : 80;
   }

   public boolean func_175461_cl() {
      return this.func_175468_a(4);
   }

   public void func_175467_a(boolean var1) {
      this.func_175473_a(4, var1);
      if (var1) {
         this.func_70105_a(1.9975F, 1.9975F);
         this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
         this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(8.0D);
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(80.0D);
         this.func_110163_bv();
         this.field_175481_bq.func_179479_b(400);
      }

   }

   public void func_175465_cm() {
      this.func_175467_a(true);
      this.field_175486_bm = this.field_175485_bl = 1.0F;
   }

   private void func_175463_b(int var1) {
      this.field_70180_af.func_75692_b(17, var1);
   }

   public boolean func_175474_cn() {
      return this.field_70180_af.func_75679_c(17) != 0;
   }

   public EntityLivingBase func_175466_co() {
      if (!this.func_175474_cn()) {
         return null;
      } else if (this.field_70170_p.field_72995_K) {
         if (this.field_175478_bn != null) {
            return this.field_175478_bn;
         } else {
            Entity var1 = this.field_70170_p.func_73045_a(this.field_70180_af.func_75679_c(17));
            if (var1 instanceof EntityLivingBase) {
               this.field_175478_bn = (EntityLivingBase)var1;
               return this.field_175478_bn;
            } else {
               return null;
            }
         }
      } else {
         return this.func_70638_az();
      }
   }

   public void func_145781_i(int var1) {
      super.func_145781_i(var1);
      if (var1 == 16) {
         if (this.func_175461_cl() && this.field_70130_N < 1.0F) {
            this.func_70105_a(1.9975F, 1.9975F);
         }
      } else if (var1 == 17) {
         this.field_175479_bo = 0;
         this.field_175478_bn = null;
      }

   }

   public int func_70627_aG() {
      return 160;
   }

   protected String func_70639_aQ() {
      if (!this.func_70090_H()) {
         return "mob.guardian.land.idle";
      } else {
         return this.func_175461_cl() ? "mob.guardian.elder.idle" : "mob.guardian.idle";
      }
   }

   protected String func_70621_aR() {
      if (!this.func_70090_H()) {
         return "mob.guardian.land.hit";
      } else {
         return this.func_175461_cl() ? "mob.guardian.elder.hit" : "mob.guardian.hit";
      }
   }

   protected String func_70673_aS() {
      if (!this.func_70090_H()) {
         return "mob.guardian.land.death";
      } else {
         return this.func_175461_cl() ? "mob.guardian.elder.death" : "mob.guardian.death";
      }
   }

   protected boolean func_70041_e_() {
      return false;
   }

   public float func_70047_e() {
      return this.field_70131_O * 0.5F;
   }

   public float func_180484_a(BlockPos var1) {
      return this.field_70170_p.func_180495_p(var1).func_177230_c().func_149688_o() == Material.field_151586_h ? 10.0F + this.field_70170_p.func_175724_o(var1) - 0.5F : super.func_180484_a(var1);
   }

   public void func_70636_d() {
      if (this.field_70170_p.field_72995_K) {
         this.field_175484_c = this.field_175482_b;
         if (!this.func_70090_H()) {
            this.field_175483_bk = 2.0F;
            if (this.field_70181_x > 0.0D && this.field_175480_bp && !this.func_174814_R()) {
               this.field_70170_p.func_72980_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, "mob.guardian.flop", 1.0F, 1.0F, false);
            }

            this.field_175480_bp = this.field_70181_x < 0.0D && this.field_70170_p.func_175677_d((new BlockPos(this)).func_177977_b(), false);
         } else if (this.func_175472_n()) {
            if (this.field_175483_bk < 0.5F) {
               this.field_175483_bk = 4.0F;
            } else {
               this.field_175483_bk += (0.5F - this.field_175483_bk) * 0.1F;
            }
         } else {
            this.field_175483_bk += (0.125F - this.field_175483_bk) * 0.2F;
         }

         this.field_175482_b += this.field_175483_bk;
         this.field_175486_bm = this.field_175485_bl;
         if (!this.func_70090_H()) {
            this.field_175485_bl = this.field_70146_Z.nextFloat();
         } else if (this.func_175472_n()) {
            this.field_175485_bl += (0.0F - this.field_175485_bl) * 0.25F;
         } else {
            this.field_175485_bl += (1.0F - this.field_175485_bl) * 0.06F;
         }

         if (this.func_175472_n() && this.func_70090_H()) {
            Vec3 var1 = this.func_70676_i(0.0F);

            for(int var2 = 0; var2 < 2; ++var2) {
               this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N - var1.field_72450_a * 1.5D, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O - var1.field_72448_b * 1.5D, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N - var1.field_72449_c * 1.5D, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_175474_cn()) {
            if (this.field_175479_bo < this.func_175464_ck()) {
               ++this.field_175479_bo;
            }

            EntityLivingBase var14 = this.func_175466_co();
            if (var14 != null) {
               this.func_70671_ap().func_75651_a(var14, 90.0F, 90.0F);
               this.func_70671_ap().func_75649_a();
               double var15 = (double)this.func_175477_p(0.0F);
               double var4 = var14.field_70165_t - this.field_70165_t;
               double var6 = var14.field_70163_u + (double)(var14.field_70131_O * 0.5F) - (this.field_70163_u + (double)this.func_70047_e());
               double var8 = var14.field_70161_v - this.field_70161_v;
               double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
               var4 /= var10;
               var6 /= var10;
               var8 /= var10;
               double var12 = this.field_70146_Z.nextDouble();

               while(var12 < var10) {
                  var12 += 1.8D - var15 + this.field_70146_Z.nextDouble() * (1.7D - var15);
                  this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_BUBBLE, this.field_70165_t + var4 * var12, this.field_70163_u + var6 * var12 + (double)this.func_70047_e(), this.field_70161_v + var8 * var12, 0.0D, 0.0D, 0.0D);
               }
            }
         }
      }

      if (this.field_70171_ac) {
         this.func_70050_g(300);
      } else if (this.field_70122_E) {
         this.field_70181_x += 0.5D;
         this.field_70159_w += (double)((this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * 0.4F);
         this.field_70179_y += (double)((this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * 0.4F);
         this.field_70177_z = this.field_70146_Z.nextFloat() * 360.0F;
         this.field_70122_E = false;
         this.field_70160_al = true;
      }

      if (this.func_175474_cn()) {
         this.field_70177_z = this.field_70759_as;
      }

      super.func_70636_d();
   }

   public float func_175471_a(float var1) {
      return this.field_175484_c + (this.field_175482_b - this.field_175484_c) * var1;
   }

   public float func_175469_o(float var1) {
      return this.field_175486_bm + (this.field_175485_bl - this.field_175486_bm) * var1;
   }

   public float func_175477_p(float var1) {
      return ((float)this.field_175479_bo + var1) / (float)this.func_175464_ck();
   }

   protected void func_70619_bc() {
      super.func_70619_bc();
      if (this.func_175461_cl()) {
         boolean var1 = true;
         boolean var2 = true;
         boolean var3 = true;
         boolean var4 = true;
         if ((this.field_70173_aa + this.func_145782_y()) % 1200 == 0) {
            Potion var5 = Potion.field_76419_f;
            List var6 = this.field_70170_p.func_175661_b(EntityPlayerMP.class, new Predicate<EntityPlayerMP>() {
               public boolean apply(EntityPlayerMP var1) {
                  return EntityGuardian.this.func_70068_e(var1) < 2500.0D && var1.field_71134_c.func_180239_c();
               }

               // $FF: synthetic method
               public boolean apply(Object var1) {
                  return this.apply((EntityPlayerMP)var1);
               }
            });
            Iterator var7 = var6.iterator();

            label30:
            while(true) {
               EntityPlayerMP var8;
               do {
                  if (!var7.hasNext()) {
                     break label30;
                  }

                  var8 = (EntityPlayerMP)var7.next();
               } while(var8.func_70644_a(var5) && var8.func_70660_b(var5).func_76458_c() >= 2 && var8.func_70660_b(var5).func_76459_b() >= 1200);

               var8.field_71135_a.func_147359_a(new S2BPacketChangeGameState(10, 0.0F));
               var8.func_70690_d(new PotionEffect(var5.field_76415_H, 6000, 2));
            }
         }

         if (!this.func_110175_bO()) {
            this.func_175449_a(new BlockPos(this), 16);
         }
      }

   }

   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(3) + this.field_70146_Z.nextInt(var2 + 1);
      if (var3 > 0) {
         this.func_70099_a(new ItemStack(Items.field_179562_cC, var3, 0), 1.0F);
      }

      if (this.field_70146_Z.nextInt(3 + var2) > 1) {
         this.func_70099_a(new ItemStack(Items.field_151115_aP, 1, ItemFishFood.FishType.COD.func_150976_a()), 1.0F);
      } else if (this.field_70146_Z.nextInt(3 + var2) > 1) {
         this.func_70099_a(new ItemStack(Items.field_179563_cD, 1, 0), 1.0F);
      }

      if (var1 && this.func_175461_cl()) {
         this.func_70099_a(new ItemStack(Blocks.field_150360_v, 1, 1), 1.0F);
      }

   }

   protected void func_82164_bB() {
      ItemStack var1 = ((WeightedRandomFishable)WeightedRandom.func_76271_a(this.field_70146_Z, EntityFishHook.func_174855_j())).func_150708_a(this.field_70146_Z);
      this.func_70099_a(var1, 1.0F);
   }

   protected boolean func_70814_o() {
      return true;
   }

   public boolean func_70058_J() {
      return this.field_70170_p.func_72917_a(this.func_174813_aQ(), this) && this.field_70170_p.func_72945_a(this, this.func_174813_aQ()).isEmpty();
   }

   public boolean func_70601_bi() {
      return (this.field_70146_Z.nextInt(20) == 0 || !this.field_70170_p.func_175710_j(new BlockPos(this))) && super.func_70601_bi();
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (!this.func_175472_n() && !var1.func_82725_o() && var1.func_76364_f() instanceof EntityLivingBase) {
         EntityLivingBase var3 = (EntityLivingBase)var1.func_76364_f();
         if (!var1.func_94541_c()) {
            var3.func_70097_a(DamageSource.func_92087_a(this), 2.0F);
            var3.func_85030_a("damage.thorns", 0.5F, 1.0F);
         }
      }

      this.field_175481_bq.func_179480_f();
      return super.func_70097_a(var1, var2);
   }

   public int func_70646_bf() {
      return 180;
   }

   public void func_70612_e(float var1, float var2) {
      if (this.func_70613_aW()) {
         if (this.func_70090_H()) {
            this.func_70060_a(var1, var2, 0.1F);
            this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
            this.field_70159_w *= 0.8999999761581421D;
            this.field_70181_x *= 0.8999999761581421D;
            this.field_70179_y *= 0.8999999761581421D;
            if (!this.func_175472_n() && this.func_70638_az() == null) {
               this.field_70181_x -= 0.005D;
            }
         } else {
            super.func_70612_e(var1, var2);
         }
      } else {
         super.func_70612_e(var1, var2);
      }

   }

   static class GuardianMoveHelper extends EntityMoveHelper {
      private EntityGuardian field_179930_g;

      public GuardianMoveHelper(EntityGuardian var1) {
         super(var1);
         this.field_179930_g = var1;
      }

      public void func_75641_c() {
         if (this.field_75643_f && !this.field_179930_g.func_70661_as().func_75500_f()) {
            double var1 = this.field_75646_b - this.field_179930_g.field_70165_t;
            double var3 = this.field_75647_c - this.field_179930_g.field_70163_u;
            double var5 = this.field_75644_d - this.field_179930_g.field_70161_v;
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            var7 = (double)MathHelper.func_76133_a(var7);
            var3 /= var7;
            float var9 = (float)(MathHelper.func_181159_b(var5, var1) * 180.0D / 3.1415927410125732D) - 90.0F;
            this.field_179930_g.field_70177_z = this.func_75639_a(this.field_179930_g.field_70177_z, var9, 30.0F);
            this.field_179930_g.field_70761_aq = this.field_179930_g.field_70177_z;
            float var10 = (float)(this.field_75645_e * this.field_179930_g.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e());
            this.field_179930_g.func_70659_e(this.field_179930_g.func_70689_ay() + (var10 - this.field_179930_g.func_70689_ay()) * 0.125F);
            double var11 = Math.sin((double)(this.field_179930_g.field_70173_aa + this.field_179930_g.func_145782_y()) * 0.5D) * 0.05D;
            double var13 = Math.cos((double)(this.field_179930_g.field_70177_z * 3.1415927F / 180.0F));
            double var15 = Math.sin((double)(this.field_179930_g.field_70177_z * 3.1415927F / 180.0F));
            EntityGuardian var10000 = this.field_179930_g;
            var10000.field_70159_w += var11 * var13;
            var10000 = this.field_179930_g;
            var10000.field_70179_y += var11 * var15;
            var11 = Math.sin((double)(this.field_179930_g.field_70173_aa + this.field_179930_g.func_145782_y()) * 0.75D) * 0.05D;
            var10000 = this.field_179930_g;
            var10000.field_70181_x += var11 * (var15 + var13) * 0.25D;
            var10000 = this.field_179930_g;
            var10000.field_70181_x += (double)this.field_179930_g.func_70689_ay() * var3 * 0.1D;
            EntityLookHelper var17 = this.field_179930_g.func_70671_ap();
            double var18 = this.field_179930_g.field_70165_t + var1 / var7 * 2.0D;
            double var20 = (double)this.field_179930_g.func_70047_e() + this.field_179930_g.field_70163_u + var3 / var7 * 1.0D;
            double var22 = this.field_179930_g.field_70161_v + var5 / var7 * 2.0D;
            double var24 = var17.func_180423_e();
            double var26 = var17.func_180422_f();
            double var28 = var17.func_180421_g();
            if (!var17.func_180424_b()) {
               var24 = var18;
               var26 = var20;
               var28 = var22;
            }

            this.field_179930_g.func_70671_ap().func_75650_a(var24 + (var18 - var24) * 0.125D, var26 + (var20 - var26) * 0.125D, var28 + (var22 - var28) * 0.125D, 10.0F, 40.0F);
            this.field_179930_g.func_175476_l(true);
         } else {
            this.field_179930_g.func_70659_e(0.0F);
            this.field_179930_g.func_175476_l(false);
         }
      }
   }

   static class AIGuardianAttack extends EntityAIBase {
      private EntityGuardian field_179456_a;
      private int field_179455_b;

      public AIGuardianAttack(EntityGuardian var1) {
         super();
         this.field_179456_a = var1;
         this.func_75248_a(3);
      }

      public boolean func_75250_a() {
         EntityLivingBase var1 = this.field_179456_a.func_70638_az();
         return var1 != null && var1.func_70089_S();
      }

      public boolean func_75253_b() {
         return super.func_75253_b() && (this.field_179456_a.func_175461_cl() || this.field_179456_a.func_70068_e(this.field_179456_a.func_70638_az()) > 9.0D);
      }

      public void func_75249_e() {
         this.field_179455_b = -10;
         this.field_179456_a.func_70661_as().func_75499_g();
         this.field_179456_a.func_70671_ap().func_75651_a(this.field_179456_a.func_70638_az(), 90.0F, 90.0F);
         this.field_179456_a.field_70160_al = true;
      }

      public void func_75251_c() {
         this.field_179456_a.func_175463_b(0);
         this.field_179456_a.func_70624_b((EntityLivingBase)null);
         this.field_179456_a.field_175481_bq.func_179480_f();
      }

      public void func_75246_d() {
         EntityLivingBase var1 = this.field_179456_a.func_70638_az();
         this.field_179456_a.func_70661_as().func_75499_g();
         this.field_179456_a.func_70671_ap().func_75651_a(var1, 90.0F, 90.0F);
         if (!this.field_179456_a.func_70685_l(var1)) {
            this.field_179456_a.func_70624_b((EntityLivingBase)null);
         } else {
            ++this.field_179455_b;
            if (this.field_179455_b == 0) {
               this.field_179456_a.func_175463_b(this.field_179456_a.func_70638_az().func_145782_y());
               this.field_179456_a.field_70170_p.func_72960_a(this.field_179456_a, (byte)21);
            } else if (this.field_179455_b >= this.field_179456_a.func_175464_ck()) {
               float var2 = 1.0F;
               if (this.field_179456_a.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) {
                  var2 += 2.0F;
               }

               if (this.field_179456_a.func_175461_cl()) {
                  var2 += 2.0F;
               }

               var1.func_70097_a(DamageSource.func_76354_b(this.field_179456_a, this.field_179456_a), var2);
               var1.func_70097_a(DamageSource.func_76358_a(this.field_179456_a), (float)this.field_179456_a.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e());
               this.field_179456_a.func_70624_b((EntityLivingBase)null);
            } else if (this.field_179455_b >= 60 && this.field_179455_b % 20 == 0) {
            }

            super.func_75246_d();
         }
      }
   }

   static class GuardianTargetSelector implements Predicate<EntityLivingBase> {
      private EntityGuardian field_179916_a;

      public GuardianTargetSelector(EntityGuardian var1) {
         super();
         this.field_179916_a = var1;
      }

      public boolean apply(EntityLivingBase var1) {
         return (var1 instanceof EntityPlayer || var1 instanceof EntitySquid) && var1.func_70068_e(this.field_179916_a) > 9.0D;
      }

      // $FF: synthetic method
      public boolean apply(Object var1) {
         return this.apply((EntityLivingBase)var1);
      }
   }
}
