package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class FossilFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<FossilFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ResourceLocation.CODEC.listOf().fieldOf("fossil_structures").forGetter((var0x) -> {
         return var0x.fossilStructures;
      }), ResourceLocation.CODEC.listOf().fieldOf("overlay_structures").forGetter((var0x) -> {
         return var0x.overlayStructures;
      }), StructureProcessorType.LIST_CODEC.fieldOf("fossil_processors").forGetter((var0x) -> {
         return var0x.fossilProcessors;
      }), StructureProcessorType.LIST_CODEC.fieldOf("overlay_processors").forGetter((var0x) -> {
         return var0x.overlayProcessors;
      }), Codec.intRange(0, 7).fieldOf("max_empty_corners_allowed").forGetter((var0x) -> {
         return var0x.maxEmptyCornersAllowed;
      })).apply(var0, FossilFeatureConfiguration::new);
   });
   public final List<ResourceLocation> fossilStructures;
   public final List<ResourceLocation> overlayStructures;
   public final Holder<StructureProcessorList> fossilProcessors;
   public final Holder<StructureProcessorList> overlayProcessors;
   public final int maxEmptyCornersAllowed;

   public FossilFeatureConfiguration(List<ResourceLocation> var1, List<ResourceLocation> var2, Holder<StructureProcessorList> var3, Holder<StructureProcessorList> var4, int var5) {
      super();
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Fossil structure lists need at least one entry");
      } else if (var1.size() != var2.size()) {
         throw new IllegalArgumentException("Fossil structure lists must be equal lengths");
      } else {
         this.fossilStructures = var1;
         this.overlayStructures = var2;
         this.fossilProcessors = var3;
         this.overlayProcessors = var4;
         this.maxEmptyCornersAllowed = var5;
      }
   }
}
