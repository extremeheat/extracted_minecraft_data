package net.minecraft.world.entity;

import java.util.List;
import net.minecraft.world.phys.Vec3;

public enum EntityAttachment {
   PASSENGER(EntityAttachment.Fallback.AT_HEIGHT),
   VEHICLE(EntityAttachment.Fallback.AT_FEET),
   NAME_TAG(EntityAttachment.Fallback.AT_HEIGHT),
   WARDEN_CHEST(EntityAttachment.Fallback.AT_CENTER);

   private final EntityAttachment.Fallback fallback;

   private EntityAttachment(EntityAttachment.Fallback var3) {
      this.fallback = var3;
   }

   public List<Vec3> createFallbackPoints(float var1, float var2) {
      return this.fallback.create(var1, var2);
   }

   public interface Fallback {
      List<Vec3> ZERO = List.of(Vec3.ZERO);
      EntityAttachment.Fallback AT_FEET = (var0, var1) -> ZERO;
      EntityAttachment.Fallback AT_HEIGHT = (var0, var1) -> List.of(new Vec3(0.0, (double)var1, 0.0));
      EntityAttachment.Fallback AT_CENTER = (var0, var1) -> List.of(new Vec3(0.0, (double)var1 / 2.0, 0.0));

      List<Vec3> create(float var1, float var2);
   }
}
