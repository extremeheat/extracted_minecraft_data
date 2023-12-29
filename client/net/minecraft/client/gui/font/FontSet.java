package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FontSet implements AutoCloseable {
   private static final RandomSource RANDOM = RandomSource.create();
   private static final float LARGE_FORWARD_ADVANCE = 32.0F;
   private final TextureManager textureManager;
   private final ResourceLocation name;
   private BakedGlyph missingGlyph;
   private BakedGlyph whiteGlyph;
   private final List<GlyphProvider> providers = Lists.newArrayList();
   private final CodepointMap<BakedGlyph> glyphs = new CodepointMap<>(var0 -> new BakedGlyph[var0], var0 -> new BakedGlyph[var0][]);
   private final CodepointMap<FontSet.GlyphInfoFilter> glyphInfos = new CodepointMap<>(
      var0 -> new FontSet.GlyphInfoFilter[var0], var0 -> new FontSet.GlyphInfoFilter[var0][]
   );
   private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
   private final List<FontTexture> textures = Lists.newArrayList();

   public FontSet(TextureManager var1, ResourceLocation var2) {
      super();
      this.textureManager = var1;
      this.name = var2;
   }

   public void reload(List<GlyphProvider> var1) {
      this.closeProviders();
      this.closeTextures();
      this.glyphs.clear();
      this.glyphInfos.clear();
      this.glyphsByWidth.clear();
      this.missingGlyph = SpecialGlyphs.MISSING.bake(this::stitch);
      this.whiteGlyph = SpecialGlyphs.WHITE.bake(this::stitch);
      IntOpenHashSet var2 = new IntOpenHashSet();

      for(GlyphProvider var4 : var1) {
         var2.addAll(var4.getSupportedGlyphs());
      }

      HashSet var5 = Sets.newHashSet();
      var2.forEach(var3 -> {
         for(GlyphProvider var5xx : var1) {
            GlyphInfo var6 = var5xx.getGlyph(var3);
            if (var6 != null) {
               var5.add(var5xx);
               if (var6 != SpecialGlyphs.MISSING) {
                  ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(var6.getAdvance(false)), var0 -> new IntArrayList())).add(var3);
               }
               break;
            }
         }
      });
      var1.stream().filter(var5::contains).forEach(this.providers::add);
   }

   @Override
   public void close() {
      this.closeProviders();
      this.closeTextures();
   }

   private void closeProviders() {
      for(GlyphProvider var2 : this.providers) {
         var2.close();
      }

      this.providers.clear();
   }

   private void closeTextures() {
      for(FontTexture var2 : this.textures) {
         var2.close();
      }

      this.textures.clear();
   }

   private static boolean hasFishyAdvance(GlyphInfo var0) {
      float var1 = var0.getAdvance(false);
      if (!(var1 < 0.0F) && !(var1 > 32.0F)) {
         float var2 = var0.getAdvance(true);
         return var2 < 0.0F || var2 > 32.0F;
      } else {
         return true;
      }
   }

   private FontSet.GlyphInfoFilter computeGlyphInfo(int var1) {
      GlyphInfo var2 = null;

      for(GlyphProvider var4 : this.providers) {
         GlyphInfo var5 = var4.getGlyph(var1);
         if (var5 != null) {
            if (var2 == null) {
               var2 = var5;
            }

            if (!hasFishyAdvance(var5)) {
               return new FontSet.GlyphInfoFilter(var2, var5);
            }
         }
      }

      return var2 != null ? new FontSet.GlyphInfoFilter(var2, SpecialGlyphs.MISSING) : FontSet.GlyphInfoFilter.MISSING;
   }

   public GlyphInfo getGlyphInfo(int var1, boolean var2) {
      return this.glyphInfos.computeIfAbsent(var1, this::computeGlyphInfo).select(var2);
   }

   private BakedGlyph computeBakedGlyph(int var1) {
      for(GlyphProvider var3 : this.providers) {
         GlyphInfo var4 = var3.getGlyph(var1);
         if (var4 != null) {
            return var4.bake(this::stitch);
         }
      }

      return this.missingGlyph;
   }

   public BakedGlyph getGlyph(int var1) {
      return this.glyphs.computeIfAbsent(var1, this::computeBakedGlyph);
   }

   private BakedGlyph stitch(SheetGlyphInfo var1) {
      for(FontTexture var3 : this.textures) {
         BakedGlyph var4 = var3.add(var1);
         if (var4 != null) {
            return var4;
         }
      }

      ResourceLocation var7 = this.name.withSuffix("/" + this.textures.size());
      boolean var8 = var1.isColored();
      GlyphRenderTypes var9 = var8 ? GlyphRenderTypes.createForColorTexture(var7) : GlyphRenderTypes.createForIntensityTexture(var7);
      FontTexture var5 = new FontTexture(var9, var8);
      this.textures.add(var5);
      this.textureManager.register(var7, var5);
      BakedGlyph var6 = var5.add(var1);
      return var6 == null ? this.missingGlyph : var6;
   }

   public BakedGlyph getRandomGlyph(GlyphInfo var1) {
      IntList var2 = (IntList)this.glyphsByWidth.get(Mth.ceil(var1.getAdvance(false)));
      return var2 != null && !var2.isEmpty() ? this.getGlyph(var2.getInt(RANDOM.nextInt(var2.size()))) : this.missingGlyph;
   }

   public BakedGlyph whiteGlyph() {
      return this.whiteGlyph;
   }

   static record GlyphInfoFilter(GlyphInfo a, GlyphInfo b) {
      private final GlyphInfo glyphInfo;
      private final GlyphInfo glyphInfoNotFishy;
      static final FontSet.GlyphInfoFilter MISSING = new FontSet.GlyphInfoFilter(SpecialGlyphs.MISSING, SpecialGlyphs.MISSING);

      GlyphInfoFilter(GlyphInfo var1, GlyphInfo var2) {
         super();
         this.glyphInfo = var1;
         this.glyphInfoNotFishy = var2;
      }

      GlyphInfo select(boolean var1) {
         return var1 ? this.glyphInfoNotFishy : this.glyphInfo;
      }
   }
}
