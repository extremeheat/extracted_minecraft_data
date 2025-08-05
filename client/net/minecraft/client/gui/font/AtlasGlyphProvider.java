package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakeableGlyph;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class AtlasGlyphProvider {
   private final TextureAtlas atlas;
   private final GlyphRenderTypes renderTypes;
   private final GlyphSource missingWrapper;
   private final Map<ResourceLocation, GlyphSource> wrapperCache = new HashMap();
   private final Function<ResourceLocation, GlyphSource> spriteResolver;

   public AtlasGlyphProvider(TextureAtlas var1) {
      super();
      this.atlas = var1;
      this.renderTypes = GlyphRenderTypes.createForColorTexture(var1.location());
      TextureAtlasSprite var2 = var1.missingSprite();
      this.missingWrapper = this.createSprite(var2);
      this.spriteResolver = (var3) -> {
         TextureAtlasSprite var4 = var1.getSprite(var3);
         return var4 == var2 ? this.missingWrapper : this.createSprite(var4);
      };
   }

   private GlyphSource createSprite(TextureAtlasSprite var1) {
      GpuTextureView var2 = this.atlas.getTextureView();
      return new SingleSpriteSource(new SpriteWrapper(AtlasGlyphProvider.SpriteWrapper.wrapSprite(this.renderTypes, var2, var1)));
   }

   public GlyphSource sourceForSprite(ResourceLocation var1) {
      return (GlyphSource)this.wrapperCache.computeIfAbsent(var1, this.spriteResolver);
   }

   static record SingleSpriteSource(BakeableGlyph glyph) implements GlyphSource {
      SingleSpriteSource(BakeableGlyph var1) {
         super();
         this.glyph = var1;
      }

      public BakeableGlyph getGlyph(int var1) {
         return this.glyph;
      }

      public BakeableGlyph getRandomGlyph(RandomSource var1, int var2) {
         return this.glyph;
      }
   }

   static record SpriteWrapper(BakedGlyph baked) implements BakeableGlyph {
      public static final float WIDTH = 8.0F;
      public static final float HEIGHT = 8.0F;
      public static final GlyphInfo GLYPH_INFO = () -> 8.0F;

      SpriteWrapper(BakedGlyph var1) {
         super();
         this.baked = var1;
      }

      static BakedGlyph wrapSprite(GlyphRenderTypes var0, GpuTextureView var1, TextureAtlasSprite var2) {
         return new BakedGlyph(var0, var1, var2.getU0(), var2.getU1(), var2.getV0(), var2.getV1(), 0.0F, 8.0F, -1.0F, 7.0F);
      }

      public GlyphInfo info() {
         return GLYPH_INFO;
      }
   }
}
