package net.minecraft.client.renderer;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.GlyphRenderTypes;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;

public class PlayerSkinRenderCache {
   public static final RenderType DEFAULT_PLAYER_SKIN_RENDER_TYPE = playerSkinRenderType(DefaultPlayerSkin.getDefaultSkin());
   public static final Duration CACHE_DURATION = Duration.ofMinutes(5L);
   private final LoadingCache<ResolvableProfile, CompletableFuture<Optional<RenderInfo>>> renderInfoCache;
   private final LoadingCache<ResolvableProfile, RenderInfo> defaultSkinCache;
   final TextureManager textureManager;
   final SkinManager skinManager;
   final ProfileResolver profileResolver;

   public PlayerSkinRenderCache(TextureManager var1, SkinManager var2, ProfileResolver var3) {
      super();
      this.renderInfoCache = CacheBuilder.newBuilder().expireAfterAccess(CACHE_DURATION).build(new CacheLoader<ResolvableProfile, CompletableFuture<Optional<RenderInfo>>>() {
         public CompletableFuture<Optional<RenderInfo>> load(ResolvableProfile var1) {
            return var1.resolveProfile(PlayerSkinRenderCache.this.profileResolver).thenCompose((var2) -> PlayerSkinRenderCache.this.skinManager.get(var2).thenApply((var3) -> var3.map((var3x) -> PlayerSkinRenderCache.this.new RenderInfo(var2, var3x, var1.skinPatch()))));
         }

         // $FF: synthetic method
         public Object load(final Object var1) throws Exception {
            return this.load((ResolvableProfile)var1);
         }
      });
      this.defaultSkinCache = CacheBuilder.newBuilder().expireAfterAccess(CACHE_DURATION).build(new CacheLoader<ResolvableProfile, RenderInfo>() {
         public RenderInfo load(ResolvableProfile var1) {
            GameProfile var2 = var1.partialProfile();
            return PlayerSkinRenderCache.this.new RenderInfo(var2, DefaultPlayerSkin.get(var2), var1.skinPatch());
         }

         // $FF: synthetic method
         public Object load(final Object var1) throws Exception {
            return this.load((ResolvableProfile)var1);
         }
      });
      this.textureManager = var1;
      this.skinManager = var2;
      this.profileResolver = var3;
   }

   public RenderInfo getOrDefault(ResolvableProfile var1) {
      RenderInfo var2 = (RenderInfo)((Optional)this.lookup(var1).getNow(Optional.empty())).orElse((Object)null);
      return var2 != null ? var2 : (RenderInfo)this.defaultSkinCache.getUnchecked(var1);
   }

   public Supplier<RenderInfo> createLookup(ResolvableProfile var1) {
      RenderInfo var2 = (RenderInfo)this.defaultSkinCache.getUnchecked(var1);
      CompletableFuture var3 = (CompletableFuture)this.renderInfoCache.getUnchecked(var1);
      Optional var4 = (Optional)var3.getNow((Object)null);
      if (var4 != null) {
         RenderInfo var5 = (RenderInfo)var4.orElse(var2);
         return () -> var5;
      } else {
         return () -> (RenderInfo)((Optional)var3.getNow(Optional.empty())).orElse(var2);
      }
   }

   public CompletableFuture<Optional<RenderInfo>> lookup(ResolvableProfile var1) {
      return (CompletableFuture)this.renderInfoCache.getUnchecked(var1);
   }

   static RenderType playerSkinRenderType(PlayerSkin var0) {
      return SkullBlockRenderer.getPlayerSkinRenderType(var0.body().texturePath());
   }

   public final class RenderInfo {
      private final GameProfile gameProfile;
      private final PlayerSkin playerSkin;
      @Nullable
      private RenderType itemRenderType;
      @Nullable
      private GpuTextureView textureView;
      @Nullable
      private GlyphRenderTypes glyphRenderTypes;

      public RenderInfo(final GameProfile var2, final PlayerSkin var3, final PlayerSkin.Patch var4) {
         super();
         this.gameProfile = var2;
         this.playerSkin = var3.with(var4);
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
