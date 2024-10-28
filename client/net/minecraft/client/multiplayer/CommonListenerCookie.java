package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.world.flag.FeatureFlagSet;

public record CommonListenerCookie(GameProfile localGameProfile, WorldSessionTelemetryManager telemetryManager, RegistryAccess.Frozen receivedRegistries, FeatureFlagSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerData serverData, @Nullable Screen postDisconnectScreen, Map<ResourceLocation, byte[]> serverCookies, @Nullable ChatComponent.State chatState, boolean strictErrorHandling, Map<String, String> customReportDetails, ServerLinks serverLinks) {
   public CommonListenerCookie(GameProfile var1, WorldSessionTelemetryManager var2, RegistryAccess.Frozen var3, FeatureFlagSet var4, @Nullable String var5, @Nullable ServerData var6, @Nullable Screen var7, Map<ResourceLocation, byte[]> var8, @Nullable ChatComponent.State var9, @Deprecated(forRemoval = true) boolean var10, Map<String, String> var11, ServerLinks var12) {
      super();
      this.localGameProfile = var1;
      this.telemetryManager = var2;
      this.receivedRegistries = var3;
      this.enabledFeatures = var4;
      this.serverBrand = var5;
      this.serverData = var6;
      this.postDisconnectScreen = var7;
      this.serverCookies = var8;
      this.chatState = var9;
      this.strictErrorHandling = var10;
      this.customReportDetails = var11;
      this.serverLinks = var12;
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

   public Map<String, String> customReportDetails() {
      return this.customReportDetails;
   }

   public ServerLinks serverLinks() {
      return this.serverLinks;
   }
}
