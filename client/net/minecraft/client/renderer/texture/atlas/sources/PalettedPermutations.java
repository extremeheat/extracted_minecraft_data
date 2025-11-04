package net.minecraft.client.renderer.texture.atlas.sources;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record PalettedPermutations(List<Identifier> textures, Identifier paletteKey, Map<String, Identifier> permutations, String separator) implements SpriteSource {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final String DEFAULT_SEPARATOR = "_";
   public static final MapCodec<PalettedPermutations> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(PalettedPermutations::textures), Identifier.CODEC.fieldOf("palette_key").forGetter(PalettedPermutations::paletteKey), Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("permutations").forGetter(PalettedPermutations::permutations), Codec.STRING.optionalFieldOf("separator", "_").forGetter(PalettedPermutations::separator)).apply(var0, PalettedPermutations::new));

   public PalettedPermutations(List<Identifier> var1, Identifier var2, Map<String, Identifier> var3) {
      this(var1, var2, var3, "_");
   }

   public PalettedPermutations(List<Identifier> var1, Identifier var2, Map<String, Identifier> var3, String var4) {
      super();
      this.textures = var1;
      this.paletteKey = var2;
      this.permutations = var3;
      this.separator = var4;
   }

   public void run(ResourceManager var1, SpriteSource.Output var2) {
      Supplier var3 = Suppliers.memoize(() -> loadPaletteEntryFromImage(var1, this.paletteKey));
      HashMap var4 = new HashMap();
      this.permutations.forEach((var3x, var4x) -> var4.put(var3x, Suppliers.memoize(() -> createPaletteMapping((int[])var3.get(), loadPaletteEntryFromImage(var1, var4x)))));

      for(Identifier var6 : this.textures) {
         Identifier var7 = TEXTURE_ID_CONVERTER.idToFile(var6);
         Optional var8 = var1.getResource(var7);
         if (var8.isEmpty()) {
            LOGGER.warn("Unable to find texture {}", var7);
         } else {
            LazyLoadedImage var9 = new LazyLoadedImage(var7, (Resource)var8.get(), var4.size());

            for(Map.Entry var11 : var4.entrySet()) {
               String var10001 = this.separator;
               Identifier var12 = var6.withSuffix(var10001 + (String)var11.getKey());
               var2.add(var12, (SpriteSource.DiscardableLoader)(new PalettedSpriteSupplier(var9, (java.util.function.Supplier)var11.getValue(), var12)));
            }
         }
      }

   }

   private static IntUnaryOperator createPaletteMapping(int[] var0, int[] var1) {
      if (var1.length != var0.length) {
         LOGGER.warn("Palette mapping has different sizes: {} and {}", var0.length, var1.length);
         throw new IllegalArgumentException();
      } else {
         Int2IntOpenHashMap var2 = new Int2IntOpenHashMap(var1.length);

         for(int var3 = 0; var3 < var0.length; ++var3) {
            int var4 = var0[var3];
            if (ARGB.alpha(var4) != 0) {
               var2.put(ARGB.transparent(var4), var1[var3]);
            }
         }

         return (var1x) -> {
            int var2x = ARGB.alpha(var1x);
            if (var2x == 0) {
               return var1x;
            } else {
               int var3 = ARGB.transparent(var1x);
               int var4 = var2.getOrDefault(var3, ARGB.opaque(var3));
               int var5 = ARGB.alpha(var4);
               return ARGB.color(var2x * var5 / 255, var4);
            }
         };
      }
   }

   private static int[] loadPaletteEntryFromImage(ResourceManager var0, Identifier var1) {
      Optional var2 = var0.getResource(TEXTURE_ID_CONVERTER.idToFile(var1));
      if (var2.isEmpty()) {
         LOGGER.error("Failed to load palette image {}", var1);
         throw new IllegalArgumentException();
      } else {
         try {
            InputStream var3 = ((Resource)var2.get()).open();

            int[] var5;
            try (NativeImage var4 = NativeImage.read(var3)) {
               var5 = var4.getPixels();
            } catch (Throwable var10) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }
               }

               throw var10;
            }

            if (var3 != null) {
               var3.close();
            }

            return var5;
         } catch (Exception var11) {
            LOGGER.error("Couldn't load texture {}", var1, var11);
            throw new IllegalArgumentException();
         }
      }
   }

   public MapCodec<PalettedPermutations> codec() {
      return MAP_CODEC;
   }

   static record PalettedSpriteSupplier(LazyLoadedImage baseImage, java.util.function.Supplier<IntUnaryOperator> palette, Identifier permutationLocation) implements SpriteSource.DiscardableLoader {
      PalettedSpriteSupplier(LazyLoadedImage var1, java.util.function.Supplier<IntUnaryOperator> var2, Identifier var3) {
         super();
         this.baseImage = var1;
         this.palette = var2;
         this.permutationLocation = var3;
      }

      public @Nullable SpriteContents get(SpriteResourceLoader var1) {
         SpriteContents var3;
         try {
            NativeImage var2 = this.baseImage.get().mappedCopy((IntUnaryOperator)this.palette.get());
            var3 = new SpriteContents(this.permutationLocation, new FrameSize(var2.getWidth(), var2.getHeight()), var2);
            return var3;
         } catch (IllegalArgumentException | IOException var7) {
            PalettedPermutations.LOGGER.error("unable to apply palette to {}", this.permutationLocation, var7);
            var3 = null;
         } finally {
            this.baseImage.release();
         }

         return var3;
      }

      public void discard() {
         this.baseImage.release();
      }
   }
}
