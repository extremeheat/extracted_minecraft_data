package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
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
      this.func_70661_as().func_75491_a(true);
      this.field_70714_bg.func_75776_a(1, new EntityAIAttackOnCollide(this, 1.0, true));
      this.field_70714_bg.func_75776_a(2, new EntityAIMoveTowardsTarget(this, 0.9, 32.0F));
      this.field_70714_bg.func_75776_a(3, new EntityAIMoveThroughVillage(this, 0.6, true));
      this.field_70714_bg.func_75776_a(4, new EntityAIMoveTowardsRestriction(this, 1.0));
      this.field_70714_bg.func_75776_a(5, new EntityAILookAtVillager(this));
      this.field_70714_bg.func_75776_a(6, new EntityAIWander(this, 0.6));
      this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIDefendVillage(this));
      this.field_70715_bh.func_75776_a(2, new EntityAIHurtByTarget(this, false));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, IMob.field_82192_a));
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, (byte)0);
   }

   @Override
   public boolean func_70650_aV() {
      return true;
   }

   @Override
   protected void func_70629_bd() {
      if (--this.field_70858_e <= 0) {
         this.field_70858_e = 70 + this.field_70146_Z.nextInt(50);
         this.field_70857_d = this.field_70170_p
            .field_72982_D
            .func_75550_a(
               MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v), 32
            );
         if (this.field_70857_d == null) {
            this.func_110177_bN();
         } else {
            ChunkCoordinates var1 = this.field_70857_d.func_75577_a();
            this.func_110171_b(var1.field_71574_a, var1.field_71572_b, var1.field_71573_c, (int)((float)this.field_70857_d.func_75568_b() * 0.6F));
         }
      }

      super.func_70629_bd();
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(100.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25);
   }

   @Override
   protected int func_70682_h(int var1) {
      return var1;
   }

   @Override
   protected void func_82167_n(Entity var1) {
      if (var1 instanceof IMob && this.func_70681_au().nextInt(20) == 0) {
         this.func_70624_b((EntityLivingBase)var1);
      }

      super.func_82167_n(var1);
   }

   @Override
   public void func_70636_d() {
      super.func_70636_d();
      if (this.field_70855_f > 0) {
         --this.field_70855_f;
      }

      if (this.field_70856_g > 0) {
         --this.field_70856_g;
      }

      if (this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 2.500000277905201E-7 && this.field_70146_Z.nextInt(5) == 0) {
         int var1 = MathHelper.func_76128_c(this.field_70165_t);
         int var2 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224 - (double)this.field_70129_M);
         int var3 = MathHelper.func_76128_c(this.field_70161_v);
         Block var4 = this.field_70170_p.func_147439_a(var1, var2, var3);
         if (var4.func_149688_o() != Material.field_151579_a) {
            this.field_70170_p
               .func_72869_a(
                  "blockcrack_" + Block.func_149682_b(var4) + "_" + this.field_70170_p.func_72805_g(var1, var2, var3),
                  this.field_70165_t + ((double)this.field_70146_Z.nextFloat() - 0.5) * (double)this.field_70130_N,
                  this.field_70121_D.field_72338_b + 0.1,
                  this.field_70161_v + ((double)this.field_70146_Z.nextFloat() - 0.5) * (double)this.field_70130_N,
                  4.0 * ((double)this.field_70146_Z.nextFloat() - 0.5),
                  0.5,
                  ((double)this.field_70146_Z.nextFloat() - 0.5) * 4.0
               );
         }
      }
   }

   @Override
   public boolean func_70686_a(Class var1) {
      return this.func_70850_q() && EntityPlayer.class.isAssignableFrom(var1) ? false : super.func_70686_a(var1);
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("PlayerCreated", this.func_70850_q());
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70849_f(var1.func_74767_n("PlayerCreated"));
   }

   @Override
   public boolean func_70652_k(Entity var1) {
      this.field_70855_f = 10;
      this.field_70170_p.func_72960_a(this, (byte)4);
      boolean var2 = var1.func_70097_a(DamageSource.func_76358_a(this), (float)(7 + this.field_70146_Z.nextInt(15)));
      if (var2) {
         var1.field_70181_x += 0.4000000059604645;
      }

      this.func_85030_a("mob.irongolem.throw", 1.0F, 1.0F);
      return var2;
   }

   @Override
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

   @Override
   protected String func_70621_aR() {
      return "mob.irongolem.hit";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.irongolem.death";
   }

   @Override
   protected void func_145780_a(int var1, int var2, int var3, Block var4) {
      this.func_85030_a("mob.irongolem.walk", 1.0F, 1.0F);
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      int var3 = this.field_70146_Z.nextInt(3);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.func_145778_a(Item.func_150898_a(Blocks.field_150328_O), 1, 0.0F);
      }

      int var6 = 3 + this.field_70146_Z.nextInt(3);

      for(int var5 = 0; var5 < var6; ++var5) {
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

   @Override
   public void func_70645_a(DamageSource var1) {
      if (!this.func_70850_q() && this.field_70717_bb != null && this.field_70857_d != null) {
         this.field_70857_d.func_82688_a(this.field_70717_bb.func_70005_c_(), -5);
      }

      super.func_70645_a(var1);
   }
}
