package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityPolarBear extends EntityAnimal {
   private static final DataParameter<Boolean> field_189798_bx;
   private float field_189799_by;
   private float field_189800_bz;
   private int field_189797_bB;

   public EntityPolarBear(World var1) {
      super(EntityType.field_200786_Z, var1);
      this.func_70105_a(1.3F, 1.4F);
   }

   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return new EntityPolarBear(this.field_70170_p);
   }

   public boolean func_70877_b(ItemStack var1) {
      return false;
   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityPolarBear.AIMeleeAttack());
      this.field_70714_bg.func_75776_a(1, new EntityPolarBear.AIPanic());
      this.field_70714_bg.func_75776_a(4, new EntityAIFollowParent(this, 1.25D));
      this.field_70714_bg.func_75776_a(5, new EntityAIWander(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(7, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityPolarBear.AIHurtByTarget());
      this.field_70715_bh.func_75776_a(2, new EntityPolarBear.AIAttackPlayer());
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(30.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(20.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(6.0D);
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      int var3 = MathHelper.func_76128_c(this.field_70165_t);
      int var4 = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
      int var5 = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos var6 = new BlockPos(var3, var4, var5);
      Biome var7 = var1.func_180494_b(var6);
      if (var7 != Biomes.field_76776_l && var7 != Biomes.field_203620_Z) {
         return super.func_205020_a(var1, var2);
      } else {
         return var1.func_201669_a(var6, 0) > 8 && var1.func_180495_p(var6.func_177977_b()).func_177230_c() == Blocks.field_150432_aD;
      }
   }

   protected SoundEvent func_184639_G() {
      return this.func_70631_g_() ? SoundEvents.field_190027_es : SoundEvents.field_190026_er;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_190029_eu;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_190028_et;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      this.func_184185_a(SoundEvents.field_190030_ev, 0.15F, 1.0F);
   }

   protected void func_189796_de() {
      if (this.field_189797_bB <= 0) {
         this.func_184185_a(SoundEvents.field_190031_ew, 1.0F, 1.0F);
         this.field_189797_bB = 40;
      }

   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_189969_E;
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_189798_bx, false);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         this.field_189799_by = this.field_189800_bz;
         if (this.func_189793_df()) {
            this.field_189800_bz = MathHelper.func_76131_a(this.field_189800_bz + 1.0F, 0.0F, 6.0F);
         } else {
            this.field_189800_bz = MathHelper.func_76131_a(this.field_189800_bz - 1.0F, 0.0F, 6.0F);
         }
      }

      if (this.field_189797_bB > 0) {
         --this.field_189797_bB;
      }

   }

   public boolean func_70652_k(Entity var1) {
      boolean var2 = var1.func_70097_a(DamageSource.func_76358_a(this), (float)((int)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e()));
      if (var2) {
         this.func_174815_a(this, var1);
      }

      return var2;
   }

   public boolean func_189793_df() {
      return (Boolean)this.field_70180_af.func_187225_a(field_189798_bx);
   }

   public void func_189794_p(boolean var1) {
      this.field_70180_af.func_187227_b(field_189798_bx, var1);
   }

   public float func_189795_r(float var1) {
      return (this.field_189799_by + (this.field_189800_bz - this.field_189799_by) * var1) / 6.0F;
   }

   protected float func_189749_co() {
      return 0.98F;
   }

   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      if (var2 instanceof EntityPolarBear.GroupData) {
         if (((EntityPolarBear.GroupData)var2).field_190101_a) {
            this.func_70873_a(-24000);
         }
      } else {
         EntityPolarBear.GroupData var4 = new EntityPolarBear.GroupData();
         var4.field_190101_a = true;
         var2 = var4;
      }

      return (IEntityLivingData)var2;
   }

   static {
      field_189798_bx = EntityDataManager.func_187226_a(EntityPolarBear.class, DataSerializers.field_187198_h);
   }

   class AIPanic extends EntityAIPanic {
      public AIPanic() {
         super(EntityPolarBear.this, 2.0D);
      }

      public boolean func_75250_a() {
         return !EntityPolarBear.this.func_70631_g_() && !EntityPolarBear.this.func_70027_ad() ? false : super.func_75250_a();
      }
   }

   class AIMeleeAttack extends EntityAIAttackMelee {
      public AIMeleeAttack() {
         super(EntityPolarBear.this, 1.25D, true);
      }

      protected void func_190102_a(EntityLivingBase var1, double var2) {
         double var4 = this.func_179512_a(var1);
         if (var2 <= var4 && this.field_75439_d <= 0) {
            this.field_75439_d = 20;
            this.field_75441_b.func_70652_k(var1);
            EntityPolarBear.this.func_189794_p(false);
         } else if (var2 <= var4 * 2.0D) {
            if (this.field_75439_d <= 0) {
               EntityPolarBear.this.func_189794_p(false);
               this.field_75439_d = 20;
            }

            if (this.field_75439_d <= 10) {
               EntityPolarBear.this.func_189794_p(true);
               EntityPolarBear.this.func_189796_de();
            }
         } else {
            this.field_75439_d = 20;
            EntityPolarBear.this.func_189794_p(false);
         }

      }

      public void func_75251_c() {
         EntityPolarBear.this.func_189794_p(false);
         super.func_75251_c();
      }

      protected double func_179512_a(EntityLivingBase var1) {
         return (double)(4.0F + var1.field_70130_N);
      }
   }

   class AIAttackPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
      public AIAttackPlayer() {
         super(EntityPolarBear.this, EntityPlayer.class, 20, true, true, (Predicate)null);
      }

      public boolean func_75250_a() {
         if (EntityPolarBear.this.func_70631_g_()) {
            return false;
         } else {
            if (super.func_75250_a()) {
               List var1 = EntityPolarBear.this.field_70170_p.func_72872_a(EntityPolarBear.class, EntityPolarBear.this.func_174813_aQ().func_72314_b(8.0D, 4.0D, 8.0D));
               Iterator var2 = var1.iterator();

               while(var2.hasNext()) {
                  EntityPolarBear var3 = (EntityPolarBear)var2.next();
                  if (var3.func_70631_g_()) {
                     return true;
                  }
               }
            }

            EntityPolarBear.this.func_70624_b((EntityLivingBase)null);
            return false;
         }
      }

      protected double func_111175_f() {
         return super.func_111175_f() * 0.5D;
      }
   }

   class AIHurtByTarget extends EntityAIHurtByTarget {
      public AIHurtByTarget() {
         super(EntityPolarBear.this, false);
      }

      public void func_75249_e() {
         super.func_75249_e();
         if (EntityPolarBear.this.func_70631_g_()) {
            this.func_190105_f();
            this.func_75251_c();
         }

      }

      protected void func_179446_a(EntityCreature var1, EntityLivingBase var2) {
         if (var1 instanceof EntityPolarBear && !var1.func_70631_g_()) {
            super.func_179446_a(var1, var2);
         }

      }
   }

   static class GroupData implements IEntityLivingData {
      public boolean field_190101_a;

      private GroupData() {
         super();
      }

      // $FF: synthetic method
      GroupData(Object var1) {
         this();
      }
   }
}
