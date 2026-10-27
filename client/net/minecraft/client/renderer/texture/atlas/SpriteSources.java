package net.minecraft.client.renderer.texture.atlas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.renderer.texture.atlas.sources.SourceFilter;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.core.Registry;

public class SpriteSources {
   public static final Codec<SpriteSource> CODEC;
   public static final Codec<List<SpriteSource>> FILE_CODEC;

   public SpriteSources() {
      super();
   }

   public static Object bootstrap(final Registry<MapCodec<? extends SpriteSource>> registry) {
      Registry.register(registry, (String)"single", SingleFile.MAP_CODEC);
      Registry.register(registry, (String)"directory", DirectoryLister.MAP_CODEC);
      Registry.register(registry, (String)"filter", SourceFilter.MAP_CODEC);
      Registry.register(registry, (String)"unstitch", Unstitcher.MAP_CODEC);
      return Registry.register(registry, (String)"paletted_permutations", PalettedPermutations.MAP_CODEC);
   }

   static {
      CODEC = BuiltInClientRegistries.SPRITE_SOURCE_TYPE.byNameCodec().dispatch(SpriteSource::codec, (c) -> c);
      FILE_CODEC = CODEC.listOf().fieldOf("sources").codec();
   }
}
