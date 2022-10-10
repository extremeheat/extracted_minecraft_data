package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityIronGolem extends EntityGolem {
   protected static final DataParameter<Byte> field_184750_a;
   private int field_70858_e;
   @Nullable
   private Village field_70857_d;
   private int field_70855_f;
   private int field_70856_g;

   public EntityIronGolem(World var1) {
      super(EntityType.field_200757_aw, var1);
      this.func_70105_a(1.4F, 2.7F);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(1, new EntityAIAttackMelee(this, 1.0D, true));
      this.field_70714_bg.func_75776_a(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
      this.field_70714_bg.func_75776_a(3, new EntityAIMoveThroughVillage(this, 0.6D, true));
      this.field_70714_bg.func_75776_a(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.field_70714_bg.func_75776_a(5, new EntityAILookAtVillager(this));
      this.field_70714_bg.func_75776_a(6, new EntityAIWanderAvoidWater(this, 0.6D));
      this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIDefendVillage(this));
      this.field_70715_bh.func_75776_a(2, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, false, true, (var0) -> {
         return var0 != null && IMob.field_175450_e.test(var0) && !(var0 instanceof EntityCreeper);
      }));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184750_a, (byte)0);
   }

   protected void func_70619_bc() {
      if (--this.field_70858_e <= 0) {
         this.field_70858_e = 70 + this.field_70146_Z.nextInt(50);
         this.field_70857_d = this.field_70170_p.func_175714_ae().func_176056_a(new BlockPos(this), 32);
         if (this.field_70857_d == null) {
            this.func_110177_bN();
         } else {
            BlockPos var1 = this.field_70857_d.func_180608_a();
            this.func_175449_a(var1, (int)((float)this.field_70857_d.func_75568_b() * 0.6F));
         }
      }

      super.func_70619_bc();
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(100.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
      this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111128_a(1.0D);
   }

   protected int func_70682_h(int var1) {
      return var1;
   }

   protected void func_82167_n(Entity var1) {
      if (var1 instanceof IMob && !(var1 instanceof EntityCreeper) && this.func_70681_au().nextInt(20) == 0) {
         this.func_70624_b((EntityLivingBase)var1);
      }

      super.func_82167_n(var1);
   }

   public void func_70636_d() {
      super.func_70636_d();
      if (this.field_70855_f > 0) {
         --this.field_70855_f;
      }

      if (this.field_70856_g > 0) {
         --this.field_70856_g;
      }

      if (this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 2.500000277905201E-7D && this.field_70146_Z.nextInt(5) == 0) {
         int var1 = MathHelper.func_76128_c(this.field_70165_t);
         int var2 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
         int var3 = MathHelper.func_76128_c(this.field_70161_v);
         IBlockState var4 = this.field_70170_p.func_180495_p(new BlockPos(var1, var2, var3));
         if (!var4.func_196958_f()) {
            this.field_70170_p.func_195594_a(new BlockParticleData(Particles.field_197611_d, var4), this.field_70165_t + ((double)this.field_70146_Z.nextFloat() - 0.5D) * (double)this.field_70130_N, this.func_174813_aQ().field_72338_b + 0.1D, this.field_70161_v + ((double)this.field_70146_Z.nextFloat() - 0.5D) * (double)this.field_70130_N, 4.0D * ((double)this.field_70146_Z.nextFloat() - 0.5D), 0.5D, ((double)this.field_70146_Z.nextFloat() - 0.5D) * 4.0D);
         }
      }

   }

   public boolean func_70686_a(Class<? extends EntityLivingBase> var1) {
      if (this.func_70850_q() && EntityPlayer.class.isAssignableFrom(var1)) {
         return false;
      } else {
         return var1 == EntityCreeper.class ? false : super.func_70686_a(var1);
      }
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("PlayerCreated", this.func_70850_q());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70849_f(var1.func_74767_n("PlayerCreated"));
   }

   public boolean func_70652_k(Entity var1) {
      this.field_70855_f = 10;
      this.field_70170_p.func_72960_a(this, (byte)4);
      boolean var2 = var1.func_70097_a(DamageSource.func_76358_a(this), (float)(7 + this.field_70146_Z.nextInt(15)));
      if (var2) {
         var1.field_70181_x += 0.4000000059604645D;
         this.func_174815_a(this, var1);
      }

      this.func_184185_a(SoundEvents.field_187596_cD, 1.0F, 1.0F);
      return var2;
   }

   public void func_70103_a(byte var1) {
      if (var1 == 4) {
         this.field_70855_f = 10;
         this.func_184185_a(SoundEvents.field_187596_cD, 1.0F, 1.0F);
      } else if (var1 == 11) {
         this.field_70856_g = 400;
      } else if (var1 == 34) {
         this.field_70856_g = 0;
      } else {
         super.func_70103_a(var1);
      }

   }

   public Village func_70852_n() {
      return this.field_70857_d;
   }

   public int func_70854_o() {
      return this.field_70855_f;
   }

   public void func_70851_e(boolean var1) {
      if (var1) {
         this.field_70856_g = 400;
         this.field_70170_p.func_72960_a(this, (byte)11);
      } else {
         this.field_70856_g = 0;
         this.field_70170_p.func_72960_a(this, (byte)34);
      }

   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187602_cF;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187599_cE;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      this.func_184185_a(SoundEvents.field_187605_cG, 1.0F, 1.0F);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186443_y;
   }

   public int func_70853_p() {
      return this.field_70856_g;
   }

   public boolean func_70850_q() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184750_a) & 1) != 0;
   }

   public void func_70849_f(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184750_a);
      if (var1) {
         this.field_70180_af.func_187227_b(field_184750_a, (byte)(var2 | 1));
      } else {
         this.field_70180_af.func_187227_b(field_184750_a, (byte)(var2 & -2));
      }

   }

   public void func_70645_a(DamageSource var1) {
      if (!this.func_70850_q() && this.field_70717_bb != null && this.field_70857_d != null) {
         this.field_70857_d.func_82688_a(this.field_70717_bb.func_146103_bH().getName(), -5);
      }

      super.func_70645_a(var1);
   }

   public boolean func_205019_a(IWorldReaderBase var1) {
      BlockPos var2 = new BlockPos(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      IBlockState var3 = var1.func_180495_p(var2);
      IBlockState var4 = var1.func_180495_p(var2.func_177977_b());
      IBlockState var5 = var1.func_180495_p(var2.func_177984_a());
      return var4.func_185896_q() && WorldEntitySpawner.func_206851_a(var5, var5.func_204520_s()) && WorldEntitySpawner.func_206851_a(var3, Fluids.field_204541_a.func_207188_f()) && var1.func_195586_b(this, this.func_174813_aQ()) && var1.func_195587_c(this, this.func_174813_aQ());
   }

   static {
      field_184750_a = EntityDataManager.func_187226_a(EntityIronGolem.class, DataSerializers.field_187191_a);
   }
}
