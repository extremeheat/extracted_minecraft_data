package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
   public static final UUID field_110179_h = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
   public static final AttributeModifier field_110181_i = new AttributeModifier(field_110179_h, "Fleeing speed bonus", 2.0, 2).func_111168_a(false);
   private PathEntity field_70786_d;
   protected Entity field_70789_a;
   protected boolean field_70787_b;
   protected int field_70788_c;
   private ChunkCoordinates field_70775_bC = new ChunkCoordinates(0, 0, 0);
   private float field_70772_bD = -1.0F;
   private EntityAIBase field_110178_bs = new EntityAIMoveTowardsRestriction(this, 1.0);
   private boolean field_110180_bt;

   public EntityCreature(World var1) {
      super(var1);
   }

   protected boolean func_70780_i() {
      return false;
   }

   @Override
   protected void func_70626_be() {
      this.field_70170_p.field_72984_F.func_76320_a("ai");
      if (this.field_70788_c > 0 && --this.field_70788_c == 0) {
         IAttributeInstance var1 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
         var1.func_111124_b(field_110181_i);
      }

      this.field_70787_b = this.func_70780_i();
      float var21 = 16.0F;
      if (this.field_70789_a == null) {
         this.field_70789_a = this.func_70782_k();
         if (this.field_70789_a != null) {
            this.field_70786_d = this.field_70170_p.func_72865_a(this, this.field_70789_a, var21, true, false, false, true);
         }
      } else if (this.field_70789_a.func_70089_S()) {
         float var2 = this.field_70789_a.func_70032_d(this);
         if (this.func_70685_l(this.field_70789_a)) {
            this.func_70785_a(this.field_70789_a, var2);
         }
      } else {
         this.field_70789_a = null;
      }

      if (this.field_70789_a instanceof EntityPlayerMP && ((EntityPlayerMP)this.field_70789_a).field_71134_c.func_73083_d()) {
         this.field_70789_a = null;
      }

      this.field_70170_p.field_72984_F.func_76319_b();
      if (this.field_70787_b || this.field_70789_a == null || this.field_70786_d != null && this.field_70146_Z.nextInt(20) != 0) {
         if (!this.field_70787_b
            && (this.field_70786_d == null && this.field_70146_Z.nextInt(180) == 0 || this.field_70146_Z.nextInt(120) == 0 || this.field_70788_c > 0)
            && this.field_70708_bq < 100) {
            this.func_70779_j();
         }
      } else {
         this.field_70786_d = this.field_70170_p.func_72865_a(this, this.field_70789_a, var21, true, false, false, true);
      }

      int var22 = MathHelper.func_76128_c(this.field_70121_D.field_72338_b + 0.5);
      boolean var3 = this.func_70090_H();
      boolean var4 = this.func_70058_J();
      this.field_70125_A = 0.0F;
      if (this.field_70786_d != null && this.field_70146_Z.nextInt(100) != 0) {
         this.field_70170_p.field_72984_F.func_76320_a("followpath");
         Vec3 var5 = this.field_70786_d.func_75878_a(this);
         double var6 = (double)(this.field_70130_N * 2.0F);

         while(var5 != null && var5.func_72445_d(this.field_70165_t, var5.field_72448_b, this.field_70161_v) < var6 * var6) {
            this.field_70786_d.func_75875_a();
            if (this.field_70786_d.func_75879_b()) {
               var5 = null;
               this.field_70786_d = null;
            } else {
               var5 = this.field_70786_d.func_75878_a(this);
            }
         }

         this.field_70703_bu = false;
         if (var5 != null) {
            double var8 = var5.field_72450_a - this.field_70165_t;
            double var10 = var5.field_72449_c - this.field_70161_v;
            double var12 = var5.field_72448_b - (double)var22;
            float var14 = (float)(Math.atan2(var10, var8) * 180.0 / 3.1415927410125732) - 90.0F;
            float var15 = MathHelper.func_76142_g(var14 - this.field_70177_z);
            this.field_70701_bs = (float)this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e();
            if (var15 > 30.0F) {
               var15 = 30.0F;
            }

            if (var15 < -30.0F) {
               var15 = -30.0F;
            }

            this.field_70177_z += var15;
            if (this.field_70787_b && this.field_70789_a != null) {
               double var16 = this.field_70789_a.field_70165_t - this.field_70165_t;
               double var18 = this.field_70789_a.field_70161_v - this.field_70161_v;
               float var20 = this.field_70177_z;
               this.field_70177_z = (float)(Math.atan2(var18, var16) * 180.0 / 3.1415927410125732) - 90.0F;
               var15 = (var20 - this.field_70177_z + 90.0F) * 3.1415927F / 180.0F;
               this.field_70702_br = -MathHelper.func_76126_a(var15) * this.field_70701_bs * 1.0F;
               this.field_70701_bs = MathHelper.func_76134_b(var15) * this.field_70701_bs * 1.0F;
            }

            if (var12 > 0.0) {
               this.field_70703_bu = true;
            }
         }

         if (this.field_70789_a != null) {
            this.func_70625_a(this.field_70789_a, 30.0F, 30.0F);
         }

         if (this.field_70123_F && !this.func_70781_l()) {
            this.field_70703_bu = true;
         }

         if (this.field_70146_Z.nextFloat() < 0.8F && (var3 || var4)) {
            this.field_70703_bu = true;
         }

         this.field_70170_p.field_72984_F.func_76319_b();
      } else {
         super.func_70626_be();
         this.field_70786_d = null;
      }
   }

   protected void func_70779_j() {
      this.field_70170_p.field_72984_F.func_76320_a("stroll");
      boolean var1 = false;
      int var2 = -1;
      int var3 = -1;
      int var4 = -1;
      float var5 = -99999.0F;

      for(int var6 = 0; var6 < 10; ++var6) {
         int var7 = MathHelper.func_76128_c(this.field_70165_t + (double)this.field_70146_Z.nextInt(13) - 6.0);
         int var8 = MathHelper.func_76128_c(this.field_70163_u + (double)this.field_70146_Z.nextInt(7) - 3.0);
         int var9 = MathHelper.func_76128_c(this.field_70161_v + (double)this.field_70146_Z.nextInt(13) - 6.0);
         float var10 = this.func_70783_a(var7, var8, var9);
         if (var10 > var5) {
            var5 = var10;
            var2 = var7;
            var3 = var8;
            var4 = var9;
            var1 = true;
         }
      }

      if (var1) {
         this.field_70786_d = this.field_70170_p.func_72844_a(this, var2, var3, var4, 10.0F, true, false, false, true);
      }

      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected void func_70785_a(Entity var1, float var2) {
   }

   public float func_70783_a(int var1, int var2, int var3) {
      return 0.0F;
   }

   protected Entity func_70782_k() {
      return null;
   }

   @Override
   public boolean func_70601_bi() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_70121_D.field_72338_b);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      return super.func_70601_bi() && this.func_70783_a(var1, var2, var3) >= 0.0F;
   }

   public boolean func_70781_l() {
      return this.field_70786_d != null;
   }

   public void func_70778_a(PathEntity var1) {
      this.field_70786_d = var1;
   }

   public Entity func_70777_m() {
      return this.field_70789_a;
   }

   public void func_70784_b(Entity var1) {
      this.field_70789_a = var1;
   }

   public boolean func_110173_bK() {
      return this.func_110176_b(
         MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
      );
   }

   public boolean func_110176_b(int var1, int var2, int var3) {
      if (this.field_70772_bD == -1.0F) {
         return true;
      } else {
         return this.field_70775_bC.func_71569_e(var1, var2, var3) < this.field_70772_bD * this.field_70772_bD;
      }
   }

   public void func_110171_b(int var1, int var2, int var3, int var4) {
      this.field_70775_bC.func_71571_b(var1, var2, var3);
      this.field_70772_bD = (float)var4;
   }

   public ChunkCoordinates func_110172_bL() {
      return this.field_70775_bC;
   }

   public float func_110174_bM() {
      return this.field_70772_bD;
   }

   public void func_110177_bN() {
      this.field_70772_bD = -1.0F;
   }

   public boolean func_110175_bO() {
      return this.field_70772_bD != -1.0F;
   }

   @Override
   protected void func_110159_bB() {
      super.func_110159_bB();
      if (this.func_110167_bD() && this.func_110166_bE() != null && this.func_110166_bE().field_70170_p == this.field_70170_p) {
         Entity var1 = this.func_110166_bE();
         this.func_110171_b((int)var1.field_70165_t, (int)var1.field_70163_u, (int)var1.field_70161_v, 5);
         float var2 = this.func_70032_d(var1);
         if (this instanceof EntityTameable && ((EntityTameable)this).func_70906_o()) {
            if (var2 > 10.0F) {
               this.func_110160_i(true, true);
            }

            return;
         }

         if (!this.field_110180_bt) {
            this.field_70714_bg.func_75776_a(2, this.field_110178_bs);
            this.func_70661_as().func_75491_a(false);
            this.field_110180_bt = true;
         }

         this.func_142017_o(var2);
         if (var2 > 4.0F) {
            this.func_70661_as().func_75497_a(var1, 1.0);
         }

         if (var2 > 6.0F) {
            double var3 = (var1.field_70165_t - this.field_70165_t) / (double)var2;
            double var5 = (var1.field_70163_u - this.field_70163_u) / (double)var2;
            double var7 = (var1.field_70161_v - this.field_70161_v) / (double)var2;
            this.field_70159_w += var3 * Math.abs(var3) * 0.4;
            this.field_70181_x += var5 * Math.abs(var5) * 0.4;
            this.field_70179_y += var7 * Math.abs(var7) * 0.4;
         }

         if (var2 > 10.0F) {
            this.func_110160_i(true, true);
         }
      } else if (!this.func_110167_bD() && this.field_110180_bt) {
         this.field_110180_bt = false;
         this.field_70714_bg.func_85156_a(this.field_110178_bs);
         this.func_70661_as().func_75491_a(true);
         this.func_110177_bN();
      }
   }

   protected void func_142017_o(float var1) {
   }
}
