package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.SignatureState;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class SkinManager {
   static final Logger LOGGER = LogUtils.getLogger();
   private final MinecraftSessionService sessionService;
   private final LoadingCache<SkinManager.CacheKey, CompletableFuture<PlayerSkin>> skinCache;
   private final SkinManager.TextureCache skinTextures;
   private final SkinManager.TextureCache capeTextures;
   private final SkinManager.TextureCache elytraTextures;

   public SkinManager(TextureManager var1, Path var2, final MinecraftSessionService var3, final Executor var4) {
      super();
      this.sessionService = var3;
      this.skinTextures = new SkinManager.TextureCache(var1, var2, Type.SKIN);
      this.capeTextures = new SkinManager.TextureCache(var1, var2, Type.CAPE);
      this.elytraTextures = new SkinManager.TextureCache(var1, var2, Type.ELYTRA);
      this.skinCache = CacheBuilder.newBuilder()
         .expireAfterAccess(Duration.ofSeconds(15L))
         .build(new CacheLoader<SkinManager.CacheKey, CompletableFuture<PlayerSkin>>() {
            public CompletableFuture<PlayerSkin> load(SkinManager.CacheKey var1) {
               return CompletableFuture.<MinecraftProfileTextures>supplyAsync(() -> {
                  Property var2 = var1.packedTextures();
                  if (var2 == null) {
                     return MinecraftProfileTextures.EMPTY;
                  } else {
                     MinecraftProfileTextures var3x = var3.unpackTextures(var2);
                     if (var3x.signatureState() == SignatureState.INVALID) {
                        SkinManager.LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", var1.profileId());
                     }

                     return var3x;
                  }
               }, Util.backgroundExecutor()).thenComposeAsync(var2 -> SkinManager.this.registerTextures(var1.profileId(), var2), var4);
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
      Property var2 = this.sessionService.getPackedTextures(var1);
      return (CompletableFuture<PlayerSkin>)this.skinCache.getUnchecked(new SkinManager.CacheKey(var1.getId(), var2));
   }

   CompletableFuture<PlayerSkin> registerTextures(UUID var1, MinecraftProfileTextures var2) {
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
            var6x -> new PlayerSkin(
                  (ResourceLocation)var4.join(),
                  var11,
                  (ResourceLocation)var8.join(),
                  (ResourceLocation)var10.join(),
                  var5,
                  var2.signatureState() == SignatureState.SIGNED
               )
         );
   }

   static record CacheKey(UUID profileId, @Nullable Property packedTextures) {
      CacheKey(UUID profileId, @Nullable Property packedTextures) {
         super();
         this.profileId = profileId;
         this.packedTextures = packedTextures;
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
         String var2 = switch (this.type) {
            case SKIN -> "skins";
            case CAPE -> "capes";
            case ELYTRA -> "elytra";
            default -> throw new MatchException(null, null);
         };
         return new ResourceLocation(var2 + "/" + var1);
      }
   }
}
