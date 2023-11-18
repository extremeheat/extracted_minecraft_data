package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class SkinManager {
   private static final String PROPERTY_TEXTURES = "textures";
   private final LoadingCache<SkinManager.CacheKey, CompletableFuture<PlayerSkin>> skinCache;
   private final SkinManager.TextureCache skinTextures;
   private final SkinManager.TextureCache capeTextures;
   private final SkinManager.TextureCache elytraTextures;

   public SkinManager(TextureManager var1, Path var2, final MinecraftSessionService var3, final Executor var4) {
      super();
      this.skinTextures = new SkinManager.TextureCache(var1, var2, Type.SKIN);
      this.capeTextures = new SkinManager.TextureCache(var1, var2, Type.CAPE);
      this.elytraTextures = new SkinManager.TextureCache(var1, var2, Type.ELYTRA);
      this.skinCache = CacheBuilder.newBuilder()
         .expireAfterAccess(Duration.ofSeconds(15L))
         .build(new CacheLoader<SkinManager.CacheKey, CompletableFuture<PlayerSkin>>() {
            public CompletableFuture<PlayerSkin> load(SkinManager.CacheKey var1) {
               GameProfile var2 = var1.profile();
               return CompletableFuture.<SkinManager.TextureInfo>supplyAsync(() -> {
                  try {
                     try {
                        return SkinManager.TextureInfo.unpack(var3.getTextures(var2, true), true);
                     } catch (InsecurePublicKeyException var3x) {
                        return SkinManager.TextureInfo.unpack(var3.getTextures(var2, false), false);
                     }
                  } catch (Throwable var4x) {
                     return SkinManager.TextureInfo.EMPTY;
                  }
               }, Util.backgroundExecutor()).thenComposeAsync(var2x -> SkinManager.this.registerTextures(var2, var2x), var4);
            }
         });
   }

   public Supplier<PlayerSkin> lookupInsecure(GameProfile var1) {
      CompletableFuture var2 = this.getOrLoad(var1);
      PlayerSkin var3 = DefaultPlayerSkin.get(var1);
      return () -> var2.getNow(var3);
   }

   public PlayerSkin getInsecureSkin(GameProfile var1) {
      PlayerSkin var2 = this.getOrLoad(var1).getNow(null);
      return var2 != null ? var2 : DefaultPlayerSkin.get(var1);
   }

   public CompletableFuture<PlayerSkin> getOrLoad(GameProfile var1) {
      return (CompletableFuture<PlayerSkin>)this.skinCache.getUnchecked(new SkinManager.CacheKey(var1));
   }

   CompletableFuture<PlayerSkin> registerTextures(GameProfile var1, SkinManager.TextureInfo var2) {
      MinecraftProfileTexture var3 = var2.skin();
      CompletableFuture var4;
      PlayerSkin.Model var5;
      if (var3 != null) {
         var4 = this.skinTextures.getOrLoad(var3);
         var5 = PlayerSkin.Model.byName(var3.getMetadata("model"));
      } else {
         PlayerSkin var6 = DefaultPlayerSkin.get(var1);
         var4 = CompletableFuture.completedFuture(var6.texture());
         var5 = var6.model();
      }

      String var11 = Optionull.map(var3, MinecraftProfileTexture::getUrl);
      MinecraftProfileTexture var7 = var2.cape();
      CompletableFuture var8 = var7 != null ? this.capeTextures.getOrLoad(var7) : CompletableFuture.completedFuture(null);
      MinecraftProfileTexture var9 = var2.elytra();
      CompletableFuture var10 = var9 != null ? this.elytraTextures.getOrLoad(var9) : CompletableFuture.completedFuture(null);
      return CompletableFuture.allOf(var4, var8, var10)
         .thenApply(
            var6x -> new PlayerSkin((ResourceLocation)var4.join(), var11, (ResourceLocation)var8.join(), (ResourceLocation)var10.join(), var5, var2.secure())
         );
   }

   @Nullable
   static Property getTextureProperty(GameProfile var0) {
      return (Property)Iterables.getFirst(var0.getProperties().get("textures"), null);
   }

   static record CacheKey(GameProfile a) {
      private final GameProfile profile;

      CacheKey(GameProfile var1) {
         super();
         this.profile = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof SkinManager.CacheKey)) {
            return false;
         } else {
            SkinManager.CacheKey var2 = (SkinManager.CacheKey)var1;
            return this.profile.getId().equals(var2.profile.getId()) && Objects.equals(this.texturesData(), var2.texturesData());
         }
      }

      @Override
      public int hashCode() {
         return this.profile.getId().hashCode() + Objects.hashCode(this.texturesData()) * 31;
      }

      @Nullable
      private String texturesData() {
         Property var1 = SkinManager.getTextureProperty(this.profile);
         return var1 != null ? var1.value() : null;
      }
   }

   static class TextureCache {
      private final TextureManager textureManager;
      private final Path root;
      private final Type type;
      private final Map<String, CompletableFuture<ResourceLocation>> textures = new Object2ObjectOpenHashMap();

      TextureCache(TextureManager var1, Path var2, Type var3) {
         super();
         this.textureManager = var1;
         this.root = var2;
         this.type = var3;
      }

      public CompletableFuture<ResourceLocation> getOrLoad(MinecraftProfileTexture var1) {
         String var2 = var1.getHash();
         CompletableFuture var3 = this.textures.get(var2);
         if (var3 == null) {
            var3 = this.registerTexture(var1);
            this.textures.put(var2, var3);
         }

         return var3;
      }

      private CompletableFuture<ResourceLocation> registerTexture(MinecraftProfileTexture var1) {
         String var2 = Hashing.sha1().hashUnencodedChars(var1.getHash()).toString();
         ResourceLocation var3 = this.getTextureLocation(var2);
         Path var4 = this.root.resolve(var2.length() > 2 ? var2.substring(0, 2) : "xx").resolve(var2);
         CompletableFuture var5 = new CompletableFuture();
         HttpTexture var6 = new HttpTexture(
            var4.toFile(), var1.getUrl(), DefaultPlayerSkin.getDefaultTexture(), this.type == Type.SKIN, () -> var5.complete(var3)
         );
         this.textureManager.register(var3, var6);
         return var5;
      }

      private ResourceLocation getTextureLocation(String var1) {
         String var2 = switch(this.type) {
            case SKIN -> "skins";
            case CAPE -> "capes";
            case ELYTRA -> "elytra";
            default -> throw new IncompatibleClassChangeError();
         };
         return new ResourceLocation(var2 + "/" + var1);
      }
   }

   static record TextureInfo(@Nullable MinecraftProfileTexture b, @Nullable MinecraftProfileTexture c, @Nullable MinecraftProfileTexture d, boolean e) {
      @Nullable
      private final MinecraftProfileTexture skin;
      @Nullable
      private final MinecraftProfileTexture cape;
      @Nullable
      private final MinecraftProfileTexture elytra;
      private final boolean secure;
      public static final SkinManager.TextureInfo EMPTY = new SkinManager.TextureInfo(null, null, null, true);

      private TextureInfo(@Nullable MinecraftProfileTexture var1, @Nullable MinecraftProfileTexture var2, @Nullable MinecraftProfileTexture var3, boolean var4) {
         super();
         this.skin = var1;
         this.cape = var2;
         this.elytra = var3;
         this.secure = var4;
      }

      public static SkinManager.TextureInfo unpack(Map<Type, MinecraftProfileTexture> var0, boolean var1) {
         return var0.isEmpty()
            ? EMPTY
            : new SkinManager.TextureInfo(
               (MinecraftProfileTexture)var0.get(Type.SKIN),
               (MinecraftProfileTexture)var0.get(Type.CAPE),
               (MinecraftProfileTexture)var0.get(Type.ELYTRA),
               var1
            );
      }
   }
}
