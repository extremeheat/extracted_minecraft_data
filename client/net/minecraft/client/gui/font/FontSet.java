package net.minecraft.client.gui.font;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphBitmap;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.UnbakedGlyph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EffectGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class FontSet implements AutoCloseable {
   private static final float LARGE_FORWARD_ADVANCE = 32.0F;
   private static final BakedGlyph INVISIBLE_MISSING_GLYPH = new BakedGlyph() {
      public GlyphInfo info() {
         return SpecialGlyphs.MISSING;
      }

      public TextRenderable.@Nullable Styled createGlyph(final float x, final float y, final int color, final int shadowColor, final Style style, final float boldOffset, final float shadowOffset) {
         return null;
      }
   };
   private final GlyphStitcher stitcher;
   private final UnbakedGlyph.Stitcher wrappedStitcher = new UnbakedGlyph.Stitcher() {
      {
         Objects.requireNonNull(FontSet.this);
      }

      public BakedGlyph stitch(final GlyphInfo glyphInfo, final GlyphBitmap glyphBitmap) {
         return (BakedGlyph)Objects.requireNonNullElse(FontSet.this.stitcher.stitch(glyphInfo, glyphBitmap), FontSet.this.missingGlyph);
      }

      public BakedGlyph getMissing() {
         return FontSet.this.missingGlyph;
      }
   };
   private List<GlyphProvider.Conditional> allProviders = List.of();
   private List<GlyphProvider> activeProviders = List.of();
   private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
   private final CodepointMap<SelectedGlyphs> glyphCache = new CodepointMap<SelectedGlyphs>((x$0) -> new SelectedGlyphs[x$0], (x$0) -> new SelectedGlyphs[x$0][]);
   private final IntFunction<SelectedGlyphs> glyphGetter = this::computeGlyphInfo;
   private BakedGlyph missingGlyph;
   private final Supplier<BakedGlyph> missingGlyphGetter;
   private final SelectedGlyphs missingSelectedGlyphs;
   private @Nullable EffectGlyph whiteGlyph;
   private final GlyphSource anyGlyphs;
   private final GlyphSource nonFishyGlyphs;

   public FontSet(final GlyphStitcher stitcher) {
      super();
      this.missingGlyph = INVISIBLE_MISSING_GLYPH;
      this.missingGlyphGetter = () -> this.missingGlyph;
      this.missingSelectedGlyphs = new SelectedGlyphs(this.missingGlyphGetter, this.missingGlyphGetter);
      this.anyGlyphs = new Source(false);
      this.nonFishyGlyphs = new Source(true);
      this.stitcher = stitcher;
   }

   public void reload(final List<GlyphProvider.Conditional> providers, final Set<FontOption> options) {
      this.allProviders = providers;
      this.reload(options);
   }

   public void reload(final Set<FontOption> options) {
      this.activeProviders = List.of();
      this.resetTextures();
      this.activeProviders = this.selectProviders(this.allProviders, options);
   }

   private void resetTextures() {
      this.stitcher.reset();
      this.glyphCache.clear();
      this.glyphsByWidth.clear();
      this.missingGlyph = (BakedGlyph)Objects.requireNonNull(SpecialGlyphs.MISSING.bake(this.stitcher));
      this.whiteGlyph = SpecialGlyphs.WHITE.bake(this.stitcher);
   }

   private List<GlyphProvider> selectProviders(final List<GlyphProvider.Conditional> providers, final Set<FontOption> options) {
      IntSet supportedGlyphs = new IntOpenHashSet();
      List<GlyphProvider> selectedProviders = new ArrayList();

      for(GlyphProvider.Conditional conditionalProvider : providers) {
         if (conditionalProvider.filter().apply(options)) {
            selectedProviders.add(conditionalProvider.provider());
            supportedGlyphs.addAll(conditionalProvider.provider().getSupportedGlyphs());
         }
      }

      Set<GlyphProvider> usedProviders = Sets.newHashSet();
      supportedGlyphs.forEach((codepoint) -> {
         for(GlyphProvider provider : selectedProviders) {
            UnbakedGlyph glyph = provider.getGlyph(codepoint);
            if (glyph != null) {
               usedProviders.add(provider);
               if (glyph.info() != SpecialGlyphs.MISSING) {
                  ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(glyph.info().getAdvance(false)), (w) -> new IntArrayList())).add(codepoint);
               }
               break;
            }
         }

      });
      Stream var10000 = selectedProviders.stream();
      Objects.requireNonNull(usedProviders);
      return var10000.filter(usedProviders::contains).toList();
   }

   public void close() {
      this.stitcher.close();
   }

   private static boolean hasFishyAdvance(final GlyphInfo glyph) {
      float advance = glyph.getAdvance(false);
      if (!(advance < 0.0F) && !(advance > 32.0F)) {
         float boldAdvance = glyph.getAdvance(true);
         return boldAdvance < 0.0F || boldAdvance > 32.0F;
      } else {
         return true;
      }
   }

   private SelectedGlyphs computeGlyphInfo(final int codepoint) {
      DelayedBake firstGlyph = null;

      for(GlyphProvider provider : this.activeProviders) {
         UnbakedGlyph glyph = provider.getGlyph(codepoint);
         if (glyph != null) {
            if (firstGlyph == null) {
               firstGlyph = new DelayedBake(glyph);
            }

            if (!hasFishyAdvance(glyph.info())) {
               if (firstGlyph.unbaked == glyph) {
                  return new SelectedGlyphs(firstGlyph, firstGlyph);
               }

               return new SelectedGlyphs(firstGlyph, new DelayedBake(glyph));
            }
         }
      }

      if (firstGlyph != null) {
         return new SelectedGlyphs(firstGlyph, this.missingGlyphGetter);
      } else {
         return this.missingSelectedGlyphs;
      }
   }

   private SelectedGlyphs getGlyph(final int codepoint) {
      return this.glyphCache.computeIfAbsent(codepoint, this.glyphGetter);
   }

   public BakedGlyph getRandomGlyph(final RandomSource random, final int width) {
      IntList chars = (IntList)this.glyphsByWidth.get(width);
      return chars != null && !chars.isEmpty() ? (BakedGlyph)this.getGlyph(chars.getInt(random.nextInt(chars.size()))).nonFishy().get() : this.missingGlyph;
   }

   public EffectGlyph whiteGlyph() {
      return (EffectGlyph)Objects.requireNonNull(this.whiteGlyph);
   }

   public GlyphSource source(final boolean nonFishyOnly) {
      return nonFishyOnly ? this.nonFishyGlyphs : this.anyGlyphs;
   }

   private class DelayedBake implements Supplier<BakedGlyph> {
      private final UnbakedGlyph unbaked;
      private @Nullable BakedGlyph baked;

      private DelayedBake(final UnbakedGlyph unbaked) {
         Objects.requireNonNull(FontSet.this);
         super();
         this.unbaked = unbaked;
      }

      public BakedGlyph get() {
         if (this.baked == null) {
            this.baked = this.unbaked.bake(FontSet.this.wrappedStitcher);
         }

         return this.baked;
      }
   }

   private static record SelectedGlyphs(Supplier<BakedGlyph> any, Supplier<BakedGlyph> nonFishy) {
      private SelectedGlyphs {
         super();
      }

      private Supplier<BakedGlyph> select(final boolean filterFishy) {
         return filterFishy ? this.nonFishy : this.any;
      }
   }

   public class Source implements GlyphSource {
      private final boolean filterFishyGlyphs;

      public Source(final boolean filterFishyGlyphs) {
         Objects.requireNonNull(FontSet.this);
         super();
         this.filterFishyGlyphs = filterFishyGlyphs;
      }

      public BakedGlyph getGlyph(final int codepoint) {
         return (BakedGlyph)FontSet.this.getGlyph(codepoint).select(this.filterFishyGlyphs).get();
      }

      public BakedGlyph getRandomGlyph(final RandomSource random, final int width) {
         return FontSet.this.getRandomGlyph(random, width);
      }
   }
}
