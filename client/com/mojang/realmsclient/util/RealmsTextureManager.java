package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.util.UUIDTypeAdapter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTextureManager {
   private static final Map<String, RealmsTextureManager.RealmsTexture> TEXTURES = Maps.newHashMap();
   private static final Map<String, Boolean> SKIN_FETCH_STATUS = Maps.newHashMap();
   private static final Map<String, String> FETCHED_SKINS = Maps.newHashMap();
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation TEMPLATE_ICON_LOCATION = new ResourceLocation("textures/gui/presets/isles.png");

   public static void bindWorldTemplate(String var0, @Nullable String var1) {
      if (var1 == null) {
         Minecraft.getInstance().getTextureManager().bind(TEMPLATE_ICON_LOCATION);
      } else {
         int var2 = getTextureId(var0, var1);
         RenderSystem.bindTexture(var2);
      }
   }

   public static void withBoundFace(String var0, Runnable var1) {
      RenderSystem.pushTextureAttributes();

      try {
         bindFace(var0);
         var1.run();
      } finally {
         RenderSystem.popAttributes();
      }

   }

   private static void bindDefaultFace(UUID var0) {
      Minecraft.getInstance().getTextureManager().bind(DefaultPlayerSkin.getDefaultSkin(var0));
   }

   private static void bindFace(final String var0) {
      UUID var1 = UUIDTypeAdapter.fromString(var0);
      if (TEXTURES.containsKey(var0)) {
         RenderSystem.bindTexture(((RealmsTextureManager.RealmsTexture)TEXTURES.get(var0)).textureId);
      } else if (SKIN_FETCH_STATUS.containsKey(var0)) {
         if (!(Boolean)SKIN_FETCH_STATUS.get(var0)) {
            bindDefaultFace(var1);
         } else if (FETCHED_SKINS.containsKey(var0)) {
            int var3 = getTextureId(var0, (String)FETCHED_SKINS.get(var0));
            RenderSystem.bindTexture(var3);
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
      int var2;
      if (TEXTURES.containsKey(var0)) {
         RealmsTextureManager.RealmsTexture var3 = (RealmsTextureManager.RealmsTexture)TEXTURES.get(var0);
         if (var3.image.equals(var1)) {
            return var3.textureId;
         }

         RenderSystem.deleteTexture(var3.textureId);
         var2 = var3.textureId;
      } else {
         var2 = GlStateManager._genTexture();
      }

      IntBuffer var13 = null;
      int var4 = 0;
      int var5 = 0;

      try {
         ByteArrayInputStream var7 = new ByteArrayInputStream((new Base64()).decode(var1));

         BufferedImage var6;
         try {
            var6 = ImageIO.read(var7);
         } finally {
            IOUtils.closeQuietly(var7);
         }

         var4 = var6.getWidth();
         var5 = var6.getHeight();
         int[] var8 = new int[var4 * var5];
         var6.getRGB(0, 0, var4, var5, var8, 0, var4);
         var13 = ByteBuffer.allocateDirect(4 * var4 * var5).order(ByteOrder.nativeOrder()).asIntBuffer();
         var13.put(var8);
         var13.flip();
      } catch (IOException var12) {
         var12.printStackTrace();
      }

      RenderSystem.activeTexture(33984);
      RenderSystem.bindTexture(var2);
      TextureUtil.initTexture(var13, var4, var5);
      TEXTURES.put(var0, new RealmsTextureManager.RealmsTexture(var1, var2));
      return var2;
   }

   public static class RealmsTexture {
      private final String image;
      private final int textureId;

      public RealmsTexture(String var1, int var2) {
         super();
         this.image = var1;
         this.textureId = var2;
      }
   }
}
