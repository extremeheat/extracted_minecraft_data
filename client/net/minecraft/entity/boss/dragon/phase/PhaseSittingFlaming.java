package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PhaseSittingFlaming extends PhaseSittingBase {
   private int field_188664_b;
   private int field_188665_c;
   private EntityAreaEffectCloud field_188666_d;

   public PhaseSittingFlaming(EntityDragon var1) {
      super(var1);
   }

   public void func_188657_b() {
      ++this.field_188664_b;
      if (this.field_188664_b % 2 == 0 && this.field_188664_b < 10) {
         Vec3d var1 = this.field_188661_a.func_184665_a(1.0F).func_72432_b();
         var1.func_178785_b(-0.7853982F);
         double var2 = this.field_188661_a.field_70986_h.field_70165_t;
         double var4 = this.field_188661_a.field_70986_h.field_70163_u + (double)(this.field_188661_a.field_70986_h.field_70131_O / 2.0F);
         double var6 = this.field_188661_a.field_70986_h.field_70161_v;

         for(int var8 = 0; var8 < 8; ++var8) {
            double var9 = var2 + this.field_188661_a.func_70681_au().nextGaussian() / 2.0D;
            double var11 = var4 + this.field_188661_a.func_70681_au().nextGaussian() / 2.0D;
            double var13 = var6 + this.field_188661_a.func_70681_au().nextGaussian() / 2.0D;

            for(int var15 = 0; var15 < 6; ++var15) {
               this.field_188661_a.field_70170_p.func_195594_a(Particles.field_197616_i, var9, var11, var13, -var1.field_72450_a * 0.07999999821186066D * (double)var15, -var1.field_72448_b * 0.6000000238418579D, -var1.field_72449_c * 0.07999999821186066D * (double)var15);
            }

            var1.func_178785_b(0.19634955F);
         }
      }

   }

   public void func_188659_c() {
      ++this.field_188664_b;
      if (this.field_188664_b >= 200) {
         if (this.field_188665_c >= 4) {
            this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188745_e);
         } else {
            this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188747_g);
         }
      } else if (this.field_188664_b == 10) {
         Vec3d var1 = (new Vec3d(this.field_188661_a.field_70986_h.field_70165_t - this.field_188661_a.field_70165_t, 0.0D, this.field_188661_a.field_70986_h.field_70161_v - this.field_188661_a.field_70161_v)).func_72432_b();
         float var2 = 5.0F;
         double var3 = this.field_188661_a.field_70986_h.field_70165_t + var1.field_72450_a * 5.0D / 2.0D;
         double var5 = this.field_188661_a.field_70986_h.field_70161_v + var1.field_72449_c * 5.0D / 2.0D;
         double var7 = this.field_188661_a.field_70986_h.field_70163_u + (double)(this.field_188661_a.field_70986_h.field_70131_O / 2.0F);
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(MathHelper.func_76128_c(var3), MathHelper.func_76128_c(var7), MathHelper.func_76128_c(var5));

         while(this.field_188661_a.field_70170_p.func_175623_d(var9)) {
            --var7;
            var9.func_181079_c(MathHelper.func_76128_c(var3), MathHelper.func_76128_c(var7), MathHelper.func_76128_c(var5));
         }

         var7 = (double)(MathHelper.func_76128_c(var7) + 1);
         this.field_188666_d = new EntityAreaEffectCloud(this.field_188661_a.field_70170_p, var3, var7, var5);
         this.field_188666_d.func_184481_a(this.field_188661_a);
         this.field_188666_d.func_184483_a(5.0F);
         this.field_188666_d.func_184486_b(200);
         this.field_188666_d.func_195059_a(Particles.field_197616_i);
         this.field_188666_d.func_184496_a(new PotionEffect(MobEffects.field_76433_i));
         this.field_188661_a.field_70170_p.func_72838_d(this.field_188666_d);
      }

   }

   public void func_188660_d() {
      this.field_188664_b = 0;
      ++this.field_188665_c;
   }

   public void func_188658_e() {
      if (this.field_188666_d != null) {
         this.field_188666_d.func_70106_y();
         this.field_188666_d = null;
      }

   }

   public PhaseType<PhaseSittingFlaming> func_188652_i() {
      return PhaseType.field_188746_f;
   }

   public void func_188663_j() {
      this.field_188665_c = 0;
   }
}
