package net.minecraft.world.entity;

public enum EntitySpawnReason {
   NATURAL,
   CHUNK_GENERATION,
   SPAWNER,
   STRUCTURE,
   BREEDING,
   MOB_SUMMONED,
   JOCKEY,
   EVENT,
   CONVERSION,
   REINFORCEMENT,
   TRIGGERED,
   BUCKET,
   SPAWN_ITEM_USE,
   COMMAND,
   DISPENSER,
   PATROL,
   TRIAL_SPAWNER,
   LOAD,
   DIMENSION_TRAVEL;

   private EntitySpawnReason() {
   }

   public static boolean isSpawner(final EntitySpawnReason reason) {
      return reason == SPAWNER || reason == TRIAL_SPAWNER;
   }

   public static boolean ignoresLightRequirements(final EntitySpawnReason reason) {
      return reason == TRIAL_SPAWNER;
   }

   // $FF: synthetic method
   private static EntitySpawnReason[] $values() {
      return new EntitySpawnReason[]{NATURAL, CHUNK_GENERATION, SPAWNER, STRUCTURE, BREEDING, MOB_SUMMONED, JOCKEY, EVENT, CONVERSION, REINFORCEMENT, TRIGGERED, BUCKET, SPAWN_ITEM_USE, COMMAND, DISPENSER, PATROL, TRIAL_SPAWNER, LOAD, DIMENSION_TRAVEL};
   }
}
