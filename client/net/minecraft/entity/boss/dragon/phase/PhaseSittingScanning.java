package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PhaseSittingScanning extends PhaseSittingBase {
   private int field_188667_b;

   public PhaseSittingScanning(EntityDragon var1) {
      super(var1);
   }

   public void func_188659_c() {
      ++this.field_188667_b;
      EntityPlayer var1 = this.field_188661_a.field_70170_p.func_184142_a(this.field_188661_a, 20.0D, 10.0D);
      if (var1 != null) {
         if (this.field_188667_b > 25) {
            this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188748_h);
         } else {
            Vec3d var2 = (new Vec3d(var1.field_70165_t - this.field_188661_a.field_70165_t, 0.0D, var1.field_70161_v - this.field_188661_a.field_70161_v)).func_72432_b();
            Vec3d var3 = (new Vec3d((double)MathHelper.func_76126_a(this.field_188661_a.field_70177_z * 0.017453292F), 0.0D, (double)(-MathHelper.func_76134_b(this.field_188661_a.field_70177_z * 0.017453292F)))).func_72432_b();
            float var4 = (float)var3.func_72430_b(var2);
            float var5 = (float)(Math.acos((double)var4) * 57.2957763671875D) + 0.5F;
            if (var5 < 0.0F || var5 > 10.0F) {
               double var6 = var1.field_70165_t - this.field_188661_a.field_70986_h.field_70165_t;
               double var8 = var1.field_70161_v - this.field_188661_a.field_70986_h.field_70161_v;
               double var10 = MathHelper.func_151237_a(MathHelper.func_76138_g(180.0D - MathHelper.func_181159_b(var6, var8) * 57.2957763671875D - (double)this.field_188661_a.field_70177_z), -100.0D, 100.0D);
               EntityDragon var10000 = this.field_188661_a;
               var10000.field_70704_bt *= 0.8F;
               float var12 = MathHelper.func_76133_a(var6 * var6 + var8 * var8) + 1.0F;
               float var13 = var12;
               if (var12 > 40.0F) {
                  var12 = 40.0F;
               }

               var10000 = this.field_188661_a;
               var10000.field_70704_bt = (float)((double)var10000.field_70704_bt + var10 * (double)(0.7F / var12 / var13));
               var10000 = this.field_188661_a;
               var10000.field_70177_z += this.field_188661_a.field_70704_bt;
            }
         }
      } else if (this.field_188667_b >= 100) {
         var1 = this.field_188661_a.field_70170_p.func_184142_a(this.field_188661_a, 150.0D, 150.0D);
         this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188745_e);
         if (var1 != null) {
            this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188749_i);
            ((PhaseChargingPlayer)this.field_188661_a.func_184670_cT().func_188757_b(PhaseType.field_188749_i)).func_188668_a(new Vec3d(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v));
         }
      }

   }

   public void func_188660_d() {
      this.field_188667_b = 0;
   }

   public PhaseType<PhaseSittingScanning> func_188652_i() {
      return PhaseType.field_188747_g;
   }
}
