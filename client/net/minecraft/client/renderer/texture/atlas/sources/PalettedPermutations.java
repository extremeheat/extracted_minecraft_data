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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.FastColor;
import org.slf4j.Logger;

public class PalettedPermutations implements SpriteSource {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<PalettedPermutations> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.list(ResourceLocation.CODEC).fieldOf("textures").forGetter((var0x) -> {
         return var0x.textures;
      }), ResourceLocation.CODEC.fieldOf("palette_key").forGetter((var0x) -> {
         return var0x.paletteKey;
      }), Codec.unboundedMap(Codec.STRING, ResourceLocation.CODEC).fieldOf("permutations").forGetter((var0x) -> {
         return var0x.permutations;
      })).apply(var0, PalettedPermutations::new);
   });
   private final List<ResourceLocation> textures;
   private final Map<String, ResourceLocation> permutations;
   private final ResourceLocation paletteKey;

   private PalettedPermutations(List<ResourceLocation> var1, ResourceLocation var2, Map<String, ResourceLocation> var3) {
      super();
      this.textures = var1;
      this.permutations = var3;
      this.paletteKey = var2;
   }

   public void run(ResourceManager var1, SpriteSource.Output var2) {
      Supplier var3 = Suppliers.memoize(() -> {
         return loadPaletteEntryFromImage(var1, this.paletteKey);
      });
      HashMap var4 = new HashMap();
      this.permutations.forEach((var3x, var4x) -> {
         var4.put(var3x, Suppliers.memoize(() -> {
            return createPaletteMapping((int[])var3.get(), loadPaletteEntryFromImage(var1, var4x));
         }));
      });
      Iterator var5 = this.textures.iterator();

      while(true) {
         while(var5.hasNext()) {
            ResourceLocation var6 = (ResourceLocation)var5.next();
            ResourceLocation var7 = TEXTURE_ID_CONVERTER.idToFile(var6);
            Optional var8 = var1.getResource(var7);
            if (var8.isEmpty()) {
               LOGGER.warn("Unable to find texture {}", var7);
            } else {
               LazyLoadedImage var9 = new LazyLoadedImage(var7, (Resource)var8.get(), var4.size());
               Iterator var10 = var4.entrySet().iterator();

               while(var10.hasNext()) {
                  Map.Entry var11 = (Map.Entry)var10.next();
                  ResourceLocation var12 = var6.withSuffix("_" + (String)var11.getKey());
                  var2.add(var12, (SpriteSource.SpriteSupplier)(new PalettedSpriteSupplier(var9, (java.util.function.Supplier)var11.getValue(), var12)));
               }
            }
         }

         return;
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
            if (FastColor.ABGR32.alpha(var4) != 0) {
               var2.put(FastColor.ABGR32.transparent(var4), var1[var3]);
            }
         }

         return (var1x) -> {
            int var2x = FastColor.ABGR32.alpha(var1x);
            if (var2x == 0) {
               return var1x;
            } else {
               int var3 = FastColor.ABGR32.transparent(var1x);
               int var4 = var2.getOrDefault(var3, FastColor.ABGR32.opaque(var3));
               int var5 = FastColor.ABGR32.alpha(var4);
               return FastColor.ABGR32.color(var2x * var5 / 255, var4);
            }
         };
      }
   }

   public static int[] loadPaletteEntryFromImage(ResourceManager param0, ResourceLocation param1) {
      // $FF: Couldn't be decompiled
   }

   public SpriteSourceType type() {
      return SpriteSources.PALETTED_PERMUTATIONS;
   }

   static record PalettedSpriteSupplier(LazyLoadedImage baseImage, java.util.function.Supplier<IntUnaryOperator> palette, ResourceLocation permutationLocation) implements SpriteSource.SpriteSupplier {
      PalettedSpriteSupplier(LazyLoadedImage var1, java.util.function.Supplier<IntUnaryOperator> var2, ResourceLocation var3) {
         super();
         this.baseImage = var1;
         this.palette = var2;
         this.permutationLocation = var3;
      }

      @Nullable
      public SpriteContents apply(SpriteResourceLoader var1) {
         SpriteContents var3;
         try {
            NativeImage var2 = this.baseImage.get().mappedCopy((IntUnaryOperator)this.palette.get());
            var3 = new SpriteContents(this.permutationLocation, new FrameSize(var2.getWidth(), var2.getHeight()), var2, ResourceMetadata.EMPTY);
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

      public LazyLoadedImage baseImage() {
         return this.baseImage;
      }

      public java.util.function.Supplier<IntUnaryOperator> palette() {
         return this.palette;
      }

      public ResourceLocation permutationLocation() {
         return this.permutationLocation;
      }

      // $FF: synthetic method
      @Nullable
      public Object apply(final Object var1) {
         return this.apply((SpriteResourceLoader)var1);
      }
   }
}
