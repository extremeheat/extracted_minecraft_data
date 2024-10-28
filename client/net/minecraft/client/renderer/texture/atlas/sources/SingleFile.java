package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class SingleFile implements SpriteSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<SingleFile> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ResourceLocation.CODEC.fieldOf("resource").forGetter((var0x) -> {
         return var0x.resourceId;
      }), ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter((var0x) -> {
         return var0x.spriteId;
      })).apply(var0, SingleFile::new);
   });
   private final ResourceLocation resourceId;
   private final Optional<ResourceLocation> spriteId;

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

   public SpriteSourceType type() {
      return SpriteSources.SINGLE_FILE;
   }
}
