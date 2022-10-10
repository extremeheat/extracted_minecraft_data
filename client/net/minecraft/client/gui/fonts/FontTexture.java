package net.minecraft.client.gui.fonts;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class FontTexture extends AbstractTexture implements Closeable {
   private final ResourceLocation field_211133_f;
   private final boolean field_211512_g;
   private final FontTexture.Entry field_211135_h;

   public FontTexture(ResourceLocation var1, boolean var2) {
      super();
      this.field_211133_f = var1;
      this.field_211512_g = var2;
      this.field_211135_h = new FontTexture.Entry(0, 0, 256, 256);
      TextureUtil.func_211681_a(var2 ? NativeImage.PixelFormatGLCode.RGBA : NativeImage.PixelFormatGLCode.INTENSITY, this.func_110552_b(), 256, 256);
   }

   public void func_195413_a(IResourceManager var1) {
   }

   public void close() {
      this.func_147631_c();
   }

   @Nullable
   public TexturedGlyph func_211131_a(IGlyphInfo var1) {
      if (var1.func_211579_f() != this.field_211512_g) {
         return null;
      } else {
         FontTexture.Entry var2 = this.field_211135_h.func_211224_a(var1);
         if (var2 != null) {
            this.func_195412_h();
            var1.func_211573_a(var2.field_211225_a, var2.field_211226_b);
            float var3 = 256.0F;
            float var4 = 256.0F;
            float var5 = 0.01F;
            return new TexturedGlyph(this.field_211133_f, ((float)var2.field_211225_a + 0.01F) / 256.0F, ((float)var2.field_211225_a - 0.01F + (float)var1.func_211202_a()) / 256.0F, ((float)var2.field_211226_b + 0.01F) / 256.0F, ((float)var2.field_211226_b - 0.01F + (float)var1.func_211203_b()) / 256.0F, var1.func_211198_f(), var1.func_211199_g(), var1.func_211200_h(), var1.func_211204_i());
         } else {
            return null;
         }
      }
   }

   public ResourceLocation func_211132_a() {
      return this.field_211133_f;
   }

   static class Entry {
      final int field_211225_a;
      final int field_211226_b;
      final int field_211227_c;
      final int field_211228_d;
      FontTexture.Entry field_211229_e;
      FontTexture.Entry field_211230_f;
      boolean field_211231_g;

      private Entry(int var1, int var2, int var3, int var4) {
         super();
         this.field_211225_a = var1;
         this.field_211226_b = var2;
         this.field_211227_c = var3;
         this.field_211228_d = var4;
      }

      @Nullable
      FontTexture.Entry func_211224_a(IGlyphInfo var1) {
         if (this.field_211229_e != null && this.field_211230_f != null) {
            FontTexture.Entry var6 = this.field_211229_e.func_211224_a(var1);
            if (var6 == null) {
               var6 = this.field_211230_f.func_211224_a(var1);
            }

            return var6;
         } else if (this.field_211231_g) {
            return null;
         } else {
            int var2 = var1.func_211202_a();
            int var3 = var1.func_211203_b();
            if (var2 <= this.field_211227_c && var3 <= this.field_211228_d) {
               if (var2 == this.field_211227_c && var3 == this.field_211228_d) {
                  this.field_211231_g = true;
                  return this;
               } else {
                  int var4 = this.field_211227_c - var2;
                  int var5 = this.field_211228_d - var3;
                  if (var4 > var5) {
                     this.field_211229_e = new FontTexture.Entry(this.field_211225_a, this.field_211226_b, var2, this.field_211228_d);
                     this.field_211230_f = new FontTexture.Entry(this.field_211225_a + var2 + 1, this.field_211226_b, this.field_211227_c - var2 - 1, this.field_211228_d);
                  } else {
                     this.field_211229_e = new FontTexture.Entry(this.field_211225_a, this.field_211226_b, this.field_211227_c, var3);
                     this.field_211230_f = new FontTexture.Entry(this.field_211225_a, this.field_211226_b + var3 + 1, this.field_211227_c, this.field_211228_d - var3 - 1);
                  }

                  return this.field_211229_e.func_211224_a(var1);
               }
            } else {
               return null;
            }
         }
      }

      // $FF: synthetic method
      Entry(int var1, int var2, int var3, int var4, Object var5) {
         this(var1, var2, var3, var4);
      }
   }
}
