package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class EntitySlime extends EntityLiving implements IMob {
   public float field_70813_a;
   public float field_70811_b;
   public float field_70812_c;
   private boolean field_175452_bi;

   public EntitySlime(World var1) {
      super(var1);
      this.field_70765_h = new EntitySlime.SlimeMoveHelper(this);
      this.field_70714_bg.func_75776_a(1, new EntitySlime.AISlimeFloat(this));
      this.field_70714_bg.func_75776_a(2, new EntitySlime.AISlimeAttack(this));
      this.field_70714_bg.func_75776_a(3, new EntitySlime.AISlimeFaceRandom(this));
      this.field_70714_bg.func_75776_a(5, new EntitySlime.AISlimeHop(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIFindEntityNearestPlayer(this));
      this.field_70715_bh.func_75776_a(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, (byte)1);
   }

   protected void func_70799_a(int var1) {
      this.field_70180_af.func_75692_b(16, (byte)var1);
      this.func_70105_a(0.51000005F * (float)var1, 0.51000005F * (float)var1);
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)(var1 * var1));
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a((double)(0.2F + 0.1F * (float)var1));
      this.func_70606_j(this.func_110138_aP());
      this.field_70728_aV = var1;
   }

   public int func_70809_q() {
      return this.field_70180_af.func_75683_a(16);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Size", this.func_70809_q() - 1);
      var1.func_74757_a("wasOnGround", this.field_175452_bi);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      int var2 = var1.func_74762_e("Size");
      if (var2 < 0) {
         var2 = 0;
      }

      this.func_70799_a(var2 + 1);
      this.field_175452_bi = var1.func_74767_n("wasOnGround");
   }

   protected EnumParticleTypes func_180487_n() {
      return EnumParticleTypes.SLIME;
   }

   protected String func_70803_o() {
      return "mob.slime." + (this.func_70809_q() > 1 ? "big" : "small");
   }

   public void func_70071_h_() {
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL && this.func_70809_q() > 0) {
         this.field_70128_L = true;
      }

      this.field_70811_b += (this.field_70813_a - this.field_70811_b) * 0.5F;
      this.field_70812_c = this.field_70811_b;
      super.func_70071_h_();
      if (this.field_70122_E && !this.field_175452_bi) {
         int var1 = this.func_70809_q();

         for(int var2 = 0; var2 < var1 * 8; ++var2) {
            float var3 = this.field_70146_Z.nextFloat() * 3.1415927F * 2.0F;
            float var4 = this.field_70146_Z.nextFloat() * 0.5F + 0.5F;
            float var5 = MathHelper.func_76126_a(var3) * (float)var1 * 0.5F * var4;
            float var6 = MathHelper.func_76134_b(var3) * (float)var1 * 0.5F * var4;
            World var10000 = this.field_70170_p;
            EnumParticleTypes var10001 = this.func_180487_n();
            double var10002 = this.field_70165_t + (double)var5;
            double var10004 = this.field_70161_v + (double)var6;
            var10000.func_175688_a(var10001, var10002, this.func_174813_aQ().field_72338_b, var10004, 0.0D, 0.0D, 0.0D);
         }

         if (this.func_70804_p()) {
            this.func_85030_a(this.func_70803_o(), this.func_70599_aP(), ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         }

         this.field_70813_a = -0.5F;
      } else if (!this.field_70122_E && this.field_175452_bi) {
         this.field_70813_a = 1.0F;
      }

      this.field_175452_bi = this.field_70122_E;
      this.func_70808_l();
   }

   protected void func_70808_l() {
      this.field_70813_a *= 0.6F;
   }

   protected int func_70806_k() {
      return this.field_70146_Z.nextInt(20) + 10;
   }

   protected EntitySlime func_70802_j() {
      return new EntitySlime(this.field_70170_p);
   }

   public void func_145781_i(int var1) {
      if (var1 == 16) {
         int var2 = this.func_70809_q();
         this.func_70105_a(0.51000005F * (float)var2, 0.51000005F * (float)var2);
         this.field_70177_z = this.field_70759_as;
         this.field_70761_aq = this.field_70759_as;
         if (this.func_70090_H() && this.field_70146_Z.nextInt(20) == 0) {
            this.func_71061_d_();
         }
      }

      super.func_145781_i(var1);
   }

   public void func_70106_y() {
      int var1 = this.func_70809_q();
      if (!this.field_70170_p.field_72995_K && var1 > 1 && this.func_110143_aJ() <= 0.0F) {
         int var2 = 2 + this.field_70146_Z.nextInt(3);

         for(int var3 = 0; var3 < var2; ++var3) {
            float var4 = ((float)(var3 % 2) - 0.5F) * (float)var1 / 4.0F;
            float var5 = ((float)(var3 / 2) - 0.5F) * (float)var1 / 4.0F;
            EntitySlime var6 = this.func_70802_j();
            if (this.func_145818_k_()) {
               var6.func_96094_a(this.func_95999_t());
            }

            if (this.func_104002_bU()) {
               var6.func_110163_bv();
            }

            var6.func_70799_a(var1 / 2);
            var6.func_70012_b(this.field_70165_t + (double)var4, this.field_70163_u + 0.5D, this.field_70161_v + (double)var5, this.field_70146_Z.nextFloat() * 360.0F, 0.0F);
            this.field_70170_p.func_72838_d(var6);
         }
      }

      super.func_70106_y();
   }

   public void func_70108_f(Entity var1) {
      super.func_70108_f(var1);
      if (var1 instanceof EntityIronGolem && this.func_70800_m()) {
         this.func_175451_e((EntityLivingBase)var1);
      }

   }

   public void func_70100_b_(EntityPlayer var1) {
      if (this.func_70800_m()) {
         this.func_175451_e(var1);
      }

   }

   protected void func_175451_e(EntityLivingBase var1) {
      int var2 = this.func_70809_q();
      if (this.func_70685_l(var1) && this.func_70068_e(var1) < 0.6D * (double)var2 * 0.6D * (double)var2 && var1.func_70097_a(DamageSource.func_76358_a(this), (float)this.func_70805_n())) {
         this.func_85030_a("mob.attack", 1.0F, (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         this.func_174815_a(this, var1);
      }

   }

   public float func_70047_e() {
      return 0.625F * this.field_70131_O;
   }

   protected boolean func_70800_m() {
      return this.func_70809_q() > 1;
   }

   protected int func_70805_n() {
      return this.func_70809_q();
   }

   protected String func_70621_aR() {
      return "mob.slime." + (this.func_70809_q() > 1 ? "big" : "small");
   }

   protected String func_70673_aS() {
      return "mob.slime." + (this.func_70809_q() > 1 ? "big" : "small");
   }

   protected Item func_146068_u() {
      return this.func_70809_q() == 1 ? Items.field_151123_aH : null;
   }

   public boolean func_70601_bi() {
      BlockPos var1 = new BlockPos(MathHelper.func_76128_c(this.field_70165_t), 0, MathHelper.func_76128_c(this.field_70161_v));
      Chunk var2 = this.field_70170_p.func_175726_f(var1);
      if (this.field_70170_p.func_72912_H().func_76067_t() == WorldType.field_77138_c && this.field_70146_Z.nextInt(4) != 1) {
         return false;
      } else {
         if (this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL) {
            BiomeGenBase var3 = this.field_70170_p.func_180494_b(var1);
            if (var3 == BiomeGenBase.field_76780_h && this.field_70163_u > 50.0D && this.field_70163_u < 70.0D && this.field_70146_Z.nextFloat() < 0.5F && this.field_70146_Z.nextFloat() < this.field_70170_p.func_130001_d() && this.field_70170_p.func_175671_l(new BlockPos(this)) <= this.field_70146_Z.nextInt(8)) {
               return super.func_70601_bi();
            }

            if (this.field_70146_Z.nextInt(10) == 0 && var2.func_76617_a(987234911L).nextInt(10) == 0 && this.field_70163_u < 40.0D) {
               return super.func_70601_bi();
            }
         }

         return false;
      }
   }

   protected float func_70599_aP() {
      return 0.4F * (float)this.func_70809_q();
   }

   public int func_70646_bf() {
      return 0;
   }

   protected boolean func_70807_r() {
      return this.func_70809_q() > 0;
   }

   protected boolean func_70804_p() {
      return this.func_70809_q() > 2;
   }

   protected void func_70664_aZ() {
      this.field_70181_x = 0.41999998688697815D;
      this.field_70160_al = true;
   }

   public IEntityLivingData func_180482_a(DifficultyInstance var1, IEntityLivingData var2) {
      int var3 = this.field_70146_Z.nextInt(3);
      if (var3 < 2 && this.field_70146_Z.nextFloat() < 0.5F * var1.func_180170_c()) {
         ++var3;
      }

      int var4 = 1 << var3;
      this.func_70799_a(var4);
      return super.func_180482_a(var1, var2);
   }

   static class AISlimeHop extends EntityAIBase {
      private EntitySlime field_179458_a;

      public AISlimeHop(EntitySlime var1) {
         super();
         this.field_179458_a = var1;
         this.func_75248_a(5);
      }

      public boolean func_75250_a() {
         return true;
      }

      public void func_75246_d() {
         ((EntitySlime.SlimeMoveHelper)this.field_179458_a.func_70605_aq()).func_179921_a(1.0D);
      }
   }

   static class AISlimeFloat extends EntityAIBase {
      private EntitySlime field_179457_a;

      public AISlimeFloat(EntitySlime var1) {
         super();
         this.field_179457_a = var1;
         this.func_75248_a(5);
         ((PathNavigateGround)var1.func_70661_as()).func_179693_d(true);
      }

      public boolean func_75250_a() {
         return this.field_179457_a.func_70090_H() || this.field_179457_a.func_180799_ab();
      }

      public void func_75246_d() {
         if (this.field_179457_a.func_70681_au().nextFloat() < 0.8F) {
            this.field_179457_a.func_70683_ar().func_75660_a();
         }

         ((EntitySlime.SlimeMoveHelper)this.field_179457_a.func_70605_aq()).func_179921_a(1.2D);
      }
   }

   static class AISlimeFaceRandom extends EntityAIBase {
      private EntitySlime field_179461_a;
      private float field_179459_b;
      private int field_179460_c;

      public AISlimeFaceRandom(EntitySlime var1) {
         super();
         this.field_179461_a = var1;
         this.func_75248_a(2);
      }

      public boolean func_75250_a() {
         return this.field_179461_a.func_70638_az() == null && (this.field_179461_a.field_70122_E || this.field_179461_a.func_70090_H() || this.field_179461_a.func_180799_ab());
      }

      public void func_75246_d() {
         if (--this.field_179460_c <= 0) {
            this.field_179460_c = 40 + this.field_179461_a.func_70681_au().nextInt(60);
            this.field_179459_b = (float)this.field_179461_a.func_70681_au().nextInt(360);
         }

         ((EntitySlime.SlimeMoveHelper)this.field_179461_a.func_70605_aq()).func_179920_a(this.field_179459_b, false);
      }
   }

   static class AISlimeAttack extends EntityAIBase {
      private EntitySlime field_179466_a;
      private int field_179465_b;

      public AISlimeAttack(EntitySlime var1) {
         super();
         this.field_179466_a = var1;
         this.func_75248_a(2);
      }

      public boolean func_75250_a() {
         EntityLivingBase var1 = this.field_179466_a.func_70638_az();
         if (var1 == null) {
            return false;
         } else if (!var1.func_70089_S()) {
            return false;
         } else {
            return !(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).field_71075_bZ.field_75102_a;
         }
      }

      public void func_75249_e() {
         this.field_179465_b = 300;
         super.func_75249_e();
      }

      public boolean func_75253_b() {
         EntityLivingBase var1 = this.field_179466_a.func_70638_az();
         if (var1 == null) {
            return false;
         } else if (!var1.func_70089_S()) {
            return false;
         } else if (var1 instanceof EntityPlayer && ((EntityPlayer)var1).field_71075_bZ.field_75102_a) {
            return false;
         } else {
            return --this.field_179465_b > 0;
         }
      }

      public void func_75246_d() {
         this.field_179466_a.func_70625_a(this.field_179466_a.func_70638_az(), 10.0F, 10.0F);
         ((EntitySlime.SlimeMoveHelper)this.field_179466_a.func_70605_aq()).func_179920_a(this.field_179466_a.field_70177_z, this.field_179466_a.func_70800_m());
      }
   }

   static class SlimeMoveHelper extends EntityMoveHelper {
      private float field_179922_g;
      private int field_179924_h;
      private EntitySlime field_179925_i;
      private boolean field_179923_j;

      public SlimeMoveHelper(EntitySlime var1) {
         super(var1);
         this.field_179925_i = var1;
      }

      public void func_179920_a(float var1, boolean var2) {
         this.field_179922_g = var1;
         this.field_179923_j = var2;
      }

      public void func_179921_a(double var1) {
         this.field_75645_e = var1;
         this.field_75643_f = true;
      }

      public void func_75641_c() {
         this.field_75648_a.field_70177_z = this.func_75639_a(this.field_75648_a.field_70177_z, this.field_179922_g, 30.0F);
         this.field_75648_a.field_70759_as = this.field_75648_a.field_70177_z;
         this.field_75648_a.field_70761_aq = this.field_75648_a.field_70177_z;
         if (!this.field_75643_f) {
            this.field_75648_a.func_70657_f(0.0F);
         } else {
            this.field_75643_f = false;
            if (this.field_75648_a.field_70122_E) {
               this.field_75648_a.func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
               if (this.field_179924_h-- <= 0) {
                  this.field_179924_h = this.field_179925_i.func_70806_k();
                  if (this.field_179923_j) {
                     this.field_179924_h /= 3;
                  }

                  this.field_179925_i.func_70683_ar().func_75660_a();
                  if (this.field_179925_i.func_70807_r()) {
                     this.field_179925_i.func_85030_a(this.field_179925_i.func_70803_o(), this.field_179925_i.func_70599_aP(), ((this.field_179925_i.func_70681_au().nextFloat() - this.field_179925_i.func_70681_au().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                  }
               } else {
                  this.field_179925_i.field_70702_br = this.field_179925_i.field_70701_bs = 0.0F;
                  this.field_75648_a.func_70659_e(0.0F);
               }
            } else {
               this.field_75648_a.func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
            }

         }
      }
   }
}
