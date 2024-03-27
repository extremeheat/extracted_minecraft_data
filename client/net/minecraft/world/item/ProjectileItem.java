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

   default ProjectileItem.DispenseConfig createDispenseConfig() {
      return ProjectileItem.DispenseConfig.DEFAULT;
   }

   default void shoot(Projectile var1, double var2, double var4, double var6, float var8, float var9) {
      var1.shoot(var2, var4, var6, var8, var9);
   }

   public static record DispenseConfig(ProjectileItem.PositionFunction b, float c, float d, OptionalInt e) {
      private final ProjectileItem.PositionFunction positionFunction;
      private final float uncertainty;
      private final float power;
      private final OptionalInt overrideDispenseEvent;
      public static final ProjectileItem.DispenseConfig DEFAULT = builder().build();

      public DispenseConfig(ProjectileItem.PositionFunction var1, float var2, float var3, OptionalInt var4) {
         super();
         this.positionFunction = var1;
         this.uncertainty = var2;
         this.power = var3;
         this.overrideDispenseEvent = var4;
      }

      public static ProjectileItem.DispenseConfig.Builder builder() {
         return new ProjectileItem.DispenseConfig.Builder();
      }

      public static class Builder {
         private ProjectileItem.PositionFunction positionFunction = (var0, var1) -> DispenserBlock.getDispensePosition(var0, 0.7, new Vec3(0.0, 0.1, 0.0));
         private float uncertainty = 6.0F;
         private float power = 1.1F;
         private OptionalInt overrideDispenseEvent = OptionalInt.empty();

         public Builder() {
            super();
         }

         public ProjectileItem.DispenseConfig.Builder positionFunction(ProjectileItem.PositionFunction var1) {
            this.positionFunction = var1;
            return this;
         }

         public ProjectileItem.DispenseConfig.Builder uncertainty(float var1) {
            this.uncertainty = var1;
            return this;
         }

         public ProjectileItem.DispenseConfig.Builder power(float var1) {
            this.power = var1;
            return this;
         }

         public ProjectileItem.DispenseConfig.Builder overrideDispenseEvent(int var1) {
            this.overrideDispenseEvent = OptionalInt.of(var1);
            return this;
         }

         public ProjectileItem.DispenseConfig build() {
            return new ProjectileItem.DispenseConfig(this.positionFunction, this.uncertainty, this.power, this.overrideDispenseEvent);
         }
      }
   }

   @FunctionalInterface
   public interface PositionFunction {
      Position getDispensePosition(BlockSource var1, Direction var2);
   }
}
