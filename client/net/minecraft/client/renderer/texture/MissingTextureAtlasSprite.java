package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

public final class MissingTextureAtlasSprite {
   private static final int MISSING_IMAGE_WIDTH = 16;
   private static final int MISSING_IMAGE_HEIGHT = 16;
   private static final String MISSING_TEXTURE_NAME = "missingno";
   private static final ResourceLocation MISSING_TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("missingno");
   private static final ResourceMetadata SPRITE_METADATA;
   @Nullable
   private static DynamicTexture missingTexture;

   public MissingTextureAtlasSprite() {
      super();
   }

   private static NativeImage generateMissingImage(int var0, int var1) {
      NativeImage var2 = new NativeImage(var0, var1, false);
      int var3 = -16777216;
      int var4 = -524040;

      for(int var5 = 0; var5 < var1; ++var5) {
         for(int var6 = 0; var6 < var0; ++var6) {
            if (var5 < var1 / 2 ^ var6 < var0 / 2) {
               var2.setPixelRGBA(var6, var5, -524040);
            } else {
               var2.setPixelRGBA(var6, var5, -16777216);
            }
         }
      }

      return var2;
   }

   public static SpriteContents create() {
      NativeImage var0 = generateMissingImage(16, 16);
      return new SpriteContents(MISSING_TEXTURE_LOCATION, new FrameSize(16, 16), var0, SPRITE_METADATA);
   }

   public static ResourceLocation getLocation() {
      return MISSING_TEXTURE_LOCATION;
   }

   public static DynamicTexture getTexture() {
      if (missingTexture == null) {
         NativeImage var0 = generateMissingImage(16, 16);
         var0.untrack();
         missingTexture = new DynamicTexture(var0);
         Minecraft.getInstance().getTextureManager().register((ResourceLocation)MISSING_TEXTURE_LOCATION, (AbstractTexture)missingTexture);
      }

      return missingTexture;
   }

   static {
      SPRITE_METADATA = (new ResourceMetadata.Builder()).put(AnimationMetadataSection.SERIALIZER, new AnimationMetadataSection(ImmutableList.of(new AnimationFrame(0, -1)), 16, 16, 1, false)).build();
   }
}
