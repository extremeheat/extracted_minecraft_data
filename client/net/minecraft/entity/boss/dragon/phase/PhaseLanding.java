package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Particles;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class PhaseLanding extends PhaseBase {
   private Vec3d field_188685_b;

   public PhaseLanding(EntityDragon var1) {
      super(var1);
   }

   public void func_188657_b() {
      Vec3d var1 = this.field_188661_a.func_184665_a(1.0F).func_72432_b();
      var1.func_178785_b(-0.7853982F);
      double var2 = this.field_188661_a.field_70986_h.field_70165_t;
      double var4 = this.field_188661_a.field_70986_h.field_70163_u + (double)(this.field_188661_a.field_70986_h.field_70131_O / 2.0F);
      double var6 = this.field_188661_a.field_70986_h.field_70161_v;

      for(int var8 = 0; var8 < 8; ++var8) {
         double var9 = var2 + this.field_188661_a.func_70681_au().nextGaussian() / 2.0D;
         double var11 = var4 + this.field_188661_a.func_70681_au().nextGaussian() / 2.0D;
         double var13 = var6 + this.field_188661_a.func_70681_au().nextGaussian() / 2.0D;
         this.field_188661_a.field_70170_p.func_195594_a(Particles.field_197616_i, var9, var11, var13, -var1.field_72450_a * 0.07999999821186066D + this.field_188661_a.field_70159_w, -var1.field_72448_b * 0.30000001192092896D + this.field_188661_a.field_70181_x, -var1.field_72449_c * 0.07999999821186066D + this.field_188661_a.field_70179_y);
         var1.func_178785_b(0.19634955F);
      }

   }

   public void func_188659_c() {
      if (this.field_188685_b == null) {
         this.field_188685_b = new Vec3d(this.field_188661_a.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.field_186139_a));
      }

      if (this.field_188685_b.func_186679_c(this.field_188661_a.field_70165_t, this.field_188661_a.field_70163_u, this.field_188661_a.field_70161_v) < 1.0D) {
         ((PhaseSittingFlaming)this.field_188661_a.func_184670_cT().func_188757_b(PhaseType.field_188746_f)).func_188663_j();
         this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188747_g);
      }

   }

   public float func_188651_f() {
      return 1.5F;
   }

   public float func_188653_h() {
      float var1 = MathHelper.func_76133_a(this.field_188661_a.field_70159_w * this.field_188661_a.field_70159_w + this.field_188661_a.field_70179_y * this.field_188661_a.field_70179_y) + 1.0F;
      float var2 = Math.min(var1, 40.0F);
      return var2 / var1;
   }

   public void func_188660_d() {
      this.field_188685_b = null;
   }

   @Nullable
   public Vec3d func_188650_g() {
      return this.field_188685_b;
   }

   public PhaseType<PhaseLanding> func_188652_i() {
      return PhaseType.field_188744_d;
   }
}
