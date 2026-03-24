package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public interface GlyphProviderDefinition {
   MapCodec<GlyphProviderDefinition> MAP_CODEC = GlyphProviderType.CODEC.dispatchMap(GlyphProviderDefinition::type, GlyphProviderType::mapCodec);

   GlyphProviderType type();

   Either<Loader, Reference> unpack();

   public static record Reference(Identifier id) {
      public Reference {
         super();
      }
   }

   public static record Conditional(GlyphProviderDefinition definition, FontOption.Filter filter) {
      public static final Codec<Conditional> CODEC = RecordCodecBuilder.create((i) -> i.group(GlyphProviderDefinition.MAP_CODEC.forGetter(Conditional::definition), FontOption.Filter.CODEC.optionalFieldOf("filter", FontOption.Filter.ALWAYS_PASS).forGetter(Conditional::filter)).apply(i, Conditional::new));

      public Conditional {
         super();
      }
   }

   public interface Loader {
      GlyphProvider load(ResourceManager resourceManager) throws IOException;
   }
}
