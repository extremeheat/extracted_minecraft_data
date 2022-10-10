package net.minecraft.entity.boss.dragon.phase;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseStrafePlayer extends PhaseBase {
   private static final Logger field_188689_b = LogManager.getLogger();
   private int field_188690_c;
   private Path field_188691_d;
   private Vec3d field_188692_e;
   private EntityLivingBase field_188693_f;
   private boolean field_188694_g;

   public PhaseStrafePlayer(EntityDragon var1) {
      super(var1);
   }

   public void func_188659_c() {
      if (this.field_188693_f == null) {
         field_188689_b.warn("Skipping player strafe phase because no player was found");
         this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188741_a);
      } else {
         double var1;
         double var3;
         double var9;
         if (this.field_188691_d != null && this.field_188691_d.func_75879_b()) {
            var1 = this.field_188693_f.field_70165_t;
            var3 = this.field_188693_f.field_70161_v;
            double var5 = var1 - this.field_188661_a.field_70165_t;
            double var7 = var3 - this.field_188661_a.field_70161_v;
            var9 = (double)MathHelper.func_76133_a(var5 * var5 + var7 * var7);
            double var11 = Math.min(0.4000000059604645D + var9 / 80.0D - 1.0D, 10.0D);
            this.field_188692_e = new Vec3d(var1, this.field_188693_f.field_70163_u + var11, var3);
         }

         var1 = this.field_188692_e == null ? 0.0D : this.field_188692_e.func_186679_c(this.field_188661_a.field_70165_t, this.field_188661_a.field_70163_u, this.field_188661_a.field_70161_v);
         if (var1 < 100.0D || var1 > 22500.0D) {
            this.func_188687_j();
         }

         var3 = 64.0D;
         if (this.field_188693_f.func_70068_e(this.field_188661_a) < 4096.0D) {
            if (this.field_188661_a.func_70685_l(this.field_188693_f)) {
               ++this.field_188690_c;
               Vec3d var25 = (new Vec3d(this.field_188693_f.field_70165_t - this.field_188661_a.field_70165_t, 0.0D, this.field_188693_f.field_70161_v - this.field_188661_a.field_70161_v)).func_72432_b();
               Vec3d var6 = (new Vec3d((double)MathHelper.func_76126_a(this.field_188661_a.field_70177_z * 0.017453292F), 0.0D, (double)(-MathHelper.func_76134_b(this.field_188661_a.field_70177_z * 0.017453292F)))).func_72432_b();
               float var26 = (float)var6.func_72430_b(var25);
               float var8 = (float)(Math.acos((double)var26) * 57.2957763671875D);
               var8 += 0.5F;
               if (this.field_188690_c >= 5 && var8 >= 0.0F && var8 < 10.0F) {
                  var9 = 1.0D;
                  Vec3d var27 = this.field_188661_a.func_70676_i(1.0F);
                  double var12 = this.field_188661_a.field_70986_h.field_70165_t - var27.field_72450_a * 1.0D;
                  double var14 = this.field_188661_a.field_70986_h.field_70163_u + (double)(this.field_188661_a.field_70986_h.field_70131_O / 2.0F) + 0.5D;
                  double var16 = this.field_188661_a.field_70986_h.field_70161_v - var27.field_72449_c * 1.0D;
                  double var18 = this.field_188693_f.field_70165_t - var12;
                  double var20 = this.field_188693_f.field_70163_u + (double)(this.field_188693_f.field_70131_O / 2.0F) - (var14 + (double)(this.field_188661_a.field_70986_h.field_70131_O / 2.0F));
                  double var22 = this.field_188693_f.field_70161_v - var16;
                  this.field_188661_a.field_70170_p.func_180498_a((EntityPlayer)null, 1017, new BlockPos(this.field_188661_a), 0);
                  EntityDragonFireball var24 = new EntityDragonFireball(this.field_188661_a.field_70170_p, this.field_188661_a, var18, var20, var22);
                  var24.func_70012_b(var12, var14, var16, 0.0F, 0.0F);
                  this.field_188661_a.field_70170_p.func_72838_d(var24);
                  this.field_188690_c = 0;
                  if (this.field_188691_d != null) {
                     while(!this.field_188691_d.func_75879_b()) {
                        this.field_188691_d.func_75875_a();
                     }
                  }

                  this.field_188661_a.func_184670_cT().func_188758_a(PhaseType.field_188741_a);
               }
            } else if (this.field_188690_c > 0) {
               --this.field_188690_c;
            }
         } else if (this.field_188690_c > 0) {
            --this.field_188690_c;
         }

      }
   }

   private void func_188687_j() {
      if (this.field_188691_d == null || this.field_188691_d.func_75879_b()) {
         int var1 = this.field_188661_a.func_184671_o();
         int var2 = var1;
         if (this.field_188661_a.func_70681_au().nextInt(8) == 0) {
            this.field_188694_g = !this.field_188694_g;
            var2 = var1 + 6;
         }

         if (this.field_188694_g) {
            ++var2;
         } else {
            --var2;
         }

         if (this.field_188661_a.func_184664_cU() != null && this.field_188661_a.func_184664_cU().func_186092_c() > 0) {
            var2 %= 12;
            if (var2 < 0) {
               var2 += 12;
            }
         } else {
            var2 -= 12;
            var2 &= 7;
            var2 += 12;
         }

         this.field_188691_d = this.field_188661_a.func_184666_a(var1, var2, (PathPoint)null);
         if (this.field_188691_d != null) {
            this.field_188691_d.func_75875_a();
         }
      }

      this.func_188688_k();
   }

   private void func_188688_k() {
      if (this.field_188691_d != null && !this.field_188691_d.func_75879_b()) {
         Vec3d var1 = this.field_188691_d.func_186310_f();
         this.field_188691_d.func_75875_a();
         double var2 = var1.field_72450_a;
         double var6 = var1.field_72449_c;

         double var4;
         do {
            var4 = var1.field_72448_b + (double)(this.field_188661_a.func_70681_au().nextFloat() * 20.0F);
         } while(var4 < var1.field_72448_b);

         this.field_188692_e = new Vec3d(var2, var4, var6);
      }

   }

   public void func_188660_d() {
      this.field_188690_c = 0;
      this.field_188692_e = null;
      this.field_188691_d = null;
      this.field_188693_f = null;
   }

   public void func_188686_a(EntityLivingBase var1) {
      this.field_188693_f = var1;
      int var2 = this.field_188661_a.func_184671_o();
      int var3 = this.field_188661_a.func_184663_l(this.field_188693_f.field_70165_t, this.field_188693_f.field_70163_u, this.field_188693_f.field_70161_v);
      int var4 = MathHelper.func_76128_c(this.field_188693_f.field_70165_t);
      int var5 = MathHelper.func_76128_c(this.field_188693_f.field_70161_v);
      double var6 = (double)var4 - this.field_188661_a.field_70165_t;
      double var8 = (double)var5 - this.field_188661_a.field_70161_v;
      double var10 = (double)MathHelper.func_76133_a(var6 * var6 + var8 * var8);
      double var12 = Math.min(0.4000000059604645D + var10 / 80.0D - 1.0D, 10.0D);
      int var14 = MathHelper.func_76128_c(this.field_188693_f.field_70163_u + var12);
      PathPoint var15 = new PathPoint(var4, var14, var5);
      this.field_188691_d = this.field_188661_a.func_184666_a(var2, var3, var15);
      if (this.field_188691_d != null) {
         this.field_188691_d.func_75875_a();
         this.func_188688_k();
      }

   }

   @Nullable
   public Vec3d func_188650_g() {
      return this.field_188692_e;
   }

   public PhaseType<PhaseStrafePlayer> func_188652_i() {
      return PhaseType.field_188742_b;
   }
}
