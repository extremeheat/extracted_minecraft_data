package net.minecraft.client.renderer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jspecify.annotations.Nullable;

public class PlayerSkinRenderCache {
   public static final RenderType DEFAULT_PLAYER_SKIN_RENDER_TYPE = playerSkinRenderType(DefaultPlayerSkin.getDefaultSkin());
   public static final Duration CACHE_DURATION = Duration.ofMinutes(5L);
   private final LoadingCache<ResolvableProfile, CompletableFuture<Optional<RenderInfo>>> renderInfoCache;
   private final LoadingCache<ResolvableProfile, RenderInfo> defaultSkinCache;
   private final TextureManager textureManager;
   private final SkinManager skinManager;
   private final ProfileResolver profileResolver;

   public PlayerSkinRenderCache(final TextureManager textureManager, final SkinManager skinManager, final ProfileResolver profileResolver) {
      super();
      this.renderInfoCache = CacheBuilder.newBuilder().expireAfterAccess(CACHE_DURATION).build(new CacheLoader<ResolvableProfile, CompletableFuture<Optional<RenderInfo>>>() {
         {
            Objects.requireNonNull(PlayerSkinRenderCache.this);
         }

         public CompletableFuture<Optional<RenderInfo>> load(final ResolvableProfile profile) {
            return profile.resolveProfile(PlayerSkinRenderCache.this.profileResolver).thenCompose((resolvedProfile) -> PlayerSkinRenderCache.this.skinManager.get(resolvedProfile).thenApply((playerSkin) -> playerSkin.map((skin) -> PlayerSkinRenderCache.this.new RenderInfo(resolvedProfile, skin, profile.skinPatch()))));
         }
      });
      this.defaultSkinCache = CacheBuilder.newBuilder().expireAfterAccess(CACHE_DURATION).build(new CacheLoader<ResolvableProfile, RenderInfo>() {
         {
            Objects.requireNonNull(PlayerSkinRenderCache.this);
         }

         public RenderInfo load(final ResolvableProfile profile) {
            GameProfile temporaryProfile = profile.partialProfile();
            return PlayerSkinRenderCache.this.new RenderInfo(temporaryProfile, DefaultPlayerSkin.get(temporaryProfile), profile.skinPatch());
         }
      });
      this.textureManager = textureManager;
      this.skinManager = skinManager;
      this.profileResolver = profileResolver;
   }

   public RenderInfo getOrDefault(final ResolvableProfile profile) {
      RenderInfo result = (RenderInfo)((Optional)this.lookup(profile).getNow(Optional.empty())).orElse((Object)null);
      return result != null ? result : (RenderInfo)this.defaultSkinCache.getUnchecked(profile);
   }

   public Supplier<RenderInfo> createLookup(final ResolvableProfile profile) {
      RenderInfo defaultForProfile = (RenderInfo)this.defaultSkinCache.getUnchecked(profile);
      CompletableFuture<Optional<RenderInfo>> future = (CompletableFuture)this.renderInfoCache.getUnchecked(profile);
      Optional<RenderInfo> currentValue = (Optional)future.getNow((Object)null);
      if (currentValue != null) {
         RenderInfo finalValue = (RenderInfo)currentValue.orElse(defaultForProfile);
         return () -> finalValue;
      } else {
         return () -> (RenderInfo)((Optional)future.getNow(Optional.empty())).orElse(defaultForProfile);
      }
   }

   public CompletableFuture<Optional<RenderInfo>> lookup(final ResolvableProfile profile) {
      return (CompletableFuture)this.renderInfoCache.getUnchecked(profile);
   }

   private static RenderType playerSkinRenderType(final PlayerSkin playerSkin) {
      return SkullBlockRenderer.getPlayerSkinRenderType(playerSkin.body().texturePath());
   }

   public final class RenderInfo {
      private final GameProfile gameProfile;
      private final PlayerSkin playerSkin;
      private @Nullable RenderType itemRenderType;
      private @Nullable GpuTextureView textureView;
      private @Nullable GlyphRenderTypes glyphRenderTypes;

      public RenderInfo(final GameProfile gameProfile, final PlayerSkin playerSkin, final PlayerSkin.Patch patch) {
         Objects.requireNonNull(PlayerSkinRenderCache.this);
         super();
         this.gameProfile = gameProfile;
         this.playerSkin = playerSkin.with(patch);
      }

      public GameProfile gameProfile() {
         return this.gameProfile;
      }

      public PlayerSkin playerSkin() {
         return this.playerSkin;
      }

      public RenderType renderType() {
         if (this.itemRenderType == null) {
            this.itemRenderType = PlayerSkinRenderCache.playerSkinRenderType(this.playerSkin);
         }

         return this.itemRenderType;
      }

      public GpuTextureView textureView() {
         if (this.textureView == null) {
            this.textureView = PlayerSkinRenderCache.this.textureManager.getTexture(this.playerSkin.body().texturePath()).getTextureView();
         }

         return this.textureView;
      }

      public GlyphRenderTypes glyphRenderTypes() {
         if (this.glyphRenderTypes == null) {
            this.glyphRenderTypes = GlyphRenderTypes.createForColorTexture(this.playerSkin.body().texturePath());
         }

         return this.glyphRenderTypes;
      }

      public boolean equals(final Object o) {
         boolean var10000;
         if (this != o) {
            label28: {
               if (o instanceof RenderInfo) {
                  RenderInfo that = (RenderInfo)o;
                  if (this.gameProfile.equals(that.gameProfile) && this.playerSkin.equals(that.playerSkin)) {
                     break label28;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }

      public int hashCode() {
         int result = 1;
         result = 31 * result + this.gameProfile.hashCode();
         result = 31 * result + this.playerSkin.hashCode();
         return result;
      }
   }
}
