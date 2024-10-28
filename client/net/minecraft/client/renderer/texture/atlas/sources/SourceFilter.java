package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ResourceLocationPattern;

public class SourceFilter implements SpriteSource {
   public static final MapCodec<SourceFilter> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ResourceLocationPattern.CODEC.fieldOf("pattern").forGetter((var0x) -> {
         return var0x.filter;
      })).apply(var0, SourceFilter::new);
   });
   private final ResourceLocationPattern filter;

   public SourceFilter(ResourceLocationPattern var1) {
      super();
      this.filter = var1;
   }

   public void run(ResourceManager var1, SpriteSource.Output var2) {
      var2.removeAll(this.filter.locationPredicate());
   }

   public SpriteSourceType type() {
      return SpriteSources.FILTER;
   }
}
