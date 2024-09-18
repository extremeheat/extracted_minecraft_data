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

   public static boolean isSpawner(EntitySpawnReason var0) {
      return var0 == SPAWNER || var0 == TRIAL_SPAWNER;
   }

   public static boolean ignoresLightRequirements(EntitySpawnReason var0) {
      return var0 == TRIAL_SPAWNER;
   }
}
