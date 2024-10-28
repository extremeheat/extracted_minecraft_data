package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;

public record CommonListenerCookie(GameProfile localGameProfile, WorldSessionTelemetryManager telemetryManager, RegistryAccess.Frozen receivedRegistries, FeatureFlagSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerData serverData, @Nullable Screen postDisconnectScreen, Map<ResourceLocation, byte[]> serverCookies, @Nullable ChatComponent.State chatState, boolean strictErrorHandling) {
   public CommonListenerCookie(GameProfile localGameProfile, WorldSessionTelemetryManager telemetryManager, RegistryAccess.Frozen receivedRegistries, FeatureFlagSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerData serverData, @Nullable Screen postDisconnectScreen, Map<ResourceLocation, byte[]> serverCookies, @Nullable ChatComponent.State chatState, @Deprecated(forRemoval = true) boolean strictErrorHandling) {
      super();
      this.localGameProfile = localGameProfile;
      this.telemetryManager = telemetryManager;
      this.receivedRegistries = receivedRegistries;
      this.enabledFeatures = enabledFeatures;
      this.serverBrand = serverBrand;
      this.serverData = serverData;
      this.postDisconnectScreen = postDisconnectScreen;
      this.serverCookies = serverCookies;
      this.chatState = chatState;
      this.strictErrorHandling = strictErrorHandling;
   }

   public GameProfile localGameProfile() {
      return this.localGameProfile;
   }

   public WorldSessionTelemetryManager telemetryManager() {
      return this.telemetryManager;
   }

   public RegistryAccess.Frozen receivedRegistries() {
      return this.receivedRegistries;
   }

   public FeatureFlagSet enabledFeatures() {
      return this.enabledFeatures;
   }

   @Nullable
   public String serverBrand() {
      return this.serverBrand;
   }

   @Nullable
   public ServerData serverData() {
      return this.serverData;
   }

   @Nullable
   public Screen postDisconnectScreen() {
      return this.postDisconnectScreen;
   }

   public Map<ResourceLocation, byte[]> serverCookies() {
      return this.serverCookies;
   }

   @Nullable
   public ChatComponent.State chatState() {
      return this.chatState;
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public boolean strictErrorHandling() {
      return this.strictErrorHandling;
   }
}
