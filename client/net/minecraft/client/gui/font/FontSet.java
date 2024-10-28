package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class FontSet implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RandomSource RANDOM = RandomSource.create();
   private static final float LARGE_FORWARD_ADVANCE = 32.0F;
   private final TextureManager textureManager;
   private final ResourceLocation name;
   private BakedGlyph missingGlyph;
   private BakedGlyph whiteGlyph;
   private List<GlyphProvider.Conditional> allProviders = List.of();
   private List<GlyphProvider> activeProviders = List.of();
   private final CodepointMap<BakedGlyph> glyphs = new CodepointMap((var0) -> {
      return new BakedGlyph[var0];
   }, (var0) -> {
      return new BakedGlyph[var0][];
   });
   private final CodepointMap<GlyphInfoFilter> glyphInfos = new CodepointMap((var0) -> {
      return new GlyphInfoFilter[var0];
   }, (var0) -> {
      return new GlyphInfoFilter[var0][];
   });
   private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
   private final List<FontTexture> textures = Lists.newArrayList();
   private final IntFunction<GlyphInfoFilter> glyphInfoGetter = this::computeGlyphInfo;
   private final IntFunction<BakedGlyph> glyphGetter = this::computeBakedGlyph;

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
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         GlyphProvider.Conditional var6 = (GlyphProvider.Conditional)var5.next();
         if (var6.filter().apply(var2)) {
            var4.add(var6.provider());
            var3.addAll(var6.provider().getSupportedGlyphs());
         }
      }

      HashSet var7 = Sets.newHashSet();
      var3.forEach((var3x) -> {
         Iterator var4x = var4.iterator();

         while(var4x.hasNext()) {
            GlyphProvider var5 = (GlyphProvider)var4x.next();
            GlyphInfo var6 = var5.getGlyph(var3x);
            if (var6 != null) {
               var7.add(var5);
               if (var6 != SpecialGlyphs.MISSING) {
                  ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(var6.getAdvance(false)), (var0) -> {
                     return new IntArrayList();
                  })).add(var3x);
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
      this.closeTextures();
   }

   private void closeTextures() {
      Iterator var1 = this.textures.iterator();

      while(var1.hasNext()) {
         FontTexture var2 = (FontTexture)var1.next();
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

   private GlyphInfoFilter computeGlyphInfo(int var1) {
      GlyphInfo var2 = null;
      Iterator var3 = this.activeProviders.iterator();

      while(var3.hasNext()) {
         GlyphProvider var4 = (GlyphProvider)var3.next();
         GlyphInfo var5 = var4.getGlyph(var1);
         if (var5 != null) {
            if (var2 == null) {
               var2 = var5;
            }

            if (!hasFishyAdvance(var5)) {
               return new GlyphInfoFilter(var2, var5);
            }
         }
      }

      if (var2 != null) {
         return new GlyphInfoFilter(var2, SpecialGlyphs.MISSING);
      } else {
         return FontSet.GlyphInfoFilter.MISSING;
      }
   }

   public GlyphInfo getGlyphInfo(int var1, boolean var2) {
      return ((GlyphInfoFilter)this.glyphInfos.computeIfAbsent(var1, this.glyphInfoGetter)).select(var2);
   }

   private BakedGlyph computeBakedGlyph(int var1) {
      Iterator var2 = this.activeProviders.iterator();

      GlyphInfo var4;
      do {
         if (!var2.hasNext()) {
            LOGGER.warn("Couldn't find glyph for character {} (\\u{})", Character.toString(var1), String.format("%04x", var1));
            return this.missingGlyph;
         }

         GlyphProvider var3 = (GlyphProvider)var2.next();
         var4 = var3.getGlyph(var1);
      } while(var4 == null);

      return var4.bake(this::stitch);
   }

   public BakedGlyph getGlyph(int var1) {
      return (BakedGlyph)this.glyphs.computeIfAbsent(var1, this.glyphGetter);
   }

   private BakedGlyph stitch(SheetGlyphInfo var1) {
      Iterator var2 = this.textures.iterator();

      BakedGlyph var4;
      do {
         if (!var2.hasNext()) {
            ResourceLocation var7 = this.name.withSuffix("/" + this.textures.size());
            boolean var8 = var1.isColored();
            GlyphRenderTypes var9 = var8 ? GlyphRenderTypes.createForColorTexture(var7) : GlyphRenderTypes.createForIntensityTexture(var7);
            FontTexture var5 = new FontTexture(var9, var8);
            this.textures.add(var5);
            this.textureManager.register((ResourceLocation)var7, (AbstractTexture)var5);
            BakedGlyph var6 = var5.add(var1);
            return var6 == null ? this.missingGlyph : var6;
         }

         FontTexture var3 = (FontTexture)var2.next();
         var4 = var3.add(var1);
      } while(var4 == null);

      return var4;
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

   private static record GlyphInfoFilter(GlyphInfo glyphInfo, GlyphInfo glyphInfoNotFishy) {
      static final GlyphInfoFilter MISSING;

      GlyphInfoFilter(GlyphInfo var1, GlyphInfo var2) {
         super();
         this.glyphInfo = var1;
         this.glyphInfoNotFishy = var2;
      }

      GlyphInfo select(boolean var1) {
         return var1 ? this.glyphInfoNotFishy : this.glyphInfo;
      }

      public GlyphInfo glyphInfo() {
         return this.glyphInfo;
      }

      public GlyphInfo glyphInfoNotFishy() {
         return this.glyphInfoNotFishy;
      }

      static {
         MISSING = new GlyphInfoFilter(SpecialGlyphs.MISSING, SpecialGlyphs.MISSING);
      }
   }
}
