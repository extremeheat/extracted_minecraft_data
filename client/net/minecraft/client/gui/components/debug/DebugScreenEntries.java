package net.minecraft.client.gui.components.debug;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class DebugScreenEntries {
   private static final Map<ResourceLocation, DebugScreenEntry> ENTRIES_BY_LOCATION = new HashMap();
   public static final ResourceLocation GAME_VERSION = register((String)"game_version", new DebugEntryVersion());
   public static final ResourceLocation FPS = register((String)"fps", new DebugEntryFps());
   public static final ResourceLocation TPS = register((String)"tps", new DebugEntryTps());
   public static final ResourceLocation MEMORY = register((String)"memory", new DebugEntryMemory());
   public static final ResourceLocation SYSTEM_SPECS = register((String)"system_specs", new DebugEntrySystemSpecs());
   public static final ResourceLocation LOOKING_AT_BLOCK = register((String)"looking_at_block", new DebugEntryLookingAtBlock());
   public static final ResourceLocation LOOKING_AT_FLUID = register((String)"looking_at_fluid", new DebugEntryLookingAtFluid());
   public static final ResourceLocation LOOKING_AT_ENTITY = register((String)"looking_at_entity", new DebugEntryLookingAtEntity());
   public static final ResourceLocation CHUNK_RENDER_STATS = register((String)"chunk_render_stats", new DebugEntryChunkRenderStats());
   public static final ResourceLocation CHUNK_GENERATION_STATS = register((String)"chunk_generation_stats", new DebugEntryChunkGeneration());
   public static final ResourceLocation ENTITY_RENDER_STATS = register((String)"entity_render_stats", new DebugEntryEntityRenderStats());
   public static final ResourceLocation PARTICLE_RENDER_STATS = register((String)"particle_render_stats", new DebugEntryParticleRenderStats());
   public static final ResourceLocation CHUNK_SOURCE_STATS = register((String)"chunk_source_stats", new DebugEntryChunkSourceStats());
   public static final ResourceLocation PLAYER_POSITION = register((String)"player_position", new DebugEntryPosition());
   public static final ResourceLocation PLAYER_SECTION_POSITION = register((String)"player_section_position", new DebugEntrySectionPosition());
   public static final ResourceLocation LIGHT_LEVELS = register((String)"light_levels", new DebugEntryLight());
   public static final ResourceLocation HEIGHTMAP = register((String)"heightmap", new DebugEntryHeightmap());
   public static final ResourceLocation BIOME = register((String)"biome", new DebugEntryBiome());
   public static final ResourceLocation LOCAL_DIFFICULTY = register((String)"local_difficulty", new DebugEntryLocalDifficulty());
   public static final ResourceLocation ENTITY_SPAWN_COUNTS = register((String)"entity_spawn_counts", new DebugEntrySpawnCounts());
   public static final ResourceLocation SOUND_MOOD = register((String)"sound_mood", new DebugEntrySoundMood());
   public static final ResourceLocation POST_EFFECT = register((String)"post_effect", new DebugEntryPostEffect());
   public static final ResourceLocation ENTITY_HITBOXES = register((String)"entity_hitboxes", new DebugEntryNoop());
   public static final ResourceLocation CHUNK_BORDERS = register((String)"chunk_borders", new DebugEntryNoop());
   public static final ResourceLocation THREE_DIMENSIONAL_CROSSHAIR = register((String)"3d_crosshair", new DebugEntryNoop());
   public static final ResourceLocation CHUNK_SECTION_PATHS = register((String)"chunk_section_paths", new DebugEntryNoop());
   public static final ResourceLocation GPU_UTILIZATION = register((String)"gpu_utilization", new DebugEntryGpuUtilization());
   public static final ResourceLocation SIMPLE_PERFORMANCE_IMPACTORS = register((String)"simple_performance_impactors", new DebugEntrySimplePerformanceImpactors());
   public static final ResourceLocation CHUNK_SECTION_OCTREE = register((String)"chunk_section_octree", new DebugEntryNoop());
   public static final ResourceLocation CHUNK_SECTION_VISIBILITY = register((String)"chunk_section_visibility", new DebugEntryNoop());
   public static final Map<DebugScreenProfile, Map<ResourceLocation, DebugScreenEntryStatus>> PROFILES;

   public DebugScreenEntries() {
      super();
   }

   private static ResourceLocation register(String var0, DebugScreenEntry var1) {
      return register(ResourceLocation.withDefaultNamespace(var0), var1);
   }

   private static ResourceLocation register(ResourceLocation var0, DebugScreenEntry var1) {
      ENTRIES_BY_LOCATION.put(var0, var1);
      return var0;
   }

   public static Map<ResourceLocation, DebugScreenEntry> allEntries() {
      return Map.copyOf(ENTRIES_BY_LOCATION);
   }

   @Nullable
   public static DebugScreenEntry getEntry(ResourceLocation var0) {
      return (DebugScreenEntry)ENTRIES_BY_LOCATION.get(var0);
   }

   static {
      Map var0 = Map.of(THREE_DIMENSIONAL_CROSSHAIR, DebugScreenEntryStatus.IN_F3, GAME_VERSION, DebugScreenEntryStatus.IN_F3, TPS, DebugScreenEntryStatus.IN_F3, FPS, DebugScreenEntryStatus.IN_F3, MEMORY, DebugScreenEntryStatus.IN_F3, SYSTEM_SPECS, DebugScreenEntryStatus.IN_F3, PLAYER_POSITION, DebugScreenEntryStatus.IN_F3, PLAYER_SECTION_POSITION, DebugScreenEntryStatus.IN_F3, SIMPLE_PERFORMANCE_IMPACTORS, DebugScreenEntryStatus.IN_F3);
      Map var1 = Map.of(TPS, DebugScreenEntryStatus.IN_F3, FPS, DebugScreenEntryStatus.ALWAYS_ON, GPU_UTILIZATION, DebugScreenEntryStatus.IN_F3, MEMORY, DebugScreenEntryStatus.IN_F3, SIMPLE_PERFORMANCE_IMPACTORS, DebugScreenEntryStatus.IN_F3);
      PROFILES = Map.of(DebugScreenProfile.DEFAULT, var0, DebugScreenProfile.PERFORMANCE, var1);
   }
}
