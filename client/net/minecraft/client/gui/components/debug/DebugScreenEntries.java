package net.minecraft.client.gui.components.debug;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class DebugScreenEntries {
   private static final Map<Identifier, DebugScreenEntry> ENTRIES_BY_ID = new HashMap();
   public static final Identifier GAME_VERSION = register((String)"game_version", new DebugEntryVersion());
   public static final Identifier FPS = register((String)"fps", new DebugEntryFps());
   public static final Identifier TPS = register((String)"tps", new DebugEntryTps());
   public static final Identifier MEMORY = register((String)"memory", new DebugEntryMemory());
   public static final Identifier DETAILED_MEMORY = register((String)"detailed_memory", new DebugEntryDetailedMemory());
   public static final Identifier SYSTEM_SPECS = register((String)"system_specs", new DebugEntrySystemSpecs());
   public static final Identifier LOOKING_AT_BLOCK_STATE = register((String)"looking_at_block_state", new DebugEntryLookingAt.BlockStateInfo());
   public static final Identifier LOOKING_AT_BLOCK_TAGS = register((String)"looking_at_block_tags", new DebugEntryLookingAt.BlockTagInfo());
   public static final Identifier LOOKING_AT_FLUID_STATE = register((String)"looking_at_fluid_state", new DebugEntryLookingAt.FluidStateInfo());
   public static final Identifier LOOKING_AT_FLUID_TAGS = register((String)"looking_at_fluid_tags", new DebugEntryLookingAt.FluidTagInfo());
   public static final Identifier LOOKING_AT_ENTITY = register((String)"looking_at_entity", new DebugEntryLookingAtEntity());
   public static final Identifier LOOKING_AT_ENTITY_TAGS = register((String)"looking_at_entity_tags", new DebugEntryLookingAtEntityTags());
   public static final Identifier CHUNK_RENDER_STATS = register((String)"chunk_render_stats", new DebugEntryChunkRenderStats());
   public static final Identifier CHUNK_GENERATION_STATS = register((String)"chunk_generation_stats", new DebugEntryChunkGeneration());
   public static final Identifier ENTITY_RENDER_STATS = register((String)"entity_render_stats", new DebugEntryEntityRenderStats());
   public static final Identifier PARTICLE_RENDER_STATS = register((String)"particle_render_stats", new DebugEntryParticleRenderStats());
   public static final Identifier CHUNK_SOURCE_STATS = register((String)"chunk_source_stats", new DebugEntryChunkSourceStats());
   public static final Identifier PLAYER_POSITION = register((String)"player_position", new DebugEntryPosition());
   public static final Identifier PLAYER_SECTION_POSITION = register((String)"player_section_position", new DebugEntrySectionPosition());
   public static final Identifier PLAYER_SPEED = register((String)"player_speed", new DebugEntryPlayerSpeed());
   public static final Identifier LIGHT_LEVELS = register((String)"light_levels", new DebugEntryLight());
   public static final Identifier HEIGHTMAP = register((String)"heightmap", new DebugEntryHeightmap());
   public static final Identifier BIOME = register((String)"biome", new DebugEntryBiome());
   public static final Identifier LOCAL_DIFFICULTY = register((String)"local_difficulty", new DebugEntryLocalDifficulty());
   public static final Identifier DAY_COUNT = register((String)"day_count", new DebugEntryDayCount());
   public static final Identifier ENTITY_SPAWN_COUNTS = register((String)"entity_spawn_counts", new DebugEntrySpawnCounts());
   public static final Identifier SOUND_MOOD = register((String)"sound_mood", new DebugEntrySoundMood());
   public static final Identifier SOUND_CACHE = register((String)"sound_cache", new DebugEntrySoundCache());
   public static final Identifier POST_EFFECTS = register((String)"post_effects", new DebugEntryPostEffects());
   public static final Identifier ENTITY_HITBOXES = register((String)"entity_hitboxes", new DebugEntryNoop());
   public static final Identifier CHUNK_BORDERS = register((String)"chunk_borders", new DebugEntryNoop());
   public static final Identifier THREE_DIMENSIONAL_CROSSHAIR = register((String)"3d_crosshair", new DebugEntryNoop());
   public static final Identifier CHUNK_SECTION_PATHS = register((String)"chunk_section_paths", new DebugEntryNoop());
   public static final Identifier GPU_UTILIZATION = register((String)"gpu_utilization", new DebugEntryGpuUtilization());
   public static final Identifier SIMPLE_PERFORMANCE_IMPACTORS = register((String)"simple_performance_impactors", new DebugEntrySimplePerformanceImpactors());
   public static final Identifier CHUNK_SECTION_OCTREE = register((String)"chunk_section_octree", new DebugEntryNoop());
   public static final Identifier VISUALIZE_WATER_LEVELS = register((String)"visualize_water_levels", new DebugEntryNoop());
   public static final Identifier VISUALIZE_HEIGHTMAP = register((String)"visualize_heightmap", new DebugEntryNoop());
   public static final Identifier VISUALIZE_COLLISION_BOXES = register((String)"visualize_collision_boxes", new DebugEntryNoop());
   public static final Identifier VISUALIZE_ENTITY_SUPPORTING_BLOCKS = register((String)"visualize_entity_supporting_blocks", new DebugEntryNoop());
   public static final Identifier VISUALIZE_BLOCK_LIGHT_LEVELS = register((String)"visualize_block_light_levels", new DebugEntryNoop());
   public static final Identifier VISUALIZE_SKY_LIGHT_LEVELS = register((String)"visualize_sky_light_levels", new DebugEntryNoop());
   public static final Identifier VISUALIZE_SOLID_FACES = register((String)"visualize_solid_faces", new DebugEntryNoop());
   public static final Identifier VISUALIZE_CHUNKS_ON_SERVER = register((String)"visualize_chunks_on_server", new DebugEntryNoop());
   public static final Identifier VISUALIZE_SKY_LIGHT_SECTIONS = register((String)"visualize_sky_light_sections", new DebugEntryNoop());
   public static final Identifier CHUNK_SECTION_VISIBILITY = register((String)"chunk_section_visibility", new DebugEntryNoop());
   public static final Map<DebugScreenProfile, Map<Identifier, DebugScreenEntryStatus>> PROFILES;

   public DebugScreenEntries() {
      super();
   }

   private static Identifier register(final String id, final DebugScreenEntry entry) {
      return register(Identifier.withDefaultNamespace(id), entry);
   }

   private static Identifier register(final Identifier identifier, final DebugScreenEntry entry) {
      ENTRIES_BY_ID.put(identifier, entry);
      return identifier;
   }

   public static Map<Identifier, DebugScreenEntry> allEntries() {
      return Map.copyOf(ENTRIES_BY_ID);
   }

   public static @Nullable DebugScreenEntry getEntry(final Identifier id) {
      return (DebugScreenEntry)ENTRIES_BY_ID.get(id);
   }

   static {
      Map<Identifier, DebugScreenEntryStatus> defaultProfile = Map.of(THREE_DIMENSIONAL_CROSSHAIR, DebugScreenEntryStatus.IN_OVERLAY, GAME_VERSION, DebugScreenEntryStatus.IN_OVERLAY, TPS, DebugScreenEntryStatus.IN_OVERLAY, FPS, DebugScreenEntryStatus.IN_OVERLAY, MEMORY, DebugScreenEntryStatus.IN_OVERLAY, SYSTEM_SPECS, DebugScreenEntryStatus.IN_OVERLAY, PLAYER_POSITION, DebugScreenEntryStatus.IN_OVERLAY, PLAYER_SECTION_POSITION, DebugScreenEntryStatus.IN_OVERLAY, SIMPLE_PERFORMANCE_IMPACTORS, DebugScreenEntryStatus.IN_OVERLAY);
      Map<Identifier, DebugScreenEntryStatus> performance = Map.of(TPS, DebugScreenEntryStatus.IN_OVERLAY, FPS, DebugScreenEntryStatus.ALWAYS_ON, GPU_UTILIZATION, DebugScreenEntryStatus.IN_OVERLAY, MEMORY, DebugScreenEntryStatus.IN_OVERLAY, SIMPLE_PERFORMANCE_IMPACTORS, DebugScreenEntryStatus.IN_OVERLAY);
      PROFILES = Map.of(DebugScreenProfile.DEFAULT, defaultProfile, DebugScreenProfile.PERFORMANCE, performance);
   }
}
