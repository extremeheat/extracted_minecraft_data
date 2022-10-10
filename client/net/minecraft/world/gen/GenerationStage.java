package net.minecraft.world.gen;

public class GenerationStage {
   public static enum Carving {
      AIR,
      LIQUID;

      private Carving() {
      }
   }

   public static enum Decoration {
      RAW_GENERATION,
      LOCAL_MODIFICATIONS,
      UNDERGROUND_STRUCTURES,
      SURFACE_STRUCTURES,
      UNDERGROUND_ORES,
      UNDERGROUND_DECORATION,
      VEGETAL_DECORATION,
      TOP_LAYER_MODIFICATION;

      private Decoration() {
      }
   }
}
