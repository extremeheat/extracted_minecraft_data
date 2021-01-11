package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
   private static final ExecutorService field_152794_b;
   private final TextureManager field_152795_c;
   private final File field_152796_d;
   private final MinecraftSessionService field_152797_e;
   private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> field_152798_f;

   public SkinManager(TextureManager var1, File var2, MinecraftSessionService var3) {
      super();
      this.field_152795_c = var1;
      this.field_152796_d = var2;
      this.field_152797_e = var3;
      this.field_152798_f = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
         public Map<Type, MinecraftProfileTexture> load(GameProfile var1) throws Exception {
            return Minecraft.func_71410_x().func_152347_ac().getTextures(var1, false);
         }

         // $FF: synthetic method
         public Object load(Object var1) throws Exception {
            return this.load((GameProfile)var1);
         }
      });
   }

   public ResourceLocation func_152792_a(MinecraftProfileTexture var1, Type var2) {
      return this.func_152789_a(var1, var2, (SkinManager.SkinAvailableCallback)null);
   }

   public ResourceLocation func_152789_a(final MinecraftProfileTexture var1, final Type var2, final SkinManager.SkinAvailableCallback var3) {
      final ResourceLocation var4 = new ResourceLocation("skins/" + var1.getHash());
      ITextureObject var5 = this.field_152795_c.func_110581_b(var4);
      if (var5 != null) {
         if (var3 != null) {
            var3.func_180521_a(var2, var4, var1);
         }
      } else {
         File var6 = new File(this.field_152796_d, var1.getHash().length() > 2 ? var1.getHash().substring(0, 2) : "xx");
         File var7 = new File(var6, var1.getHash());
         final ImageBufferDownload var8 = var2 == Type.SKIN ? new ImageBufferDownload() : null;
         ThreadDownloadImageData var9 = new ThreadDownloadImageData(var7, var1.getUrl(), DefaultPlayerSkin.func_177335_a(), new IImageBuffer() {
            public BufferedImage func_78432_a(BufferedImage var1x) {
               if (var8 != null) {
                  var1x = var8.func_78432_a(var1x);
               }

               return var1x;
            }

            public void func_152634_a() {
               if (var8 != null) {
                  var8.func_152634_a();
               }

               if (var3 != null) {
                  var3.func_180521_a(var2, var4, var1);
               }

            }
         });
         this.field_152795_c.func_110579_a(var4, var9);
      }

      return var4;
   }

   public void func_152790_a(final GameProfile var1, final SkinManager.SkinAvailableCallback var2, final boolean var3) {
      field_152794_b.submit(new Runnable() {
         public void run() {
            final HashMap var1x = Maps.newHashMap();

            try {
               var1x.putAll(SkinManager.this.field_152797_e.getTextures(var1, var3));
            } catch (InsecureTextureException var3x) {
            }

            if (var1x.isEmpty() && var1.getId().equals(Minecraft.func_71410_x().func_110432_I().func_148256_e().getId())) {
               var1.getProperties().clear();
               var1.getProperties().putAll(Minecraft.func_71410_x().func_181037_M());
               var1x.putAll(SkinManager.this.field_152797_e.getTextures(var1, false));
            }

            Minecraft.func_71410_x().func_152344_a(new Runnable() {
               public void run() {
                  if (var1x.containsKey(Type.SKIN)) {
                     SkinManager.this.func_152789_a((MinecraftProfileTexture)var1x.get(Type.SKIN), Type.SKIN, var2);
                  }

                  if (var1x.containsKey(Type.CAPE)) {
                     SkinManager.this.func_152789_a((MinecraftProfileTexture)var1x.get(Type.CAPE), Type.CAPE, var2);
                  }

               }
            });
         }
      });
   }

   public Map<Type, MinecraftProfileTexture> func_152788_a(GameProfile var1) {
      return (Map)this.field_152798_f.getUnchecked(var1);
   }

   static {
      field_152794_b = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
   }

   public interface SkinAvailableCallback {
      void func_180521_a(Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
