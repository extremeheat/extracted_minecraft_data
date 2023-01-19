package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.renderer.texture.atlas.sources.SourceFilter;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.ResourceLocation;

public class SpriteSources {
   private static final BiMap<ResourceLocation, SpriteSourceType> TYPES = HashBiMap.create();
   public static final SpriteSourceType SINGLE_FILE = register("single", SingleFile.CODEC);
   public static final SpriteSourceType DIRECTORY = register("directory", DirectoryLister.CODEC);
   public static final SpriteSourceType FILTER = register("filter", SourceFilter.CODEC);
   public static final SpriteSourceType UNSTITCHER = register("unstitch", Unstitcher.CODEC);
   public static Codec<SpriteSourceType> TYPE_CODEC = ResourceLocation.CODEC.flatXmap(var0 -> {
      SpriteSourceType var1 = (SpriteSourceType)TYPES.get(var0);
      return var1 != null ? DataResult.success(var1) : DataResult.error("Unknown type " + var0);
   }, var0 -> {
      ResourceLocation var1 = (ResourceLocation)TYPES.inverse().get(var0);
      return var0 != null ? DataResult.success(var1) : DataResult.error("Unknown type " + var1);
   });
   public static Codec<SpriteSource> CODEC = TYPE_CODEC.dispatch(SpriteSource::type, SpriteSourceType::codec);
   public static Codec<List<SpriteSource>> FILE_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(CODEC.listOf().fieldOf("sources").forGetter(var0x -> var0x)).apply(var0, var0x -> var0x)
   );

   public SpriteSources() {
      super();
   }

   private static SpriteSourceType register(String var0, Codec<? extends SpriteSource> var1) {
      SpriteSourceType var2 = new SpriteSourceType(var1);
      ResourceLocation var3 = new ResourceLocation(var0);
      SpriteSourceType var4 = (SpriteSourceType)TYPES.putIfAbsent(var3, var2);
      if (var4 != null) {
         throw new IllegalStateException("Duplicate registration " + var3);
      } else {
         return var2;
      }
   }
}
