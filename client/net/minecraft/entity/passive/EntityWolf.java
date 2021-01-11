package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWolf extends EntityTameable {
   private float field_70926_e;
   private float field_70924_f;
   private boolean field_70925_g;
   private boolean field_70928_h;
   private float field_70929_i;
   private float field_70927_j;

   public EntityWolf(World var1) {
      super(var1);
      this.func_70105_a(0.6F, 0.8F);
      ((PathNavigateGround)this.func_70661_as()).func_179690_a(true);
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, this.field_70911_d);
      this.field_70714_bg.func_75776_a(3, new EntityAILeapAtTarget(this, 0.4F));
      this.field_70714_bg.func_75776_a(4, new EntityAIAttackOnCollide(this, 1.0D, true));
      this.field_70714_bg.func_75776_a(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
      this.field_70714_bg.func_75776_a(6, new EntityAIMate(this, 1.0D));
      this.field_70714_bg.func_75776_a(7, new EntityAIWander(this, 1.0D));
      this.field_70714_bg.func_75776_a(8, new EntityAIBeg(this, 8.0F));
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(9, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIOwnerHurtByTarget(this));
      this.field_70715_bh.func_75776_a(2, new EntityAIOwnerHurtTarget(this));
      this.field_70715_bh.func_75776_a(3, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.field_70715_bh.func_75776_a(4, new EntityAITargetNonTamed(this, EntityAnimal.class, false, new Predicate<Entity>() {
         public boolean apply(Entity var1) {
            return var1 instanceof EntitySheep || var1 instanceof EntityRabbit;
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((Entity)var1);
         }
      }));
      this.field_70715_bh.func_75776_a(5, new EntityAINearestAttackableTarget(this, EntitySkeleton.class, false));
      this.func_70903_f(false);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
      if (this.func_70909_n()) {
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0D);
      } else {
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0D);
      }

      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(2.0D);
   }

   public void func_70624_b(EntityLivingBase var1) {
      super.func_70624_b(var1);
      if (var1 == null) {
         this.func_70916_h(false);
      } else if (!this.func_70909_n()) {
         this.func_70916_h(true);
      }

   }

   protected void func_70619_bc() {
      this.field_70180_af.func_75692_b(18, this.func_110143_aJ());
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(18, new Float(this.func_110143_aJ()));
      this.field_70180_af.func_75682_a(19, new Byte((byte)0));
      this.field_70180_af.func_75682_a(20, new Byte((byte)EnumDyeColor.RED.func_176765_a()));
   }

   protected void func_180429_a(BlockPos var1, Block var2) {
      this.func_85030_a("mob.wolf.step", 0.15F, 1.0F);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("Angry", this.func_70919_bu());
      var1.func_74774_a("CollarColor", (byte)this.func_175546_cu().func_176767_b());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70916_h(var1.func_74767_n("Angry"));
      if (var1.func_150297_b("CollarColor", 99)) {
         this.func_175547_a(EnumDyeColor.func_176766_a(var1.func_74771_c("CollarColor")));
      }

   }

   protected String func_70639_aQ() {
      if (this.func_70919_bu()) {
         return "mob.wolf.growl";
      } else if (this.field_70146_Z.nextInt(3) == 0) {
         return this.func_70909_n() && this.field_70180_af.func_111145_d(18) < 10.0F ? "mob.wolf.whine" : "mob.wolf.panting";
      } else {
         return "mob.wolf.bark";
      }
   }

   protected String func_70621_aR() {
      return "mob.wolf.hurt";
   }

   protected String func_70673_aS() {
      return "mob.wolf.death";
   }

   protected float func_70599_aP() {
      return 0.4F;
   }

   protected Item func_146068_u() {
      return Item.func_150899_d(-1);
   }

   public void func_70636_d() {
      super.func_70636_d();
      if (!this.field_70170_p.field_72995_K && this.field_70925_g && !this.field_70928_h && !this.func_70781_l() && this.field_70122_E) {
         this.field_70928_h = true;
         this.field_70929_i = 0.0F;
         this.field_70927_j = 0.0F;
         this.field_70170_p.func_72960_a(this, (byte)8);
      }

      if (!this.field_70170_p.field_72995_K && this.func_70638_az() == null && this.func_70919_bu()) {
         this.func_70916_h(false);
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      this.field_70924_f = this.field_70926_e;
      if (this.func_70922_bv()) {
         this.field_70926_e += (1.0F - this.field_70926_e) * 0.4F;
      } else {
         this.field_70926_e += (0.0F - this.field_70926_e) * 0.4F;
      }

      if (this.func_70026_G()) {
         this.field_70925_g = true;
         this.field_70928_h = false;
         this.field_70929_i = 0.0F;
         this.field_70927_j = 0.0F;
      } else if ((this.field_70925_g || this.field_70928_h) && this.field_70928_h) {
         if (this.field_70929_i == 0.0F) {
            this.func_85030_a("mob.wolf.shake", this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         }

         this.field_70927_j = this.field_70929_i;
         this.field_70929_i += 0.05F;
         if (this.field_70927_j >= 2.0F) {
            this.field_70925_g = false;
            this.field_70928_h = false;
            this.field_70927_j = 0.0F;
            this.field_70929_i = 0.0F;
         }

         if (this.field_70929_i > 0.4F) {
            float var1 = (float)this.func_174813_aQ().field_72338_b;
            int var2 = (int)(MathHelper.func_76126_a((this.field_70929_i - 0.4F) * 3.1415927F) * 7.0F);

            for(int var3 = 0; var3 < var2; ++var3) {
               float var4 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N * 0.5F;
               float var5 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N * 0.5F;
               this.field_70170_p.func_175688_a(EnumParticleTypes.WATER_SPLASH, this.field_70165_t + (double)var4, (double)(var1 + 0.8F), this.field_70161_v + (double)var5, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            }
         }
      }

   }

   public boolean func_70921_u() {
      return this.field_70925_g;
   }

   public float func_70915_j(float var1) {
      return 0.75F + (this.field_70927_j + (this.field_70929_i - this.field_70927_j) * var1) / 2.0F * 0.25F;
   }

   public float func_70923_f(float var1, float var2) {
      float var3 = (this.field_70927_j + (this.field_70929_i - this.field_70927_j) * var1 + var2) / 1.8F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      } else if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      return MathHelper.func_76126_a(var3 * 3.1415927F) * MathHelper.func_76126_a(var3 * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
   }

   public float func_70917_k(float var1) {
      return (this.field_70924_f + (this.field_70926_e - this.field_70924_f) * var1) * 0.15F * 3.1415927F;
   }

   public float func_70047_e() {
      return this.field_70131_O * 0.8F;
   }

   public int func_70646_bf() {
      return this.func_70906_o() ? 20 : super.func_70646_bf();
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         Entity var3 = var1.func_76346_g();
         this.field_70911_d.func_75270_a(false);
         if (var3 != null && !(var3 instanceof EntityPlayer) && !(var3 instanceof EntityArrow)) {
            var2 = (var2 + 1.0F) / 2.0F;
         }

         return super.func_70097_a(var1, var2);
      }
   }

   public boolean func_70652_k(Entity var1) {
      boolean var2 = var1.func_70097_a(DamageSource.func_76358_a(this), (float)((int)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e()));
      if (var2) {
         this.func_174815_a(this, var1);
      }

      return var2;
   }

   public void func_70903_f(boolean var1) {
      super.func_70903_f(var1);
      if (var1) {
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(20.0D);
      } else {
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0D);
      }

      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(4.0D);
   }

   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (this.func_70909_n()) {
         if (var2 != null) {
            if (var2.func_77973_b() instanceof ItemFood) {
               ItemFood var3 = (ItemFood)var2.func_77973_b();
               if (var3.func_77845_h() && this.field_70180_af.func_111145_d(18) < 20.0F) {
                  if (!var1.field_71075_bZ.field_75098_d) {
                     --var2.field_77994_a;
                  }

                  this.func_70691_i((float)var3.func_150905_g(var2));
                  if (var2.field_77994_a <= 0) {
                     var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, (ItemStack)null);
                  }

                  return true;
               }
            } else if (var2.func_77973_b() == Items.field_151100_aR) {
               EnumDyeColor var4 = EnumDyeColor.func_176766_a(var2.func_77960_j());
               if (var4 != this.func_175546_cu()) {
                  this.func_175547_a(var4);
                  if (!var1.field_71075_bZ.field_75098_d && --var2.field_77994_a <= 0) {
                     var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, (ItemStack)null);
                  }

                  return true;
               }
            }
         }

         if (this.func_152114_e(var1) && !this.field_70170_p.field_72995_K && !this.func_70877_b(var2)) {
            this.field_70911_d.func_75270_a(!this.func_70906_o());
            this.field_70703_bu = false;
            this.field_70699_by.func_75499_g();
            this.func_70624_b((EntityLivingBase)null);
         }
      } else if (var2 != null && var2.func_77973_b() == Items.field_151103_aS && !this.func_70919_bu()) {
         if (!var1.field_71075_bZ.field_75098_d) {
            --var2.field_77994_a;
         }

         if (var2.field_77994_a <= 0) {
            var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, (ItemStack)null);
         }

         if (!this.field_70170_p.field_72995_K) {
            if (this.field_70146_Z.nextInt(3) == 0) {
               this.func_70903_f(true);
               this.field_70699_by.func_75499_g();
               this.func_70624_b((EntityLivingBase)null);
               this.field_70911_d.func_75270_a(true);
               this.func_70606_j(20.0F);
               this.func_152115_b(var1.func_110124_au().toString());
               this.func_70908_e(true);
               this.field_70170_p.func_72960_a(this, (byte)7);
            } else {
               this.func_70908_e(false);
               this.field_70170_p.func_72960_a(this, (byte)6);
            }
         }

         return true;
      }

      return super.func_70085_c(var1);
   }

   public void func_70103_a(byte var1) {
      if (var1 == 8) {
         this.field_70928_h = true;
         this.field_70929_i = 0.0F;
         this.field_70927_j = 0.0F;
      } else {
         super.func_70103_a(var1);
      }

   }

   public float func_70920_v() {
      if (this.func_70919_bu()) {
         return 1.5393804F;
      } else {
         return this.func_70909_n() ? (0.55F - (20.0F - this.field_70180_af.func_111145_d(18)) * 0.02F) * 3.1415927F : 0.62831855F;
      }
   }

   public boolean func_70877_b(ItemStack var1) {
      if (var1 == null) {
         return false;
      } else {
         return !(var1.func_77973_b() instanceof ItemFood) ? false : ((ItemFood)var1.func_77973_b()).func_77845_h();
      }
   }

   public int func_70641_bl() {
      return 8;
   }

   public boolean func_70919_bu() {
      return (this.field_70180_af.func_75683_a(16) & 2) != 0;
   }

   public void func_70916_h(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)(var2 | 2));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(var2 & -3));
      }

   }

   public EnumDyeColor func_175546_cu() {
      return EnumDyeColor.func_176766_a(this.field_70180_af.func_75683_a(20) & 15);
   }

   public void func_175547_a(EnumDyeColor var1) {
      this.field_70180_af.func_75692_b(20, (byte)(var1.func_176767_b() & 15));
   }

   public EntityWolf func_90011_a(EntityAgeable var1) {
      EntityWolf var2 = new EntityWolf(this.field_70170_p);
      String var3 = this.func_152113_b();
      if (var3 != null && var3.trim().length() > 0) {
         var2.func_152115_b(var3);
         var2.func_70903_f(true);
      }

      return var2;
   }

   public void func_70918_i(boolean var1) {
      if (var1) {
         this.field_70180_af.func_75692_b(19, (byte)1);
      } else {
         this.field_70180_af.func_75692_b(19, (byte)0);
      }

   }

   public boolean func_70878_b(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (!this.func_70909_n()) {
         return false;
      } else if (!(var1 instanceof EntityWolf)) {
         return false;
      } else {
         EntityWolf var2 = (EntityWolf)var1;
         if (!var2.func_70909_n()) {
            return false;
         } else if (var2.func_70906_o()) {
            return false;
         } else {
            return this.func_70880_s() && var2.func_70880_s();
         }
      }
   }

   public boolean func_70922_bv() {
      return this.field_70180_af.func_75683_a(19) == 1;
   }

   protected boolean func_70692_ba() {
      return !this.func_70909_n() && this.field_70173_aa > 2400;
   }

   public boolean func_142018_a(EntityLivingBase var1, EntityLivingBase var2) {
      if (!(var1 instanceof EntityCreeper) && !(var1 instanceof EntityGhast)) {
         if (var1 instanceof EntityWolf) {
            EntityWolf var3 = (EntityWolf)var1;
            if (var3.func_70909_n() && var3.func_70902_q() == var2) {
               return false;
            }
         }

         if (var1 instanceof EntityPlayer && var2 instanceof EntityPlayer && !((EntityPlayer)var2).func_96122_a((EntityPlayer)var1)) {
            return false;
         } else {
            return !(var1 instanceof EntityHorse) || !((EntityHorse)var1).func_110248_bS();
         }
      } else {
         return false;
      }
   }

   public boolean func_110164_bC() {
      return !this.func_70919_bu() && super.func_110164_bC();
   }

   // $FF: synthetic method
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }
}
