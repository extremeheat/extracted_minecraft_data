package net.minecraft.world.entity;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface ItemOwner {
   Level level();

   Vec3 position();

   float getVisualRotationYInDegrees();

   default @Nullable LivingEntity asLivingEntity() {
      return null;
   }

   static ItemOwner offsetFromOwner(final ItemOwner owner, final Vec3 offset) {
      return new OffsetFromOwner(owner, offset);
   }

   public static record OffsetFromOwner(ItemOwner owner, Vec3 offset) implements ItemOwner {
      public OffsetFromOwner {
         super();
      }

      public Level level() {
         return this.owner.level();
      }

      public Vec3 position() {
         return this.owner.position().add(this.offset);
      }

      public float getVisualRotationYInDegrees() {
         return this.owner.getVisualRotationYInDegrees();
      }

      public @Nullable LivingEntity asLivingEntity() {
         return this.owner.asLivingEntity();
      }
   }
}
