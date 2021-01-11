package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityIronGolem extends EntityGolem {
   private int field_70858_e;
   Village field_70857_d;
   private int field_70855_f;
   private int field_70856_g;

   public EntityIronGolem(World var1) {
      super(var1);
      this.func_70105_a(1.4F, 2.9F);
      ((PathNavigateGround)this.func_70661_as()).func_179690_a(true);
      this.field_70714_bg.func_75776_a(1, new EntityAIAttackOnCollide(this, 1.0D, true));
      this.field_70714_bg.func_75776_a(2, new EntityAIMoveTowardsTarget(this, 0.9D, 32.0F));
      this.field_70714_bg.func_75776_a(3, new EntityAIMoveThroughVillage(this, 0.6D, true));
      this.field_70714_bg.func_75776_a(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.field_70714_bg.func_75776_a(5, new EntityAILookAtVillager(this));
      this.field_70714_bg.func_75776_a(6, new EntityAIWander(this, 0.6D));
      this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIDefendVillage(this));
      this.field_70715_bh.func_75776_a(2, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(3, new EntityIronGolem.AINearestAttackableTargetNonCreeper(this, EntityLiving.class, 10, false, true, IMob.field_175450_e));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, (byte)0);
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
         Block var5 = var4.func_177230_c();
         if (var5.func_149688_o() != Material.field_151579_a) {
            this.field_70170_p.func_175688_a(EnumParticleTypes.BLOCK_CRACK, this.field_70165_t + ((double)this.field_70146_Z.nextFloat() - 0.5D) * (double)this.field_70130_N, this.func_174813_aQ().field_72338_b + 0.1D, this.field_70161_v + ((double)this.field_70146_Z.nextFloat() - 0.5D) * (double)this.field_70130_N, 4.0D * ((double)this.field_70146_Z.nextFloat() - 0.5D), 0.5D, ((double)this.field_70146_Z.nextFloat() - 0.5D) * 4.0D, Block.func_176210_f(var4));
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

      this.func_85030_a("mob.irongolem.throw", 1.0F, 1.0F);
      return var2;
   }

   public void func_70103_a(byte var1) {
      if (var1 == 4) {
         this.field_70855_f = 10;
         this.func_85030_a("mob.irongolem.throw", 1.0F, 1.0F);
      } else if (var1 == 11) {
         this.field_70856_g = 400;
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
      this.field_70856_g = var1 ? 400 : 0;
      this.field_70170_p.func_72960_a(this, (byte)11);
   }

   protected String func_70621_aR() {
      return "mob.irongolem.hit";
   }

   protected String func_70673_aS() {
      return "mob.irongolem.death";
   }

   protected void func_180429_a(BlockPos var1, Block var2) {
      this.func_85030_a("mob.irongolem.walk", 1.0F, 1.0F);
   }

   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(3);

      int var4;
      for(var4 = 0; var4 < var3; ++var4) {
         this.func_145778_a(Item.func_150898_a(Blocks.field_150328_O), 1, (float)BlockFlower.EnumFlowerType.POPPY.func_176968_b());
      }

      var4 = 3 + this.field_70146_Z.nextInt(3);

      for(int var5 = 0; var5 < var4; ++var5) {
         this.func_145779_a(Items.field_151042_j, 1);
      }

   }

   public int func_70853_p() {
      return this.field_70856_g;
   }

   public boolean func_70850_q() {
      return (this.field_70180_af.func_75683_a(16) & 1) != 0;
   }

   public void func_70849_f(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)(var2 | 1));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(var2 & -2));
      }

   }

   public void func_70645_a(DamageSource var1) {
      if (!this.func_70850_q() && this.field_70717_bb != null && this.field_70857_d != null) {
         this.field_70857_d.func_82688_a(this.field_70717_bb.func_70005_c_(), -5);
      }

      super.func_70645_a(var1);
   }

   static class AINearestAttackableTargetNonCreeper<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
      public AINearestAttackableTargetNonCreeper(final EntityCreature var1, Class<T> var2, int var3, boolean var4, boolean var5, final Predicate<? super T> var6) {
         super(var1, var2, var3, var4, var5, var6);
         this.field_82643_g = new Predicate<T>() {
            public boolean apply(T var1x) {
               if (var6 != null && !var6.apply(var1x)) {
                  return false;
               } else if (var1x instanceof EntityCreeper) {
                  return false;
               } else {
                  if (var1x instanceof EntityPlayer) {
                     double var2 = AINearestAttackableTargetNonCreeper.this.func_111175_f();
                     if (var1x.func_70093_af()) {
                        var2 *= 0.800000011920929D;
                     }

                     if (var1x.func_82150_aj()) {
                        float var4 = ((EntityPlayer)var1x).func_82243_bO();
                        if (var4 < 0.1F) {
                           var4 = 0.1F;
                        }

                        var2 *= (double)(0.7F * var4);
                     }

                     if ((double)var1x.func_70032_d(var1) > var2) {
                        return false;
                     }
                  }

                  return AINearestAttackableTargetNonCreeper.this.func_75296_a(var1x, false);
               }
            }

            // $FF: synthetic method
            public boolean apply(Object var1x) {
               return this.apply((EntityLivingBase)var1x);
            }
         };
      }
   }
}
