package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.DisplayData;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.client.User;
import net.minecraft.client.resources.IndexedAssetSource;
import net.minecraft.util.StringUtil;

public class GameConfig {
   public final GameConfig.UserData user;
   public final DisplayData display;
   public final GameConfig.FolderData location;
   public final GameConfig.GameData game;
   public final GameConfig.QuickPlayData quickPlay;

   public GameConfig(GameConfig.UserData var1, DisplayData var2, GameConfig.FolderData var3, GameConfig.GameData var4, GameConfig.QuickPlayData var5) {
      super();
      this.user = var1;
      this.display = var2;
      this.location = var3;
      this.game = var4;
      this.quickPlay = var5;
   }

   public static class FolderData {
      public final File gameDirectory;
      public final File resourcePackDirectory;
      public final File assetDirectory;
      @Nullable
      public final String assetIndex;

      public FolderData(File var1, File var2, File var3, @Nullable String var4) {
         super();
         this.gameDirectory = var1;
         this.resourcePackDirectory = var2;
         this.assetDirectory = var3;
         this.assetIndex = var4;
      }

      public Path getExternalAssetSource() {
         return this.assetIndex == null ? this.assetDirectory.toPath() : IndexedAssetSource.createIndexFs(this.assetDirectory.toPath(), this.assetIndex);
      }
   }

   public static class GameData {
      public final boolean demo;
      public final String launchVersion;
      public final String versionType;
      public final boolean disableMultiplayer;
      public final boolean disableChat;

      public GameData(boolean var1, String var2, String var3, boolean var4, boolean var5) {
         super();
         this.demo = var1;
         this.launchVersion = var2;
         this.versionType = var3;
         this.disableMultiplayer = var4;
         this.disableChat = var5;
      }
   }

   public static record QuickPlayData(@Nullable String path, @Nullable String singleplayer, @Nullable String multiplayer, @Nullable String realms) {
      public QuickPlayData(@Nullable String path, @Nullable String singleplayer, @Nullable String multiplayer, @Nullable String realms) {
         super();
         this.path = path;
         this.singleplayer = singleplayer;
         this.multiplayer = multiplayer;
         this.realms = realms;
      }

      public boolean isEnabled() {
         return !StringUtil.isBlank(this.singleplayer) || !StringUtil.isBlank(this.multiplayer) || !StringUtil.isBlank(this.realms);
      }
   }

   public static class UserData {
      public final User user;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserData(User var1, PropertyMap var2, PropertyMap var3, Proxy var4) {
         super();
         this.user = var1;
         this.userProperties = var2;
         this.profileProperties = var3;
         this.proxy = var4;
      }
   }
}