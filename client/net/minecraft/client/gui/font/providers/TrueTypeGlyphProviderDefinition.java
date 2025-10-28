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
import java.nio.Buffer;
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

public record TrueTypeGlyphProviderDefinition(ResourceLocation location, float size, float oversample, Shift shift, String skip) implements GlyphProviderDefinition {
   private static final Codec<String> SKIP_LIST_CODEC;
   public static final MapCodec<TrueTypeGlyphProviderDefinition> CODEC;

   public TrueTypeGlyphProviderDefinition(ResourceLocation var1, float var2, float var3, Shift var4, String var5) {
      super();
      this.location = var1;
      this.size = var2;
      this.oversample = var3;
      this.shift = var4;
      this.skip = var5;
   }

   public GlyphProviderType type() {
      return GlyphProviderType.TTF;
   }

   public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
      return Either.left(this::load);
   }

   private GlyphProvider load(ResourceManager var1) throws IOException {
      Object var2 = null;
      Object var3 = null;

      try {
         InputStream var4 = var1.open(this.location.withPrefix("font/"));

         TrueTypeGlyphProvider var21;
         try {
            ByteBuffer var19 = TextureUtil.readResource(var4);
            var19.flip();
            synchronized(FreeTypeUtil.LIBRARY_LOCK) {
               MemoryStack var6 = MemoryStack.stackPush();

               try {
                  PointerBuffer var7 = var6.mallocPointer(1);
                  FreeTypeUtil.assertError(FreeType.FT_New_Memory_Face(FreeTypeUtil.getLibrary(), var19, 0L, var7), "Initializing font face");
                  var18 = FT_Face.create(var7.get());
               } catch (Throwable var14) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var12) {
                        var14.addSuppressed(var12);
                     }
                  }

                  throw var14;
               }

               if (var6 != null) {
                  var6.close();
               }

               String var20 = FreeType.FT_Get_Font_Format(var18);
               if (!"TrueType".equals(var20)) {
                  throw new IOException("Font is not in TTF format, was " + var20);
               }

               FreeTypeUtil.assertError(FreeType.FT_Select_Charmap(var18, FreeType.FT_ENCODING_UNICODE), "Find unicode charmap");
               var21 = new TrueTypeGlyphProvider(var19, var18, this.size, this.oversample, this.shift.x, this.shift.y, this.skip);
            }
         } catch (Throwable var16) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var11) {
                  var16.addSuppressed(var11);
               }
            }

            throw var16;
         }

         if (var4 != null) {
            var4.close();
         }

         return var21;
      } catch (Exception var17) {
         synchronized(FreeTypeUtil.LIBRARY_LOCK) {
            if (var2 != null) {
               FreeType.FT_Done_Face((FT_Face)var2);
            }
         }

         MemoryUtil.memFree((Buffer)var3);
         throw var17;
      }
   }

   static {
      SKIP_LIST_CODEC = Codec.withAlternative(Codec.STRING, Codec.STRING.listOf(), (var0) -> String.join("", var0));
      CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("file").forGetter(TrueTypeGlyphProviderDefinition::location), Codec.FLOAT.optionalFieldOf("size", 11.0F).forGetter(TrueTypeGlyphProviderDefinition::size), Codec.FLOAT.optionalFieldOf("oversample", 1.0F).forGetter(TrueTypeGlyphProviderDefinition::oversample), TrueTypeGlyphProviderDefinition.Shift.CODEC.optionalFieldOf("shift", TrueTypeGlyphProviderDefinition.Shift.NONE).forGetter(TrueTypeGlyphProviderDefinition::shift), SKIP_LIST_CODEC.optionalFieldOf("skip", "").forGetter(TrueTypeGlyphProviderDefinition::skip)).apply(var0, TrueTypeGlyphProviderDefinition::new));
   }

   public static record Shift(float x, float y) {
      final float x;
      final float y;
      public static final Shift NONE = new Shift(0.0F, 0.0F);
      public static final Codec<Shift> CODEC = Codec.floatRange(-512.0F, 512.0F).listOf().comapFlatMap((var0) -> Util.fixedSize((List)var0, 2).map((var0x) -> new Shift((Float)var0x.get(0), (Float)var0x.get(1))), (var0) -> List.of(var0.x, var0.y));

      public Shift(float var1, float var2) {
         super();
         this.x = var1;
         this.y = var2;
      }
   }
}
