package net.minecraft.client.renderer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.item.component.ResolvableProfile;

public class PlayerSkinRenderCache {
   public static final RenderType DEFAULT_PLAYER_SKIN_RENDER_TYPE = playerSkinRenderType(DefaultPlayerSkin.getDefaultSkin());
   private final LoadingCache<ResolvableProfile, CompletableFuture<RenderInfo>> renderInfoCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5L)).build(new CacheLoader<ResolvableProfile, CompletableFuture<RenderInfo>>() {
      public CompletableFuture<RenderInfo> load(ResolvableProfile var1) {
         return var1.resolveProfile(PlayerSkinRenderCache.this.profileResolver).thenCompose((var1x) -> PlayerSkinRenderCache.this.skinManager.get(var1x).thenApply((var1) -> (RenderInfo)var1.map((var1xx) -> new RenderInfo(var1x, var1xx)).orElse((Object)null)));
      }

      // $FF: synthetic method
      public Object load(final Object var1) throws Exception {
         return this.load((ResolvableProfile)var1);
      }
   });
   private final LoadingCache<ResolvableProfile, RenderInfo> defaultSkinCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofMinutes(5L)).build(new CacheLoader<ResolvableProfile, RenderInfo>() {
      public RenderInfo load(ResolvableProfile var1) {
         GameProfile var2 = var1.partialProfile();
         return new RenderInfo(var2, DefaultPlayerSkin.get(var2));
      }

      // $FF: synthetic method
      public Object load(final Object var1) throws Exception {
         return this.load((ResolvableProfile)var1);
      }
   });
   final SkinManager skinManager;
   final ProfileResolver profileResolver;

   public PlayerSkinRenderCache(SkinManager var1, ProfileResolver var2) {
      super();
      this.skinManager = var1;
      this.profileResolver = var2;
   }

   public RenderInfo getOrDefault(ResolvableProfile var1) {
      RenderInfo var2 = (RenderInfo)((CompletableFuture)this.renderInfoCache.getUnchecked(var1)).getNow((Object)null);
      return var2 != null ? var2 : (RenderInfo)this.defaultSkinCache.getUnchecked(var1);
   }

   static RenderType playerSkinRenderType(PlayerSkin var0) {
      return SkullBlockRenderer.getPlayerSkinRenderType(var0.texture());
   }

   public static final class RenderInfo {
      private final GameProfile gameProfile;
      private final PlayerSkin playerSkin;
      @Nullable
      private RenderType itemRenderType;

      public RenderInfo(GameProfile var1, PlayerSkin var2) {
         super();
         this.gameProfile = var1;
         this.playerSkin = var2;
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

      public boolean equals(Object var1) {
         boolean var10000;
         if (this != var1) {
            label28: {
               if (var1 instanceof RenderInfo) {
                  RenderInfo var2 = (RenderInfo)var1;
                  if (this.gameProfile.equals(var2.gameProfile) && this.playerSkin.equals(var2.playerSkin)) {
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
         int var1 = 1;
         var1 = 31 * var1 + this.gameProfile.hashCode();
         var1 = 31 * var1 + this.playerSkin.hashCode();
         return var1;
      }
   }
}
