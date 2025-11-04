package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphBitmap;
import com.mojang.blaze3d.font.GlyphInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class GlyphStitcher implements AutoCloseable {
   private final TextureManager textureManager;
   private final Identifier texturePrefix;
   private final List<FontTexture> textures = new ArrayList();

   public GlyphStitcher(TextureManager var1, Identifier var2) {
      super();
      this.textureManager = var1;
      this.texturePrefix = var2;
   }

   public void reset() {
      int var1 = this.textures.size();
      this.textures.clear();

      for(int var2 = 0; var2 < var1; ++var2) {
         this.textureManager.release(this.textureName(var2));
      }

   }

   public void close() {
      this.reset();
   }

   public @Nullable BakedSheetGlyph stitch(GlyphInfo var1, GlyphBitmap var2) {
      for(FontTexture var4 : this.textures) {
         BakedSheetGlyph var5 = var4.add(var1, var2);
         if (var5 != null) {
            return var5;
         }
      }

      int var8 = this.textures.size();
      Identifier var9 = this.textureName(var8);
      boolean var10 = var2.isColored();
      GlyphRenderTypes var6 = var10 ? GlyphRenderTypes.createForColorTexture(var9) : GlyphRenderTypes.createForIntensityTexture(var9);
      Objects.requireNonNull(var9);
      FontTexture var7 = new FontTexture(var9::toString, var6, var10);
      this.textures.add(var7);
      this.textureManager.register(var9, var7);
      return var7.add(var1, var2);
   }

   private Identifier textureName(int var1) {
      return this.texturePrefix.withSuffix("/" + var1);
   }
}
