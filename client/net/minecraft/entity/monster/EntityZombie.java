package net.minecraft.entity.monster;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombie extends EntityMob {
   protected static final IAttribute field_110186_bp = new RangedAttribute("zombie.spawnReinforcements", 0.0, 0.0, 1.0)
      .func_111117_a("Spawn Reinforcements Chance");
   private static final UUID field_110187_bq = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier field_110188_br = new AttributeModifier(field_110187_bq, "Baby speed boost", 0.5, 1);
   private final EntityAIBreakDoor field_146075_bs = new EntityAIBreakDoor(this);
   private int field_82234_d;
   private boolean field_146076_bu = false;
   private float field_146074_bv = -1.0F;
   private float field_146073_bw;

   public EntityZombie(World var1) {
      super(var1);
      this.func_70661_as().func_75498_b(true);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
      this.field_70714_bg.func_75776_a(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0, true));
      this.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.field_70714_bg.func_75776_a(6, new EntityAIMoveThroughVillage(this, 1.0, false));
      this.field_70714_bg.func_75776_a(7, new EntityAIWander(this, 1.0));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, 0, false));
      this.func_70105_a(0.6F, 1.8F);
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(40.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.23000000417232513);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(3.0);
      this.func_110140_aT().func_111150_b(field_110186_bp).func_111128_a(this.field_70146_Z.nextDouble() * 0.10000000149011612);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.func_70096_w().func_75682_a(12, (byte)0);
      this.func_70096_w().func_75682_a(13, (byte)0);
      this.func_70096_w().func_75682_a(14, (byte)0);
   }

   @Override
   public int func_70658_aO() {
      int var1 = super.func_70658_aO() + 2;
      if (var1 > 20) {
         var1 = 20;
      }

      return var1;
   }

   @Override
   protected boolean func_70650_aV() {
      return true;
   }

   public boolean func_146072_bX() {
      return this.field_146076_bu;
   }

   public void func_146070_a(boolean var1) {
      if (this.field_146076_bu != var1) {
         this.field_146076_bu = var1;
         if (var1) {
            this.field_70714_bg.func_75776_a(1, this.field_146075_bs);
         } else {
            this.field_70714_bg.func_85156_a(this.field_146075_bs);
         }
      }
   }

   @Override
   public boolean func_70631_g_() {
      return this.func_70096_w().func_75683_a(12) == 1;
   }

   @Override
   protected int func_70693_a(EntityPlayer var1) {
      if (this.func_70631_g_()) {
         this.field_70728_aV = (int)((float)this.field_70728_aV * 2.5F);
      }

      return super.func_70693_a(var1);
   }

   public void func_82227_f(boolean var1) {
      this.func_70096_w().func_75692_b(12, (byte)(var1 ? 1 : 0));
      if (this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
         IAttributeInstance var2 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
         var2.func_111124_b(field_110188_br);
         if (var1) {
            var2.func_111121_a(field_110188_br);
         }
      }

      this.func_146071_k(var1);
   }

   public boolean func_82231_m() {
      return this.func_70096_w().func_75683_a(13) == 1;
   }

   public void func_82229_g(boolean var1) {
      this.func_70096_w().func_75692_b(13, (byte)(var1 ? 1 : 0));
   }

   @Override
   public void func_70636_d() {
      if (this.field_70170_p.func_72935_r() && !this.field_70170_p.field_72995_K && !this.func_70631_g_()) {
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

      if (this.func_70115_ae() && this.func_70638_az() != null && this.field_70154_o instanceof EntityChicken) {
         ((EntityLiving)this.field_70154_o).func_70661_as().func_75484_a(this.func_70661_as().func_75505_d(), 1.5);
      }

      super.func_70636_d();
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (!super.func_70097_a(var1, var2)) {
         return false;
      } else {
         EntityLivingBase var3 = this.func_70638_az();
         if (var3 == null && this.func_70777_m() instanceof EntityLivingBase) {
            var3 = (EntityLivingBase)this.func_70777_m();
         }

         if (var3 == null && var1.func_76346_g() instanceof EntityLivingBase) {
            var3 = (EntityLivingBase)var1.func_76346_g();
         }

         if (var3 != null
            && this.field_70170_p.field_73013_u == EnumDifficulty.HARD
            && (double)this.field_70146_Z.nextFloat() < this.func_110148_a(field_110186_bp).func_111126_e()) {
            int var4 = MathHelper.func_76128_c(this.field_70165_t);
            int var5 = MathHelper.func_76128_c(this.field_70163_u);
            int var6 = MathHelper.func_76128_c(this.field_70161_v);
            EntityZombie var7 = new EntityZombie(this.field_70170_p);

            for(int var8 = 0; var8 < 50; ++var8) {
               int var9 = var4 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               int var10 = var5 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               int var11 = var6 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               if (World.func_147466_a(this.field_70170_p, var9, var10 - 1, var11) && this.field_70170_p.func_72957_l(var9, var10, var11) < 10) {
                  var7.func_70107_b((double)var9, (double)var10, (double)var11);
                  if (this.field_70170_p.func_72855_b(var7.field_70121_D)
                     && this.field_70170_p.func_72945_a(var7, var7.field_70121_D).isEmpty()
                     && !this.field_70170_p.func_72953_d(var7.field_70121_D)) {
                     this.field_70170_p.func_72838_d(var7);
                     var7.func_70624_b(var3);
                     var7.func_110161_a(null);
                     this.func_110148_a(field_110186_bp).func_111121_a(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806, 0));
                     var7.func_110148_a(field_110186_bp).func_111121_a(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806, 0));
                     break;
                  }
               }
            }
         }

         return true;
      }
   }

   @Override
   public void func_70071_h_() {
      if (!this.field_70170_p.field_72995_K && this.func_82230_o()) {
         int var1 = this.func_82233_q();
         this.field_82234_d -= var1;
         if (this.field_82234_d <= 0) {
            this.func_82232_p();
         }
      }

      super.func_70071_h_();
   }

   @Override
   public boolean func_70652_k(Entity var1) {
      boolean var2 = super.func_70652_k(var1);
      if (var2) {
         int var3 = this.field_70170_p.field_73013_u.func_151525_a();
         if (this.func_70694_bm() == null && this.func_70027_ad() && this.field_70146_Z.nextFloat() < (float)var3 * 0.3F) {
            var1.func_70015_d(2 * var3);
         }
      }

      return var2;
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.zombie.say";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.zombie.hurt";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.zombie.death";
   }

   @Override
   protected void func_145780_a(int var1, int var2, int var3, Block var4) {
      this.func_85030_a("mob.zombie.step", 0.15F, 1.0F);
   }

   @Override
   protected Item func_146068_u() {
      return Items.field_151078_bh;
   }

   @Override
   public EnumCreatureAttribute func_70668_bt() {
      return EnumCreatureAttribute.UNDEAD;
   }

   @Override
   protected void func_70600_l(int var1) {
      switch(this.field_70146_Z.nextInt(3)) {
         case 0:
            this.func_145779_a(Items.field_151042_j, 1);
            break;
         case 1:
            this.func_145779_a(Items.field_151172_bF, 1);
            break;
         case 2:
            this.func_145779_a(Items.field_151174_bG, 1);
      }
   }

   @Override
   protected void func_82164_bB() {
      super.func_82164_bB();
      if (this.field_70146_Z.nextFloat() < (this.field_70170_p.field_73013_u == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
         int var1 = this.field_70146_Z.nextInt(3);
         if (var1 == 0) {
            this.func_70062_b(0, new ItemStack(Items.field_151040_l));
         } else {
            this.func_70062_b(0, new ItemStack(Items.field_151037_a));
         }
      }
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.func_70631_g_()) {
         var1.func_74757_a("IsBaby", true);
      }

      if (this.func_82231_m()) {
         var1.func_74757_a("IsVillager", true);
      }

      var1.func_74768_a("ConversionTime", this.func_82230_o() ? this.field_82234_d : -1);
      var1.func_74757_a("CanBreakDoors", this.func_146072_bX());
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_74767_n("IsBaby")) {
         this.func_82227_f(true);
      }

      if (var1.func_74767_n("IsVillager")) {
         this.func_82229_g(true);
      }

      if (var1.func_150297_b("ConversionTime", 99) && var1.func_74762_e("ConversionTime") > -1) {
         this.func_82228_a(var1.func_74762_e("ConversionTime"));
      }

      this.func_146070_a(var1.func_74767_n("CanBreakDoors"));
   }

   @Override
   public void func_70074_a(EntityLivingBase var1) {
      super.func_70074_a(var1);
      if ((this.field_70170_p.field_73013_u == EnumDifficulty.NORMAL || this.field_70170_p.field_73013_u == EnumDifficulty.HARD)
         && var1 instanceof EntityVillager) {
         if (this.field_70170_p.field_73013_u != EnumDifficulty.HARD && this.field_70146_Z.nextBoolean()) {
            return;
         }

         EntityZombie var2 = new EntityZombie(this.field_70170_p);
         var2.func_82149_j(var1);
         this.field_70170_p.func_72900_e(var1);
         var2.func_110161_a(null);
         var2.func_82229_g(true);
         if (var1.func_70631_g_()) {
            var2.func_82227_f(true);
         }

         this.field_70170_p.func_72838_d(var2);
         this.field_70170_p.func_72889_a(null, 1016, (int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 0);
      }
   }

   @Override
   public IEntityLivingData func_110161_a(IEntityLivingData var1) {
      var1 = super.func_110161_a(var1);
      float var2 = this.field_70170_p.func_147462_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.func_98053_h(this.field_70146_Z.nextFloat() < 0.55F * var2);
      if (var1 == null) {
         var1 = new EntityZombie$GroupData(
            this, this.field_70170_p.field_73012_v.nextFloat() < 0.05F, this.field_70170_p.field_73012_v.nextFloat() < 0.05F, null
         );
      }

      if (var1 instanceof EntityZombie$GroupData) {
         EntityZombie$GroupData var3 = (EntityZombie$GroupData)var1;
         if (var3.field_142046_b) {
            this.func_82229_g(true);
         }

         if (var3.field_142048_a) {
            this.func_82227_f(true);
            if ((double)this.field_70170_p.field_73012_v.nextFloat() < 0.05) {
               List var4 = this.field_70170_p
                  .func_82733_a(EntityChicken.class, this.field_70121_D.func_72314_b(5.0, 3.0, 5.0), IEntitySelector.field_152785_b);
               if (!var4.isEmpty()) {
                  EntityChicken var5 = (EntityChicken)var4.get(0);
                  var5.func_152117_i(true);
                  this.func_70078_a(var5);
               }
            } else if ((double)this.field_70170_p.field_73012_v.nextFloat() < 0.05) {
               EntityChicken var9 = new EntityChicken(this.field_70170_p);
               var9.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, 0.0F);
               var9.func_110161_a(null);
               var9.func_152117_i(true);
               this.field_70170_p.func_72838_d(var9);
               this.func_70078_a(var9);
            }
         }
      }

      this.func_146070_a(this.field_70146_Z.nextFloat() < var2 * 0.1F);
      this.func_82164_bB();
      this.func_82162_bC();
      if (this.func_71124_b(4) == null) {
         Calendar var7 = this.field_70170_p.func_83015_S();
         if (var7.get(2) + 1 == 10 && var7.get(5) == 31 && this.field_70146_Z.nextFloat() < 0.25F) {
            this.func_70062_b(4, new ItemStack(this.field_70146_Z.nextFloat() < 0.1F ? Blocks.field_150428_aP : Blocks.field_150423_aK));
            this.field_82174_bp[4] = 0.0F;
         }
      }

      this.func_110148_a(SharedMonsterAttributes.field_111266_c)
         .func_111121_a(new AttributeModifier("Random spawn bonus", this.field_70146_Z.nextDouble() * 0.05000000074505806, 0));
      double var8 = this.field_70146_Z.nextDouble()
         * 1.5
         * (double)this.field_70170_p.func_147462_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      if (var8 > 1.0) {
         this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111121_a(new AttributeModifier("Random zombie-spawn bonus", var8, 2));
      }

      if (this.field_70146_Z.nextFloat() < var2 * 0.05F) {
         this.func_110148_a(field_110186_bp).func_111121_a(new AttributeModifier("Leader zombie bonus", this.field_70146_Z.nextDouble() * 0.25 + 0.5, 0));
         this.func_110148_a(SharedMonsterAttributes.field_111267_a)
            .func_111121_a(new AttributeModifier("Leader zombie bonus", this.field_70146_Z.nextDouble() * 3.0 + 1.0, 2));
         this.func_146070_a(true);
      }

      return var1;
   }

   @Override
   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.func_71045_bC();
      if (var2 != null
         && var2.func_77973_b() == Items.field_151153_ao
         && var2.func_77960_j() == 0
         && this.func_82231_m()
         && this.func_70644_a(Potion.field_76437_t)) {
         if (!var1.field_71075_bZ.field_75098_d) {
            --var2.field_77994_a;
         }

         if (var2.field_77994_a <= 0) {
            var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, null);
         }

         if (!this.field_70170_p.field_72995_K) {
            this.func_82228_a(this.field_70146_Z.nextInt(2401) + 3600);
         }

         return true;
      } else {
         return false;
      }
   }

   protected void func_82228_a(int var1) {
      this.field_82234_d = var1;
      this.func_70096_w().func_75692_b(14, (byte)1);
      this.func_82170_o(Potion.field_76437_t.field_76415_H);
      this.func_70690_d(new PotionEffect(Potion.field_76420_g.field_76415_H, var1, Math.min(this.field_70170_p.field_73013_u.func_151525_a() - 1, 0)));
      this.field_70170_p.func_72960_a(this, (byte)16);
   }

   @Override
   public void func_70103_a(byte var1) {
      if (var1 == 16) {
         this.field_70170_p
            .func_72980_b(
               this.field_70165_t + 0.5,
               this.field_70163_u + 0.5,
               this.field_70161_v + 0.5,
               "mob.zombie.remedy",
               1.0F + this.field_70146_Z.nextFloat(),
               this.field_70146_Z.nextFloat() * 0.7F + 0.3F,
               false
            );
      } else {
         super.func_70103_a(var1);
      }
   }

   @Override
   protected boolean func_70692_ba() {
      return !this.func_82230_o();
   }

   public boolean func_82230_o() {
      return this.func_70096_w().func_75683_a(14) == 1;
   }

   protected void func_82232_p() {
      EntityVillager var1 = new EntityVillager(this.field_70170_p);
      var1.func_82149_j(this);
      var1.func_110161_a(null);
      var1.func_82187_q();
      if (this.func_70631_g_()) {
         var1.func_70873_a(-24000);
      }

      this.field_70170_p.func_72900_e(this);
      this.field_70170_p.func_72838_d(var1);
      var1.func_70690_d(new PotionEffect(Potion.field_76431_k.field_76415_H, 200, 0));
      this.field_70170_p.func_72889_a(null, 1017, (int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v, 0);
   }

   protected int func_82233_q() {
      int var1 = 1;
      if (this.field_70146_Z.nextFloat() < 0.01F) {
         int var2 = 0;

         for(int var3 = (int)this.field_70165_t - 4; var3 < (int)this.field_70165_t + 4 && var2 < 14; ++var3) {
            for(int var4 = (int)this.field_70163_u - 4; var4 < (int)this.field_70163_u + 4 && var2 < 14; ++var4) {
               for(int var5 = (int)this.field_70161_v - 4; var5 < (int)this.field_70161_v + 4 && var2 < 14; ++var5) {
                  Block var6 = this.field_70170_p.func_147439_a(var3, var4, var5);
                  if (var6 == Blocks.field_150411_aY || var6 == Blocks.field_150324_C) {
                     if (this.field_70146_Z.nextFloat() < 0.3F) {
                        ++var1;
                     }

                     ++var2;
                  }
               }
            }
         }
      }

      return var1;
   }

   public void func_146071_k(boolean var1) {
      this.func_146069_a(var1 ? 0.5F : 1.0F);
   }

   @Override
   protected final void func_70105_a(float var1, float var2) {
      boolean var3 = this.field_146074_bv > 0.0F && this.field_146073_bw > 0.0F;
      this.field_146074_bv = var1;
      this.field_146073_bw = var2;
      if (!var3) {
         this.func_146069_a(1.0F);
      }
   }

   protected final void func_146069_a(float var1) {
      super.func_70105_a(this.field_146074_bv * var1, this.field_146073_bw * var1);
   }
}
