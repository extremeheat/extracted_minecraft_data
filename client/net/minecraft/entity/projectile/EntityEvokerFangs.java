package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityEvokerFangs extends Entity {
   private int field_190553_a;
   private boolean field_190554_b;
   private int field_190555_c;
   private boolean field_190556_d;
   private EntityLivingBase field_190557_e;
   private UUID field_190558_f;

   public EntityEvokerFangs(World var1) {
      super(EntityType.field_200805_s, var1);
      this.field_190555_c = 22;
      this.func_70105_a(0.5F, 0.8F);
   }

   public EntityEvokerFangs(World var1, double var2, double var4, double var6, float var8, int var9, EntityLivingBase var10) {
      this(var1);
      this.field_190553_a = var9;
      this.func_190549_a(var10);
      this.field_70177_z = var8 * 57.295776F;
      this.func_70107_b(var2, var4, var6);
   }

   protected void func_70088_a() {
   }

   public void func_190549_a(@Nullable EntityLivingBase var1) {
      this.field_190557_e = var1;
      this.field_190558_f = var1 == null ? null : var1.func_110124_au();
   }

   @Nullable
   public EntityLivingBase func_190552_j() {
      if (this.field_190557_e == null && this.field_190558_f != null && this.field_70170_p instanceof WorldServer) {
         Entity var1 = ((WorldServer)this.field_70170_p).func_175733_a(this.field_190558_f);
         if (var1 instanceof EntityLivingBase) {
            this.field_190557_e = (EntityLivingBase)var1;
         }
      }

      return this.field_190557_e;
   }

   protected void func_70037_a(NBTTagCompound var1) {
      this.field_190553_a = var1.func_74762_e("Warmup");
      if (var1.func_186855_b("OwnerUUID")) {
         this.field_190558_f = var1.func_186857_a("OwnerUUID");
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
      var1.func_74768_a("Warmup", this.field_190553_a);
      if (this.field_190558_f != null) {
         var1.func_186854_a("OwnerUUID", this.field_190558_f);
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         if (this.field_190556_d) {
            --this.field_190555_c;
            if (this.field_190555_c == 14) {
               for(int var1 = 0; var1 < 12; ++var1) {
                  double var2 = this.field_70165_t + (this.field_70146_Z.nextDouble() * 2.0D - 1.0D) * (double)this.field_70130_N * 0.5D;
                  double var4 = this.field_70163_u + 0.05D + this.field_70146_Z.nextDouble();
                  double var6 = this.field_70161_v + (this.field_70146_Z.nextDouble() * 2.0D - 1.0D) * (double)this.field_70130_N * 0.5D;
                  double var8 = (this.field_70146_Z.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  double var10 = 0.3D + this.field_70146_Z.nextDouble() * 0.3D;
                  double var12 = (this.field_70146_Z.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  this.field_70170_p.func_195594_a(Particles.field_197614_g, var2, var4 + 1.0D, var6, var8, var10, var12);
               }
            }
         }
      } else if (--this.field_190553_a < 0) {
         if (this.field_190553_a == -8) {
            List var14 = this.field_70170_p.func_72872_a(EntityLivingBase.class, this.func_174813_aQ().func_72314_b(0.2D, 0.0D, 0.2D));
            Iterator var15 = var14.iterator();

            while(var15.hasNext()) {
               EntityLivingBase var3 = (EntityLivingBase)var15.next();
               this.func_190551_c(var3);
            }
         }

         if (!this.field_190554_b) {
            this.field_70170_p.func_72960_a(this, (byte)4);
            this.field_190554_b = true;
         }

         if (--this.field_190555_c < 0) {
            this.func_70106_y();
         }
      }

   }

   private void func_190551_c(EntityLivingBase var1) {
      EntityLivingBase var2 = this.func_190552_j();
      if (var1.func_70089_S() && !var1.func_190530_aW() && var1 != var2) {
         if (var2 == null) {
            var1.func_70097_a(DamageSource.field_76376_m, 6.0F);
         } else {
            if (var2.func_184191_r(var1)) {
               return;
            }

            var1.func_70097_a(DamageSource.func_76354_b(this, var2), 6.0F);
         }

      }
   }

   public void func_70103_a(byte var1) {
      super.func_70103_a(var1);
      if (var1 == 4) {
         this.field_190556_d = true;
         if (!this.func_174814_R()) {
            this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_191242_bl, this.func_184176_by(), 1.0F, this.field_70146_Z.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   public float func_190550_a(float var1) {
      if (!this.field_190556_d) {
         return 0.0F;
      } else {
         int var2 = this.field_190555_c - 2;
         return var2 <= 0 ? 1.0F : 1.0F - ((float)var2 - var1) / 20.0F;
      }
   }
}
