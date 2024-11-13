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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.SkinTextureDownloader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class SkinManager {
   static final Logger LOGGER = LogUtils.getLogger();
   private final MinecraftSessionService sessionService;
   private final LoadingCache<CacheKey, CompletableFuture<Optional<PlayerSkin>>> skinCache;
   private final TextureCache skinTextures;
   private final TextureCache capeTextures;
   private final TextureCache elytraTextures;

   public SkinManager(Path var1, final MinecraftSessionService var2, final Executor var3) {
      super();
      this.sessionService = var2;
      this.skinTextures = new TextureCache(var1, Type.SKIN);
      this.capeTextures = new TextureCache(var1, Type.CAPE);
      this.elytraTextures = new TextureCache(var1, Type.ELYTRA);
      this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(15L)).build(new CacheLoader<CacheKey, CompletableFuture<Optional<PlayerSkin>>>() {
         public CompletableFuture<Optional<PlayerSkin>> load(CacheKey var1) {
            return CompletableFuture.supplyAsync(() -> {
               Property var2x = var1.packedTextures();
               if (var2x == null) {
                  return MinecraftProfileTextures.EMPTY;
               } else {
                  MinecraftProfileTextures var3x = var2.unpackTextures(var2x);
                  if (var3x.signatureState() == SignatureState.INVALID) {
                     SkinManager.LOGGER.warn("Profile contained invalid signature for textures property (profile id: {})", var1.profileId());
                  }

                  return var3x;
               }
            }, Util.backgroundExecutor().forName("unpackSkinTextures")).thenComposeAsync((var2x) -> SkinManager.this.registerTextures(var1.profileId(), var2x), var3).handle((var1x, var2x) -> {
               if (var2x != null) {
                  SkinManager.LOGGER.warn("Failed to load texture for profile {}", var1.profileId, var2x);
               }

               return Optional.ofNullable(var1x);
            });
         }

         // $FF: synthetic method
         public Object load(final Object var1) throws Exception {
            return this.load((CacheKey)var1);
         }
      });
   }

   public Supplier<PlayerSkin> lookupInsecure(GameProfile var1) {
      CompletableFuture var2 = this.getOrLoad(var1);
      PlayerSkin var3 = DefaultPlayerSkin.get(var1);
      return () -> (PlayerSkin)((Optional)var2.getNow(Optional.empty())).orElse(var3);
   }

   public PlayerSkin getInsecureSkin(GameProfile var1) {
      PlayerSkin var2 = (PlayerSkin)((Optional)this.getOrLoad(var1).getNow(Optional.empty())).orElse((Object)null);
      return var2 != null ? var2 : DefaultPlayerSkin.get(var1);
   }

   public CompletableFuture<Optional<PlayerSkin>> getOrLoad(GameProfile var1) {
      Property var2 = this.sessionService.getPackedTextures(var1);
      return (CompletableFuture)this.skinCache.getUnchecked(new CacheKey(var1.getId(), var2));
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

      String var11 = (String)Optionull.map(var3, MinecraftProfileTexture::getUrl);
      MinecraftProfileTexture var7 = var2.cape();
      CompletableFuture var8 = var7 != null ? this.capeTextures.getOrLoad(var7) : CompletableFuture.completedFuture((Object)null);
      MinecraftProfileTexture var9 = var2.elytra();
      CompletableFuture var10 = var9 != null ? this.elytraTextures.getOrLoad(var9) : CompletableFuture.completedFuture((Object)null);
      return CompletableFuture.allOf(var4, var8, var10).thenApply((var6x) -> new PlayerSkin((ResourceLocation)var4.join(), var11, (ResourceLocation)var8.join(), (ResourceLocation)var10.join(), var5, var2.signatureState() == SignatureState.SIGNED));
   }

   static class TextureCache {
      private final Path root;
      private final MinecraftProfileTexture.Type type;
      private final Map<String, CompletableFuture<ResourceLocation>> textures = new Object2ObjectOpenHashMap();

      TextureCache(Path var1, MinecraftProfileTexture.Type var2) {
         super();
         this.root = var1;
         this.type = var2;
      }

      public CompletableFuture<ResourceLocation> getOrLoad(MinecraftProfileTexture var1) {
         String var2 = var1.getHash();
         CompletableFuture var3 = (CompletableFuture)this.textures.get(var2);
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
         return SkinTextureDownloader.downloadAndRegisterSkin(var3, var4, var1.getUrl(), this.type == Type.SKIN);
      }

      private ResourceLocation getTextureLocation(String var1) {
         String var10000;
         switch (this.type) {
            case SKIN -> var10000 = "skins";
            case CAPE -> var10000 = "capes";
            case ELYTRA -> var10000 = "elytra";
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         String var2 = var10000;
         return ResourceLocation.withDefaultNamespace(var2 + "/" + var1);
      }
   }

   static record CacheKey(UUID profileId, @Nullable Property packedTextures) {
      final UUID profileId;

      CacheKey(UUID var1, @Nullable Property var2) {
         super();
         this.profileId = var1;
         this.packedTextures = var2;
      }
   }
}
