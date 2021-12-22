package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;

public class JigsawConfiguration implements FeatureConfiguration {
   public static final Codec<JigsawConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(JigsawConfiguration::startPool), Codec.intRange(0, 7).fieldOf("size").forGetter(JigsawConfiguration::maxDepth)).apply(var0, JigsawConfiguration::new);
   });
   private final Supplier<StructureTemplatePool> startPool;
   private final int maxDepth;

   public JigsawConfiguration(Supplier<StructureTemplatePool> var1, int var2) {
      super();
      this.startPool = var1;
      this.maxDepth = var2;
   }

   public int maxDepth() {
      return this.maxDepth;
   }

   public Supplier<StructureTemplatePool> startPool() {
      return this.startPool;
   }
}
