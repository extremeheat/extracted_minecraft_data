package com.mojang.realmsclient.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.slf4j.Logger;

public class RealmsTextureManager {
   private static final Map<String, RealmsTexture> TEXTURES = Maps.newHashMap();
   static final Map<String, Boolean> SKIN_FETCH_STATUS = Maps.newHashMap();
   static final Map<String, String> FETCHED_SKINS = Maps.newHashMap();
   static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation TEMPLATE_ICON_LOCATION = new ResourceLocation("textures/gui/presets/isles.png");

   public RealmsTextureManager() {
      super();
   }

   public static void bindWorldTemplate(String var0, @Nullable String var1) {
      if (var1 == null) {
         RenderSystem.setShaderTexture(0, TEMPLATE_ICON_LOCATION);
      } else {
         int var2 = getTextureId(var0, var1);
         RenderSystem.setShaderTexture(0, var2);
      }
   }

   public static void withBoundFace(String var0, Runnable var1) {
      bindFace(var0);
      var1.run();
   }

   private static void bindDefaultFace(UUID var0) {
      RenderSystem.setShaderTexture(0, DefaultPlayerSkin.getDefaultSkin(var0));
   }

   private static void bindFace(final String var0) {
      UUID var1 = UUIDTypeAdapter.fromString(var0);
      int var3;
      if (TEXTURES.containsKey(var0)) {
         var3 = ((RealmsTexture)TEXTURES.get(var0)).textureId;
         RenderSystem.setShaderTexture(0, var3);
      } else if (SKIN_FETCH_STATUS.containsKey(var0)) {
         if (!(Boolean)SKIN_FETCH_STATUS.get(var0)) {
            bindDefaultFace(var1);
         } else if (FETCHED_SKINS.containsKey(var0)) {
            var3 = getTextureId(var0, (String)FETCHED_SKINS.get(var0));
            RenderSystem.setShaderTexture(0, var3);
         } else {
            bindDefaultFace(var1);
         }

      } else {
         SKIN_FETCH_STATUS.put(var0, false);
         bindDefaultFace(var1);
         Thread var2 = new Thread("Realms Texture Downloader") {
            public void run() {
               Map var1 = RealmsUtil.getTextures(var0);
               if (var1.containsKey(Type.SKIN)) {
                  MinecraftProfileTexture var2 = (MinecraftProfileTexture)var1.get(Type.SKIN);
                  String var3 = var2.getUrl();
                  HttpURLConnection var4 = null;
                  RealmsTextureManager.LOGGER.debug("Downloading http texture from {}", var3);

                  try {
                     try {
                        var4 = (HttpURLConnection)(new URL(var3)).openConnection(Minecraft.getInstance().getProxy());
                        var4.setDoInput(true);
                        var4.setDoOutput(false);
                        var4.connect();
                        if (var4.getResponseCode() / 100 != 2) {
                           RealmsTextureManager.SKIN_FETCH_STATUS.remove(var0);
                           return;
                        }

                        BufferedImage var5;
                        try {
                           var5 = ImageIO.read(var4.getInputStream());
                        } catch (Exception var17) {
                           RealmsTextureManager.SKIN_FETCH_STATUS.remove(var0);
                           return;
                        } finally {
                           IOUtils.closeQuietly(var4.getInputStream());
                        }

                        var5 = (new SkinProcessor()).process(var5);
                        ByteArrayOutputStream var6 = new ByteArrayOutputStream();
                        ImageIO.write(var5, "png", var6);
                        RealmsTextureManager.FETCHED_SKINS.put(var0, (new Base64()).encodeToString(var6.toByteArray()));
                        RealmsTextureManager.SKIN_FETCH_STATUS.put(var0, true);
                     } catch (Exception var19) {
                        RealmsTextureManager.LOGGER.error("Couldn't download http texture", var19);
                        RealmsTextureManager.SKIN_FETCH_STATUS.remove(var0);
                     }

                  } finally {
                     if (var4 != null) {
                        var4.disconnect();
                     }

                  }
               } else {
                  RealmsTextureManager.SKIN_FETCH_STATUS.put(var0, true);
               }
            }
         };
         var2.setDaemon(true);
         var2.start();
      }
   }

   private static int getTextureId(String var0, String var1) {
      RealmsTexture var2 = (RealmsTexture)TEXTURES.get(var0);
      if (var2 != null && var2.image.equals(var1)) {
         return var2.textureId;
      } else {
         int var3;
         if (var2 != null) {
            var3 = var2.textureId;
         } else {
            var3 = GlStateManager._genTexture();
         }

         TextureData var4 = RealmsTextureManager.TextureData.load(var1);
         RenderSystem.activeTexture(33984);
         RenderSystem.bindTextureForSetup(var3);
         TextureUtil.initTexture(var4.data, var4.width, var4.height);
         TEXTURES.put(var0, new RealmsTexture(var1, var3));
         return var3;
      }
   }

   public static class RealmsTexture {
      final String image;
      final int textureId;

      public RealmsTexture(String var1, int var2) {
         super();
         this.image = var1;
         this.textureId = var2;
      }
   }

   static class TextureData {
      final int width;
      final int height;
      final IntBuffer data;
      private static final Supplier<TextureData> MISSING = Suppliers.memoize(() -> {
         boolean var0 = true;
         boolean var1 = true;
         IntBuffer var2 = BufferUtils.createIntBuffer(256);
         int var3 = -16777216;
         int var4 = -524040;

         for(int var5 = 0; var5 < 16; ++var5) {
            for(int var6 = 0; var6 < 16; ++var6) {
               if (var5 < 8 ^ var6 < 8) {
                  var2.put(var6 + var5 * 16, -524040);
               } else {
                  var2.put(var6 + var5 * 16, -16777216);
               }
            }
         }

         return new TextureData(16, 16, var2);
      });

      private TextureData(int var1, int var2, IntBuffer var3) {
         super();
         this.width = var1;
         this.height = var2;
         this.data = var3;
      }

      public static TextureData load(String var0) {
         try {
            ByteArrayInputStream var1 = new ByteArrayInputStream((new Base64()).decode(var0));
            BufferedImage var2 = ImageIO.read(var1);
            if (var2 != null) {
               int var3 = var2.getWidth();
               int var4 = var2.getHeight();
               int[] var5 = new int[var3 * var4];
               var2.getRGB(0, 0, var3, var4, var5, 0, var3);
               IntBuffer var6 = BufferUtils.createIntBuffer(var3 * var4);
               var6.put(var5);
               var6.flip();
               return new TextureData(var3, var4, var6);
            }

            RealmsTextureManager.LOGGER.warn("Unknown image format: {}", var0);
         } catch (IOException var7) {
            RealmsTextureManager.LOGGER.warn("Failed to load world image: {}", var0, var7);
         }

         return (TextureData)MISSING.get();
      }
   }
}
