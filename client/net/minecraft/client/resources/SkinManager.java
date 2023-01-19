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
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;

public class SkinManager {
   public static final String PROPERTY_TEXTURES = "textures";
   private final TextureManager textureManager;
   private final File skinsDirectory;
   private final MinecraftSessionService sessionService;
   private final LoadingCache<String, Map<Type, MinecraftProfileTexture>> insecureSkinCache;

   public SkinManager(TextureManager var1, File var2, final MinecraftSessionService var3) {
      super();
      this.textureManager = var1;
      this.skinsDirectory = var2;
      this.sessionService = var3;
      this.insecureSkinCache = CacheBuilder.newBuilder()
         .expireAfterAccess(15L, TimeUnit.SECONDS)
         .build(new CacheLoader<String, Map<Type, MinecraftProfileTexture>>() {
            public Map<Type, MinecraftProfileTexture> load(String var1) {
               GameProfile var2 = new GameProfile(null, "dummy_mcdummyface");
               var2.getProperties().put("textures", new Property("textures", var1, ""));
   
               try {
                  return var3.getTextures(var2, false);
               } catch (Throwable var4) {
                  return ImmutableMap.of();
               }
            }
         });
   }

   public ResourceLocation registerTexture(MinecraftProfileTexture var1, Type var2) {
      return this.registerTexture(var1, var2, null);
   }

   private ResourceLocation registerTexture(MinecraftProfileTexture var1, Type var2, @Nullable SkinManager.SkinTextureCallback var3) {
      String var4 = Hashing.sha1().hashUnencodedChars(var1.getHash()).toString();
      ResourceLocation var5 = getTextureLocation(var2, var4);
      AbstractTexture var6 = this.textureManager.getTexture(var5, MissingTextureAtlasSprite.getTexture());
      if (var6 == MissingTextureAtlasSprite.getTexture()) {
         File var7 = new File(this.skinsDirectory, var4.length() > 2 ? var4.substring(0, 2) : "xx");
         File var8 = new File(var7, var4);
         HttpTexture var9 = new HttpTexture(var8, var1.getUrl(), DefaultPlayerSkin.getDefaultSkin(), var2 == Type.SKIN, () -> {
            if (var3 != null) {
               var3.onSkinTextureAvailable(var2, var5, var1);
            }
         });
         this.textureManager.register(var5, var9);
      } else if (var3 != null) {
         var3.onSkinTextureAvailable(var2, var5, var1);
      }

      return var5;
   }

   private static ResourceLocation getTextureLocation(Type var0, String var1) {
      String var2 = switch(var0) {
         case SKIN -> "skins";
         case CAPE -> "capes";
         case ELYTRA -> "elytra";
         default -> throw new IncompatibleClassChangeError();
      };
      return new ResourceLocation(var2 + "/" + var1);
   }

   public void registerSkins(GameProfile var1, SkinManager.SkinTextureCallback var2, boolean var3) {
      Runnable var4 = () -> {
         HashMap var4x = Maps.newHashMap();

         try {
            var4x.putAll(this.sessionService.getTextures(var1, var3));
         } catch (InsecureTextureException var7) {
         }

         if (var4x.isEmpty()) {
            var1.getProperties().clear();
            if (var1.getId().equals(Minecraft.getInstance().getUser().getGameProfile().getId())) {
               var1.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
               var4x.putAll(this.sessionService.getTextures(var1, false));
            } else {
               this.sessionService.fillProfileProperties(var1, var3);

               try {
                  var4x.putAll(this.sessionService.getTextures(var1, var3));
               } catch (InsecureTextureException var6) {
               }
            }
         }

         Minecraft.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(Type.SKIN, Type.CAPE).forEach(var3xx -> {
                  if (var4x.containsKey(var3xx)) {
                     this.registerTexture((MinecraftProfileTexture)var4x.get(var3xx), var3xx, var2);
                  }
               })));
      };
      Util.backgroundExecutor().execute(var4);
   }

   public Map<Type, MinecraftProfileTexture> getInsecureSkinInformation(GameProfile var1) {
      Property var2 = (Property)Iterables.getFirst(var1.getProperties().get("textures"), null);
      return (Map<Type, MinecraftProfileTexture>)(var2 == null ? ImmutableMap.of() : (Map)this.insecureSkinCache.getUnchecked(var2.getValue()));
   }

   public ResourceLocation getInsecureSkinLocation(GameProfile var1) {
      MinecraftProfileTexture var2 = (MinecraftProfileTexture)this.getInsecureSkinInformation(var1).get(Type.SKIN);
      return var2 != null ? this.registerTexture(var2, Type.SKIN) : DefaultPlayerSkin.getDefaultSkin(UUIDUtil.getOrCreatePlayerUUID(var1));
   }

   public interface SkinTextureCallback {
      void onSkinTextureAvailable(Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
