package net.minecraft.network.protocol.game;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.VisibleForTesting;

public class VecDeltaCodec {
   private static final double TRUNCATION_STEPS = 4096.0;
   private Vec3 base;

   public VecDeltaCodec() {
      super();
      this.base = Vec3.ZERO;
   }

   @VisibleForTesting
   static long encode(double var0) {
      return Math.round(var0 * 4096.0);
   }

   @VisibleForTesting
   static double decode(long var0) {
      return (double)var0 / 4096.0;
   }

   public Vec3 decode(long var1, long var3, long var5) {
      if (var1 == 0L && var3 == 0L && var5 == 0L) {
         return this.base;
      } else {
         double var7 = var1 == 0L ? this.base.x : decode(encode(this.base.x) + var1);
         double var9 = var3 == 0L ? this.base.y : decode(encode(this.base.y) + var3);
         double var11 = var5 == 0L ? this.base.z : decode(encode(this.base.z) + var5);
         return new Vec3(var7, var9, var11);
      }
   }

   public long encodeX(Vec3 var1) {
      return encode(var1.x) - encode(this.base.x);
   }

   public long encodeY(Vec3 var1) {
      return encode(var1.y) - encode(this.base.y);
   }

   public long encodeZ(Vec3 var1) {
      return encode(var1.z) - encode(this.base.z);
   }

   public Vec3 delta(Vec3 var1) {
      return var1.subtract(this.base);
   }

   public void setBase(Vec3 var1) {
      this.base = var1;
   }

   public Vec3 getBase() {
      return this.base;
   }
}
