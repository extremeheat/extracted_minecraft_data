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
      FT_Face var2 = null;
      ByteBuffer var3 = null;

      try {
         InputStream var4 = var1.open(this.location.withPrefix("font/"));

         TrueTypeGlyphProvider var14;
         try {
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
         } catch (Throwable var11) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (var4 != null) {
            var4.close();
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

   public ResourceLocation location() {
      return this.location;
   }

   public float size() {
      return this.size;
   }

   public float oversample() {
      return this.oversample;
   }

   public Shift shift() {
      return this.shift;
   }

   public String skip() {
      return this.skip;
   }

   static {
      SKIP_LIST_CODEC = Codec.withAlternative(Codec.STRING, Codec.STRING.listOf(), (var0) -> {
         return String.join("", var0);
      });
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ResourceLocation.CODEC.fieldOf("file").forGetter(TrueTypeGlyphProviderDefinition::location), Codec.FLOAT.optionalFieldOf("size", 11.0F).forGetter(TrueTypeGlyphProviderDefinition::size), Codec.FLOAT.optionalFieldOf("oversample", 1.0F).forGetter(TrueTypeGlyphProviderDefinition::oversample), TrueTypeGlyphProviderDefinition.Shift.CODEC.optionalFieldOf("shift", TrueTypeGlyphProviderDefinition.Shift.NONE).forGetter(TrueTypeGlyphProviderDefinition::shift), SKIP_LIST_CODEC.optionalFieldOf("skip", "").forGetter(TrueTypeGlyphProviderDefinition::skip)).apply(var0, TrueTypeGlyphProviderDefinition::new);
      });
   }

   public static record Shift(float x, float y) {
      final float x;
      final float y;
      public static final Shift NONE = new Shift(0.0F, 0.0F);
      public static final Codec<Shift> CODEC;

      public Shift(float var1, float var2) {
         super();
         this.x = var1;
         this.y = var2;
      }

      public float x() {
         return this.x;
      }

      public float y() {
         return this.y;
      }

      static {
         CODEC = Codec.FLOAT.listOf().comapFlatMap((var0) -> {
            return Util.fixedSize((List)var0, 2).map((var0x) -> {
               return new Shift((Float)var0x.get(0), (Float)var0x.get(1));
            });
         }, (var0) -> {
            return List.of(var0.x, var0.y);
         });
      }
   }
}
