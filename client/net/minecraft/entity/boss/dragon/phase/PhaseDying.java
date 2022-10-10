package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Particles;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class PhaseDying extends PhaseBase {
   private Vec3d field_188672_b;
   private int field_188673_c;

   public PhaseDying(EntityDragon var1) {
      super(var1);
   }

   public void func_188657_b() {
      if (this.field_188673_c++ % 10 == 0) {
         float var1 = (this.field_188661_a.func_70681_au().nextFloat() - 0.5F) * 8.0F;
         float var2 = (this.field_188661_a.func_70681_au().nextFloat() - 0.5F) * 4.0F;
         float var3 = (this.field_188661_a.func_70681_au().nextFloat() - 0.5F) * 8.0F;
         this.field_188661_a.field_70170_p.func_195594_a(Particles.field_197626_s, this.field_188661_a.field_70165_t + (double)var1, this.field_188661_a.field_70163_u + 2.0D + (double)var2, this.field_188661_a.field_70161_v + (double)var3, 0.0D, 0.0D, 0.0D);
      }

   }

   public void func_188659_c() {
      ++this.field_188673_c;
      if (this.field_188672_b == null) {
         BlockPos var1 = this.field_188661_a.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.field_186139_a);
         this.field_188672_b = new Vec3d((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p());
      }

      double var3 = this.field_188672_b.func_186679_c(this.field_188661_a.field_70165_t, this.field_188661_a.field_70163_u, this.field_188661_a.field_70161_v);
      if (var3 >= 100.0D && var3 <= 22500.0D && !this.field_188661_a.field_70123_F && !this.field_188661_a.field_70124_G) {
         this.field_188661_a.func_70606_j(1.0F);
      } else {
         this.field_188661_a.func_70606_j(0.0F);
      }

   }

   public void func_188660_d() {
      this.field_188672_b = null;
      this.field_188673_c = 0;
   }

   public float func_188651_f() {
      return 3.0F;
   }

   @Nullable
   public Vec3d func_188650_g() {
      return this.field_188672_b;
   }

   public PhaseType<PhaseDying> func_188652_i() {
      return PhaseType.field_188750_j;
   }
}
