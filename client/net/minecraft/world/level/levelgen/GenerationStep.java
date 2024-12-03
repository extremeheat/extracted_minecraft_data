package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public class GenerationStep {
   public GenerationStep() {
      super();
   }

   public static enum Decoration implements StringRepresentable {
      RAW_GENERATION("raw_generation"),
      LAKES("lakes"),
      LOCAL_MODIFICATIONS("local_modifications"),
      UNDERGROUND_STRUCTURES("underground_structures"),
      SURFACE_STRUCTURES("surface_structures"),
      STRONGHOLDS("strongholds"),
      UNDERGROUND_ORES("underground_ores"),
      UNDERGROUND_DECORATION("underground_decoration"),
      FLUID_SPRINGS("fluid_springs"),
      VEGETAL_DECORATION("vegetal_decoration"),
      TOP_LAYER_MODIFICATION("top_layer_modification");

      public static final Codec<Decoration> CODEC = StringRepresentable.<Decoration>fromEnum(Decoration::values);
      private final String name;

      private Decoration(final String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Decoration[] $values() {
         return new Decoration[]{RAW_GENERATION, LAKES, LOCAL_MODIFICATIONS, UNDERGROUND_STRUCTURES, SURFACE_STRUCTURES, STRONGHOLDS, UNDERGROUND_ORES, UNDERGROUND_DECORATION, FLUID_SPRINGS, VEGETAL_DECORATION, TOP_LAYER_MODIFICATION};
      }
   }
}
