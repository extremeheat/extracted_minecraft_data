package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class PhaseTakeoff extends PhaseBase {
   private boolean field_188697_b;
   private Path field_188698_c;
   private Vec3d field_188699_d;

   public PhaseTakeoff(EntityDragon var1) {
      super(var1);
   }

   public void func_188659_c() {
      if (!this.field_188697_b && this.field_188698_c != null) {
         BlockPos var1 = this.field_188661_a.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.field_186139_a);
         double var2 = this.field_188661_a.func_174831_c(var1);
         if (var2 > 100.0D) {
            this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188741_a);
         }
      } else {
         this.field_188697_b = false;
         this.func_188695_j();
      }

   }

   public void func_188660_d() {
      this.field_188697_b = true;
      this.field_188698_c = null;
      this.field_188699_d = null;
   }

   private void func_188695_j() {
      int var1 = this.field_188661_a.func_184671_o();
      Vec3d var2 = this.field_188661_a.func_184665_a(1.0F);
      int var3 = this.field_188661_a.func_184663_l(-var2.field_72450_a * 40.0D, 105.0D, -var2.field_72449_c * 40.0D);
      if (this.field_188661_a.func_184664_cU() != null && this.field_188661_a.func_184664_cU().func_186092_c() > 0) {
         var3 %= 12;
         if (var3 < 0) {
            var3 += 12;
         }
      } else {
         var3 -= 12;
         var3 &= 7;
         var3 += 12;
      }

      this.field_188698_c = this.field_188661_a.func_184666_a(var1, var3, (PathPoint)null);
      if (this.field_188698_c != null) {
         this.field_188698_c.func_75875_a();
         this.func_188696_k();
      }

   }

   private void func_188696_k() {
      Vec3d var1 = this.field_188698_c.func_186310_f();
      this.field_188698_c.func_75875_a();

      double var2;
      do {
         var2 = var1.field_72448_b + (double)(this.field_188661_a.func_70681_au().nextFloat() * 20.0F);
      } while(var2 < var1.field_72448_b);

      this.field_188699_d = new Vec3d(var1.field_72450_a, var2, var1.field_72449_c);
   }

   @Nullable
   public Vec3d func_188650_g() {
      return this.field_188699_d;
   }

   public PhaseType<PhaseTakeoff> func_188652_i() {
      return PhaseType.field_188745_e;
   }
}
