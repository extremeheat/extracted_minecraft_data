package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.io.IOException;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public interface GlyphProviderDefinition {
   MapCodec<GlyphProviderDefinition> MAP_CODEC = GlyphProviderType.CODEC.dispatchMap(GlyphProviderDefinition::type, GlyphProviderType::mapCodec);

   GlyphProviderType type();

   Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack();

   public static record Conditional(GlyphProviderDefinition b, FontOption.Filter c) {
      private final GlyphProviderDefinition definition;
      private final FontOption.Filter filter;
      public static final Codec<GlyphProviderDefinition.Conditional> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  GlyphProviderDefinition.MAP_CODEC.forGetter(GlyphProviderDefinition.Conditional::definition),
                  FontOption.Filter.CODEC.optionalFieldOf("filter", FontOption.Filter.ALWAYS_PASS).forGetter(GlyphProviderDefinition.Conditional::filter)
               )
               .apply(var0, GlyphProviderDefinition.Conditional::new)
      );

      public Conditional(GlyphProviderDefinition var1, FontOption.Filter var2) {
         super();
         this.definition = var1;
         this.filter = var2;
      }
   }

   public interface Loader {
      GlyphProvider load(ResourceManager var1) throws IOException;
   }

   public static record Reference(ResourceLocation a) {
      private final ResourceLocation id;

      public Reference(ResourceLocation var1) {
         super();
         this.id = var1;
      }
   }
}
