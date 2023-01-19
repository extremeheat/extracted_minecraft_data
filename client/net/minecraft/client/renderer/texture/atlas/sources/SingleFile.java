package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class SingleFile implements SpriteSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<SingleFile> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ResourceLocation.CODEC.fieldOf("resource").forGetter(var0x -> var0x.resourceId),
               ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter(var0x -> var0x.spriteId)
            )
            .apply(var0, SingleFile::new)
   );
   private final FileToIdConverter TEXTURE_ID_CONVERTER = new FileToIdConverter("textures", ".png");
   private final ResourceLocation resourceId;
   private final Optional<ResourceLocation> spriteId;

   public SingleFile(ResourceLocation var1, Optional<ResourceLocation> var2) {
      super();
      this.resourceId = var1;
      this.spriteId = var2;
   }

   @Override
   public void run(ResourceManager var1, SpriteSource.Output var2) {
      ResourceLocation var3 = this.TEXTURE_ID_CONVERTER.idToFile(this.resourceId);
      Optional var4 = var1.getResource(var3);
      if (var4.isPresent()) {
         var2.add(this.spriteId.orElse(this.resourceId), (Resource)var4.get());
      } else {
         LOGGER.warn("Missing sprite: {}", var3);
      }
   }

   @Override
   public SpriteSourceType type() {
      return SpriteSources.SINGLE_FILE;
   }
}
