package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface ItemOwner {
   Level level();

   Vec3 position();

   float getVisualRotationYInDegrees();

   @Nullable
   default LivingEntity asLivingEntity() {
      return null;
   }

   static ItemOwner custom(Vec3 var0, Direction var1, Level var2) {
      return new CustomOwner(var0, Direction.getYRot(var1), var2);
   }

   public static record CustomOwner(Vec3 position, float getVisualRotationYInDegrees, Level level) implements ItemOwner {
      public CustomOwner(Vec3 var1, float var2, Level var3) {
         super();
         this.position = var1;
         this.getVisualRotationYInDegrees = var2;
         this.level = var3;
      }
   }
}
