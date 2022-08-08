package net.minecraft.client.gui.font.glyphs;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import java.util.function.Function;
import java.util.function.Supplier;

public enum SpecialGlyphs implements GlyphInfo {
   WHITE(() -> {
      return generate(5, 8, (var0, var1) -> {
         return -1;
      });
   }),
   MISSING(() -> {
      boolean var0 = true;
      boolean var1 = true;
      return generate(5, 8, (var0x, var1x) -> {
         boolean var2 = var0x == 0 || var0x + 1 == 5 || var1x == 0 || var1x + 1 == 8;
         return var2 ? -1 : 0;
      });
   });

   final NativeImage image;

   private static NativeImage generate(int var0, int var1, PixelProvider var2) {
      NativeImage var3 = new NativeImage(NativeImage.Format.RGBA, var0, var1, false);

      for(int var4 = 0; var4 < var1; ++var4) {
         for(int var5 = 0; var5 < var0; ++var5) {
            var3.setPixelRGBA(var5, var4, var2.getColor(var5, var4));
         }
      }

      var3.untrack();
      return var3;
   }

   private SpecialGlyphs(Supplier var3) {
      this.image = (NativeImage)var3.get();
   }

   public float getAdvance() {
      return (float)(this.image.getWidth() + 1);
   }

   public BakedGlyph bake(Function<SheetGlyphInfo, BakedGlyph> var1) {
      return (BakedGlyph)var1.apply(new SheetGlyphInfo() {
         public int getPixelWidth() {
            return SpecialGlyphs.this.image.getWidth();
         }

         public int getPixelHeight() {
            return SpecialGlyphs.this.image.getHeight();
         }

         public float getOversample() {
            return 1.0F;
         }

         public void upload(int var1, int var2) {
            SpecialGlyphs.this.image.upload(0, var1, var2, false);
         }

         public boolean isColored() {
            return true;
         }
      });
   }

   // $FF: synthetic method
   private static SpecialGlyphs[] $values() {
      return new SpecialGlyphs[]{WHITE, MISSING};
   }

   @FunctionalInterface
   private interface PixelProvider {
      int getColor(int var1, int var2);
   }
}
