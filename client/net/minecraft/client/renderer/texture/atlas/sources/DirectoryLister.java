package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class DirectoryLister implements SpriteSource {
   public static final Codec<DirectoryLister> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.STRING.fieldOf("source").forGetter(var0x -> var0x.sourcePath), Codec.STRING.fieldOf("prefix").forGetter(var0x -> var0x.idPrefix)
            )
            .apply(var0, DirectoryLister::new)
   );
   private final String sourcePath;
   private final String idPrefix;

   public DirectoryLister(String var1, String var2) {
      super();
      this.sourcePath = var1;
      this.idPrefix = var2;
   }

   @Override
   public void run(ResourceManager var1, SpriteSource.Output var2) {
      FileToIdConverter var3 = new FileToIdConverter("textures/" + this.sourcePath, ".png");
      var3.listMatchingResources(var1).forEach((var3x, var4) -> {
         ResourceLocation var5 = var3.fileToId(var3x).withPrefix(this.idPrefix);
         var2.add(var5, var4);
      });
   }

   @Override
   public SpriteSourceType type() {
      return SpriteSources.DIRECTORY;
   }
}
