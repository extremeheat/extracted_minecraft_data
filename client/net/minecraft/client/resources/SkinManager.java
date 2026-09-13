package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class SkinManager {
   public static final ResourceLocation field_152793_a = new ResourceLocation("textures/entity/steve.png");
   private static final ExecutorService field_152794_b = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
   private final TextureManager field_152795_c;
   private final File field_152796_d;
   private final MinecraftSessionService field_152797_e;
   private final LoadingCache field_152798_f;

   public SkinManager(TextureManager var1, File var2, MinecraftSessionService var3) {
      super();
      this.field_152795_c = var1;
      this.field_152796_d = var2;
      this.field_152797_e = var3;
      this.field_152798_f = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new SkinManager$1(this));
   }

   public ResourceLocation func_152792_a(MinecraftProfileTexture var1, Type var2) {
      return this.func_152789_a(var1, var2, null);
   }

   public ResourceLocation func_152789_a(MinecraftProfileTexture var1, Type var2, SkinManager$SkinAvailableCallback var3) {
      ResourceLocation var4 = new ResourceLocation("skins/" + var1.getHash());
      ITextureObject var5 = this.field_152795_c.func_110581_b(var4);
      if (var5 != null) {
         if (var3 != null) {
            var3.func_152121_a(var2, var4);
         }
      } else {
         File var6 = new File(this.field_152796_d, var1.getHash().substring(0, 2));
         File var7 = new File(var6, var1.getHash());
         ImageBufferDownload var8 = var2 == Type.SKIN ? new ImageBufferDownload() : null;
         ThreadDownloadImageData var9 = new ThreadDownloadImageData(var7, var1.getUrl(), field_152793_a, new SkinManager$2(this, var8, var3, var2, var4));
         this.field_152795_c.func_110579_a(var4, var9);
      }

      return var4;
   }

   public void func_152790_a(GameProfile var1, SkinManager$SkinAvailableCallback var2, boolean var3) {
      field_152794_b.submit(new SkinManager$3(this, var1, var3, var2));
   }

   public Map func_152788_a(GameProfile var1) {
      return (Map)this.field_152798_f.getUnchecked(var1);
   }
}
