package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public record MovementPredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z, MinMaxBounds.Doubles speed, MinMaxBounds.Doubles horizontalSpeed, MinMaxBounds.Doubles verticalSpeed, MinMaxBounds.Doubles fallDistance) {
   public static final Codec<MovementPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(MinMaxBounds.Doubles.CODEC.optionalFieldOf("x", MinMaxBounds.Doubles.ANY).forGetter(MovementPredicate::x), MinMaxBounds.Doubles.CODEC.optionalFieldOf("y", MinMaxBounds.Doubles.ANY).forGetter(MovementPredicate::y), MinMaxBounds.Doubles.CODEC.optionalFieldOf("z", MinMaxBounds.Doubles.ANY).forGetter(MovementPredicate::z), MinMaxBounds.Doubles.CODEC.optionalFieldOf("speed", MinMaxBounds.Doubles.ANY).forGetter(MovementPredicate::speed), MinMaxBounds.Doubles.CODEC.optionalFieldOf("horizontal_speed", MinMaxBounds.Doubles.ANY).forGetter(MovementPredicate::horizontalSpeed), MinMaxBounds.Doubles.CODEC.optionalFieldOf("vertical_speed", MinMaxBounds.Doubles.ANY).forGetter(MovementPredicate::verticalSpeed), MinMaxBounds.Doubles.CODEC.optionalFieldOf("fall_distance", MinMaxBounds.Doubles.ANY).forGetter(MovementPredicate::fallDistance)).apply(var0, MovementPredicate::new);
   });

   public MovementPredicate(MinMaxBounds.Doubles x, MinMaxBounds.Doubles y, MinMaxBounds.Doubles z, MinMaxBounds.Doubles speed, MinMaxBounds.Doubles horizontalSpeed, MinMaxBounds.Doubles verticalSpeed, MinMaxBounds.Doubles fallDistance) {
      super();
      this.x = x;
      this.y = y;
      this.z = z;
      this.speed = speed;
      this.horizontalSpeed = horizontalSpeed;
      this.verticalSpeed = verticalSpeed;
      this.fallDistance = fallDistance;
   }

   public static MovementPredicate speed(MinMaxBounds.Doubles var0) {
      return new MovementPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
   }

   public static MovementPredicate horizontalSpeed(MinMaxBounds.Doubles var0) {
      return new MovementPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY);
   }

   public static MovementPredicate verticalSpeed(MinMaxBounds.Doubles var0) {
      return new MovementPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0, MinMaxBounds.Doubles.ANY);
   }

   public static MovementPredicate fallDistance(MinMaxBounds.Doubles var0) {
      return new MovementPredicate(MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, MinMaxBounds.Doubles.ANY, var0);
   }

   public boolean matches(double var1, double var3, double var5, double var7) {
      if (this.x.matches(var1) && this.y.matches(var3) && this.z.matches(var5)) {
         double var9 = Mth.lengthSquared(var1, var3, var5);
         if (!this.speed.matchesSqr(var9)) {
            return false;
         } else {
            double var11 = Mth.lengthSquared(var1, var5);
            if (!this.horizontalSpeed.matchesSqr(var11)) {
               return false;
            } else {
               double var13 = Math.abs(var3);
               if (!this.verticalSpeed.matches(var13)) {
                  return false;
               } else {
                  return this.fallDistance.matches(var7);
               }
            }
         }
      } else {
         return false;
      }
   }

   public MinMaxBounds.Doubles x() {
      return this.x;
   }

   public MinMaxBounds.Doubles y() {
      return this.y;
   }

   public MinMaxBounds.Doubles z() {
      return this.z;
   }

   public MinMaxBounds.Doubles speed() {
      return this.speed;
   }

   public MinMaxBounds.Doubles horizontalSpeed() {
      return this.horizontalSpeed;
   }

   public MinMaxBounds.Doubles verticalSpeed() {
      return this.verticalSpeed;
   }

   public MinMaxBounds.Doubles fallDistance() {
      return this.fallDistance;
   }
}
