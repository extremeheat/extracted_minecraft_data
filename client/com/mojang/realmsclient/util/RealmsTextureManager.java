package com.mojang.realmsclient.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsScreen;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTextureManager {
   private static final Map<String, RealmsTextureManager.RealmsTexture> textures = new HashMap();
   private static final Map<String, Boolean> skinFetchStatus = new HashMap();
   private static final Map<String, String> fetchedSkins = new HashMap();
   private static final Logger LOGGER = LogManager.getLogger();

   public static void bindWorldTemplate(String var0, String var1) {
      if (var1 == null) {
         RealmsScreen.bind("textures/gui/presets/isles.png");
      } else {
         int var2 = getTextureId(var0, var1);
         GlStateManager.bindTexture(var2);
      }
   }

   public static void withBoundFace(String var0, Runnable var1) {
      GLX.withTextureRestore(() -> {
         bindFace(var0);
         var1.run();
      });
   }

   private static void bindDefaultFace(UUID var0) {
      RealmsScreen.bind((var0.hashCode() & 1) == 1 ? "minecraft:textures/entity/alex.png" : "minecraft:textures/entity/steve.png");
   }

   private static void bindFace(final String var0) {
      UUID var1 = UUIDTypeAdapter.fromString(var0);
      if (textures.containsKey(var0)) {
         GlStateManager.bindTexture(((RealmsTextureManager.RealmsTexture)textures.get(var0)).textureId);
      } else if (skinFetchStatus.containsKey(var0)) {
         if (!(Boolean)skinFetchStatus.get(var0)) {
            bindDefaultFace(var1);
         } else if (fetchedSkins.containsKey(var0)) {
            int var3 = getTextureId(var0, (String)fetchedSkins.get(var0));
            GlStateManager.bindTexture(var3);
         } else {
            bindDefaultFace(var1);
         }

      } else {
         skinFetchStatus.put(var0, false);
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
                        var4 = (HttpURLConnection)(new URL(var3)).openConnection(Realms.getProxy());
                        var4.setDoInput(true);
                        var4.setDoOutput(false);
                        var4.connect();
                        if (var4.getResponseCode() / 100 != 2) {
                           RealmsTextureManager.skinFetchStatus.remove(var0);
                           return;
                        }

                        BufferedImage var5;
                        try {
                           var5 = ImageIO.read(var4.getInputStream());
                        } catch (Exception var17) {
                           RealmsTextureManager.skinFetchStatus.remove(var0);
                           return;
                        } finally {
                           IOUtils.closeQuietly(var4.getInputStream());
                        }

                        var5 = (new SkinProcessor()).process(var5);
                        ByteArrayOutputStream var6 = new ByteArrayOutputStream();
                        ImageIO.write(var5, "png", var6);
                        RealmsTextureManager.fetchedSkins.put(var0, DatatypeConverter.printBase64Binary(var6.toByteArray()));
                        RealmsTextureManager.skinFetchStatus.put(var0, true);
                     } catch (Exception var19) {
                        RealmsTextureManager.LOGGER.error("Couldn't download http texture", var19);
                        RealmsTextureManager.skinFetchStatus.remove(var0);
                     }

                  } finally {
                     if (var4 != null) {
                        var4.disconnect();
                     }

                  }
               } else {
                  RealmsTextureManager.skinFetchStatus.put(var0, true);
               }
            }
         };
         var2.setDaemon(true);
         var2.start();
      }
   }

   private static int getTextureId(String var0, String var1) {
      int var2;
      if (textures.containsKey(var0)) {
         RealmsTextureManager.RealmsTexture var3 = (RealmsTextureManager.RealmsTexture)textures.get(var0);
         if (var3.image.equals(var1)) {
            return var3.textureId;
         }

         GlStateManager.deleteTexture(var3.textureId);
         var2 = var3.textureId;
      } else {
         var2 = GlStateManager.genTexture();
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

      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.bindTexture(var2);
      TextureUtil.initTexture(var13, var4, var5);
      textures.put(var0, new RealmsTextureManager.RealmsTexture(var1, var2));
      return var2;
   }

   public static class RealmsTexture {
      String image;
      int textureId;

      public RealmsTexture(String var1, int var2) {
         super();
         this.image = var1;
         this.textureId = var2;
      }
   }
}
