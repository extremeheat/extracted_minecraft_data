package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBreakBlock;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityZombie extends EntityMob {
   protected static final IAttribute field_110186_bp = (new RangedAttribute((IAttribute)null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).func_111117_a("Spawn Reinforcements Chance");
   private static final UUID field_110187_bq = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier field_110188_br;
   private static final DataParameter<Boolean> field_184737_bv;
   private static final DataParameter<Integer> field_184738_bw;
   private static final DataParameter<Boolean> field_184740_by;
   private static final DataParameter<Boolean> field_204709_bA;
   private final EntityAIBreakDoor field_146075_bs;
   private boolean field_146076_bu;
   private int field_204707_bD;
   private int field_204708_bE;
   private float field_146074_bv;
   private float field_146073_bw;

   public EntityZombie(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_146075_bs = new EntityAIBreakDoor(this);
      this.field_146074_bv = -1.0F;
      this.func_70105_a(0.6F, 1.95F);
   }

   public EntityZombie(World var1) {
      this(EntityType.field_200725_aD, var1);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(4, new EntityZombie.AIAttackTurtleEgg(Blocks.field_203213_jA, this, 1.0D, 3));
      this.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.func_175456_n();
   }

   protected void func_175456_n() {
      this.field_70714_bg.func_75776_a(2, new EntityAIZombieAttack(this, 1.0D, false));
      this.field_70714_bg.func_75776_a(6, new EntityAIMoveThroughVillage(this, 1.0D, false));
      this.field_70714_bg.func_75776_a(7, new EntityAIWanderAvoidWater(this, 1.0D));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityPigZombie.class}));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
      this.field_70715_bh.func_75776_a(5, new EntityAINearestAttackableTarget(this, EntityTurtle.class, 10, true, false, EntityTurtle.field_203029_bx));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(35.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.23000000417232513D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(3.0D);
      this.func_110148_a(SharedMonsterAttributes.field_188791_g).func_111128_a(2.0D);
      this.func_110140_aT().func_111150_b(field_110186_bp).func_111128_a(this.field_70146_Z.nextDouble() * 0.10000000149011612D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.func_184212_Q().func_187214_a(field_184737_bv, false);
      this.func_184212_Q().func_187214_a(field_184738_bw, 0);
      this.func_184212_Q().func_187214_a(field_184740_by, false);
      this.func_184212_Q().func_187214_a(field_204709_bA, false);
   }

   public boolean func_204706_dD() {
      return (Boolean)this.func_184212_Q().func_187225_a(field_204709_bA);
   }

   public void func_184724_a(boolean var1) {
      this.func_184212_Q().func_187227_b(field_184740_by, var1);
   }

   public boolean func_184734_db() {
      return (Boolean)this.func_184212_Q().func_187225_a(field_184740_by);
   }

   public boolean func_146072_bX() {
      return this.field_146076_bu;
   }

   public void func_146070_a(boolean var1) {
      if (this.func_204900_dz()) {
         if (this.field_146076_bu != var1) {
            this.field_146076_bu = var1;
            ((PathNavigateGround)this.func_70661_as()).func_179688_b(var1);
            if (var1) {
               this.field_70714_bg.func_75776_a(1, this.field_146075_bs);
            } else {
               this.field_70714_bg.func_85156_a(this.field_146075_bs);
            }
         }
      } else if (this.field_146076_bu) {
         this.field_70714_bg.func_85156_a(this.field_146075_bs);
         this.field_146076_bu = false;
      }

   }

   protected boolean func_204900_dz() {
      return true;
   }

   public boolean func_70631_g_() {
      return (Boolean)this.func_184212_Q().func_187225_a(field_184737_bv);
   }

   protected int func_70693_a(EntityPlayer var1) {
      if (this.func_70631_g_()) {
         this.field_70728_aV = (int)((float)this.field_70728_aV * 2.5F);
      }

      return super.func_70693_a(var1);
   }

   public void func_82227_f(boolean var1) {
      this.func_184212_Q().func_187227_b(field_184737_bv, var1);
      if (this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
         IAttributeInstance var2 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
         var2.func_111124_b(field_110188_br);
         if (var1) {
            var2.func_111121_a(field_110188_br);
         }
      }

      this.func_146071_k(var1);
   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_184737_bv.equals(var1)) {
         this.func_146071_k(this.func_70631_g_());
      }

      super.func_184206_a(var1);
   }

   protected boolean func_204703_dA() {
      return true;
   }

   public void func_70071_h_() {
      if (!this.field_70170_p.field_72995_K) {
         if (this.func_204706_dD()) {
            --this.field_204708_bE;
            if (this.field_204708_bE < 0) {
               this.func_207302_dI();
            }
         } else if (this.func_204703_dA()) {
            if (this.func_208600_a(FluidTags.field_206959_a)) {
               ++this.field_204707_bD;
               if (this.field_204707_bD >= 600) {
                  this.func_204704_a(300);
               }
            } else {
               this.field_204707_bD = -1;
            }
         }
      }

      super.func_70071_h_();
   }

   public void func_70636_d() {
      boolean var1 = this.func_190730_o() && this.func_204609_dp();
      if (var1) {
         ItemStack var2 = this.func_184582_a(EntityEquipmentSlot.HEAD);
         if (!var2.func_190926_b()) {
            if (var2.func_77984_f()) {
               var2.func_196085_b(var2.func_77952_i() + this.field_70146_Z.nextInt(2));
               if (var2.func_77952_i() >= var2.func_77958_k()) {
                  this.func_70669_a(var2);
                  this.func_184201_a(EntityEquipmentSlot.HEAD, ItemStack.field_190927_a);
               }
            }

            var1 = false;
         }

         if (var1) {
            this.func_70015_d(8);
         }
      }

      super.func_70636_d();
   }

   private void func_204704_a(int var1) {
      this.field_204708_bE = var1;
      this.func_184212_Q().func_187227_b(field_204709_bA, true);
   }

   protected void func_207302_dI() {
      this.func_207305_a(new EntityDrowned(this.field_70170_p));
      this.field_70170_p.func_180498_a((EntityPlayer)null, 1040, new BlockPos((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v), 0);
   }

   protected void func_207305_a(EntityZombie var1) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         var1.func_82149_j(this);
         var1.func_207301_a(this.func_98052_bS(), this.func_146072_bX(), this.func_70631_g_(), this.func_175446_cd());
         EntityEquipmentSlot[] var2 = EntityEquipmentSlot.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EntityEquipmentSlot var5 = var2[var4];
            ItemStack var6 = this.func_184582_a(var5);
            if (!var6.func_190926_b()) {
               var1.func_184201_a(var5, var6);
               var1.func_184642_a(var5, this.func_205712_c(var5));
            }
         }

         if (this.func_145818_k_()) {
            var1.func_200203_b(this.func_200201_e());
            var1.func_174805_g(this.func_174833_aM());
         }

         this.field_70170_p.func_72838_d(var1);
         this.func_70106_y();
      }
   }

   protected boolean func_190730_o() {
      return true;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (super.func_70097_a(var1, var2)) {
         EntityLivingBase var3 = this.func_70638_az();
         if (var3 == null && var1.func_76346_g() instanceof EntityLivingBase) {
            var3 = (EntityLivingBase)var1.func_76346_g();
         }

         if (var3 != null && this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD && (double)this.field_70146_Z.nextFloat() < this.func_110148_a(field_110186_bp).func_111126_e() && this.field_70170_p.func_82736_K().func_82766_b("doMobSpawning")) {
            int var4 = MathHelper.func_76128_c(this.field_70165_t);
            int var5 = MathHelper.func_76128_c(this.field_70163_u);
            int var6 = MathHelper.func_76128_c(this.field_70161_v);
            EntityZombie var7 = new EntityZombie(this.field_70170_p);

            for(int var8 = 0; var8 < 50; ++var8) {
               int var9 = var4 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               int var10 = var5 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               int var11 = var6 + MathHelper.func_76136_a(this.field_70146_Z, 7, 40) * MathHelper.func_76136_a(this.field_70146_Z, -1, 1);
               if (this.field_70170_p.func_180495_p(new BlockPos(var9, var10 - 1, var11)).func_185896_q() && this.field_70170_p.func_201696_r(new BlockPos(var9, var10, var11)) < 10) {
                  var7.func_70107_b((double)var9, (double)var10, (double)var11);
                  if (!this.field_70170_p.func_175636_b((double)var9, (double)var10, (double)var11, 7.0D) && this.field_70170_p.func_195587_c(var7, var7.func_174813_aQ()) && this.field_70170_p.func_195586_b(var7, var7.func_174813_aQ()) && !this.field_70170_p.func_72953_d(var7.func_174813_aQ())) {
                     this.field_70170_p.func_72838_d(var7);
                     var7.func_70624_b(var3);
                     var7.func_204210_a(this.field_70170_p.func_175649_E(new BlockPos(var7)), (IEntityLivingData)null, (NBTTagCompound)null);
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

   public boolean func_70652_k(Entity var1) {
      boolean var2 = super.func_70652_k(var1);
      if (var2) {
         float var3 = this.field_70170_p.func_175649_E(new BlockPos(this)).func_180168_b();
         if (this.func_184614_ca().func_190926_b() && this.func_70027_ad() && this.field_70146_Z.nextFloat() < var3 * 0.3F) {
            var1.func_70015_d(2 * (int)var3);
         }
      }

      return var2;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187899_gZ;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187934_hh;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187930_hd;
   }

   protected SoundEvent func_190731_di() {
      return SoundEvents.field_187939_hm;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      this.func_184185_a(this.func_190731_di(), 0.15F, 1.0F);
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.UNDEAD;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186383_ah;
   }

   protected void func_180481_a(DifficultyInstance var1) {
      super.func_180481_a(var1);
      if (this.field_70146_Z.nextFloat() < (this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD ? 0.05F : 0.01F)) {
         int var2 = this.field_70146_Z.nextInt(3);
         if (var2 == 0) {
            this.func_184201_a(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.field_151040_l));
         } else {
            this.func_184201_a(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.field_151037_a));
         }
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.func_70631_g_()) {
         var1.func_74757_a("IsBaby", true);
      }

      var1.func_74757_a("CanBreakDoors", this.func_146072_bX());
      var1.func_74768_a("InWaterTime", this.func_70090_H() ? this.field_204707_bD : -1);
      var1.func_74768_a("DrownedConversionTime", this.func_204706_dD() ? this.field_204708_bE : -1);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_74767_n("IsBaby")) {
         this.func_82227_f(true);
      }

      this.func_146070_a(var1.func_74767_n("CanBreakDoors"));
      this.field_204707_bD = var1.func_74762_e("InWaterTime");
      if (var1.func_150297_b("DrownedConversionTime", 99) && var1.func_74762_e("DrownedConversionTime") > -1) {
         this.func_204704_a(var1.func_74762_e("DrownedConversionTime"));
      }

   }

   public void func_70074_a(EntityLivingBase var1) {
      super.func_70074_a(var1);
      if ((this.field_70170_p.func_175659_aa() == EnumDifficulty.NORMAL || this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) && var1 instanceof EntityVillager) {
         if (this.field_70170_p.func_175659_aa() != EnumDifficulty.HARD && this.field_70146_Z.nextBoolean()) {
            return;
         }

         EntityVillager var2 = (EntityVillager)var1;
         EntityZombieVillager var3 = new EntityZombieVillager(this.field_70170_p);
         var3.func_82149_j(var2);
         this.field_70170_p.func_72900_e(var2);
         var3.func_204210_a(this.field_70170_p.func_175649_E(new BlockPos(var3)), new EntityZombie.GroupData(false), (NBTTagCompound)null);
         var3.func_190733_a(var2.func_70946_n());
         var3.func_82227_f(var2.func_70631_g_());
         var3.func_94061_f(var2.func_175446_cd());
         if (var2.func_145818_k_()) {
            var3.func_200203_b(var2.func_200201_e());
            var3.func_174805_g(var2.func_174833_aM());
         }

         this.field_70170_p.func_72838_d(var3);
         this.field_70170_p.func_180498_a((EntityPlayer)null, 1026, new BlockPos(this), 0);
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
      return var1.func_77973_b() == Items.field_151110_aK && this.func_70631_g_() && this.func_184218_aH() ? false : super.func_175448_a(var1);
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      Object var8 = super.func_204210_a(var1, var2, var3);
      float var4 = var1.func_180170_c();
      this.func_98053_h(this.field_70146_Z.nextFloat() < 0.55F * var4);
      if (var8 == null) {
         var8 = new EntityZombie.GroupData(this.field_70170_p.field_73012_v.nextFloat() < 0.05F);
      }

      if (var8 instanceof EntityZombie.GroupData) {
         EntityZombie.GroupData var5 = (EntityZombie.GroupData)var8;
         if (var5.field_142048_a) {
            this.func_82227_f(true);
            if ((double)this.field_70170_p.field_73012_v.nextFloat() < 0.05D) {
               List var6 = this.field_70170_p.func_175647_a(EntityChicken.class, this.func_174813_aQ().func_72314_b(5.0D, 3.0D, 5.0D), EntitySelectors.field_152785_b);
               if (!var6.isEmpty()) {
                  EntityChicken var7 = (EntityChicken)var6.get(0);
                  var7.func_152117_i(true);
                  this.func_184220_m(var7);
               }
            } else if ((double)this.field_70170_p.field_73012_v.nextFloat() < 0.05D) {
               EntityChicken var10 = new EntityChicken(this.field_70170_p);
               var10.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, 0.0F);
               var10.func_204210_a(var1, (IEntityLivingData)null, (NBTTagCompound)null);
               var10.func_152117_i(true);
               this.field_70170_p.func_72838_d(var10);
               this.func_184220_m(var10);
            }
         }

         this.func_146070_a(this.func_204900_dz() && this.field_70146_Z.nextFloat() < var4 * 0.1F);
         this.func_180481_a(var1);
         this.func_180483_b(var1);
      }

      if (this.func_184582_a(EntityEquipmentSlot.HEAD).func_190926_b()) {
         LocalDate var9 = LocalDate.now();
         int var11 = var9.get(ChronoField.DAY_OF_MONTH);
         int var12 = var9.get(ChronoField.MONTH_OF_YEAR);
         if (var12 == 10 && var11 == 31 && this.field_70146_Z.nextFloat() < 0.25F) {
            this.func_184201_a(EntityEquipmentSlot.HEAD, new ItemStack(this.field_70146_Z.nextFloat() < 0.1F ? Blocks.field_196628_cT : Blocks.field_196625_cS));
            this.field_184655_bs[EntityEquipmentSlot.HEAD.func_188454_b()] = 0.0F;
         }
      }

      this.func_207304_a(var4);
      return (IEntityLivingData)var8;
   }

   protected void func_207301_a(boolean var1, boolean var2, boolean var3, boolean var4) {
      this.func_98053_h(var1);
      this.func_146070_a(this.func_204900_dz() && var2);
      this.func_207304_a(this.field_70170_p.func_175649_E(new BlockPos(this)).func_180170_c());
      this.func_82227_f(var3);
      this.func_94061_f(var4);
   }

   protected void func_207304_a(float var1) {
      this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111121_a(new AttributeModifier("Random spawn bonus", this.field_70146_Z.nextDouble() * 0.05000000074505806D, 0));
      double var2 = this.field_70146_Z.nextDouble() * 1.5D * (double)var1;
      if (var2 > 1.0D) {
         this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111121_a(new AttributeModifier("Random zombie-spawn bonus", var2, 2));
      }

      if (this.field_70146_Z.nextFloat() < var1 * 0.05F) {
         this.func_110148_a(field_110186_bp).func_111121_a(new AttributeModifier("Leader zombie bonus", this.field_70146_Z.nextDouble() * 0.25D + 0.5D, 0));
         this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111121_a(new AttributeModifier("Leader zombie bonus", this.field_70146_Z.nextDouble() * 3.0D + 1.0D, 2));
         this.func_146070_a(this.func_204900_dz());
      }

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
      return this.func_70631_g_() ? 0.0D : -0.45D;
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (var1.func_76346_g() instanceof EntityCreeper) {
         EntityCreeper var2 = (EntityCreeper)var1.func_76346_g();
         if (var2.func_70830_n() && var2.func_70650_aV()) {
            var2.func_175493_co();
            ItemStack var3 = this.func_190732_dj();
            if (!var3.func_190926_b()) {
               this.func_199701_a_(var3);
            }
         }
      }

   }

   protected ItemStack func_190732_dj() {
      return new ItemStack(Items.field_196186_dz);
   }

   static {
      field_110188_br = new AttributeModifier(field_110187_bq, "Baby speed boost", 0.5D, 1);
      field_184737_bv = EntityDataManager.func_187226_a(EntityZombie.class, DataSerializers.field_187198_h);
      field_184738_bw = EntityDataManager.func_187226_a(EntityZombie.class, DataSerializers.field_187192_b);
      field_184740_by = EntityDataManager.func_187226_a(EntityZombie.class, DataSerializers.field_187198_h);
      field_204709_bA = EntityDataManager.func_187226_a(EntityZombie.class, DataSerializers.field_187198_h);
   }

   class AIAttackTurtleEgg extends EntityAIBreakBlock {
      AIAttackTurtleEgg(Block var2, EntityCreature var3, double var4, int var6) {
         super(var2, var3, var4, var6);
      }

      public void func_203114_b(IWorld var1, BlockPos var2) {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_203276_jU, SoundCategory.HOSTILE, 0.5F, 0.9F + EntityZombie.this.field_70146_Z.nextFloat() * 0.2F);
      }

      public void func_203116_c(World var1, BlockPos var2) {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_203281_iz, SoundCategory.BLOCKS, 0.7F, 0.9F + var1.field_73012_v.nextFloat() * 0.2F);
      }

      public double func_203110_f() {
         return 1.3D;
      }
   }

   public class GroupData implements IEntityLivingData {
      public boolean field_142048_a;

      private GroupData(boolean var2) {
         super();
         this.field_142048_a = var2;
      }

      // $FF: synthetic method
      GroupData(boolean var2, Object var3) {
         this(var2);
      }
   }
}
