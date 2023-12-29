package com.mojang.blaze3d.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;

public class SpaceProvider implements GlyphProvider {
   private final Int2ObjectMap<GlyphInfo.SpaceGlyphInfo> glyphs;

   public SpaceProvider(Map<Integer, Float> var1) {
      super();
      this.glyphs = new Int2ObjectOpenHashMap(var1.size());
      var1.forEach((var1x, var2) -> this.glyphs.put(var1x, (GlyphInfo.SpaceGlyphInfo)() -> var2));
   }

   @Nullable
   @Override
   public GlyphInfo getGlyph(int var1) {
      return (GlyphInfo)this.glyphs.get(var1);
   }

   @Override
   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   public static record Definition(Map<Integer, Float> c) implements GlyphProviderDefinition {
      private final Map<Integer, Float> advances;
      public static final MapCodec<SpaceProvider.Definition> CODEC = RecordCodecBuilder.mapCodec(
         var0 -> var0.group(Codec.unboundedMap(ExtraCodecs.CODEPOINT, Codec.FLOAT).fieldOf("advances").forGetter(SpaceProvider.Definition::advances))
               .apply(var0, SpaceProvider.Definition::new)
      );

      public Definition(Map<Integer, Float> var1) {
         super();
         this.advances = var1;
      }

      @Override
      public GlyphProviderType type() {
         return GlyphProviderType.SPACE;
      }

      @Override
      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         GlyphProviderDefinition.Loader var1 = var1x -> new SpaceProvider(this.advances);
         return Either.left(var1);
      }
   }
}
