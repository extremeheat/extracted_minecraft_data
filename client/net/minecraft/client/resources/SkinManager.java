package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class SkinManager {
   public static final String PROPERTY_TEXTURES = "textures";
   private final TextureManager textureManager;
   private final File skinsDirectory;
   private final MinecraftSessionService sessionService;
   private final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> insecureSkinCache;

   public SkinManager(TextureManager var1, File var2, final MinecraftSessionService var3) {
      super();
      this.textureManager = var1;
      this.skinsDirectory = var2;
      this.sessionService = var3;
      this.insecureSkinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>() {
         public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(String var1) {
            GameProfile var2 = new GameProfile((UUID)null, "dummy_mcdummyface");
            var2.getProperties().put("textures", new Property("textures", var1, ""));

            try {
               return var3.getTextures(var2, false);
            } catch (Throwable var4) {
               return ImmutableMap.of();
            }
         }

         // $FF: synthetic method
         public Object load(Object var1) throws Exception {
            return this.load((String)var1);
         }
      });
   }

   public ResourceLocation registerTexture(MinecraftProfileTexture var1, MinecraftProfileTexture.Type var2) {
      return this.registerTexture(var1, var2, (SkinTextureCallback)null);
   }

   private ResourceLocation registerTexture(MinecraftProfileTexture var1, MinecraftProfileTexture.Type var2, @Nullable SkinTextureCallback var3) {
      String var4 = Hashing.sha1().hashUnencodedChars(var1.getHash()).toString();
      ResourceLocation var5 = new ResourceLocation("skins/" + var4);
      AbstractTexture var6 = this.textureManager.getTexture(var5, MissingTextureAtlasSprite.getTexture());
      if (var6 == MissingTextureAtlasSprite.getTexture()) {
         File var7 = new File(this.skinsDirectory, var4.length() > 2 ? var4.substring(0, 2) : "xx");
         File var8 = new File(var7, var4);
         HttpTexture var9 = new HttpTexture(var8, var1.getUrl(), DefaultPlayerSkin.getDefaultSkin(), var2 == Type.SKIN, () -> {
            if (var3 != null) {
               var3.onSkinTextureAvailable(var2, var5, var1);
            }

         });
         this.textureManager.register((ResourceLocation)var5, (AbstractTexture)var9);
      } else if (var3 != null) {
         var3.onSkinTextureAvailable(var2, var5, var1);
      }

      return var5;
   }

   public void registerSkins(GameProfile var1, SkinTextureCallback var2, boolean var3) {
      Runnable var4 = () -> {
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
            RenderSystem.recordRenderCall(() -> {
               ImmutableList.of(Type.SKIN, Type.CAPE).forEach((var3) -> {
                  if (var4.containsKey(var3)) {
                     this.registerTexture((MinecraftProfileTexture)var4.get(var3), var3, var2);
                  }

               });
            });
         });
      };
      Util.backgroundExecutor().execute(var4);
   }

   public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getInsecureSkinInformation(GameProfile var1) {
      Property var2 = (Property)Iterables.getFirst(var1.getProperties().get("textures"), (Object)null);
      return (Map)(var2 == null ? ImmutableMap.of() : (Map)this.insecureSkinCache.getUnchecked(var2.getValue()));
   }

   public interface SkinTextureCallback {
      void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
