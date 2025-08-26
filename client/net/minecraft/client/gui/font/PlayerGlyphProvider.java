package net.minecraft.client.gui.font;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;

public class PlayerGlyphProvider {
   private static final float WIDTH = 8.0F;
   private static final float HEIGHT = 8.0F;
   static final GlyphInfo GLYPH_INFO = GlyphInfo.simple(8.0F);
   final PlayerSkinRenderCache playerSkinRenderCache;
   private final LoadingCache<FontDescription.PlayerSprite, GlyphSource> wrapperCache;

   public PlayerGlyphProvider(PlayerSkinRenderCache var1) {
      super();
      this.wrapperCache = CacheBuilder.newBuilder().expireAfterAccess(PlayerSkinRenderCache.CACHE_DURATION).build(new CacheLoader<FontDescription.PlayerSprite, GlyphSource>() {
         public GlyphSource load(FontDescription.PlayerSprite var1) {
            final Supplier var2 = PlayerGlyphProvider.this.playerSkinRenderCache.createLookup(var1.profile());
            final boolean var3 = var1.hat();
            return new SingleSpriteSource(new BakedGlyph() {
               public GlyphInfo info() {
                  return PlayerGlyphProvider.GLYPH_INFO;
               }

               public TextRenderable createGlyph(float var1, float var2x, int var3x, int var4, Style var5, float var6, float var7) {
                  return new Instance(var2, var3, var1, var2x, var3x, var4, var7);
               }
            });
         }

         // $FF: synthetic method
         public Object load(final Object var1) throws Exception {
            return this.load((FontDescription.PlayerSprite)var1);
         }
      });
      this.playerSkinRenderCache = var1;
   }

   public GlyphSource sourceForPlayer(FontDescription.PlayerSprite var1) {
      return (GlyphSource)this.wrapperCache.getUnchecked(var1);
   }

   static record Instance(Supplier<PlayerSkinRenderCache.RenderInfo> skin, boolean hat, float x, float y, int color, int shadowColor, float shadowOffset) implements PlainTextRenderable {
      Instance(Supplier<PlayerSkinRenderCache.RenderInfo> var1, boolean var2, float var3, float var4, int var5, int var6, float var7) {
         super();
         this.skin = var1;
         this.hat = var2;
         this.x = var3;
         this.y = var4;
         this.color = var5;
         this.shadowColor = var6;
         this.shadowOffset = var7;
      }

      public void renderSprite(Matrix4f var1, VertexConsumer var2, int var3, float var4, float var5, float var6, int var7) {
         float var8 = var4 + this.left();
         float var9 = var4 + this.right();
         float var10 = var5 + this.top();
         float var11 = var5 + this.bottom();
         renderQuad(var1, var2, var3, var8, var9, var10, var11, var6, var7, 8.0F, 8.0F, 8, 8, 64, 64);
         if (this.hat) {
            renderQuad(var1, var2, var3, var8, var9, var10, var11, var6, var7, 40.0F, 8.0F, 8, 8, 64, 64);
         }

      }

      private static void renderQuad(Matrix4f var0, VertexConsumer var1, int var2, float var3, float var4, float var5, float var6, float var7, int var8, float var9, float var10, int var11, int var12, int var13, int var14) {
         float var15 = (var9 + 0.0F) / (float)var13;
         float var16 = (var9 + (float)var11) / (float)var13;
         float var17 = (var10 + 0.0F) / (float)var14;
         float var18 = (var10 + (float)var12) / (float)var14;
         var1.addVertex(var0, var3, var5, var7).setUv(var15, var17).setColor(var8).setLight(var2);
         var1.addVertex(var0, var3, var6, var7).setUv(var15, var18).setColor(var8).setLight(var2);
         var1.addVertex(var0, var4, var6, var7).setUv(var16, var18).setColor(var8).setLight(var2);
         var1.addVertex(var0, var4, var5, var7).setUv(var16, var17).setColor(var8).setLight(var2);
      }

      public RenderType renderType(Font.DisplayMode var1) {
         return ((PlayerSkinRenderCache.RenderInfo)this.skin.get()).glyphRenderTypes().select(var1);
      }

      public RenderPipeline guiPipeline() {
         return ((PlayerSkinRenderCache.RenderInfo)this.skin.get()).glyphRenderTypes().guiPipeline();
      }

      public GpuTextureView textureView() {
         return ((PlayerSkinRenderCache.RenderInfo)this.skin.get()).textureView();
      }

      public float left() {
         return 0.0F;
      }

      public float right() {
         return 8.0F;
      }

      public float top() {
         return -1.0F;
      }

      public float bottom() {
         return 7.0F;
      }
   }
}
