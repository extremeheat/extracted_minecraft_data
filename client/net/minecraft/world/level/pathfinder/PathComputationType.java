package net.minecraft.world.level.pathfinder;

public enum PathComputationType {
   LAND,
   WATER,
   AIR;

   private PathComputationType() {
   }

   // $FF: synthetic method
   private static PathComputationType[] $values() {
      return new PathComputationType[]{LAND, WATER, AIR};
   }
}
