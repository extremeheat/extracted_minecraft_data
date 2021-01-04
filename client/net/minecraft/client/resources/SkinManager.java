package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.HttpTextureProcessor;
import net.minecraft.client.renderer.MobSkinTextureProcessor;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;

public class SkinManager {
   private static final ExecutorService EXECUTOR_SERVICE;
   private final TextureManager textureManager;
   private final File skinsDirectory;
   private final MinecraftSessionService sessionService;
   private final LoadingCache<GameProfile, Map<Type, MinecraftProfileTexture>> insecureSkinCache;

   public SkinManager(TextureManager var1, File var2, MinecraftSessionService var3) {
      super();
      this.textureManager = var1;
      this.skinsDirectory = var2;
      this.sessionService = var3;
      this.insecureSkinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<GameProfile, Map<Type, MinecraftProfileTexture>>() {
         public Map<Type, MinecraftProfileTexture> load(GameProfile var1) throws Exception {
            try {
               return Minecraft.getInstance().getMinecraftSessionService().getTextures(var1, false);
            } catch (Throwable var3) {
               return Maps.newHashMap();
            }
         }

         // $FF: synthetic method
         public Object load(Object var1) throws Exception {
            return this.load((GameProfile)var1);
         }
      });
   }

   public ResourceLocation registerTexture(MinecraftProfileTexture var1, Type var2) {
      return this.registerTexture(var1, var2, (SkinManager.SkinTextureCallback)null);
   }

   public ResourceLocation registerTexture(final MinecraftProfileTexture var1, final Type var2, @Nullable final SkinManager.SkinTextureCallback var3) {
      String var4 = Hashing.sha1().hashUnencodedChars(var1.getHash()).toString();
      final ResourceLocation var5 = new ResourceLocation("skins/" + var4);
      TextureObject var6 = this.textureManager.getTexture(var5);
      if (var6 != null) {
         if (var3 != null) {
            var3.onSkinTextureAvailable(var2, var5, var1);
         }
      } else {
         File var7 = new File(this.skinsDirectory, var4.length() > 2 ? var4.substring(0, 2) : "xx");
         File var8 = new File(var7, var4);
         final MobSkinTextureProcessor var9 = var2 == Type.SKIN ? new MobSkinTextureProcessor() : null;
         HttpTexture var10 = new HttpTexture(var8, var1.getUrl(), DefaultPlayerSkin.getDefaultSkin(), new HttpTextureProcessor() {
            public NativeImage process(NativeImage var1x) {
               return var9 != null ? var9.process(var1x) : var1x;
            }

            public void onTextureDownloaded() {
               if (var9 != null) {
                  var9.onTextureDownloaded();
               }

               if (var3 != null) {
                  var3.onSkinTextureAvailable(var2, var5, var1);
               }

            }
         });
         this.textureManager.register((ResourceLocation)var5, (TextureObject)var10);
      }

      return var5;
   }

   public void registerSkins(GameProfile var1, SkinManager.SkinTextureCallback var2, boolean var3) {
      EXECUTOR_SERVICE.submit(() -> {
         HashMap var4 = Maps.newHashMap();

         try {
            var4.putAll(this.sessionService.getTextures(var1, var3));
         } catch (InsecureTextureException var7) {
         }

         if (var4.isEmpty()) {
            var1.getProperties().clear();
            if (var1.getId().equals(Minecraft.getInstance().getUser().getGameProfile().getId())) {
               var1.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
               var4.putAll(this.sessionService.getTextures(var1, false));
            } else {
               this.sessionService.fillProfileProperties(var1, var3);

               try {
                  var4.putAll(this.sessionService.getTextures(var1, var3));
               } catch (InsecureTextureException var6) {
               }
            }
         }

         Minecraft.getInstance().execute(() -> {
            if (var4.containsKey(Type.SKIN)) {
               this.registerTexture((MinecraftProfileTexture)var4.get(Type.SKIN), Type.SKIN, var2);
            }

            if (var4.containsKey(Type.CAPE)) {
               this.registerTexture((MinecraftProfileTexture)var4.get(Type.CAPE), Type.CAPE, var2);
            }

         });
      });
   }

   public Map<Type, MinecraftProfileTexture> getInsecureSkinInformation(GameProfile var1) {
      return (Map)this.insecureSkinCache.getUnchecked(var1);
   }

   static {
      EXECUTOR_SERVICE = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
   }

   public interface SkinTextureCallback {
      void onSkinTextureAvailable(Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
