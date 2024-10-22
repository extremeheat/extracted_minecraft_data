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
import java.util.List;
import java.util.Set;
import java.util.function.IntFunction;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.SpecialGlyphs;
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
   private final CodepointMap<BakedGlyph> glyphs = new CodepointMap<>(BakedGlyph[]::new, BakedGlyph[][]::new);
   private final CodepointMap<FontSet.GlyphInfoFilter> glyphInfos = new CodepointMap<>(FontSet.GlyphInfoFilter[]::new, FontSet.GlyphInfoFilter[][]::new);
   private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap();
   private final List<FontTexture> textures = Lists.newArrayList();
   private final IntFunction<FontSet.GlyphInfoFilter> glyphInfoGetter = this::computeGlyphInfo;
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

      for (GlyphProvider.Conditional var6 : var1) {
         if (var6.filter().apply(var2)) {
            var4.add(var6.provider());
            var3.addAll(var6.provider().getSupportedGlyphs());
         }
      }

      HashSet var7 = Sets.newHashSet();
      var3.forEach(var3x -> {
         for (GlyphProvider var5 : var4) {
            GlyphInfo var6x = var5.getGlyph(var3x);
            if (var6x != null) {
               var7.add(var5);
               if (var6x != SpecialGlyphs.MISSING) {
                  ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(var6x.getAdvance(false)), var0 -> new IntArrayList())).add(var3x);
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
      for (FontTexture var2 : this.textures) {
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

      for (GlyphProvider var4 : this.activeProviders) {
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
      return this.glyphInfos.computeIfAbsent(var1, this.glyphInfoGetter).select(var2);
   }

   private BakedGlyph computeBakedGlyph(int var1) {
      for (GlyphProvider var3 : this.activeProviders) {
         GlyphInfo var4 = var3.getGlyph(var1);
         if (var4 != null) {
            return var4.bake(this::stitch);
         }
      }

      LOGGER.warn("Couldn't find glyph for character {} (\\u{})", Character.toString(var1), String.format("%04x", var1));
      return this.missingGlyph;
   }

   public BakedGlyph getGlyph(int var1) {
      return this.glyphs.computeIfAbsent(var1, this.glyphGetter);
   }

   private BakedGlyph stitch(SheetGlyphInfo var1) {
      for (FontTexture var3 : this.textures) {
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
