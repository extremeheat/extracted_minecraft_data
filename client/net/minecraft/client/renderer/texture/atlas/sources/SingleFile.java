package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public record SingleFile(ResourceLocation resourceId, Optional<ResourceLocation> spriteId) implements SpriteSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<SingleFile> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("resource").forGetter(SingleFile::resourceId), ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter(SingleFile::spriteId)).apply(var0, SingleFile::new));

   public SingleFile(ResourceLocation var1) {
      this(var1, Optional.empty());
   }

   public SingleFile(ResourceLocation var1, Optional<ResourceLocation> var2) {
      super();
      this.resourceId = var1;
      this.spriteId = var2;
   }

   public void run(ResourceManager var1, SpriteSource.Output var2) {
      ResourceLocation var3 = TEXTURE_ID_CONVERTER.idToFile(this.resourceId);
      Optional var4 = var1.getResource(var3);
      if (var4.isPresent()) {
         var2.add((ResourceLocation)this.spriteId.orElse(this.resourceId), (Resource)var4.get());
      } else {
         LOGGER.warn("Missing sprite: {}", var3);
      }

   }

   public MapCodec<SingleFile> codec() {
      return MAP_CODEC;
   }
}
