package net.minecraft.world.level.pathfinder;

public enum PathType {
   BLOCKED(-1.0F),
   OPEN(0.0F),
   WALKABLE(0.0F),
   WALKABLE_DOOR(0.0F),
   TRAPDOOR(0.0F),
   POWDER_SNOW(-1.0F),
   ON_TOP_OF_POWDER_SNOW(0.0F),
   FENCE(-1.0F),
   LAVA(-1.0F),
   WATER(8.0F),
   WATER_BORDER(8.0F),
   RAIL(0.0F),
   UNPASSABLE_RAIL(-1.0F),
   FIRE_IN_NEIGHBOR(8.0F),
   FIRE(16.0F),
   DAMAGING_IN_NEIGHBOR(8.0F),
   DAMAGING(-1.0F),
   DOOR_OPEN(0.0F),
   DOOR_WOOD_CLOSED(-1.0F),
   DOOR_IRON_CLOSED(-1.0F),
   BREACH(4.0F),
   LEAVES(-1.0F),
   STICKY_HONEY(8.0F),
   COCOA(0.0F),
   DAMAGE_CAUTIOUS(0.0F),
   ON_TOP_OF_TRAPDOOR(0.0F),
   BIG_MOBS_CLOSE_TO_DANGER(4.0F);

   private final float malus;

   private PathType(final float defaultCost) {
      this.malus = defaultCost;
   }

   public float getMalus() {
      return this.malus;
   }

   // $FF: synthetic method
   private static PathType[] $values() {
      return new PathType[]{BLOCKED, OPEN, WALKABLE, WALKABLE_DOOR, TRAPDOOR, POWDER_SNOW, ON_TOP_OF_POWDER_SNOW, FENCE, LAVA, WATER, WATER_BORDER, RAIL, UNPASSABLE_RAIL, FIRE_IN_NEIGHBOR, FIRE, DAMAGING_IN_NEIGHBOR, DAMAGING, DOOR_OPEN, DOOR_WOOD_CLOSED, DOOR_IRON_CLOSED, BREACH, LEAVES, STICKY_HONEY, COCOA, DAMAGE_CAUTIOUS, ON_TOP_OF_TRAPDOOR, BIG_MOBS_CLOSE_TO_DANGER};
   }
}
