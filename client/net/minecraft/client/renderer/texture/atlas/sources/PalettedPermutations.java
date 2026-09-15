package net.minecraft.client.renderer.texture.atlas.sources;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.palette.Palette;
import net.minecraft.client.resources.palette.PaletteMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record PalettedPermutations(List<Identifier> textures, Identifier paletteKey, Map<String, Identifier> permutations, String separator) implements SpriteSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String DEFAULT_SEPARATOR = "_";
   public static final MapCodec<PalettedPermutations> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(PalettedPermutations::textures), Identifier.CODEC.fieldOf("palette_key").forGetter(PalettedPermutations::paletteKey), Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("permutations").forGetter(PalettedPermutations::permutations), Codec.STRING.optionalFieldOf("separator", "_").forGetter(PalettedPermutations::separator)).apply(i, PalettedPermutations::new));

   public PalettedPermutations(final List<Identifier> textures, final Identifier paletteKey, final Map<String, Identifier> permutations) {
      this(textures, paletteKey, permutations, "_");
   }

   public PalettedPermutations {
      super();
   }

   public void run(final ResourceManager resourceManager, final SpriteSource.Output output) {
      Supplier<Palette> paletteKeySupplier = Suppliers.memoize(() -> loadPaletteEntryFromImage(resourceManager, this.paletteKey));
      Map<String, Supplier<PaletteMapping>> palettes = new HashMap();
      this.permutations.forEach((suffix, palette) -> palettes.put(suffix, Suppliers.memoize(() -> PaletteMapping.create((Palette)paletteKeySupplier.get(), loadPaletteEntryFromImage(resourceManager, palette)))));

      for(Identifier textureLocation : this.textures) {
         Identifier textureId = TEXTURE_ID_CONVERTER.idToFile(textureLocation);
         Optional<Resource> resource = resourceManager.getResource(textureId);
         if (resource.isEmpty()) {
            LOGGER.warn("Unable to find texture {}", textureId);
         } else {
            LazyLoadedImage baseImage = new LazyLoadedImage(textureId, (Resource)resource.get(), palettes.size());

            for(Map.Entry<String, Supplier<PaletteMapping>> entry : palettes.entrySet()) {
               String var10001 = this.separator;
               Identifier permutationLocation = textureLocation.withSuffix(var10001 + (String)entry.getKey());
               output.add(permutationLocation, (SpriteSource.DiscardableLoader)(new PalettedSpriteSupplier(baseImage, (Supplier)entry.getValue(), permutationLocation)));
            }
         }
      }

   }

   private static Palette loadPaletteEntryFromImage(final ResourceManager resourceManager, final Identifier paletteId) {
      Optional<Resource> resource = resourceManager.getResource(Palette.ID_CONVERTER.idToFile(paletteId));
      if (resource.isEmpty()) {
         LOGGER.error("Failed to load palette image {}", paletteId);
         throw new IllegalArgumentException();
      } else {
         try {
            return Palette.load((Resource)resource.get());
         } catch (Exception exception) {
            LOGGER.error("Couldn't load texture {}", paletteId, exception);
            throw new IllegalArgumentException();
         }
      }
   }

   public MapCodec<PalettedPermutations> codec() {
      return MAP_CODEC;
   }

   private static record PalettedSpriteSupplier(LazyLoadedImage baseImage, Supplier<PaletteMapping> palette, Identifier permutationLocation) implements SpriteSource.DiscardableLoader {
      private PalettedSpriteSupplier {
         super();
      }

      public @Nullable SpriteContents get(final SpriteResourceLoader loader) {
         SpriteContents var3;
         try {
            NativeImage image = this.baseImage.get().mappedCopy((IntUnaryOperator)this.palette.get());
            var3 = new SpriteContents(this.permutationLocation, new FrameSize(image.getWidth(), image.getHeight()), image);
            return var3;
         } catch (IllegalArgumentException | IOException e) {
            PalettedPermutations.LOGGER.error("unable to apply palette to {}", this.permutationLocation, e);
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
