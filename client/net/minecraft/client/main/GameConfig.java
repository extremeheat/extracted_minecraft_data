package net.minecraft.client.main;

import com.mojang.blaze3d.platform.DisplayData;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import net.minecraft.client.PreferredGraphicsApi;
import net.minecraft.client.User;
import net.minecraft.client.resources.IndexedAssetSource;
import net.minecraft.util.StringUtil;
import org.jspecify.annotations.Nullable;

public class GameConfig {
   public final UserData user;
   public final DisplayData display;
   public final FolderData location;
   public final GameData game;
   public final QuickPlayData quickPlay;

   public GameConfig(final UserData userData, final DisplayData displayData, final FolderData folderData, final GameData gameData, final QuickPlayData quickPlayData) {
      super();
      this.user = userData;
      this.display = displayData;
      this.location = folderData;
      this.game = gameData;
      this.quickPlay = quickPlayData;
   }

   public static class GameData {
      public final boolean demo;
      public final String launchVersion;
      public final String versionType;
      public final boolean disableMultiplayer;
      public final boolean disableChat;
      public final boolean captureTracyImages;
      public final boolean renderDebugLabels;
      public final boolean vulkanValidation;
      public final @Nullable PreferredGraphicsApi forcedGraphicsApi;
      public final boolean offlineDeveloperMode;

      public GameData(final boolean demo, final String launchVersion, final String versionType, final boolean disableMultiplayer, final boolean disableChat, final boolean captureTracyImages, final boolean vulkanValidation, final boolean renderDebugLabels, final @Nullable PreferredGraphicsApi forcedGraphicsApi, final boolean offlineDeveloperMode) {
         super();
         this.demo = demo;
         this.launchVersion = launchVersion;
         this.versionType = versionType;
         this.disableMultiplayer = disableMultiplayer;
         this.disableChat = disableChat;
         this.captureTracyImages = captureTracyImages;
         this.vulkanValidation = vulkanValidation;
         this.renderDebugLabels = renderDebugLabels;
         this.forcedGraphicsApi = forcedGraphicsApi;
         this.offlineDeveloperMode = offlineDeveloperMode;
      }
   }

   public static class UserData {
      public final User user;
      public final Proxy proxy;

      public UserData(final User user, final Proxy proxy) {
         super();
         this.user = user;
         this.proxy = proxy;
      }
   }

   public static class FolderData {
      public final File gameDirectory;
      public final File resourcePackDirectory;
      public final File assetDirectory;
      public final @Nullable String assetIndex;

      public FolderData(final File gameDirectory, final File resourcePackDirectory, final File assetDirectory, final @Nullable String assetIndex) {
         super();
         this.gameDirectory = gameDirectory;
         this.resourcePackDirectory = resourcePackDirectory;
         this.assetDirectory = assetDirectory;
         this.assetIndex = assetIndex;
      }

      public Path getExternalAssetSource() {
         return this.assetIndex == null ? this.assetDirectory.toPath() : IndexedAssetSource.createIndexFs(this.assetDirectory.toPath(), this.assetIndex);
      }
   }

   public sealed interface QuickPlayVariant permits GameConfig.QuickPlaySinglePlayerData, GameConfig.QuickPlayMultiplayerData, GameConfig.QuickPlayRealmsData, GameConfig.QuickPlayDisabled {
      QuickPlayVariant DISABLED = new QuickPlayDisabled();

      boolean isEnabled();
   }

   public static record QuickPlaySinglePlayerData(@Nullable String worldId) implements QuickPlayVariant {
      public QuickPlaySinglePlayerData {
         super();
      }

      public boolean isEnabled() {
         return true;
      }
   }

   public static record QuickPlayMultiplayerData(String serverAddress) implements QuickPlayVariant {
      public QuickPlayMultiplayerData {
         super();
      }

      public boolean isEnabled() {
         return !StringUtil.isBlank(this.serverAddress);
      }
   }

   public static record QuickPlayRealmsData(String realmId) implements QuickPlayVariant {
      public QuickPlayRealmsData {
         super();
      }

      public boolean isEnabled() {
         return !StringUtil.isBlank(this.realmId);
      }
   }

   public static record QuickPlayDisabled() implements QuickPlayVariant {
      public QuickPlayDisabled() {
         super();
      }

      public boolean isEnabled() {
         return false;
      }
   }

   public static record QuickPlayData(@Nullable String logPath, QuickPlayVariant variant) {
      public QuickPlayData {
         super();
      }

      public boolean isEnabled() {
         return this.variant.isEnabled();
      }
   }
}
