package net.minecraft.client.entity;

import com.mojang.logging.LogUtils;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.decoration.MannequinProfile;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class ClientMannequin extends Mannequin implements ClientAvatarEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component BELOW_NAME_TAG = Component.translatable("entity.minecraft.mannequin.label");
   private final ClientAvatarState avatarState = new ClientAvatarState();
   @Nullable
   private CompletableFuture<Optional<PlayerSkin>> skinLookup;
   private PlayerSkin skin;
   private final PlayerSkinRenderCache skinRenderCache;

   public ClientMannequin(Level var1, PlayerSkinRenderCache var2) {
      super(var1);
      this.skinRenderCache = var2;
      this.skin = DefaultPlayerSkin.getDefaultSkin();
   }

   public void tick() {
      super.tick();
      this.avatarState.tick(this.position(), this.getDeltaMovement());
      if (this.skinLookup != null && this.skinLookup.isDone()) {
         try {
            ((Optional)this.skinLookup.get()).ifPresent((var1) -> this.skin = var1);
            this.skinLookup = null;
         } catch (Exception var2) {
            LOGGER.error("Error when trying to look up skin", var2);
         }
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (var1 == DATA_PROFILE) {
         this.getProfile().ifRight((var1x) -> this.skinLookup = this.skinRenderCache.lookup(var1x).thenApply((var0) -> var0.map(PlayerSkinRenderCache.RenderInfo::playerSkin))).ifLeft((var1x) -> this.skin = mannequinProfileToPlayerSkin(var1x));
      }

   }

   private static PlayerSkin mannequinProfileToPlayerSkin(MannequinProfile var0) {
      return new PlayerSkin(mapTexture(var0.texture()), (String)null, (ResourceLocation)var0.capeTexture().map(ClientMannequin::mapTexture).orElse((Object)null), (ResourceLocation)var0.elytraTexture().map(ClientMannequin::mapTexture).orElse((Object)null), var0.model(), false);
   }

   private static ResourceLocation mapTexture(ResourceLocation var0) {
      return var0.withPath((UnaryOperator)((var0x) -> "textures/" + var0x + ".png"));
   }

   public ClientAvatarState avatarState() {
      return this.avatarState;
   }

   public PlayerSkin getSkin() {
      return this.skin;
   }

   @Nullable
   public Component belowNameDisplay() {
      return BELOW_NAME_TAG;
   }

   @Nullable
   public Parrot.Variant getParrotVariantOnShoulder(boolean var1) {
      return null;
   }

   public boolean showExtraEars() {
      return false;
   }
}
