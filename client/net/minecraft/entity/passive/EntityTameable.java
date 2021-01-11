package net.minecraft.entity.passive;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
   protected EntityAISit field_70911_d = new EntityAISit(this);

   public EntityTameable(World var1) {
      super(var1);
      this.func_175544_ck();
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, (byte)0);
      this.field_70180_af.func_75682_a(17, "");
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.func_152113_b() == null) {
         var1.func_74778_a("OwnerUUID", "");
      } else {
         var1.func_74778_a("OwnerUUID", this.func_152113_b());
      }

      var1.func_74757_a("Sitting", this.func_70906_o());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      String var2 = "";
      if (var1.func_150297_b("OwnerUUID", 8)) {
         var2 = var1.func_74779_i("OwnerUUID");
      } else {
         String var3 = var1.func_74779_i("Owner");
         var2 = PreYggdrasilConverter.func_152719_a(var3);
      }

      if (var2.length() > 0) {
         this.func_152115_b(var2);
         this.func_70903_f(true);
      }

      this.field_70911_d.func_75270_a(var1.func_74767_n("Sitting"));
      this.func_70904_g(var1.func_74767_n("Sitting"));
   }

   protected void func_70908_e(boolean var1) {
      EnumParticleTypes var2 = EnumParticleTypes.HEART;
      if (!var1) {
         var2 = EnumParticleTypes.SMOKE_NORMAL;
      }

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var6 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var8 = this.field_70146_Z.nextGaussian() * 0.02D;
         this.field_70170_p.func_175688_a(var2, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var4, var6, var8);
      }

   }

   public void func_70103_a(byte var1) {
      if (var1 == 7) {
         this.func_70908_e(true);
      } else if (var1 == 6) {
         this.func_70908_e(false);
      } else {
         super.func_70103_a(var1);
      }

   }

   public boolean func_70909_n() {
      return (this.field_70180_af.func_75683_a(16) & 4) != 0;
   }

   public void func_70903_f(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)(var2 | 4));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(var2 & -5));
      }

      this.func_175544_ck();
   }

   protected void func_175544_ck() {
   }

   public boolean func_70906_o() {
      return (this.field_70180_af.func_75683_a(16) & 1) != 0;
   }

   public void func_70904_g(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)(var2 | 1));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(var2 & -2));
      }

   }

   public String func_152113_b() {
      return this.field_70180_af.func_75681_e(17);
   }

   public void func_152115_b(String var1) {
      this.field_70180_af.func_75692_b(17, var1);
   }

   public EntityLivingBase func_70902_q() {
      try {
         UUID var1 = UUID.fromString(this.func_152113_b());
         return var1 == null ? null : this.field_70170_p.func_152378_a(var1);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public boolean func_152114_e(EntityLivingBase var1) {
      return var1 == this.func_70902_q();
   }

   public EntityAISit func_70907_r() {
      return this.field_70911_d;
   }

   public boolean func_142018_a(EntityLivingBase var1, EntityLivingBase var2) {
      return true;
   }

   public Team func_96124_cp() {
      if (this.func_70909_n()) {
         EntityLivingBase var1 = this.func_70902_q();
         if (var1 != null) {
            return var1.func_96124_cp();
         }
      }

      return super.func_96124_cp();
   }

   public boolean func_142014_c(EntityLivingBase var1) {
      if (this.func_70909_n()) {
         EntityLivingBase var2 = this.func_70902_q();
         if (var1 == var2) {
            return true;
         }

         if (var2 != null) {
            return var2.func_142014_c(var1);
         }
      }

      return super.func_142014_c(var1);
   }

   public void func_70645_a(DamageSource var1) {
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_82736_K().func_82766_b("showDeathMessages") && this.func_145818_k_() && this.func_70902_q() instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.func_70902_q()).func_145747_a(this.func_110142_aN().func_151521_b());
      }

      super.func_70645_a(var1);
   }

   // $FF: synthetic method
   public Entity func_70902_q() {
      return this.func_70902_q();
   }
}
