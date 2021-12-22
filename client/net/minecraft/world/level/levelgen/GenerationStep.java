package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.StringRepresentable;

public class GenerationStep {
   public GenerationStep() {
      super();
   }

   public static enum Carving implements StringRepresentable {
      AIR("air"),
      LIQUID("liquid");

      public static final Codec<GenerationStep.Carving> CODEC = StringRepresentable.fromEnum(GenerationStep.Carving::values, GenerationStep.Carving::byName);
      private static final Map<String, GenerationStep.Carving> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(GenerationStep.Carving::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Carving(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      @Nullable
      public static GenerationStep.Carving byName(String var0) {
         return (GenerationStep.Carving)BY_NAME.get(var0);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static GenerationStep.Carving[] $values() {
         return new GenerationStep.Carving[]{AIR, LIQUID};
      }
   }

   public static enum Decoration {
      RAW_GENERATION,
      LAKES,
      LOCAL_MODIFICATIONS,
      UNDERGROUND_STRUCTURES,
      SURFACE_STRUCTURES,
      STRONGHOLDS,
      UNDERGROUND_ORES,
      UNDERGROUND_DECORATION,
      FLUID_SPRINGS,
      VEGETAL_DECORATION,
      TOP_LAYER_MODIFICATION;

      private Decoration() {
      }

      // $FF: synthetic method
      private static GenerationStep.Decoration[] $values() {
         return new GenerationStep.Decoration[]{RAW_GENERATION, LAKES, LOCAL_MODIFICATIONS, UNDERGROUND_STRUCTURES, SURFACE_STRUCTURES, STRONGHOLDS, UNDERGROUND_ORES, UNDERGROUND_DECORATION, FLUID_SPRINGS, VEGETAL_DECORATION, TOP_LAYER_MODIFICATION};
      }
   }
}
