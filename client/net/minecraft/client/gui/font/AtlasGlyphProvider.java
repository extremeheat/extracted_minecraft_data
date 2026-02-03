package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class AtlasGlyphProvider {
   private static final GlyphInfo GLYPH_INFO = GlyphInfo.simple(8.0F);
   private final TextureAtlas atlas;
   private final GlyphRenderTypes renderTypes;
   private final GlyphSource missingWrapper;
   private final Map<Identifier, GlyphSource> wrapperCache = new HashMap();
   private final Function<Identifier, GlyphSource> spriteResolver;

   public AtlasGlyphProvider(final TextureAtlas atlas) {
      super();
      this.atlas = atlas;
      this.renderTypes = GlyphRenderTypes.createForColorTexture(atlas.location());
      TextureAtlasSprite missingSprite = atlas.missingSprite();
      this.missingWrapper = this.createSprite(missingSprite);
      this.spriteResolver = (id) -> {
         TextureAtlasSprite sprite = atlas.getSprite(id);
         return sprite == missingSprite ? this.missingWrapper : this.createSprite(sprite);
      };
   }

   public GlyphSource sourceForSprite(final Identifier spriteId) {
      return (GlyphSource)this.wrapperCache.computeIfAbsent(spriteId, this.spriteResolver);
   }

   private GlyphSource createSprite(final TextureAtlasSprite sprite) {
      return new SingleSpriteSource(new BakedGlyph() {
         {
            Objects.requireNonNull(AtlasGlyphProvider.this);
         }

         public GlyphInfo info() {
            return AtlasGlyphProvider.GLYPH_INFO;
         }

         public TextRenderable.Styled createGlyph(final float x, final float y, final int color, final int shadowColor, final Style style, final float boldOffset, final float shadowOffset) {
            return new Instance(AtlasGlyphProvider.this.renderTypes, AtlasGlyphProvider.this.atlas.getTextureView(), sprite, x, y, color, shadowColor, shadowOffset, style);
         }
      });
   }

   private static record Instance(GlyphRenderTypes renderTypes, GpuTextureView textureView, TextureAtlasSprite sprite, float x, float y, int color, int shadowColor, float shadowOffset, Style style) implements PlainTextRenderable {
      private Instance {
         super();
      }

      public void renderSprite(final Matrix4f pose, final VertexConsumer buffer, final int packedLightCoords, final float offsetX, final float offsetY, final float z, final int color) {
         float x0 = offsetX + this.left();
         float x1 = offsetX + this.right();
         float y0 = offsetY + this.top();
         float y1 = offsetY + this.bottom();
         buffer.addVertex((Matrix4fc)pose, x0, y0, z).setUv(this.sprite.getU0(), this.sprite.getV0()).setColor(color).setLight(packedLightCoords);
         buffer.addVertex((Matrix4fc)pose, x0, y1, z).setUv(this.sprite.getU0(), this.sprite.getV1()).setColor(color).setLight(packedLightCoords);
         buffer.addVertex((Matrix4fc)pose, x1, y1, z).setUv(this.sprite.getU1(), this.sprite.getV1()).setColor(color).setLight(packedLightCoords);
         buffer.addVertex((Matrix4fc)pose, x1, y0, z).setUv(this.sprite.getU1(), this.sprite.getV0()).setColor(color).setLight(packedLightCoords);
      }

      public RenderType renderType(final Font.DisplayMode displayMode) {
         return this.renderTypes.select(displayMode);
      }

      public RenderPipeline guiPipeline() {
         return this.renderTypes.guiPipeline();
      }
   }
}
