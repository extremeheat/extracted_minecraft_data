package net.minecraft.world.entity;

import java.util.List;
import net.minecraft.world.phys.Vec3;

public enum EntityAttachment {
   PASSENGER(EntityAttachment.Fallback.AT_HEIGHT),
   VEHICLE(EntityAttachment.Fallback.AT_FEET),
   NAME_TAG(EntityAttachment.Fallback.AT_HEIGHT),
   WARDEN_CHEST(EntityAttachment.Fallback.AT_CENTER);

   private final Fallback fallback;

   private EntityAttachment(final Fallback fallback) {
      this.fallback = fallback;
   }

   public List<Vec3> createFallbackPoints(final float width, final float height) {
      return this.fallback.create(width, height);
   }

   // $FF: synthetic method
   private static EntityAttachment[] $values() {
      return new EntityAttachment[]{PASSENGER, VEHICLE, NAME_TAG, WARDEN_CHEST};
   }

   public interface Fallback {
      List<Vec3> ZERO = List.of(Vec3.ZERO);
      Fallback AT_FEET = (width, height) -> ZERO;
      Fallback AT_HEIGHT = (width, height) -> List.of(new Vec3(0.0, (double)height, 0.0));
      Fallback AT_CENTER = (width, height) -> List.of(new Vec3(0.0, (double)height / 2.0, 0.0));

      List<Vec3> create(float width, float height);
   }
}
