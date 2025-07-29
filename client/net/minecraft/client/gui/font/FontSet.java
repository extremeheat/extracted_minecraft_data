package net.minecraft.client.gui.font;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakeableGlyph;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FontSet implements AutoCloseable {
   private static final float LARGE_FORWARD_ADVANCE = 32.0F;
   final GlyphStitcher stitcher;
   private final ResourceLocation name;
   private List<GlyphProvider.Conditional> allProviders = List.of();
   private List<GlyphProvider> activeProviders = List.of();
   private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
   private final CodepointMap<SelectedGlyphs> glyphCache = new CodepointMap<SelectedGlyphs>((var0) -> new SelectedGlyphs[var0], (var0) -> new SelectedGlyphs[var0][]);
   private final IntFunction<SelectedGlyphs> glyphGetter = this::computeGlyphInfo;
   final BakeableGlyphImpl missingGlyph;
   private final SelectedGlyphs missingSelectedGlyphs;
   private final BakeableGlyphImpl whiteGlyph;
   private final GlyphSource anyGlyphs = new Source(false);
   private final GlyphSource nonFishyGlyphs = new Source(true);

   public FontSet(GlyphStitcher var1, ResourceLocation var2) {
      super();
      this.stitcher = var1;
      this.name = var2;
      this.missingGlyph = new BakeableGlyphImpl(SpecialGlyphs.MISSING);
      this.missingSelectedGlyphs = new SelectedGlyphs(this.missingGlyph, this.missingGlyph);
      this.whiteGlyph = new BakeableGlyphImpl(SpecialGlyphs.WHITE);
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
      this.stitcher.reset();
      this.glyphCache.clear();
      this.glyphsByWidth.clear();
      this.missingGlyph.forceBake();
      this.whiteGlyph.forceBake();
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
      var3.forEach((var3x) -> {
         for(GlyphProvider var5 : var4) {
            GlyphInfo var6 = var5.getGlyph(var3x);
            if (var6 != null) {
               var7.add(var5);
               if (var6 != SpecialGlyphs.MISSING) {
                  ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(var6.getAdvance(false)), (var0) -> new IntArrayList())).add(var3x);
               }
               break;
            }
         }

      });
      Stream var10000 = var4.stream();
      Objects.requireNonNull(var7);
      return var10000.filter(var7::contains).toList();
   }

   public void close() {
      this.stitcher.close();
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

   private SelectedGlyphs computeGlyphInfo(int var1) {
      BakeableGlyphImpl var2 = null;

      for(GlyphProvider var4 : this.activeProviders) {
         GlyphInfo var5 = var4.getGlyph(var1);
         if (var5 != null) {
            if (var2 == null) {
               var2 = new BakeableGlyphImpl(var5);
            }

            if (!hasFishyAdvance(var5)) {
               if (var2.info == var5) {
                  return new SelectedGlyphs(var2, var2);
               }

               return new SelectedGlyphs(var2, new BakeableGlyphImpl(var5));
            }
         }
      }

      if (var2 != null) {
         return new SelectedGlyphs(var2, this.missingGlyph);
      } else {
         return this.missingSelectedGlyphs;
      }
   }

   SelectedGlyphs getGlyph(int var1) {
      return this.glyphCache.computeIfAbsent(var1, this.glyphGetter);
   }

   public BakeableGlyph getRandomGlyph(RandomSource var1, int var2) {
      IntList var3 = (IntList)this.glyphsByWidth.get(var2);
      return var3 != null && !var3.isEmpty() ? this.getGlyph(var3.getInt(var1.nextInt(var3.size()))).nonFishy() : this.missingGlyph;
   }

   public ResourceLocation name() {
      return this.name;
   }

   public BakeableGlyph whiteGlyph() {
      return this.whiteGlyph;
   }

   public GlyphSource source(boolean var1) {
      return var1 ? this.nonFishyGlyphs : this.anyGlyphs;
   }

   class BakeableGlyphImpl implements BakeableGlyph {
      final GlyphInfo info;
      @Nullable
      private BakedGlyph baked;

      BakeableGlyphImpl(final GlyphInfo var2) {
         super();
         this.info = var2;
      }

      public GlyphInfo info() {
         return this.info;
      }

      private BakedGlyph bake() {
         BakedGlyph var1 = this.info.bake(FontSet.this.stitcher);
         return (BakedGlyph)Objects.requireNonNullElse(var1, FontSet.this.missingGlyph.baked);
      }

      public BakedGlyph baked() {
         if (this.baked == null) {
            this.baked = this.bake();
         }

         return this.baked;
      }

      public void forceBake() {
         this.baked = this.bake();
      }
   }

   static record SelectedGlyphs(BakeableGlyphImpl any, BakeableGlyphImpl nonFishy) {
      SelectedGlyphs(BakeableGlyphImpl var1, BakeableGlyphImpl var2) {
         super();
         this.any = var1;
         this.nonFishy = var2;
      }

      BakeableGlyphImpl select(boolean var1) {
         return var1 ? this.nonFishy : this.any;
      }
   }

   public class Source implements GlyphSource {
      private final boolean filterFishyGlyphs;

      public Source(final boolean var2) {
         super();
         this.filterFishyGlyphs = var2;
      }

      public BakeableGlyph getGlyph(int var1) {
         return FontSet.this.getGlyph(var1).select(this.filterFishyGlyphs);
      }

      public BakeableGlyph getRandomGlyph(RandomSource var1, int var2) {
         return FontSet.this.getRandomGlyph(var1, var2);
      }
   }
}
