package net.minecraft.entity.monster;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
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
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombie extends EntityMob {
   protected static final IAttribute field_110186_bp = (new RangedAttribute((IAttribute)null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).func_111117_a("Spawn Reinforcements Chance");
   private static final UUID field_110187_bq = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier field_110188_br;
   private final EntityAIBreakDoor field_146075_bs = new EntityAIBreakDoor(this);
   private int field_82234_d;
   private boolean field_146076_bu = false;
   private float field_146074_bv = -1.0F;
   private float field_146073_bw;

   public EntityZombie(World var1) {
      super(var1);
      ((PathNavigateGround)this.func_70661_as()).func_179688_b(true);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
      this.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.field_70714_bg.func_75776_a(7, new EntityAIWander(this, 1.0D));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.func_175456_n();
      this.func_70105_a(0.6F, 1.95F);
   }

   protected void func_175456_n() {
      this.field_70714_bg.func_75776_a(4, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
      this.field_70714_bg.func_75776_a(4, new EntityAIAttackOnCollide(this, EntityIronGolem.class, 1.0D, true));
      this.field_70714_bg.func_75776_a(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityPigZombie.class}));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(35.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.23000000417232513D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(3.0D);
      this.func_110140_aT().func_111150_b(field_110186_bp).func_111128_a(this.field_70146_Z.nextDouble() * 0.10000000149011612D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.func_70096_w().func_75682_a(12, (byte)0);
      this.func_70096_w().func_75682_a(13, (byte)0);
      this.func_70096_w().func_75682_a(14, (byte)0);
   }

   public int func_70658_aO() {
      int var1 = super.func_70658_aO() + 2;
      if (var1 > 20) {
         var1 = 20;
      }

      return var1;
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

   public boolean func_70631_g_() {
      return this.func_70096_w().func_75683_a(12) == 1;
   }

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

   public void func_70636_d() {
      if (this.field_70170_p.func_72935_r() && !this.field_70170_p.field_72995_K && !this.func_70631_g_()) {
         float var1 = this.func_70013_c(1.0F);
         BlockPos var2 = new BlockPos(this.field_70165_t, (double)Math.round(this.field_70163_u), this.field_70161_v);
         if (var1 > 0.5F && this.field_70146_Z.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && this.field_70170_p.func_175678_i(var2)) {
            boolean var3 = true;
            ItemStack var4 = this.func_71124_b(4);
            if (var4 != null) {
               if (var4.func_77984_f()) {
                  var4.func_77964_b(var4.func_77952_i() + this.field_70146_Z.nextInt(2));
                  if (var4.func_77952_i() >= var4.func_77958_k()) {
                     this.func_70669_a(var4);
                     this.func_70062_b(4, (ItemStack)null);
                  }
               }

               var3 = false;
            }

            if (var3) {
               this.func_70015_d(8);
            }
         }
      }

      if (this.func_70115_ae() && this.func_70638_az() != null && this.field_70154_o instanceof EntityChicken) {
         ((EntityLiving)this.field_70154_o).func_70661_as().func_75484_a(this.func_70661_as().func_75505_d(), 1.5D);
      }

      super.func_70636_d();
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (super.func_70097_a(var1, var2)) {
         EntityLivingBase var3 = this.func_70638_az();
         if (var3 == null && var1.func_76346_g() instanceof EntityLivingBase) {
            var3 = (EntityLivingBase)var1.func_76346_g();
         }

         if (var3 != null && this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD && (double)this.field_70146_Z.nextFloat() < this.func_110148_a(field_110186_bp).func_111126_e()) {
            int var4 = MathHelper.func_76128_c(this.field_70165_t);
            int var5 = MathHelper.func_76128_c(this.field_70163_u);
            int var6 = MathHelper.func_76128_c(this.field_70161_v);
            EntityZombie var7 = new EntityZombie(this.field_70170_p);

            for(int var8 = 0; var8 < 50; ++var8) {
               int var9 = var4 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               int var10 = var5 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               int var11 = var6 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               if (World.func_175683_a(this.field_70170_p, new BlockPos(var9, var10 - 1, var11)) && this.field_70170_p.func_175671_l(new BlockPos(var9, var10, var11)) < 10) {
                  var7.func_70107_b((double)var9, (double)var10, (double)var11);
                  if (!this.field_70170_p.func_175636_b((double)var9, (double)var10, (double)var11, 7.0D) && this.field_70170_p.func_72917_a(var7.func_174813_aQ(), var7) && this.field_70170_p.func_72945_a(var7, var7.func_174813_aQ()).isEmpty() && !this.field_70170_p.func_72953_d(var7.func_174813_aQ())) {
                     this.field_70170_p.func_72838_d(var7);
                     var7.func_70624_b(var3);
                     var7.func_180482_a(this.field_70170_p.func_175649_E(new BlockPos(var7)), (IEntityLivingData)null);
                     this.func_110148_a(field_110186_bp).func_111121_a(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, 0));
                     var7.func_110148_a(field_110186_bp).func_111121_a(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, 0));
                     break;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

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

   public boolean func_70652_k(Entity var1) {
      boolean var2 = super.func_70652_k(var1);
      if (var2) {
         int var3 = this.field_70170_p.func_175659_aa().func_151525_a();
         if (this.func_70694_bm() == null && this.func_70027_ad() && this.field_70146_Z.nextFloat() < (float)var3 * 0.3F) {
            var1.func_70015_d(2 * var3);
         }
      }

      return var2;
   }

   protected String func_70639_aQ() {
      return "mob.zombie.say";
   }

   protected String func_70621_aR() {
      return "mob.zombie.hurt";
   }

   protected String func_70673_aS() {
      return "mob.zombie.death";
   }

   protected void func_180429_a(BlockPos var1, Block var2) {
      this.func_85030_a("mob.zombie.step", 0.15F, 1.0F);
   }

   protected Item func_146068_u() {
      return Items.field_151078_bh;
   }

   public EnumCreatureAttribute func_70668_bt() {
      return EnumCreatureAttribute.UNDEAD;
   }

   protected void func_82164_bB() {
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

   protected void func_180481_a(DifficultyInstance var1) {
      super.func_180481_a(var1);
      if (this.field_70146_Z.nextFloat() < (this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
         int var2 = this.field_70146_Z.nextInt(3);
         if (var2 == 0) {
            this.func_70062_b(0, new ItemStack(Items.field_151040_l));
         } else {
            this.func_70062_b(0, new ItemStack(Items.field_151037_a));
         }
      }

   }

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

   public void func_70074_a(EntityLivingBase var1) {
      super.func_70074_a(var1);
      if ((this.field_70170_p.func_175659_aa() == EnumDifficulty.NORMAL || this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) && var1 instanceof EntityVillager) {
         if (this.field_70170_p.func_175659_aa() != EnumDifficulty.HARD && this.field_70146_Z.nextBoolean()) {
            return;
         }

         EntityLiving var2 = (EntityLiving)var1;
         EntityZombie var3 = new EntityZombie(this.field_70170_p);
         var3.func_82149_j(var1);
         this.field_70170_p.func_72900_e(var1);
         var3.func_180482_a(this.field_70170_p.func_175649_E(new BlockPos(var3)), (IEntityLivingData)null);
         var3.func_82229_g(true);
         if (var1.func_70631_g_()) {
            var3.func_82227_f(true);
         }

         var3.func_94061_f(var2.func_175446_cd());
         if (var2.func_145818_k_()) {
            var3.func_96094_a(var2.func_95999_t());
            var3.func_174805_g(var2.func_174833_aM());
         }

         this.field_70170_p.func_72838_d(var3);
         this.field_70170_p.func_180498_a((EntityPlayer)null, 1016, new BlockPos((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v), 0);
      }

   }

   public float func_70047_e() {
      float var1 = 1.74F;
      if (this.func_70631_g_()) {
         var1 = (float)((double)var1 - 0.81D);
      }

      return var1;
   }

   protected boolean func_175448_a(ItemStack var1) {
      return var1.func_77973_b() == Items.field_151110_aK && this.func_70631_g_() && this.func_70115_ae() ? false : super.func_175448_a(var1);
   }

   public IEntityLivingData func_180482_a(DifficultyInstance var1, IEntityLivingData var2) {
      Object var7 = super.func_180482_a(var1, var2);
      float var3 = var1.func_180170_c();
      this.func_98053_h(this.field_70146_Z.nextFloat() < 0.55F * var3);
      if (var7 == null) {
         var7 = new EntityZombie.GroupData(this.field_70170_p.field_73012_v.nextFloat() < 0.05F, this.field_70170_p.field_73012_v.nextFloat() < 0.05F);
      }

      if (var7 instanceof EntityZombie.GroupData) {
         EntityZombie.GroupData var4 = (EntityZombie.GroupData)var7;
         if (var4.field_142046_b) {
            this.func_82229_g(true);
         }

         if (var4.field_142048_a) {
            this.func_82227_f(true);
            if ((double)this.field_70170_p.field_73012_v.nextFloat() < 0.05D) {
               List var5 = this.field_70170_p.func_175647_a(EntityChicken.class, this.func_174813_aQ().func_72314_b(5.0D, 3.0D, 5.0D), EntitySelectors.field_152785_b);
               if (!var5.isEmpty()) {
                  EntityChicken var6 = (EntityChicken)var5.get(0);
                  var6.func_152117_i(true);
                  this.func_70078_a(var6);
               }
            } else if ((double)this.field_70170_p.field_73012_v.nextFloat() < 0.05D) {
               EntityChicken var10 = new EntityChicken(this.field_70170_p);
               var10.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, 0.0F);
               var10.func_180482_a(var1, (IEntityLivingData)null);
               var10.func_152117_i(true);
               this.field_70170_p.func_72838_d(var10);
               this.func_70078_a(var10);
            }
         }
      }

      this.func_146070_a(this.field_70146_Z.nextFloat() < var3 * 0.1F);
      this.func_180481_a(var1);
      this.func_180483_b(var1);
      if (this.func_71124_b(4) == null) {
         Calendar var8 = this.field_70170_p.func_83015_S();
         if (var8.get(2) + 1 == 10 && var8.get(5) == 31 && this.field_70146_Z.nextFloat() < 0.25F) {
            this.func_70062_b(4, new ItemStack(this.field_70146_Z.nextFloat() < 0.1F ? Blocks.field_150428_aP : Blocks.field_150423_aK));
            this.field_82174_bp[4] = 0.0F;
         }
      }

      this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111121_a(new AttributeModifier("Random spawn bonus", this.field_70146_Z.nextDouble() * 0.05000000074505806D, 0));
      double var9 = this.field_70146_Z.nextDouble() * 1.5D * (double)var3;
      if (var9 > 1.0D) {
         this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111121_a(new AttributeModifier("Random zombie-spawn bonus", var9, 2));
      }

      if (this.field_70146_Z.nextFloat() < var3 * 0.05F) {
         this.func_110148_a(field_110186_bp).func_111121_a(new AttributeModifier("Leader zombie bonus", this.field_70146_Z.nextDouble() * 0.25D + 0.5D, 0));
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111121_a(new AttributeModifier("Leader zombie bonus", this.field_70146_Z.nextDouble() * 3.0D + 1.0D, 2));
         this.func_146070_a(true);
      }

      return (IEntityLivingData)var7;
   }

   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.func_71045_bC();
      if (var2 != null && var2.func_77973_b() == Items.field_151153_ao && var2.func_77960_j() == 0 && this.func_82231_m() && this.func_70644_a(Potion.field_76437_t)) {
         if (!var1.field_71075_bZ.field_75098_d) {
            --var2.field_77994_a;
         }

         if (var2.field_77994_a <= 0) {
            var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, (ItemStack)null);
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
      this.func_70690_d(new PotionEffect(Potion.field_76420_g.field_76415_H, var1, Math.min(this.field_70170_p.func_175659_aa().func_151525_a() - 1, 0)));
      this.field_70170_p.func_72960_a(this, (byte)16);
   }

   public void func_70103_a(byte var1) {
      if (var1 == 16) {
         if (!this.func_174814_R()) {
            this.field_70170_p.func_72980_b(this.field_70165_t + 0.5D, this.field_70163_u + 0.5D, this.field_70161_v + 0.5D, "mob.zombie.remedy", 1.0F + this.field_70146_Z.nextFloat(), this.field_70146_Z.nextFloat() * 0.7F + 0.3F, false);
         }
      } else {
         super.func_70103_a(var1);
      }

   }

   protected boolean func_70692_ba() {
      return !this.func_82230_o();
   }

   public boolean func_82230_o() {
      return this.func_70096_w().func_75683_a(14) == 1;
   }

   protected void func_82232_p() {
      EntityVillager var1 = new EntityVillager(this.field_70170_p);
      var1.func_82149_j(this);
      var1.func_180482_a(this.field_70170_p.func_175649_E(new BlockPos(var1)), (IEntityLivingData)null);
      var1.func_82187_q();
      if (this.func_70631_g_()) {
         var1.func_70873_a(-24000);
      }

      this.field_70170_p.func_72900_e(this);
      var1.func_94061_f(this.func_175446_cd());
      if (this.func_145818_k_()) {
         var1.func_96094_a(this.func_95999_t());
         var1.func_174805_g(this.func_174833_aM());
      }

      this.field_70170_p.func_72838_d(var1);
      var1.func_70690_d(new PotionEffect(Potion.field_76431_k.field_76415_H, 200, 0));
      this.field_70170_p.func_180498_a((EntityPlayer)null, 1017, new BlockPos((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v), 0);
   }

   protected int func_82233_q() {
      int var1 = 1;
      if (this.field_70146_Z.nextFloat() < 0.01F) {
         int var2 = 0;
         BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

         for(int var4 = (int)this.field_70165_t - 4; var4 < (int)this.field_70165_t + 4 && var2 < 14; ++var4) {
            for(int var5 = (int)this.field_70163_u - 4; var5 < (int)this.field_70163_u + 4 && var2 < 14; ++var5) {
               for(int var6 = (int)this.field_70161_v - 4; var6 < (int)this.field_70161_v + 4 && var2 < 14; ++var6) {
                  Block var7 = this.field_70170_p.func_180495_p(var3.func_181079_c(var4, var5, var6)).func_177230_c();
                  if (var7 == Blocks.field_150411_aY || var7 == Blocks.field_150324_C) {
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

   public double func_70033_W() {
      return this.func_70631_g_() ? 0.0D : -0.35D;
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (var1.func_76346_g() instanceof EntityCreeper && !(this instanceof EntityPigZombie) && ((EntityCreeper)var1.func_76346_g()).func_70830_n() && ((EntityCreeper)var1.func_76346_g()).func_70650_aV()) {
         ((EntityCreeper)var1.func_76346_g()).func_175493_co();
         this.func_70099_a(new ItemStack(Items.field_151144_bL, 1, 2), 0.0F);
      }

   }

   static {
      field_110188_br = new AttributeModifier(field_110187_bq, "Baby speed boost", 0.5D, 1);
   }

   class GroupData implements IEntityLivingData {
      public boolean field_142048_a;
      public boolean field_142046_b;

      private GroupData(boolean var2, boolean var3) {
         super();
         this.field_142048_a = false;
         this.field_142046_b = false;
         this.field_142048_a = var2;
         this.field_142046_b = var3;
      }

      // $FF: synthetic method
      GroupData(boolean var2, boolean var3, Object var4) {
         this(var2, var3);
      }
   }
}
