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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
   private List<GlyphProvider.Conditional> allProviders = List.of();
   private List<GlyphProvider> activeProviders = List.of();
   private final CodepointMap<BakedGlyph> glyphs = new CodepointMap<>(var0 -> new BakedGlyph[var0], var0 -> new BakedGlyph[var0][]);
   private final CodepointMap<FontSet.GlyphInfoFilter> glyphInfos = new CodepointMap(
      var0 -> new FontSet.GlyphInfoFilter[var0], var0 -> new FontSet.GlyphInfoFilter[var0][]
   );
   private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
   private final List<FontTexture> textures = Lists.newArrayList();

   public FontSet(TextureManager var1, ResourceLocation var2) {
      super();
      this.textureManager = var1;
      this.name = var2;
   }

   public void reload(List<GlyphProvider.Conditional> var1, Set<FontOption> var2) {
      this.allProviders = var1;
      this.reload(var2);
   }

   public void reload(Set<FontOption> var1) {
      this.activeProviders = List.of();
      this.resetTextures();
      this.activeProviders = this.selectProviders(this.allProviders, var1);
   }

   private void resetTextures() {
      this.closeTextures();
      this.glyphs.clear();
      this.glyphInfos.clear();
      this.glyphsByWidth.clear();
      this.missingGlyph = SpecialGlyphs.MISSING.bake(this::stitch);
      this.whiteGlyph = SpecialGlyphs.WHITE.bake(this::stitch);
   }

   private List<GlyphProvider> selectProviders(List<GlyphProvider.Conditional> var1, Set<FontOption> var2) {
      IntOpenHashSet var3 = new IntOpenHashSet();
      ArrayList var4 = new ArrayList();

      for(GlyphProvider.Conditional var6 : var1) {
         if (var6.filter().apply(var2)) {
            var4.add(var6.provider());
            var3.addAll(var6.provider().getSupportedGlyphs());
         }
      }

      HashSet var7 = Sets.newHashSet();
      var3.forEach(var3x -> {
         for(GlyphProvider var5 : var4) {
            GlyphInfo var6xx = var5.getGlyph(var3x);
            if (var6xx != null) {
               var7.add(var5);
               if (var6xx != SpecialGlyphs.MISSING) {
                  ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(var6xx.getAdvance(false)), var0 -> new IntArrayList())).add(var3x);
               }
               break;
            }
         }
      });
      return var4.stream().filter(var7::contains).toList();
   }

   @Override
   public void close() {
      this.closeTextures();
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

      for(GlyphProvider var4 : this.activeProviders) {
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
      return ((FontSet.GlyphInfoFilter)this.glyphInfos.computeIfAbsent(var1, this::computeGlyphInfo)).select(var2);
   }

   private BakedGlyph computeBakedGlyph(int var1) {
      for(GlyphProvider var3 : this.activeProviders) {
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

   public ResourceLocation name() {
      return this.name;
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
