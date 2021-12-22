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
   PATROL;

   private MobSpawnType() {
   }

   // $FF: synthetic method
   private static MobSpawnType[] $values() {
      return new MobSpawnType[]{NATURAL, CHUNK_GENERATION, SPAWNER, STRUCTURE, BREEDING, MOB_SUMMONED, JOCKEY, EVENT, CONVERSION, REINFORCEMENT, TRIGGERED, BUCKET, SPAWN_EGG, COMMAND, DISPENSER, PATROL};
   }
}
