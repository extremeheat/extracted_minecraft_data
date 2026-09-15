package net.minecraft.network.protocol.game;

import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.entity.PositionPath;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class VecDeltaCodec {
   private static final double TRUNCATION_STEPS = 4096.0;
   private Vec3 base;

   public VecDeltaCodec() {
      super();
      this.base = Vec3.ZERO;
   }

   @VisibleForTesting
   static long encode(final double input) {
      return Math.round(input * 4096.0);
   }

   @VisibleForTesting
   static double decode(final long v) {
      return (double)v / 4096.0;
   }

   public static double encodingPrecisionLoss(final double d) {
      return decode(encode(d)) - d;
   }

   public static boolean isDeltaTooBig(final long xa, final long ya, final long za) {
      return xa < -32768L || xa > 32767L || ya < -32768L || ya > 32767L || za < -32768L || za > 32767L;
   }

   public Vec3 decode(final long xa, final long ya, final long za) {
      if (xa == 0L && ya == 0L && za == 0L) {
         return this.base;
      } else {
         double x = xa == 0L ? this.base.x : decode(encode(this.base.x) + xa);
         double y = ya == 0L ? this.base.y : decode(encode(this.base.y) + ya);
         double z = za == 0L ? this.base.z : decode(encode(this.base.z) + za);
         return new Vec3(x, y, z);
      }
   }

   public @Nullable VecDelta tryEncode(final PositionPath position) {
      Objects.requireNonNull(position);
      byte var3 = 0;
      Object var15;
      //$FF: var3->value
      //0->net/minecraft/world/entity/PositionPath$Linear
      //1->net/minecraft/world/entity/PositionPath$Stepped
      switch (position.typeSwitch<invokedynamic>(position, var3)) {
         case 0:
            PositionPath.Linear var4 = (PositionPath.Linear)position;
            PositionPath.Linear var16 = var4;

            try {
               var17 = var16.endPosition();
            } catch (Throwable var11) {
               throw new MatchException(var11.toString(), var11);
            }

            Vec3 pos = var17;
            var15 = this.tryEncode(pos);
            break;
         case 1:
            PositionPath.Stepped pos = (PositionPath.Stepped)position;
            PositionPath.Stepped var10000 = pos;

            try {
               var10000.endPosition();
            } catch (Throwable var10) {
               throw new MatchException(var10.toString(), var10);
            }

            var10000 = pos;

            try {
               var14 = var10000.steps();
            } catch (Throwable var9) {
               throw new MatchException(var9.toString(), var9);
            }

            List steps = var14;
            var15 = VecDelta.Stepped.tryEncode(this, steps);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (VecDelta)var15;
   }

   public @Nullable VecDelta tryEncode(final Vec3 pos) {
      long xa = this.encodeX(pos);
      long ya = this.encodeY(pos);
      long za = this.encodeZ(pos);
      return isDeltaTooBig(xa, ya, za) ? null : new VecDelta.Linear((short)((int)xa), (short)((int)ya), (short)((int)za));
   }

   public long encodeX(final Vec3 pos) {
      return encode(pos.x) - encode(this.base.x);
   }

   public long encodeY(final Vec3 pos) {
      return encode(pos.y) - encode(this.base.y);
   }

   public long encodeZ(final Vec3 pos) {
      return encode(pos.z) - encode(this.base.z);
   }

   public Vec3 delta(final Vec3 pos) {
      return pos.subtract(this.base);
   }

   public void setBase(final Vec3 base) {
      this.base = base;
   }

   public Vec3 getBase() {
      return this.base;
   }
}
