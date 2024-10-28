package net.minecraft.world.item;

import java.util.OptionalInt;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public interface ProjectileItem {
   Projectile asProjectile(Level var1, Position var2, ItemStack var3, Direction var4);

   default DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.DEFAULT;
   }

   default void shoot(Projectile var1, double var2, double var4, double var6, float var8, float var9) {
      var1.shoot(var2, var4, var6, var8, var9);
   }

   public static record DispenseConfig(PositionFunction positionFunction, float uncertainty, float power, OptionalInt overrideDispenseEvent) {
      public static final DispenseConfig DEFAULT = builder().build();

      public DispenseConfig(PositionFunction positionFunction, float uncertainty, float power, OptionalInt overrideDispenseEvent) {
         super();
         this.positionFunction = positionFunction;
         this.uncertainty = uncertainty;
         this.power = power;
         this.overrideDispenseEvent = overrideDispenseEvent;
      }

      public static Builder builder() {
         return new Builder();
      }

      public PositionFunction positionFunction() {
         return this.positionFunction;
      }

      public float uncertainty() {
         return this.uncertainty;
      }

      public float power() {
         return this.power;
      }

      public OptionalInt overrideDispenseEvent() {
         return this.overrideDispenseEvent;
      }

      public static class Builder {
         private PositionFunction positionFunction = (var0, var1) -> {
            return DispenserBlock.getDispensePosition(var0, 0.7, new Vec3(0.0, 0.1, 0.0));
         };
         private float uncertainty = 6.0F;
         private float power = 1.1F;
         private OptionalInt overrideDispenseEvent = OptionalInt.empty();

         public Builder() {
            super();
         }

         public Builder positionFunction(PositionFunction var1) {
            this.positionFunction = var1;
            return this;
         }

         public Builder uncertainty(float var1) {
            this.uncertainty = var1;
            return this;
         }

         public Builder power(float var1) {
            this.power = var1;
            return this;
         }

         public Builder overrideDispenseEvent(int var1) {
            this.overrideDispenseEvent = OptionalInt.of(var1);
            return this;
         }

         public DispenseConfig build() {
            return new DispenseConfig(this.positionFunction, this.uncertainty, this.power, this.overrideDispenseEvent);
         }
      }
   }

   @FunctionalInterface
   public interface PositionFunction {
      Position getDispensePosition(BlockSource var1, Direction var2);
   }
}
