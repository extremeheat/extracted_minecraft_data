package net.minecraft.client.multiplayer;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.SignedMessageValidator;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CryptException;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;
import org.slf4j.Logger;

public class PlayerInfo {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GameProfile profile;
   private final Map<Type, ResourceLocation> textureLocations = Maps.newEnumMap(Type.class);
   private GameType gameMode;
   private int latency;
   private boolean pendingTextures;
   @Nullable
   private String skinModel;
   @Nullable
   private Component tabListDisplayName;
   private int lastHealth;
   private int displayHealth;
   private long lastHealthTime;
   private long healthBlinkTime;
   private long renderVisibilityId;
   @Nullable
   private final ProfilePublicKey profilePublicKey;
   private final SignedMessageValidator messageValidator;

   public PlayerInfo(ClientboundPlayerInfoPacket.PlayerUpdate var1, SignatureValidator var2, boolean var3) {
      super();
      this.profile = var1.getProfile();
      this.gameMode = var1.getGameMode();
      this.latency = var1.getLatency();
      this.tabListDisplayName = var1.getDisplayName();
      ProfilePublicKey var4 = null;

      try {
         ProfilePublicKey.Data var5 = var1.getProfilePublicKey();
         if (var5 != null) {
            var4 = ProfilePublicKey.createValidated(var2, this.profile.getId(), var5);
         }
      } catch (InsecurePublicKeyException | CryptException var6) {
         LOGGER.error("Failed to retrieve publicKey property for profile {}", this.profile.getId(), var6);
      }

      this.profilePublicKey = var4;
      this.messageValidator = SignedMessageValidator.create(var4, var3);
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   @Nullable
   public ProfilePublicKey getProfilePublicKey() {
      return this.profilePublicKey;
   }

   public SignedMessageValidator getMessageValidator() {
      return this.messageValidator;
   }

   @Nullable
   public GameType getGameMode() {
      return this.gameMode;
   }

   protected void setGameMode(GameType var1) {
      this.gameMode = var1;
   }

   public int getLatency() {
      return this.latency;
   }

   protected void setLatency(int var1) {
      this.latency = var1;
   }

   public boolean isCapeLoaded() {
      return this.getCapeLocation() != null;
   }

   public boolean isSkinLoaded() {
      return this.getSkinLocation() != null;
   }

   public String getModelName() {
      return this.skinModel == null ? DefaultPlayerSkin.getSkinModelName(this.profile.getId()) : this.skinModel;
   }

   public ResourceLocation getSkinLocation() {
      this.registerTextures();
      return (ResourceLocation)MoreObjects.firstNonNull(this.textureLocations.get(Type.SKIN), DefaultPlayerSkin.getDefaultSkin(this.profile.getId()));
   }

   @Nullable
   public ResourceLocation getCapeLocation() {
      this.registerTextures();
      return this.textureLocations.get(Type.CAPE);
   }

   @Nullable
   public ResourceLocation getElytraLocation() {
      this.registerTextures();
      return this.textureLocations.get(Type.ELYTRA);
   }

   @Nullable
   public PlayerTeam getTeam() {
      return Minecraft.getInstance().level.getScoreboard().getPlayersTeam(this.getProfile().getName());
   }

   protected void registerTextures() {
      synchronized(this) {
         if (!this.pendingTextures) {
            this.pendingTextures = true;
            Minecraft.getInstance().getSkinManager().registerSkins(this.profile, (var1, var2, var3) -> {
               this.textureLocations.put(var1, var2);
               if (var1 == Type.SKIN) {
                  this.skinModel = var3.getMetadata("model");
                  if (this.skinModel == null) {
                     this.skinModel = "default";
                  }
               }
            }, true);
         }
      }
   }

   public void setTabListDisplayName(@Nullable Component var1) {
      this.tabListDisplayName = var1;
   }

   @Nullable
   public Component getTabListDisplayName() {
      return this.tabListDisplayName;
   }

   public int getLastHealth() {
      return this.lastHealth;
   }

   public void setLastHealth(int var1) {
      this.lastHealth = var1;
   }

   public int getDisplayHealth() {
      return this.displayHealth;
   }

   public void setDisplayHealth(int var1) {
      this.displayHealth = var1;
   }

   public long getLastHealthTime() {
      return this.lastHealthTime;
   }

   public void setLastHealthTime(long var1) {
      this.lastHealthTime = var1;
   }

   public long getHealthBlinkTime() {
      return this.healthBlinkTime;
   }

   public void setHealthBlinkTime(long var1) {
      this.healthBlinkTime = var1;
   }

   public long getRenderVisibilityId() {
      return this.renderVisibilityId;
   }

   public void setRenderVisibilityId(long var1) {
      this.renderVisibilityId = var1;
   }
}
