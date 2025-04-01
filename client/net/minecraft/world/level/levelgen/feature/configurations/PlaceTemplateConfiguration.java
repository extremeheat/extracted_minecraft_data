package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;

public record PlaceTemplateConfiguration(List<ResourceLocation> templates, Optional<Rotation> forcedRotation) implements FeatureConfiguration {
   public static final Codec<PlaceTemplateConfiguration> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.listOf().fieldOf("template").forGetter(PlaceTemplateConfiguration::templates), Rotation.CODEC.optionalFieldOf("forced_rotation").forGetter(PlaceTemplateConfiguration::forcedRotation)).apply(var0, PlaceTemplateConfiguration::new));

   public PlaceTemplateConfiguration(List<ResourceLocation> var1) {
      this(var1, Optional.empty());
   }

   public PlaceTemplateConfiguration(List<ResourceLocation> var1, Rotation var2) {
      this(var1, Optional.of(var2));
   }

   public PlaceTemplateConfiguration(List<ResourceLocation> var1, Optional<Rotation> var2) {
      super();
      this.templates = var1;
      this.forcedRotation = var2;
   }
}
