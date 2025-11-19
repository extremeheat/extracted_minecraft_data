package net.minecraft.client.entity;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ClientMannequin extends Mannequin implements ClientAvatarEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final PlayerSkin DEFAULT_SKIN;
   private final ClientAvatarState avatarState = new ClientAvatarState();
   private @Nullable CompletableFuture<Optional<PlayerSkin>> skinLookup;
   private PlayerSkin skin;
   private final PlayerSkinRenderCache skinRenderCache;

   public static void registerOverrides(PlayerSkinRenderCache var0) {
      Mannequin.constructor = (var1, var2) -> (Mannequin)(var2 instanceof ClientLevel ? new ClientMannequin(var2, var0) : new Mannequin(var1, var2));
   }

   public ClientMannequin(Level var1, PlayerSkinRenderCache var2) {
      super(var1);
      this.skin = DEFAULT_SKIN;
      this.skinRenderCache = var2;
   }

   public void tick() {
      super.tick();
      this.avatarState.tick(this.position(), this.getDeltaMovement());
      if (this.skinLookup != null && this.skinLookup.isDone()) {
         try {
            ((Optional)this.skinLookup.get()).ifPresent(this::setSkin);
            this.skinLookup = null;
         } catch (Exception var2) {
            LOGGER.error("Error when trying to look up skin", var2);
         }
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (var1.equals(DATA_PROFILE)) {
         this.updateSkin();
      }

   }

   private void updateSkin() {
      if (this.skinLookup != null) {
         CompletableFuture var1 = this.skinLookup;
         this.skinLookup = null;
         var1.cancel(false);
      }

      this.skinLookup = this.skinRenderCache.lookup(this.getProfile()).thenApply((var0) -> var0.map(PlayerSkinRenderCache.RenderInfo::playerSkin));
   }

   public ClientAvatarState avatarState() {
      return this.avatarState;
   }

   public PlayerSkin getSkin() {
      return this.skin;
   }

   private void setSkin(PlayerSkin var1) {
      this.skin = var1;
   }

   public @Nullable Component belowNameDisplay() {
      return this.getDescription();
   }

   public Parrot.@Nullable Variant getParrotVariantOnShoulder(boolean var1) {
      return null;
   }

   public boolean showExtraEars() {
      return false;
   }

   static {
      DEFAULT_SKIN = DefaultPlayerSkin.get(Mannequin.DEFAULT_PROFILE.partialProfile());
   }
}
