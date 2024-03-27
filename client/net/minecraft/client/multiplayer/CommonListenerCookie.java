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
   GameProfile a,
   WorldSessionTelemetryManager b,
   RegistryAccess.Frozen c,
   FeatureFlagSet d,
   @Nullable String e,
   @Nullable ServerData f,
   @Nullable Screen g,
   Map<ResourceLocation, byte[]> h,
   @Nullable ChatComponent.State i
) {
   private final GameProfile localGameProfile;
   private final WorldSessionTelemetryManager telemetryManager;
   private final RegistryAccess.Frozen receivedRegistries;
   private final FeatureFlagSet enabledFeatures;
   @Nullable
   private final String serverBrand;
   @Nullable
   private final ServerData serverData;
   @Nullable
   private final Screen postDisconnectScreen;
   private final Map<ResourceLocation, byte[]> serverCookies;
   @Nullable
   private final ChatComponent.State chatState;

   public CommonListenerCookie(
      GameProfile var1,
      WorldSessionTelemetryManager var2,
      RegistryAccess.Frozen var3,
      FeatureFlagSet var4,
      @Nullable String var5,
      @Nullable ServerData var6,
      @Nullable Screen var7,
      Map<ResourceLocation, byte[]> var8,
      @Nullable ChatComponent.State var9
   ) {
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
   }
}
