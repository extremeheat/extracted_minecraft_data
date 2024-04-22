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

public record CommonListenerCookie(
   GameProfile localGameProfile,
   WorldSessionTelemetryManager telemetryManager,
   RegistryAccess.Frozen receivedRegistries,
   FeatureFlagSet enabledFeatures,
   @Nullable String serverBrand,
   @Nullable ServerData serverData,
   @Nullable Screen postDisconnectScreen,
   Map<ResourceLocation, byte[]> serverCookies,
   @Nullable ChatComponent.State chatState,
   @Deprecated(forRemoval = true) boolean strictErrorHandling
) {
   public CommonListenerCookie(
      GameProfile localGameProfile,
      WorldSessionTelemetryManager telemetryManager,
      RegistryAccess.Frozen receivedRegistries,
      FeatureFlagSet enabledFeatures,
      @Nullable String serverBrand,
      @Nullable ServerData serverData,
      @Nullable Screen postDisconnectScreen,
      Map<ResourceLocation, byte[]> serverCookies,
      @Nullable ChatComponent.State chatState,
      @Deprecated(forRemoval = true) boolean strictErrorHandling
   ) {
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
}