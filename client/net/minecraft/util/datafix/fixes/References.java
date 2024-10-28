package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;

public class References {
   public static final DSL.TypeReference LEVEL = reference("level");
   public static final DSL.TypeReference PLAYER = reference("player");
   public static final DSL.TypeReference CHUNK = reference("chunk");
   public static final DSL.TypeReference HOTBAR = reference("hotbar");
   public static final DSL.TypeReference OPTIONS = reference("options");
   public static final DSL.TypeReference STRUCTURE = reference("structure");
   public static final DSL.TypeReference STATS = reference("stats");
   public static final DSL.TypeReference SAVED_DATA_COMMAND_STORAGE = reference("saved_data/command_storage");
   public static final DSL.TypeReference SAVED_DATA_FORCED_CHUNKS = reference("saved_data/chunks");
   public static final DSL.TypeReference SAVED_DATA_MAP_DATA = reference("saved_data/map_data");
   public static final DSL.TypeReference SAVED_DATA_MAP_INDEX = reference("saved_data/idcounts");
   public static final DSL.TypeReference SAVED_DATA_RAIDS = reference("saved_data/raids");
   public static final DSL.TypeReference SAVED_DATA_RANDOM_SEQUENCES = reference("saved_data/random_sequences");
   public static final DSL.TypeReference SAVED_DATA_STRUCTURE_FEATURE_INDICES = reference("saved_data/structure_feature_indices");
   public static final DSL.TypeReference SAVED_DATA_SCOREBOARD = reference("saved_data/scoreboard");
   public static final DSL.TypeReference ADVANCEMENTS = reference("advancements");
   public static final DSL.TypeReference POI_CHUNK = reference("poi_chunk");
   public static final DSL.TypeReference ENTITY_CHUNK = reference("entity_chunk");
   public static final DSL.TypeReference BLOCK_ENTITY = reference("block_entity");
   public static final DSL.TypeReference ITEM_STACK = reference("item_stack");
   public static final DSL.TypeReference BLOCK_STATE = reference("block_state");
   public static final DSL.TypeReference FLAT_BLOCK_STATE = reference("flat_block_state");
   public static final DSL.TypeReference DATA_COMPONENTS = reference("data_components");
   public static final DSL.TypeReference ENTITY_NAME = reference("entity_name");
   public static final DSL.TypeReference ENTITY_TREE = reference("entity_tree");
   public static final DSL.TypeReference ENTITY = reference("entity");
   public static final DSL.TypeReference BLOCK_NAME = reference("block_name");
   public static final DSL.TypeReference ITEM_NAME = reference("item_name");
   public static final DSL.TypeReference GAME_EVENT_NAME = reference("game_event_name");
   public static final DSL.TypeReference UNTAGGED_SPAWNER = reference("untagged_spawner");
   public static final DSL.TypeReference STRUCTURE_FEATURE = reference("structure_feature");
   public static final DSL.TypeReference OBJECTIVE = reference("objective");
   public static final DSL.TypeReference TEAM = reference("team");
   public static final DSL.TypeReference RECIPE = reference("recipe");
   public static final DSL.TypeReference BIOME = reference("biome");
   public static final DSL.TypeReference MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = reference("multi_noise_biome_source_parameter_list");
   public static final DSL.TypeReference WORLD_GEN_SETTINGS = reference("world_gen_settings");

   public References() {
      super();
   }

   public static DSL.TypeReference reference(final String var0) {
      return new DSL.TypeReference() {
         public String typeName() {
            return var0;
         }

         public String toString() {
            return "@" + var0;
         }
      };
   }
}
