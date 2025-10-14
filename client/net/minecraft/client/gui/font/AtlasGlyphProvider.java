package net.minecraft.client.gui.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class AtlasGlyphProvider {
   static final GlyphInfo GLYPH_INFO = GlyphInfo.simple(8.0F);
   final TextureAtlas atlas;
   final GlyphRenderTypes renderTypes;
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

   public GlyphSource sourceForSprite(ResourceLocation var1) {
      return (GlyphSource)this.wrapperCache.computeIfAbsent(var1, this.spriteResolver);
   }

   private GlyphSource createSprite(final TextureAtlasSprite var1) {
      return new SingleSpriteSource(new BakedGlyph() {
         public GlyphInfo info() {
            return AtlasGlyphProvider.GLYPH_INFO;
         }

         public TextRenderable.Styled createGlyph(float var1x, float var2, int var3, int var4, Style var5, float var6, float var7) {
            return new Instance(AtlasGlyphProvider.this.renderTypes, AtlasGlyphProvider.this.atlas.getTextureView(), var1, var1x, var2, var3, var4, var7, var5);
         }
      });
   }

   static record Instance(GlyphRenderTypes renderTypes, GpuTextureView textureView, TextureAtlasSprite sprite, float x, float y, int color, int shadowColor, float shadowOffset, Style style) implements PlainTextRenderable {
      Instance(GlyphRenderTypes var1, GpuTextureView var2, TextureAtlasSprite var3, float var4, float var5, int var6, int var7, float var8, Style var9) {
         super();
         this.renderTypes = var1;
         this.textureView = var2;
         this.sprite = var3;
         this.x = var4;
         this.y = var5;
         this.color = var6;
         this.shadowColor = var7;
         this.shadowOffset = var8;
         this.style = var9;
      }

      public void renderSprite(Matrix4f var1, VertexConsumer var2, int var3, float var4, float var5, float var6, int var7) {
         float var8 = var4 + this.left();
         float var9 = var4 + this.right();
         float var10 = var5 + this.top();
         float var11 = var5 + this.bottom();
         var2.addVertex((Matrix4fc)var1, var8, var10, var6).setUv(this.sprite.getU0(), this.sprite.getV0()).setColor(var7).setLight(var3);
         var2.addVertex((Matrix4fc)var1, var8, var11, var6).setUv(this.sprite.getU0(), this.sprite.getV1()).setColor(var7).setLight(var3);
         var2.addVertex((Matrix4fc)var1, var9, var11, var6).setUv(this.sprite.getU1(), this.sprite.getV1()).setColor(var7).setLight(var3);
         var2.addVertex((Matrix4fc)var1, var9, var10, var6).setUv(this.sprite.getU1(), this.sprite.getV0()).setColor(var7).setLight(var3);
      }

      public RenderType renderType(Font.DisplayMode var1) {
         return this.renderTypes.select(var1);
      }

      public RenderPipeline guiPipeline() {
         return this.renderTypes.guiPipeline();
      }
   }
}
