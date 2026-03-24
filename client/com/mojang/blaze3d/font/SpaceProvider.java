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
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.client.gui.font.providers.GlyphProviderType;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

public class SpaceProvider implements GlyphProvider {
   private final Int2ObjectMap<EmptyGlyph> glyphs;

   public SpaceProvider(final Map<Integer, Float> advances) {
      super();
      this.glyphs = new Int2ObjectOpenHashMap(advances.size());
      advances.forEach((codepoint, advance) -> this.glyphs.put(codepoint, new EmptyGlyph(advance)));
   }

   public @Nullable UnbakedGlyph getGlyph(final int codepoint) {
      return (UnbakedGlyph)this.glyphs.get(codepoint);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   public static record Definition(Map<Integer, Float> advances) implements GlyphProviderDefinition {
      public static final MapCodec<Definition> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.unboundedMap(ExtraCodecs.CODEPOINT, Codec.FLOAT).fieldOf("advances").forGetter(Definition::advances)).apply(i, Definition::new));

      public Definition {
         super();
      }

      public GlyphProviderType type() {
         return GlyphProviderType.SPACE;
      }

      public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
         GlyphProviderDefinition.Loader loader = (resourceManager) -> new SpaceProvider(this.advances);
         return Either.left(loader);
      }
   }
}
