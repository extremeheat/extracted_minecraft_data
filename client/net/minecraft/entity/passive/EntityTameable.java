package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
   protected static final DataParameter<Byte> field_184755_bv;
   protected static final DataParameter<Optional<UUID>> field_184756_bw;
   protected EntityAISit field_70911_d;

   protected EntityTameable(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.func_175544_ck();
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184755_bv, (byte)0);
      this.field_70180_af.func_187214_a(field_184756_bw, Optional.empty());
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.func_184753_b() == null) {
         var1.func_74778_a("OwnerUUID", "");
      } else {
         var1.func_74778_a("OwnerUUID", this.func_184753_b().toString());
      }

      var1.func_74757_a("Sitting", this.func_70906_o());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      String var2;
      if (var1.func_150297_b("OwnerUUID", 8)) {
         var2 = var1.func_74779_i("OwnerUUID");
      } else {
         String var3 = var1.func_74779_i("Owner");
         var2 = PreYggdrasilConverter.func_187473_a(this.func_184102_h(), var3);
      }

      if (!var2.isEmpty()) {
         try {
            this.func_184754_b(UUID.fromString(var2));
            this.func_70903_f(true);
         } catch (Throwable var4) {
            this.func_70903_f(false);
         }
      }

      if (this.field_70911_d != null) {
         this.field_70911_d.func_75270_a(var1.func_74767_n("Sitting"));
      }

      this.func_70904_g(var1.func_74767_n("Sitting"));
   }

   public boolean func_184652_a(EntityPlayer var1) {
      return !this.func_110167_bD();
   }

   protected void func_70908_e(boolean var1) {
      BasicParticleType var2 = Particles.field_197633_z;
      if (!var1) {
         var2 = Particles.field_197601_L;
      }

      for(int var3 = 0; var3 < 7; ++var3) {
         double var4 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var6 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var8 = this.field_70146_Z.nextGaussian() * 0.02D;
         this.field_70170_p.func_195594_a(var2, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var4, var6, var8);
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
      return ((Byte)this.field_70180_af.func_187225_a(field_184755_bv) & 4) != 0;
   }

   public void func_70903_f(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184755_bv);
      if (var1) {
         this.field_70180_af.func_187227_b(field_184755_bv, (byte)(var2 | 4));
      } else {
         this.field_70180_af.func_187227_b(field_184755_bv, (byte)(var2 & -5));
      }

      this.func_175544_ck();
   }

   protected void func_175544_ck() {
   }

   public boolean func_70906_o() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184755_bv) & 1) != 0;
   }

   public void func_70904_g(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184755_bv);
      if (var1) {
         this.field_70180_af.func_187227_b(field_184755_bv, (byte)(var2 | 1));
      } else {
         this.field_70180_af.func_187227_b(field_184755_bv, (byte)(var2 & -2));
      }

   }

   @Nullable
   public UUID func_184753_b() {
      return (UUID)((Optional)this.field_70180_af.func_187225_a(field_184756_bw)).orElse((Object)null);
   }

   public void func_184754_b(@Nullable UUID var1) {
      this.field_70180_af.func_187227_b(field_184756_bw, Optional.ofNullable(var1));
   }

   public void func_193101_c(EntityPlayer var1) {
      this.func_70903_f(true);
      this.func_184754_b(var1.func_110124_au());
      if (var1 instanceof EntityPlayerMP) {
         CriteriaTriggers.field_193136_w.func_193178_a((EntityPlayerMP)var1, this);
      }

   }

   @Nullable
   public EntityLivingBase func_70902_q() {
      try {
         UUID var1 = this.func_184753_b();
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

   public boolean func_184191_r(Entity var1) {
      if (this.func_70909_n()) {
         EntityLivingBase var2 = this.func_70902_q();
         if (var1 == var2) {
            return true;
         }

         if (var2 != null) {
            return var2.func_184191_r(var1);
         }
      }

      return super.func_184191_r(var1);
   }

   public void func_70645_a(DamageSource var1) {
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_82736_K().func_82766_b("showDeathMessages") && this.func_70902_q() instanceof EntityPlayerMP) {
         this.func_70902_q().func_145747_a(this.func_110142_aN().func_151521_b());
      }

      super.func_70645_a(var1);
   }

   // $FF: synthetic method
   @Nullable
   public Entity func_70902_q() {
      return this.func_70902_q();
   }

   static {
      field_184755_bv = EntityDataManager.func_187226_a(EntityTameable.class, DataSerializers.field_187191_a);
      field_184756_bw = EntityDataManager.func_187226_a(EntityTameable.class, DataSerializers.field_187203_m);
   }
}
