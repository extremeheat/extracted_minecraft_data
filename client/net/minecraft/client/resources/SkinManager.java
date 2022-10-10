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
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
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
            try {
               return Minecraft.func_71410_x().func_152347_ac().getTextures(var1, false);
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

   public ResourceLocation func_152792_a(MinecraftProfileTexture var1, Type var2) {
      return this.func_152789_a(var1, var2, (SkinManager.SkinAvailableCallback)null);
   }

   public ResourceLocation func_152789_a(final MinecraftProfileTexture var1, final Type var2, @Nullable final SkinManager.SkinAvailableCallback var3) {
      String var4 = Hashing.sha1().hashUnencodedChars(var1.getHash()).toString();
      final ResourceLocation var5 = new ResourceLocation("skins/" + var4);
      ITextureObject var6 = this.field_152795_c.func_110581_b(var5);
      if (var6 != null) {
         if (var3 != null) {
            var3.onSkinTextureAvailable(var2, var5, var1);
         }
      } else {
         File var7 = new File(this.field_152796_d, var4.length() > 2 ? var4.substring(0, 2) : "xx");
         File var8 = new File(var7, var4);
         final ImageBufferDownload var9 = var2 == Type.SKIN ? new ImageBufferDownload() : null;
         ThreadDownloadImageData var10 = new ThreadDownloadImageData(var8, var1.getUrl(), DefaultPlayerSkin.func_177335_a(), new IImageBuffer() {
            public NativeImage func_195786_a(NativeImage var1x) {
               return var9 != null ? var9.func_195786_a(var1x) : var1x;
            }

            public void func_152634_a() {
               if (var9 != null) {
                  var9.func_152634_a();
               }

               if (var3 != null) {
                  var3.onSkinTextureAvailable(var2, var5, var1);
               }

            }
         });
         this.field_152795_c.func_110579_a(var5, var10);
      }

      return var5;
   }

   public void func_152790_a(GameProfile var1, SkinManager.SkinAvailableCallback var2, boolean var3) {
      field_152794_b.submit(() -> {
         HashMap var4 = Maps.newHashMap();

         try {
            var4.putAll(this.field_152797_e.getTextures(var1, var3));
         } catch (InsecureTextureException var7) {
         }

         if (var4.isEmpty()) {
            var1.getProperties().clear();
            if (var1.getId().equals(Minecraft.func_71410_x().func_110432_I().func_148256_e().getId())) {
               var1.getProperties().putAll(Minecraft.func_71410_x().func_181037_M());
               var4.putAll(this.field_152797_e.getTextures(var1, false));
            } else {
               this.field_152797_e.fillProfileProperties(var1, var3);

               try {
                  var4.putAll(this.field_152797_e.getTextures(var1, var3));
               } catch (InsecureTextureException var6) {
               }
            }
         }

         Minecraft.func_71410_x().func_152344_a(() -> {
            if (var4.containsKey(Type.SKIN)) {
               this.func_152789_a((MinecraftProfileTexture)var4.get(Type.SKIN), Type.SKIN, var2);
            }

            if (var4.containsKey(Type.CAPE)) {
               this.func_152789_a((MinecraftProfileTexture)var4.get(Type.CAPE), Type.CAPE, var2);
            }

         });
      });
   }

   public Map<Type, MinecraftProfileTexture> func_152788_a(GameProfile var1) {
      return (Map)this.field_152798_f.getUnchecked(var1);
   }

   static {
      field_152794_b = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
   }

   public interface SkinAvailableCallback {
      void onSkinTextureAvailable(Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
