package net.minecraft.world.entity;

public enum MobSpawnType {
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
   SPAWN_EGG,
   COMMAND,
   DISPENSER,
   PATROL,
   TRIAL_SPAWNER;

   private MobSpawnType() {
   }

   public static boolean isSpawner(MobSpawnType var0) {
      return var0 == SPAWNER || var0 == TRIAL_SPAWNER;
   }

   public static boolean ignoresLightRequirements(MobSpawnType var0) {
      return var0 == TRIAL_SPAWNER;
   }

   // $FF: synthetic method
   private static MobSpawnType[] $values() {
      return new MobSpawnType[]{NATURAL, CHUNK_GENERATION, SPAWNER, STRUCTURE, BREEDING, MOB_SUMMONED, JOCKEY, EVENT, CONVERSION, REINFORCEMENT, TRIGGERED, BUCKET, SPAWN_EGG, COMMAND, DISPENSER, PATROL, TRIAL_SPAWNER};
   }
}
