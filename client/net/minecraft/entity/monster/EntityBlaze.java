package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityBlaze extends EntityMob {
   private float field_70847_d = 0.5F;
   private int field_70848_e;
   private static final DataParameter<Byte> field_184712_c;

   public EntityBlaze(World var1) {
      super(EntityType.field_200792_f, var1);
      this.func_184644_a(PathNodeType.WATER, -1.0F);
      this.func_184644_a(PathNodeType.LAVA, 8.0F);
      this.func_184644_a(PathNodeType.DANGER_FIRE, 0.0F);
      this.func_184644_a(PathNodeType.DAMAGE_FIRE, 0.0F);
      this.field_70178_ae = true;
      this.field_70728_aV = 10;
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(4, new EntityBlaze.AIFireballAttack(this));
      this.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.field_70714_bg.func_75776_a(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(6.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.23000000417232513D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(48.0D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184712_c, (byte)0);
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187594_A;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187603_D;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187600_C;
   }

   public int func_70070_b() {
      return 15728880;
   }

   public float func_70013_c() {
      return 1.0F;
   }

   public void func_70636_d() {
      if (!this.field_70122_E && this.field_70181_x < 0.0D) {
         this.field_70181_x *= 0.6D;
      }

      if (this.field_70170_p.field_72995_K) {
         if (this.field_70146_Z.nextInt(24) == 0 && !this.func_174814_R()) {
            this.field_70170_p.func_184134_a(this.field_70165_t + 0.5D, this.field_70163_u + 0.5D, this.field_70161_v + 0.5D, SoundEvents.field_187597_B, this.func_184176_by(), 1.0F + this.field_70146_Z.nextFloat(), this.field_70146_Z.nextFloat() * 0.7F + 0.3F, false);
         }

         for(int var1 = 0; var1 < 2; ++var1) {
            this.field_70170_p.func_195594_a(Particles.field_197594_E, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, 0.0D, 0.0D, 0.0D);
         }
      }

      super.func_70636_d();
   }

   protected void func_70619_bc() {
      if (this.func_203008_ap()) {
         this.func_70097_a(DamageSource.field_76369_e, 1.0F);
      }

      --this.field_70848_e;
      if (this.field_70848_e <= 0) {
         this.field_70848_e = 100;
         this.field_70847_d = 0.5F + (float)this.field_70146_Z.nextGaussian() * 3.0F;
      }

      EntityLivingBase var1 = this.func_70638_az();
      if (var1 != null && var1.field_70163_u + (double)var1.func_70047_e() > this.field_70163_u + (double)this.func_70047_e() + (double)this.field_70847_d) {
         this.field_70181_x += (0.30000001192092896D - this.field_70181_x) * 0.30000001192092896D;
         this.field_70160_al = true;
      }

      super.func_70619_bc();
   }

   public void func_180430_e(float var1, float var2) {
   }

   public boolean func_70027_ad() {
      return this.func_70845_n();
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186433_o;
   }

   public boolean func_70845_n() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184712_c) & 1) != 0;
   }

   public void func_70844_e(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184712_c);
      if (var1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 &= -2;
      }

      this.field_70180_af.func_187227_b(field_184712_c, var2);
   }

   protected boolean func_70814_o() {
      return true;
   }

   static {
      field_184712_c = EntityDataManager.func_187226_a(EntityBlaze.class, DataSerializers.field_187191_a);
   }

   static class AIFireballAttack extends EntityAIBase {
      private final EntityBlaze field_179469_a;
      private int field_179467_b;
      private int field_179468_c;

      public AIFireballAttack(EntityBlaze var1) {
         super();
         this.field_179469_a = var1;
         this.func_75248_a(3);
      }

      public boolean func_75250_a() {
         EntityLivingBase var1 = this.field_179469_a.func_70638_az();
         return var1 != null && var1.func_70089_S();
      }

      public void func_75249_e() {
         this.field_179467_b = 0;
      }

      public void func_75251_c() {
         this.field_179469_a.func_70844_e(false);
      }

      public void func_75246_d() {
         --this.field_179468_c;
         EntityLivingBase var1 = this.field_179469_a.func_70638_az();
         double var2 = this.field_179469_a.func_70068_e(var1);
         if (var2 < 4.0D) {
            if (this.field_179468_c <= 0) {
               this.field_179468_c = 20;
               this.field_179469_a.func_70652_k(var1);
            }

            this.field_179469_a.func_70605_aq().func_75642_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, 1.0D);
         } else if (var2 < this.func_191523_f() * this.func_191523_f()) {
            double var4 = var1.field_70165_t - this.field_179469_a.field_70165_t;
            double var6 = var1.func_174813_aQ().field_72338_b + (double)(var1.field_70131_O / 2.0F) - (this.field_179469_a.field_70163_u + (double)(this.field_179469_a.field_70131_O / 2.0F));
            double var8 = var1.field_70161_v - this.field_179469_a.field_70161_v;
            if (this.field_179468_c <= 0) {
               ++this.field_179467_b;
               if (this.field_179467_b == 1) {
                  this.field_179468_c = 60;
                  this.field_179469_a.func_70844_e(true);
               } else if (this.field_179467_b <= 4) {
                  this.field_179468_c = 6;
               } else {
                  this.field_179468_c = 100;
                  this.field_179467_b = 0;
                  this.field_179469_a.func_70844_e(false);
               }

               if (this.field_179467_b > 1) {
                  float var10 = MathHelper.func_76129_c(MathHelper.func_76133_a(var2)) * 0.5F;
                  this.field_179469_a.field_70170_p.func_180498_a((EntityPlayer)null, 1018, new BlockPos((int)this.field_179469_a.field_70165_t, (int)this.field_179469_a.field_70163_u, (int)this.field_179469_a.field_70161_v), 0);

                  for(int var11 = 0; var11 < 1; ++var11) {
                     EntitySmallFireball var12 = new EntitySmallFireball(this.field_179469_a.field_70170_p, this.field_179469_a, var4 + this.field_179469_a.func_70681_au().nextGaussian() * (double)var10, var6, var8 + this.field_179469_a.func_70681_au().nextGaussian() * (double)var10);
                     var12.field_70163_u = this.field_179469_a.field_70163_u + (double)(this.field_179469_a.field_70131_O / 2.0F) + 0.5D;
                     this.field_179469_a.field_70170_p.func_72838_d(var12);
                  }
               }
            }

            this.field_179469_a.func_70671_ap().func_75651_a(var1, 10.0F, 10.0F);
         } else {
            this.field_179469_a.func_70661_as().func_75499_g();
            this.field_179469_a.func_70605_aq().func_75642_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, 1.0D);
         }

         super.func_75246_d();
      }

      private double func_191523_f() {
         IAttributeInstance var1 = this.field_179469_a.func_110148_a(SharedMonsterAttributes.field_111265_b);
         return var1 == null ? 16.0D : var1.func_111126_e();
      }
   }
}
