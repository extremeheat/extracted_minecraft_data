package net.minecraft.client.multiplayer;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.PlayerTeam;

public class PlayerInfo {
   private final GameProfile profile;
   private final Map<Type, ResourceLocation> textureLocations = Maps.newEnumMap(Type.class);
   private GameType gameMode;
   private int latency;
   private boolean pendingTextures;
   private String skinModel;
   private Component tabListDisplayName;
   private int lastHealth;
   private int displayHealth;
   private long lastHealthTime;
   private long healthBlinkTime;
   private long renderVisibilityId;

   public PlayerInfo(GameProfile var1) {
      super();
      this.profile = var1;
   }

   public PlayerInfo(ClientboundPlayerInfoPacket.PlayerUpdate var1) {
      super();
      this.profile = var1.getProfile();
      this.gameMode = var1.getGameMode();
      this.latency = var1.getLatency();
      this.tabListDisplayName = var1.getDisplayName();
   }

   public GameProfile getProfile() {
      return this.profile;
   }

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
      return (ResourceLocation)this.textureLocations.get(Type.CAPE);
   }

   @Nullable
   public ResourceLocation getElytraLocation() {
      this.registerTextures();
      return (ResourceLocation)this.textureLocations.get(Type.ELYTRA);
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
               switch(var1) {
               case SKIN:
                  this.textureLocations.put(Type.SKIN, var2);
                  this.skinModel = var3.getMetadata("model");
                  if (this.skinModel == null) {
                     this.skinModel = "default";
                  }
                  break;
               case CAPE:
                  this.textureLocations.put(Type.CAPE, var2);
                  break;
               case ELYTRA:
                  this.textureLocations.put(Type.ELYTRA, var2);
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
