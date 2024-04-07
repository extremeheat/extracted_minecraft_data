package net.minecraft.client.gui.font.providers;

import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;

public record TrueTypeGlyphProviderDefinition(ResourceLocation location, float size, float oversample, TrueTypeGlyphProviderDefinition.Shift shift, String skip)
   implements GlyphProviderDefinition {
   private static final Codec<String> SKIP_LIST_CODEC = Codec.withAlternative(Codec.STRING, Codec.STRING.listOf(), var0 -> String.join("", var0));
   public static final MapCodec<TrueTypeGlyphProviderDefinition> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ResourceLocation.CODEC.fieldOf("file").forGetter(TrueTypeGlyphProviderDefinition::location),
               Codec.FLOAT.optionalFieldOf("size", 11.0F).forGetter(TrueTypeGlyphProviderDefinition::size),
               Codec.FLOAT.optionalFieldOf("oversample", 1.0F).forGetter(TrueTypeGlyphProviderDefinition::oversample),
               TrueTypeGlyphProviderDefinition.Shift.CODEC
                  .optionalFieldOf("shift", TrueTypeGlyphProviderDefinition.Shift.NONE)
                  .forGetter(TrueTypeGlyphProviderDefinition::shift),
               SKIP_LIST_CODEC.optionalFieldOf("skip", "").forGetter(TrueTypeGlyphProviderDefinition::skip)
            )
            .apply(var0, TrueTypeGlyphProviderDefinition::new)
   );

   public TrueTypeGlyphProviderDefinition(ResourceLocation location, float size, float oversample, TrueTypeGlyphProviderDefinition.Shift shift, String skip) {
      super();
      this.location = location;
      this.size = size;
      this.oversample = oversample;
      this.shift = shift;
      this.skip = skip;
   }

   @Override
   public GlyphProviderType type() {
      return GlyphProviderType.TTF;
   }

   @Override
   public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
      return Either.left(this::load);
   }

   private GlyphProvider load(ResourceManager var1) throws IOException {
      FT_Face var2 = null;
      ByteBuffer var3 = null;

      try {
         TrueTypeGlyphProvider var14;
         try (InputStream var4 = var1.open(this.location.withPrefix("font/"))) {
            var3 = TextureUtil.readResource(var4);
            var3.flip();
            MemoryStack var5 = MemoryStack.stackPush();

            try {
               PointerBuffer var6 = var5.mallocPointer(1);
               FreeTypeUtil.checkError(FreeType.FT_New_Memory_Face(FreeTypeUtil.getLibrary(), var3, 0L, var6), "Initializing font face");
               var2 = FT_Face.create(var6.get());
            } catch (Throwable var10) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (var5 != null) {
               var5.close();
            }

            String var13 = FreeType.FT_Get_Font_Format(var2);
            if (!"TrueType".equals(var13)) {
               throw new IOException("Font is not in TTF format, was " + var13);
            }

            FreeTypeUtil.checkError(FreeType.FT_Select_Charmap(var2, FreeType.FT_ENCODING_UNICODE), "Find unicode charmap");
            var14 = new TrueTypeGlyphProvider(var3, var2, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
         }

         return var14;
      } catch (Exception var12) {
         if (var2 != null) {
            FreeType.FT_Done_Face(var2);
         }

         MemoryUtil.memFree(var3);
         throw var12;
      }
   }

   public static record Shift(float x, float y) {
      public static final TrueTypeGlyphProviderDefinition.Shift NONE = new TrueTypeGlyphProviderDefinition.Shift(0.0F, 0.0F);
      public static final Codec<TrueTypeGlyphProviderDefinition.Shift> CODEC = Codec.FLOAT
         .listOf()
         .comapFlatMap(
            var0 -> Util.fixedSize(var0, 2).map(var0x -> new TrueTypeGlyphProviderDefinition.Shift((Float)var0x.get(0), (Float)var0x.get(1))),
            var0 -> List.of(var0.x, var0.y)
         );

      public Shift(float x, float y) {
         super();
         this.x = x;
         this.y = y;
      }
   }
}
