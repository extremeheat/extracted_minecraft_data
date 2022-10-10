package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class PhaseHoldingPattern extends PhaseBase {
   private Path field_188677_b;
   private Vec3d field_188678_c;
   private boolean field_188679_d;

   public PhaseHoldingPattern(EntityDragon var1) {
      super(var1);
   }

   public PhaseType<PhaseHoldingPattern> func_188652_i() {
      return PhaseType.field_188741_a;
   }

   public void func_188659_c() {
      double var1 = this.field_188678_c == null ? 0.0D : this.field_188678_c.func_186679_c(this.field_188661_a.field_70165_t, this.field_188661_a.field_70163_u, this.field_188661_a.field_70161_v);
      if (var1 < 100.0D || var1 > 22500.0D || this.field_188661_a.field_70123_F || this.field_188661_a.field_70124_G) {
         this.func_188675_j();
      }

   }

   public void func_188660_d() {
      this.field_188677_b = null;
      this.field_188678_c = null;
   }

   @Nullable
   public Vec3d func_188650_g() {
      return this.field_188678_c;
   }

   private void func_188675_j() {
      int var2;
      if (this.field_188677_b != null && this.field_188677_b.func_75879_b()) {
         BlockPos var1 = this.field_188661_a.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(EndPodiumFeature.field_186139_a));
         var2 = this.field_188661_a.func_184664_cU() == null ? 0 : this.field_188661_a.func_184664_cU().func_186092_c();
         if (this.field_188661_a.func_70681_au().nextInt(var2 + 3) == 0) {
            this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188743_c);
            return;
         }

         double var3 = 64.0D;
         EntityPlayer var5 = this.field_188661_a.field_70170_p.func_184139_a(var1, var3, var3);
         if (var5 != null) {
            var3 = var5.func_174831_c(var1) / 512.0D;
         }

         if (var5 != null && (this.field_188661_a.func_70681_au().nextInt(MathHelper.func_76130_a((int)var3) + 2) == 0 || this.field_188661_a.func_70681_au().nextInt(var2 + 2) == 0)) {
            this.func_188674_a(var5);
            return;
         }
      }

      if (this.field_188677_b == null || this.field_188677_b.func_75879_b()) {
         int var6 = this.field_188661_a.func_184671_o();
         var2 = var6;
         if (this.field_188661_a.func_70681_au().nextInt(8) == 0) {
            this.field_188679_d = !this.field_188679_d;
            var2 = var6 + 6;
         }

         if (this.field_188679_d) {
            ++var2;
         } else {
            --var2;
         }

         if (this.field_188661_a.func_184664_cU() != null && this.field_188661_a.func_184664_cU().func_186092_c() >= 0) {
            var2 %= 12;
            if (var2 < 0) {
               var2 += 12;
            }
         } else {
            var2 -= 12;
            var2 &= 7;
            var2 += 12;
         }

         this.field_188677_b = this.field_188661_a.func_184666_a(var6, var2, (PathPoint)null);
         if (this.field_188677_b != null) {
            this.field_188677_b.func_75875_a();
         }
      }

      this.func_188676_k();
   }

   private void func_188674_a(EntityPlayer var1) {
      this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188742_b);
      ((PhaseStrafePlayer)this.field_188661_a.func_184670_cT().func_188757_b(PhaseType.field_188742_b)).func_188686_a(var1);
   }

   private void func_188676_k() {
      if (this.field_188677_b != null && !this.field_188677_b.func_75879_b()) {
         Vec3d var1 = this.field_188677_b.func_186310_f();
         this.field_188677_b.func_75875_a();
         double var2 = var1.field_72450_a;
         double var4 = var1.field_72449_c;

         double var6;
         do {
            var6 = var1.field_72448_b + (double)(this.field_188661_a.func_70681_au().nextFloat() * 20.0F);
         } while(var6 < var1.field_72448_b);

         this.field_188678_c = new Vec3d(var2, var6, var4);
      }

   }

   public void func_188655_a(EntityEnderCrystal var1, BlockPos var2, DamageSource var3, @Nullable EntityPlayer var4) {
      if (var4 != null && !var4.field_71075_bZ.field_75102_a) {
         this.func_188674_a(var4);
      }

   }
}
