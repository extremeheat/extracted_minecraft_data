package net.minecraft.entity.monster;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;

public class EntitySkeleton extends EntityMob implements IRangedAttackMob {
   private EntityAIArrowAttack field_85037_d = new EntityAIArrowAttack(this, 1.0, 20, 60, 15.0F);
   private EntityAIAttackOnCollide field_85038_e = new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.2, false);

   public EntitySkeleton(World var1) {
      super(var1);
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIRestrictSun(this));
      this.field_70714_bg.func_75776_a(3, new EntityAIFleeSun(this, 1.0));
      this.field_70714_bg.func_75776_a(5, new EntityAIWander(this, 1.0));
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(6, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      if (var1 != null && !var1.field_72995_K) {
         this.func_85036_m();
      }
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(13, new Byte((byte)0));
   }

   @Override
   public boolean func_70650_aV() {
      return true;
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.skeleton.say";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.skeleton.hurt";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.skeleton.death";
   }

   @Override
   protected void func_145780_a(int var1, int var2, int var3, Block var4) {
      this.func_85030_a("mob.skeleton.step", 0.15F, 1.0F);
   }

   @Override
   public boolean func_70652_k(Entity var1) {
      if (super.func_70652_k(var1)) {
         if (this.func_82202_m() == 1 && var1 instanceof EntityLivingBase) {
            ((EntityLivingBase)var1).func_70690_d(new PotionEffect(Potion.field_82731_v.field_76415_H, 200));
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public EnumCreatureAttribute func_70668_bt() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   public void func_70636_d() {
      if (this.field_70170_p.func_72935_r() && !this.field_70170_p.field_72995_K) {
         float var1 = this.func_70013_c(1.0F);
         if (var1 > 0.5F
            && this.field_70146_Z.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F
            && this.field_70170_p
               .func_72937_j(
                  MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
               )) {
            boolean var2 = true;
            ItemStack var3 = this.func_71124_b(4);
            if (var3 != null) {
               if (var3.func_77984_f()) {
                  var3.func_77964_b(var3.func_77952_i() + this.field_70146_Z.nextInt(2));
                  if (var3.func_77952_i() >= var3.func_77958_k()) {
                     this.func_70669_a(var3);
                     this.func_70062_b(4, null);
                  }
               }

               var2 = false;
            }

            if (var2) {
               this.func_70015_d(8);
            }
         }
      }

      if (this.field_70170_p.field_72995_K && this.func_82202_m() == 1) {
         this.func_70105_a(0.72F, 2.34F);
      }

      super.func_70636_d();
   }

   @Override
   public void func_70098_U() {
      super.func_70098_U();
      if (this.field_70154_o instanceof EntityCreature) {
         EntityCreature var1 = (EntityCreature)this.field_70154_o;
         this.field_70761_aq = var1.field_70761_aq;
      }
   }

   @Override
   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (var1.func_76364_f() instanceof EntityArrow && var1.func_76346_g() instanceof EntityPlayer) {
         EntityPlayer var2 = (EntityPlayer)var1.func_76346_g();
         double var3 = var2.field_70165_t - this.field_70165_t;
         double var5 = var2.field_70161_v - this.field_70161_v;
         if (var3 * var3 + var5 * var5 >= 2500.0) {
            var2.func_71029_a(AchievementList.field_76020_v);
         }
      }
   }

   @Override
   protected Item func_146068_u() {
      return Items.field_151032_g;
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      if (this.func_82202_m() == 1) {
         int var3 = this.field_70146_Z.nextInt(3 + var2) - 1;

         for(int var4 = 0; var4 < var3; ++var4) {
            this.func_145779_a(Items.field_151044_h, 1);
         }
      } else {
         int var5 = this.field_70146_Z.nextInt(3 + var2);

         for(int var7 = 0; var7 < var5; ++var7) {
            this.func_145779_a(Items.field_151032_g, 1);
         }
      }

      int var6 = this.field_70146_Z.nextInt(3 + var2);

      for(int var8 = 0; var8 < var6; ++var8) {
         this.func_145779_a(Items.field_151103_aS, 1);
      }
   }

   @Override
   protected void func_70600_l(int var1) {
      if (this.func_82202_m() == 1) {
         this.func_70099_a(new ItemStack(Items.field_151144_bL, 1, 1), 0.0F);
      }
   }

   @Override
   protected void func_82164_bB() {
      super.func_82164_bB();
      this.func_70062_b(0, new ItemStack(Items.field_151031_f));
   }

   @Override
   public IEntityLivingData func_110161_a(IEntityLivingData var1) {
      var1 = super.func_110161_a(var1);
      if (this.field_70170_p.field_73011_w instanceof WorldProviderHell && this.func_70681_au().nextInt(5) > 0) {
         this.field_70714_bg.func_75776_a(4, this.field_85038_e);
         this.func_82201_a(1);
         this.func_70062_b(0, new ItemStack(Items.field_151052_q));
         this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(4.0);
      } else {
         this.field_70714_bg.func_75776_a(4, this.field_85037_d);
         this.func_82164_bB();
         this.func_82162_bC();
      }

      this.func_98053_h(this.field_70146_Z.nextFloat() < 0.55F * this.field_70170_p.func_147462_b(this.field_70165_t, this.field_70163_u, this.field_70161_v));
      if (this.func_71124_b(4) == null) {
         Calendar var2 = this.field_70170_p.func_83015_S();
         if (var2.get(2) + 1 == 10 && var2.get(5) == 31 && this.field_70146_Z.nextFloat() < 0.25F) {
            this.func_70062_b(4, new ItemStack(this.field_70146_Z.nextFloat() < 0.1F ? Blocks.field_150428_aP : Blocks.field_150423_aK));
            this.field_82174_bp[4] = 0.0F;
         }
      }

      return var1;
   }

   public void func_85036_m() {
      this.field_70714_bg.func_85156_a(this.field_85038_e);
      this.field_70714_bg.func_85156_a(this.field_85037_d);
      ItemStack var1 = this.func_70694_bm();
      if (var1 != null && var1.func_77973_b() == Items.field_151031_f) {
         this.field_70714_bg.func_75776_a(4, this.field_85037_d);
      } else {
         this.field_70714_bg.func_75776_a(4, this.field_85038_e);
      }
   }

   @Override
   public void func_82196_d(EntityLivingBase var1, float var2) {
      EntityArrow var3 = new EntityArrow(this.field_70170_p, this, var1, 1.6F, (float)(14 - this.field_70170_p.field_73013_u.func_151525_a() * 4));
      int var4 = EnchantmentHelper.func_77506_a(Enchantment.field_77345_t.field_77352_x, this.func_70694_bm());
      int var5 = EnchantmentHelper.func_77506_a(Enchantment.field_77344_u.field_77352_x, this.func_70694_bm());
      var3.func_70239_b(
         (double)(var2 * 2.0F) + this.field_70146_Z.nextGaussian() * 0.25 + (double)((float)this.field_70170_p.field_73013_u.func_151525_a() * 0.11F)
      );
      if (var4 > 0) {
         var3.func_70239_b(var3.func_70242_d() + (double)var4 * 0.5 + 0.5);
      }

      if (var5 > 0) {
         var3.func_70240_a(var5);
      }

      if (EnchantmentHelper.func_77506_a(Enchantment.field_77343_v.field_77352_x, this.func_70694_bm()) > 0 || this.func_82202_m() == 1) {
         var3.func_70015_d(100);
      }

      this.func_85030_a("random.bow", 1.0F, 1.0F / (this.func_70681_au().nextFloat() * 0.4F + 0.8F));
      this.field_70170_p.func_72838_d(var3);
   }

   public int func_82202_m() {
      return this.field_70180_af.func_75683_a(13);
   }

   public void func_82201_a(int var1) {
      this.field_70180_af.func_75692_b(13, (byte)var1);
      this.field_70178_ae = var1 == 1;
      if (var1 == 1) {
         this.func_70105_a(0.72F, 2.34F);
      } else {
         this.func_70105_a(0.6F, 1.8F);
      }
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("SkeletonType", 99)) {
         byte var2 = var1.func_74771_c("SkeletonType");
         this.func_82201_a(var2);
      }

      this.func_85036_m();
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74774_a("SkeletonType", (byte)this.func_82202_m());
   }

   @Override
   public void func_70062_b(int var1, ItemStack var2) {
      super.func_70062_b(var1, var2);
      if (!this.field_70170_p.field_72995_K && var1 == 0) {
         this.func_85036_m();
      }
   }

   @Override
   public double func_70033_W() {
      return super.func_70033_W() - 0.5;
   }
}
