package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.LazyLoadedValue;

public final class MissingTextureAtlasSprite extends TextureAtlasSprite {
   private static final int MISSING_IMAGE_WIDTH = 16;
   private static final int MISSING_IMAGE_HEIGHT = 16;
   private static final String MISSING_TEXTURE_NAME = "missingno";
   private static final ResourceLocation MISSING_TEXTURE_LOCATION = new ResourceLocation("missingno");
   @Nullable
   private static DynamicTexture missingTexture;
   private static final LazyLoadedValue<NativeImage> MISSING_IMAGE_DATA = new LazyLoadedValue(() -> {
      NativeImage var0 = new NativeImage(16, 16, false);
      int var1 = -16777216;
      int var2 = -524040;

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            if (var3 < 8 ^ var4 < 8) {
               var0.setPixelRGBA(var4, var3, -524040);
            } else {
               var0.setPixelRGBA(var4, var3, -16777216);
            }
         }
      }

      var0.untrack();
      return var0;
   });
   private static final TextureAtlasSprite.Info INFO;

   private MissingTextureAtlasSprite(TextureAtlas var1, int var2, int var3, int var4, int var5, int var6) {
      super(var1, INFO, var2, var3, var4, var5, var6, (NativeImage)MISSING_IMAGE_DATA.get());
   }

   public static MissingTextureAtlasSprite newInstance(TextureAtlas var0, int var1, int var2, int var3, int var4, int var5) {
      return new MissingTextureAtlasSprite(var0, var1, var2, var3, var4, var5);
   }

   public static ResourceLocation getLocation() {
      return MISSING_TEXTURE_LOCATION;
   }

   public static TextureAtlasSprite.Info info() {
      return INFO;
   }

   public void close() {
      for(int var1 = 1; var1 < this.mainImage.length; ++var1) {
         this.mainImage[var1].close();
      }

   }

   public static DynamicTexture getTexture() {
      if (missingTexture == null) {
         missingTexture = new DynamicTexture((NativeImage)MISSING_IMAGE_DATA.get());
         Minecraft.getInstance().getTextureManager().register((ResourceLocation)MISSING_TEXTURE_LOCATION, (AbstractTexture)missingTexture);
      }

      return missingTexture;
   }

   static {
      INFO = new TextureAtlasSprite.Info(MISSING_TEXTURE_LOCATION, 16, 16, new AnimationMetadataSection(ImmutableList.of(new AnimationFrame(0, -1)), 16, 16, 1, false));
   }
}
