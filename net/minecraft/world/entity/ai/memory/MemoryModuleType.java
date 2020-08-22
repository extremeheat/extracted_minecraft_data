package net.minecraft.world.entity.ai.memory;

import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SerializableLong;
import net.minecraft.resources.ResourceLocation;

public class MemoryModuleType {
   public static final MemoryModuleType DUMMY = register("dummy");
   public static final MemoryModuleType HOME = register("home", Optional.of(GlobalPos::of));
   public static final MemoryModuleType JOB_SITE = register("job_site", Optional.of(GlobalPos::of));
   public static final MemoryModuleType MEETING_POINT = register("meeting_point", Optional.of(GlobalPos::of));
   public static final MemoryModuleType SECONDARY_JOB_SITE = register("secondary_job_site");
   public static final MemoryModuleType LIVING_ENTITIES = register("mobs");
   public static final MemoryModuleType VISIBLE_LIVING_ENTITIES = register("visible_mobs");
   public static final MemoryModuleType VISIBLE_VILLAGER_BABIES = register("visible_villager_babies");
   public static final MemoryModuleType NEAREST_PLAYERS = register("nearest_players");
   public static final MemoryModuleType NEAREST_VISIBLE_PLAYER = register("nearest_visible_player");
   public static final MemoryModuleType WALK_TARGET = register("walk_target");
   public static final MemoryModuleType LOOK_TARGET = register("look_target");
   public static final MemoryModuleType INTERACTION_TARGET = register("interaction_target");
   public static final MemoryModuleType BREED_TARGET = register("breed_target");
   public static final MemoryModuleType PATH = register("path");
   public static final MemoryModuleType INTERACTABLE_DOORS = register("interactable_doors");
   public static final MemoryModuleType OPENED_DOORS = register("opened_doors");
   public static final MemoryModuleType NEAREST_BED = register("nearest_bed");
   public static final MemoryModuleType HURT_BY = register("hurt_by");
   public static final MemoryModuleType HURT_BY_ENTITY = register("hurt_by_entity");
   public static final MemoryModuleType NEAREST_HOSTILE = register("nearest_hostile");
   public static final MemoryModuleType HIDING_PLACE = register("hiding_place");
   public static final MemoryModuleType HEARD_BELL_TIME = register("heard_bell_time");
   public static final MemoryModuleType CANT_REACH_WALK_TARGET_SINCE = register("cant_reach_walk_target_since");
   public static final MemoryModuleType GOLEM_LAST_SEEN_TIME = register("golem_last_seen_time");
   public static final MemoryModuleType LAST_SLEPT = register("last_slept", Optional.of(SerializableLong::of));
   public static final MemoryModuleType LAST_WOKEN = register("last_woken", Optional.of(SerializableLong::of));
   public static final MemoryModuleType LAST_WORKED_AT_POI = register("last_worked_at_poi", Optional.of(SerializableLong::of));
   private final Optional deserializer;

   private MemoryModuleType(Optional var1) {
      this.deserializer = var1;
   }

   public String toString() {
      return Registry.MEMORY_MODULE_TYPE.getKey(this).toString();
   }

   public Optional getDeserializer() {
      return this.deserializer;
   }

   private static MemoryModuleType register(String var0, Optional var1) {
      return (MemoryModuleType)Registry.register(Registry.MEMORY_MODULE_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new MemoryModuleType(var1));
   }

   private static MemoryModuleType register(String var0) {
      return (MemoryModuleType)Registry.register(Registry.MEMORY_MODULE_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new MemoryModuleType(Optional.empty()));
   }
}
