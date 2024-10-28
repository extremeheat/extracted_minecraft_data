package com.mojang.blaze3d.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.util.ExtraCodecs;

public class SpaceProvider implements GlyphProvider {
   private final Int2ObjectMap<GlyphInfo.SpaceGlyphInfo> glyphs;

   public SpaceProvider(Map<Integer, Float> var1) {
      super();
      this.glyphs = new Int2ObjectOpenHashMap(var1.size());
      var1.forEach((var1x, var2) -> {
         this.glyphs.put(var1x, () -> {
            return var2;
         });
      });
   }

   @Nullable
   public GlyphInfo getGlyph(int var1) {
      return (GlyphInfo)this.glyphs.get(var1);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   public static record Definition(Map<Integer, Float> advances) implements GlyphProviderDefinition {
      public static final MapCodec<Definition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Codec.unboundedMap(ExtraCodecs.CODEPOINT, Codec.FLOAT).fieldOf("advances").forGetter(Definition::advances)).apply(var0, Definition::new);
      });

      public Definition(Map<Integer, Float> advances) {
         super();
         this.advances = advances;
      }

      public GlyphProviderType type() {
         return GlyphProviderType.SPACE;
      }

      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         GlyphProviderDefinition.Loader var1 = (var1x) -> {
            return new SpaceProvider(this.advances);
         };
         return Either.left(var1);
      }

      public Map<Integer, Float> advances() {
         return this.advances;
      }
   }
}
