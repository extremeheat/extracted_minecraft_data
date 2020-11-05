package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;
import net.minecraft.client.gui.font.glyphs.WhiteGlyph;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FontSet implements AutoCloseable {
   private static final EmptyGlyph SPACE_GLYPH = new EmptyGlyph();
   private static final GlyphInfo SPACE_INFO = () -> {
      return 4.0F;
   };
   private static final Random RANDOM = new Random();
   private final TextureManager textureManager;
   private final ResourceLocation name;
   private BakedGlyph missingGlyph;
   private BakedGlyph whiteGlyph;
   private final List<GlyphProvider> providers = Lists.newArrayList();
   private final Int2ObjectMap<BakedGlyph> glyphs = new Int2ObjectOpenHashMap();
   private final Int2ObjectMap<GlyphInfo> glyphInfos = new Int2ObjectOpenHashMap();
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
      this.missingGlyph = this.stitch(MissingGlyph.INSTANCE);
      this.whiteGlyph = this.stitch(WhiteGlyph.INSTANCE);
      IntOpenHashSet var2 = new IntOpenHashSet();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         GlyphProvider var4 = (GlyphProvider)var3.next();
         var2.addAll(var4.getSupportedGlyphs());
      }

      HashSet var5 = Sets.newHashSet();
      var2.forEach((var3x) -> {
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            GlyphProvider var5x = (GlyphProvider)var4.next();
            Object var6 = var3x == 32 ? SPACE_INFO : var5x.getGlyph(var3x);
            if (var6 != null) {
               var5.add(var5x);
               if (var6 != MissingGlyph.INSTANCE) {
                  ((IntList)this.glyphsByWidth.computeIfAbsent(Mth.ceil(((GlyphInfo)var6).getAdvance(false)), (var0) -> {
                     return new IntArrayList();
                  })).add(var3x);
               }
               break;
            }
         }

      });
      Stream var10000 = var1.stream();
      var5.getClass();
      var10000 = var10000.filter(var5::contains);
      List var10001 = this.providers;
      var10000.forEach(var10001::add);
   }

   public void close() {
      this.closeProviders();
      this.closeTextures();
   }

   private void closeProviders() {
      Iterator var1 = this.providers.iterator();

      while(var1.hasNext()) {
         GlyphProvider var2 = (GlyphProvider)var1.next();
         var2.close();
      }

      this.providers.clear();
   }

   private void closeTextures() {
      Iterator var1 = this.textures.iterator();

      while(var1.hasNext()) {
         FontTexture var2 = (FontTexture)var1.next();
         var2.close();
      }

      this.textures.clear();
   }

   public GlyphInfo getGlyphInfo(int var1) {
      return (GlyphInfo)this.glyphInfos.computeIfAbsent(var1, (var1x) -> {
         return (GlyphInfo)(var1x == 32 ? SPACE_INFO : this.getRaw(var1x));
      });
   }

   private RawGlyph getRaw(int var1) {
      Iterator var2 = this.providers.iterator();

      RawGlyph var4;
      do {
         if (!var2.hasNext()) {
            return MissingGlyph.INSTANCE;
         }

         GlyphProvider var3 = (GlyphProvider)var2.next();
         var4 = var3.getGlyph(var1);
      } while(var4 == null);

      return var4;
   }

   public BakedGlyph getGlyph(int var1) {
      return (BakedGlyph)this.glyphs.computeIfAbsent(var1, (var1x) -> {
         return (BakedGlyph)(var1x == 32 ? SPACE_GLYPH : this.stitch(this.getRaw(var1x)));
      });
   }

   private BakedGlyph stitch(RawGlyph var1) {
      Iterator var2 = this.textures.iterator();

      BakedGlyph var4;
      do {
         if (!var2.hasNext()) {
            FontTexture var5 = new FontTexture(new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "/" + this.textures.size()), var1.isColored());
            this.textures.add(var5);
            this.textureManager.register((ResourceLocation)var5.getName(), (AbstractTexture)var5);
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

   public BakedGlyph whiteGlyph() {
      return this.whiteGlyph;
   }
}
