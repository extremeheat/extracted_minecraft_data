package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jspecify.annotations.Nullable;

public record CommonListenerCookie(LevelLoadTracker levelLoadTracker, GameProfile localGameProfile, WorldSessionTelemetryManager telemetryManager, RegistryAccess.Frozen receivedRegistries, FeatureFlagSet enabledFeatures, @Nullable String serverBrand, @Nullable ServerData serverData, @Nullable Screen postDisconnectScreen, Map<ResourceLocation, byte[]> serverCookies, ChatComponent.@Nullable State chatState, Map<String, String> customReportDetails, ServerLinks serverLinks, Map<UUID, PlayerInfo> seenPlayers, boolean seenInsecureChatWarning) {
   public CommonListenerCookie(LevelLoadTracker var1, GameProfile var2, WorldSessionTelemetryManager var3, RegistryAccess.Frozen var4, FeatureFlagSet var5, @Nullable String var6, @Nullable ServerData var7, @Nullable Screen var8, Map<ResourceLocation, byte[]> var9, ChatComponent.@Nullable State var10, Map<String, String> var11, ServerLinks var12, Map<UUID, PlayerInfo> var13, boolean var14) {
      super();
      this.levelLoadTracker = var1;
      this.localGameProfile = var2;
      this.telemetryManager = var3;
      this.receivedRegistries = var4;
      this.enabledFeatures = var5;
      this.serverBrand = var6;
      this.serverData = var7;
      this.postDisconnectScreen = var8;
      this.serverCookies = var9;
      this.chatState = var10;
      this.customReportDetails = var11;
      this.serverLinks = var12;
      this.seenPlayers = var13;
      this.seenInsecureChatWarning = var14;
   }
}
