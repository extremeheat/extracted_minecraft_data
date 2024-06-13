package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.function.Function;
import java.util.function.Supplier;

public enum SpecialGlyphs implements GlyphInfo {
   WHITE(() -> generate(5, 8, (var0, var1) -> -1)),
   MISSING(() -> {
      byte var0 = 5;
      byte var1 = 8;
      return generate(5, 8, (var0x, var1x) -> {
         boolean var2 = var0x == 0 || var0x + 1 == 5 || var1x == 0 || var1x + 1 == 8;
         return var2 ? -1 : 0;
      });
   });

   final NativeImage image;

   private static NativeImage generate(int var0, int var1, SpecialGlyphs.PixelProvider var2) {
      NativeImage var3 = new NativeImage(NativeImage.Format.RGBA, var0, var1, false);

      for (int var4 = 0; var4 < var1; var4++) {
         for (int var5 = 0; var5 < var0; var5++) {
            var3.setPixelRGBA(var5, var4, var2.getColor(var5, var4));
         }
      }

      var3.untrack();
      return var3;
   }

   private SpecialGlyphs(Supplier<NativeImage> var3) {
      this.image = (NativeImage)var3.get();
   }

   @Override
   public float getAdvance() {
      return (float)(this.image.getWidth() + 1);
   }

   @Override
   public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
      return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
         @Override
         public int getPixelWidth() {
            return SpecialGlyphs.this.image.getWidth();
         }

         @Override
         public int getPixelHeight() {
            return SpecialGlyphs.this.image.getHeight();
         }

         @Override
         public float getOversample() {
            return 1.0F;
         }

         @Override
         public void upload(int var1, int var2) {
            SpecialGlyphs.this.image.upload(0, var1, var2, false);
         }

         @Override
         public boolean isColored() {
            return true;
         }
      });
   }

   @FunctionalInterface
   interface PixelProvider {
      int getColor(int var1, int var2);
   }
}
