package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class PhaseLandingApproach extends PhaseBase {
   private Path field_188683_b;
   private Vec3d field_188684_c;

   public PhaseLandingApproach(EntityDragon var1) {
      super(var1);
   }

   public PhaseType<PhaseLandingApproach> func_188652_i() {
      return PhaseType.field_188743_c;
   }

   public void func_188660_d() {
      this.field_188683_b = null;
      this.field_188684_c = null;
   }

   public void func_188659_c() {
      double var1 = this.field_188684_c == null ? 0.0D : this.field_188684_c.func_186679_c(this.field_188661_a.field_70165_t, this.field_188661_a.field_70163_u, this.field_188661_a.field_70161_v);
      if (var1 < 100.0D || var1 > 22500.0D || this.field_188661_a.field_70123_F || this.field_188661_a.field_70124_G) {
         this.func_188681_j();
      }

   }

   @Nullable
   public Vec3d func_188650_g() {
      return this.field_188684_c;
   }

   private void func_188681_j() {
      if (this.field_188683_b == null || this.field_188683_b.func_75879_b()) {
         int var1 = this.field_188661_a.func_184671_o();
         BlockPos var2 = this.field_188661_a.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.field_186139_a);
         EntityPlayer var3 = this.field_188661_a.field_70170_p.func_184139_a(var2, 128.0D, 128.0D);
         int var4;
         if (var3 != null) {
            Vec3d var5 = (new Vec3d(var3.field_70165_t, 0.0D, var3.field_70161_v)).func_72432_b();
            var4 = this.field_188661_a.func_184663_l(-var5.field_72450_a * 40.0D, 105.0D, -var5.field_72449_c * 40.0D);
         } else {
            var4 = this.field_188661_a.func_184663_l(40.0D, (double)var2.func_177956_o(), 0.0D);
         }

         PathPoint var6 = new PathPoint(var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p());
         this.field_188683_b = this.field_188661_a.func_184666_a(var1, var4, var6);
         if (this.field_188683_b != null) {
            this.field_188683_b.func_75875_a();
         }
      }

      this.func_188682_k();
      if (this.field_188683_b != null && this.field_188683_b.func_75879_b()) {
         this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188744_d);
      }

   }

   private void func_188682_k() {
      if (this.field_188683_b != null && !this.field_188683_b.func_75879_b()) {
         Vec3d var1 = this.field_188683_b.func_186310_f();
         this.field_188683_b.func_75875_a();
         double var2 = var1.field_72450_a;
         double var4 = var1.field_72449_c;

         double var6;
         do {
            var6 = var1.field_72448_b + (double)(this.field_188661_a.func_70681_au().nextFloat() * 20.0F);
         } while(var6 < var1.field_72448_b);

         this.field_188684_c = new Vec3d(var2, var6, var4);
      }

   }
}
