package net.minecraft.client.gui.components.debug;

public class DebugGroups {
   public static final DebugGroup HELP = DebugGroup.Builder.titled("Help").build();
   public static final DebugGroup MISC = DebugGroup.Builder.titleless().build();
   public static final DebugGroup PRIORITY = DebugGroup.Builder.titleless().build();
   public static final DebugGroup LIGHT = DebugGroup.Builder.titled("Light").withAccentColor(16776960).build();
   public static final DebugGroup LOOKING_AT_BLOCK = DebugGroup.Builder.titled("Looking At Block").withAccentColor(13369599).build();
   public static final DebugGroup LOOKING_AT_FLUID = DebugGroup.Builder.titled("Looking At Fluid").withAccentColor(16763904).build();
   public static final DebugGroup LOOKING_AT_ENTITY = DebugGroup.Builder.titled("Looking At Entity").withAccentColor(65484).build();
   public static final DebugGroup MEMORY;
   public static final DebugGroup POSITION;
   public static final DebugGroup CHUNK_RENDERING;
   public static final DebugGroup PERFORMANCE_IMPACTORS;
   public static final DebugGroup SYSTEM_SPECS;
   public static final DebugGroup HEIGHTMAP;
   public static final DebugGroup CHUNK_GENERATION;
   public static final DebugGroup SPAWN_COUNTS;

   public DebugGroups() {
      super();
   }

   static {
      MEMORY = DebugGroup.Builder.titled("Memory").withAccentColor(16751360).withPreferredColumn(DebugColumn.Side.RIGHT).build();
      POSITION = DebugGroup.Builder.titled("Position").withAccentColor(16777215).withPreferredColumn(DebugColumn.Side.LEFT).build();
      CHUNK_RENDERING = DebugGroup.Builder.titled("Chunk Rendering").withAccentColor(15773856).build();
      PERFORMANCE_IMPACTORS = DebugGroup.Builder.titled("Performance Impactors").withAccentColor(65280).withPreferredColumn(DebugColumn.Side.RIGHT).build();
      SYSTEM_SPECS = DebugGroup.Builder.titled("System Specs").withAccentColor(16711680).withPreferredColumn(DebugColumn.Side.RIGHT).build();
      HEIGHTMAP = DebugGroup.Builder.titled("Heightmap").withAccentColor(43775).build();
      CHUNK_GENERATION = DebugGroup.Builder.titled("Chunk Generation").withAccentColor(10092458).build();
      SPAWN_COUNTS = DebugGroup.Builder.titled("Entity Spawn Counts").withAccentColor(16729156).build();
   }
}
