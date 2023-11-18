package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public interface GlyphProviderDefinition {
   Codec<GlyphProviderDefinition> CODEC = GlyphProviderType.CODEC.dispatch(GlyphProviderDefinition::type, var0 -> var0.mapCodec().codec());

   GlyphProviderType type();

   Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack();

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
