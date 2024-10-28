package net.minecraft.client.renderer.texture.atlas;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.renderer.texture.atlas.sources.SourceFilter;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.ResourceLocation;

public class SpriteSources {
   private static final BiMap<ResourceLocation, SpriteSourceType> TYPES = HashBiMap.create();
   public static final SpriteSourceType SINGLE_FILE;
   public static final SpriteSourceType DIRECTORY;
   public static final SpriteSourceType FILTER;
   public static final SpriteSourceType UNSTITCHER;
   public static final SpriteSourceType PALETTED_PERMUTATIONS;
   public static Codec<SpriteSourceType> TYPE_CODEC;
   public static Codec<SpriteSource> CODEC;
   public static Codec<List<SpriteSource>> FILE_CODEC;

   public SpriteSources() {
      super();
   }

   private static SpriteSourceType register(String var0, MapCodec<? extends SpriteSource> var1) {
      SpriteSourceType var2 = new SpriteSourceType(var1);
      ResourceLocation var3 = ResourceLocation.withDefaultNamespace(var0);
      SpriteSourceType var4 = (SpriteSourceType)TYPES.putIfAbsent(var3, var2);
      if (var4 != null) {
         throw new IllegalStateException("Duplicate registration " + String.valueOf(var3));
      } else {
         return var2;
      }
   }

   static {
      SINGLE_FILE = register("single", SingleFile.CODEC);
      DIRECTORY = register("directory", DirectoryLister.CODEC);
      FILTER = register("filter", SourceFilter.CODEC);
      UNSTITCHER = register("unstitch", Unstitcher.CODEC);
      PALETTED_PERMUTATIONS = register("paletted_permutations", PalettedPermutations.CODEC);
      TYPE_CODEC = ResourceLocation.CODEC.flatXmap((var0) -> {
         SpriteSourceType var1 = (SpriteSourceType)TYPES.get(var0);
         return var1 != null ? DataResult.success(var1) : DataResult.error(() -> {
            return "Unknown type " + String.valueOf(var0);
         });
      }, (var0) -> {
         ResourceLocation var1 = (ResourceLocation)TYPES.inverse().get(var0);
         return var0 != null ? DataResult.success(var1) : DataResult.error(() -> {
            return "Unknown type " + String.valueOf(var1);
         });
      });
      CODEC = TYPE_CODEC.dispatch(SpriteSource::type, SpriteSourceType::codec);
      FILE_CODEC = CODEC.listOf().fieldOf("sources").codec();
   }
}
