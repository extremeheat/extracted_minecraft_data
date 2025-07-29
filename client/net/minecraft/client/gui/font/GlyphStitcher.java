package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.SheetGlyphInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class GlyphStitcher implements AutoCloseable {
   private final TextureManager textureManager;
   private final ResourceLocation texturePrefix;
   private final List<FontTexture> textures = new ArrayList();

   public GlyphStitcher(TextureManager var1, ResourceLocation var2) {
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

   @Nullable
   public BakedGlyph stitch(SheetGlyphInfo var1) {
      for(FontTexture var3 : this.textures) {
         BakedGlyph var4 = var3.add(var1);
         if (var4 != null) {
            return var4;
         }
      }

      int var7 = this.textures.size();
      ResourceLocation var8 = this.textureName(var7);
      boolean var9 = var1.isColored();
      GlyphRenderTypes var5 = var9 ? GlyphRenderTypes.createForColorTexture(var8) : GlyphRenderTypes.createForIntensityTexture(var8);
      Objects.requireNonNull(var8);
      FontTexture var6 = new FontTexture(var8::toString, var5, var9);
      this.textures.add(var6);
      this.textureManager.register(var8, var6);
      return var6.add(var1);
   }

   private ResourceLocation textureName(int var1) {
      return this.texturePrefix.withSuffix("/" + var1);
   }
}
