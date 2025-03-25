package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ResourceLocationPattern;

public record SourceFilter(ResourceLocationPattern filter) implements SpriteSource {
   public static final MapCodec<SourceFilter> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocationPattern.CODEC.fieldOf("pattern").forGetter(SourceFilter::filter)).apply(var0, SourceFilter::new));

   public SourceFilter(final ResourceLocationPattern var1) {
      super();
      this.filter = var1;
   }

   public void run(ResourceManager var1, SpriteSource.Output var2) {
      var2.removeAll(this.filter.locationPredicate());
   }

   public MapCodec<SourceFilter> codec() {
      return MAP_CODEC;
   }
}
