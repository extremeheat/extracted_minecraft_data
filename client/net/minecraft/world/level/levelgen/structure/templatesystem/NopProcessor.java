package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;

public class NopProcessor extends StructureProcessor {
   public static final MapCodec<NopProcessor> CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final NopProcessor INSTANCE = new NopProcessor();

   private NopProcessor() {
      super();
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.NOP;
   }
}
