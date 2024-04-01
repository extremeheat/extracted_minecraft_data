package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import org.slf4j.Logger;

public class Unstitcher implements SpriteSource {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<Unstitcher> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ResourceLocation.CODEC.fieldOf("resource").forGetter(var0x -> var0x.resource),
               ExtraCodecs.nonEmptyList(Unstitcher.Region.CODEC.listOf()).fieldOf("regions").forGetter(var0x -> var0x.regions),
               Codec.DOUBLE.optionalFieldOf("divisor_x", 1.0).forGetter(var0x -> var0x.xDivisor),
               Codec.DOUBLE.optionalFieldOf("divisor_y", 1.0).forGetter(var0x -> var0x.yDivisor)
            )
            .apply(var0, Unstitcher::new)
   );
   private final ResourceLocation resource;
   private final List<Unstitcher.Region> regions;
   private final double xDivisor;
   private final double yDivisor;

   public Unstitcher(ResourceLocation var1, List<Unstitcher.Region> var2, double var3, double var5) {
      super();
      this.resource = var1;
      this.regions = var2;
      this.xDivisor = var3;
      this.yDivisor = var5;
   }

   @Override
   public void run(ResourceManager var1, SpriteSource.Output var2) {
      ResourceLocation var3 = TEXTURE_ID_CONVERTER.idToFile(this.resource);
      Optional var4 = var1.getResource(var3);
      if (var4.isPresent()) {
         LazyLoadedImage var5 = new LazyLoadedImage(var3, (Resource)var4.get(), this.regions.size());

         for(Unstitcher.Region var7 : this.regions) {
            var2.add(var7.sprite, new Unstitcher.RegionInstance(var5, var7, this.xDivisor, this.yDivisor));
         }
      } else {
         LOGGER.warn("Missing sprite: {}", var3);
      }
   }

   @Override
   public SpriteSourceType type() {
      return SpriteSources.UNSTITCHER;
   }

   static record Region(ResourceLocation b, double c, double d, double e, double f) {
      final ResourceLocation sprite;
      final double x;
      final double y;
      final double width;
      final double height;
      public static final Codec<Unstitcher.Region> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ResourceLocation.CODEC.fieldOf("sprite").forGetter(Unstitcher.Region::sprite),
                  Codec.DOUBLE.fieldOf("x").forGetter(Unstitcher.Region::x),
                  Codec.DOUBLE.fieldOf("y").forGetter(Unstitcher.Region::y),
                  Codec.DOUBLE.fieldOf("width").forGetter(Unstitcher.Region::width),
                  Codec.DOUBLE.fieldOf("height").forGetter(Unstitcher.Region::height)
               )
               .apply(var0, Unstitcher.Region::new)
      );

      private Region(ResourceLocation var1, double var2, double var4, double var6, double var8) {
         super();
         this.sprite = var1;
         this.x = var2;
         this.y = var4;
         this.width = var6;
         this.height = var8;
      }
   }

   static class RegionInstance implements SpriteSource.SpriteSupplier {
      private final LazyLoadedImage image;
      private final Unstitcher.Region region;
      private final double xDivisor;
      private final double yDivisor;

      RegionInstance(LazyLoadedImage var1, Unstitcher.Region var2, double var3, double var5) {
         super();
         this.image = var1;
         this.region = var2;
         this.xDivisor = var3;
         this.yDivisor = var5;
      }

      public SpriteContents apply(SpriteResourceLoader var1) {
         try {
            NativeImage var2 = this.image.get();
            double var3 = (double)var2.getWidth() / this.xDivisor;
            double var5 = (double)var2.getHeight() / this.yDivisor;
            int var7 = Mth.floor(this.region.x * var3);
            int var8 = Mth.floor(this.region.y * var5);
            int var9 = Mth.floor(this.region.width * var3);
            int var10 = Mth.floor(this.region.height * var5);
            NativeImage var11 = new NativeImage(NativeImage.Format.RGBA, var9, var10, false);
            var2.copyRect(var11, var7, var8, 0, 0, var9, var10, false, false);
            return new SpriteContents(this.region.sprite, new FrameSize(var9, var10), var11, ResourceMetadata.EMPTY);
         } catch (Exception var16) {
            Unstitcher.LOGGER.error("Failed to unstitch region {}", this.region.sprite, var16);
         } finally {
            this.image.release();
         }

         return MissingTextureAtlasSprite.create();
      }

      @Override
      public void discard() {
         this.image.release();
      }
   }
}
