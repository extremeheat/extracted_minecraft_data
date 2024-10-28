package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

public class OldUsersConverter {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final File OLD_IPBANLIST = new File("banned-ips.txt");
   public static final File OLD_USERBANLIST = new File("banned-players.txt");
   public static final File OLD_OPLIST = new File("ops.txt");
   public static final File OLD_WHITELIST = new File("white-list.txt");

   public OldUsersConverter() {
      super();
   }

   static List<String> readOldListFormat(File var0, Map<String, String[]> var1) throws IOException {
      List var2 = Files.readLines(var0, StandardCharsets.UTF_8);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var4 = var4.trim();
         if (!var4.startsWith("#") && var4.length() >= 1) {
            String[] var5 = var4.split("\\|");
            var1.put(var5[0].toLowerCase(Locale.ROOT), var5);
         }
      }

      return var2;
   }

   private static void lookupPlayers(MinecraftServer var0, Collection<String> var1, ProfileLookupCallback var2) {
      String[] var3 = (String[])var1.stream().filter((var0x) -> {
         return !StringUtil.isNullOrEmpty(var0x);
      }).toArray((var0x) -> {
         return new String[var0x];
      });
      if (var0.usesAuthentication()) {
         var0.getProfileRepository().findProfilesByNames(var3, var2);
      } else {
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            var2.onProfileLookupSucceeded(UUIDUtil.createOfflineProfile(var7));
         }
      }

   }

   public static boolean convertUserBanlist(final MinecraftServer var0) {
      final UserBanList var1 = new UserBanList(PlayerList.USERBANLIST_FILE);
      if (OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
         if (var1.getFile().exists()) {
            try {
               var1.load();
            } catch (IOException var6) {
               LOGGER.warn("Could not load existing file {}", var1.getFile().getName(), var6);
            }
         }

         try {
            final HashMap var2 = Maps.newHashMap();
            readOldListFormat(OLD_USERBANLIST, var2);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1x) {
                  var0.getProfileCache().add(var1x);
                  String[] var2x = (String[])var2.get(var1x.getName().toLowerCase(Locale.ROOT));
                  if (var2x == null) {
                     OldUsersConverter.LOGGER.warn("Could not convert user banlist entry for {}", var1x.getName());
                     throw new ConversionError("Profile not in the conversionlist");
                  } else {
                     Date var3 = var2x.length > 1 ? OldUsersConverter.parseDate(var2x[1], (Date)null) : null;
                     String var4 = var2x.length > 2 ? var2x[2] : null;
                     Date var5 = var2x.length > 3 ? OldUsersConverter.parseDate(var2x[3], (Date)null) : null;
                     String var6 = var2x.length > 4 ? var2x[4] : null;
                     var1.add(new UserBanListEntry(var1x, var3, var4, var5, var6));
                  }
               }

               public void onProfileLookupFailed(String var1x, Exception var2x) {
                  OldUsersConverter.LOGGER.warn("Could not lookup user banlist entry for {}", var1x, var2x);
                  if (!(var2x instanceof ProfileNotFoundException)) {
                     throw new ConversionError("Could not request user " + var1x + " from backend systems", var2x);
                  }
               }
            };
            lookupPlayers(var0, var2.keySet(), var3);
            var1.save();
            renameOldFile(OLD_USERBANLIST);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old user banlist to convert it!", var4);
            return false;
         } catch (ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertIpBanlist(MinecraftServer var0) {
      IpBanList var1 = new IpBanList(PlayerList.IPBANLIST_FILE);
      if (OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
         if (var1.getFile().exists()) {
            try {
               var1.load();
            } catch (IOException var11) {
               LOGGER.warn("Could not load existing file {}", var1.getFile().getName(), var11);
            }
         }

         try {
            HashMap var2 = Maps.newHashMap();
            readOldListFormat(OLD_IPBANLIST, var2);
            Iterator var3 = var2.keySet().iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               String[] var5 = (String[])var2.get(var4);
               Date var6 = var5.length > 1 ? parseDate(var5[1], (Date)null) : null;
               String var7 = var5.length > 2 ? var5[2] : null;
               Date var8 = var5.length > 3 ? parseDate(var5[3], (Date)null) : null;
               String var9 = var5.length > 4 ? var5[4] : null;
               var1.add(new IpBanListEntry(var4, var6, var7, var8, var9));
            }

            var1.save();
            renameOldFile(OLD_IPBANLIST);
            return true;
         } catch (IOException var10) {
            LOGGER.warn("Could not parse old ip banlist to convert it!", var10);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertOpsList(final MinecraftServer var0) {
      final ServerOpList var1 = new ServerOpList(PlayerList.OPLIST_FILE);
      if (OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
         if (var1.getFile().exists()) {
            try {
               var1.load();
            } catch (IOException var6) {
               LOGGER.warn("Could not load existing file {}", var1.getFile().getName(), var6);
            }
         }

         try {
            List var2 = Files.readLines(OLD_OPLIST, StandardCharsets.UTF_8);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1x) {
                  var0.getProfileCache().add(var1x);
                  var1.add(new ServerOpListEntry(var1x, var0.getOperatorUserPermissionLevel(), false));
               }

               public void onProfileLookupFailed(String var1x, Exception var2) {
                  OldUsersConverter.LOGGER.warn("Could not lookup oplist entry for {}", var1x, var2);
                  if (!(var2 instanceof ProfileNotFoundException)) {
                     throw new ConversionError("Could not request user " + var1x + " from backend systems", var2);
                  }
               }
            };
            lookupPlayers(var0, var2, var3);
            var1.save();
            renameOldFile(OLD_OPLIST);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old oplist to convert it!", var4);
            return false;
         } catch (ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean convertWhiteList(final MinecraftServer var0) {
      final UserWhiteList var1 = new UserWhiteList(PlayerList.WHITELIST_FILE);
      if (OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
         if (var1.getFile().exists()) {
            try {
               var1.load();
            } catch (IOException var6) {
               LOGGER.warn("Could not load existing file {}", var1.getFile().getName(), var6);
            }
         }

         try {
            List var2 = Files.readLines(OLD_WHITELIST, StandardCharsets.UTF_8);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1x) {
                  var0.getProfileCache().add(var1x);
                  var1.add(new UserWhiteListEntry(var1x));
               }

               public void onProfileLookupFailed(String var1x, Exception var2) {
                  OldUsersConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", var1x, var2);
                  if (!(var2 instanceof ProfileNotFoundException)) {
                     throw new ConversionError("Could not request user " + var1x + " from backend systems", var2);
                  }
               }
            };
            lookupPlayers(var0, var2, var3);
            var1.save();
            renameOldFile(OLD_WHITELIST);
            return true;
         } catch (IOException var4) {
            LOGGER.warn("Could not read old whitelist to convert it!", var4);
            return false;
         } catch (ConversionError var5) {
            LOGGER.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   @Nullable
   public static UUID convertMobOwnerIfNecessary(final MinecraftServer var0, String var1) {
      if (!StringUtil.isNullOrEmpty(var1) && var1.length() <= 16) {
         Optional var2 = var0.getProfileCache().get(var1).map(GameProfile::getId);
         if (var2.isPresent()) {
            return (UUID)var2.get();
         } else if (!var0.isSingleplayer() && var0.usesAuthentication()) {
            final ArrayList var3 = Lists.newArrayList();
            ProfileLookupCallback var4 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1) {
                  var0.getProfileCache().add(var1);
                  var3.add(var1);
               }

               public void onProfileLookupFailed(String var1, Exception var2) {
                  OldUsersConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", var1, var2);
               }
            };
            lookupPlayers(var0, Lists.newArrayList(new String[]{var1}), var4);
            return !var3.isEmpty() ? ((GameProfile)var3.get(0)).getId() : null;
         } else {
            return UUIDUtil.createOfflinePlayerUUID(var1);
         }
      } else {
         try {
            return UUID.fromString(var1);
         } catch (IllegalArgumentException var5) {
            return null;
         }
      }
   }

   public static boolean convertPlayers(final DedicatedServer var0) {
      final File var1 = getWorldPlayersDirectory(var0);
      final File var2 = new File(var1.getParentFile(), "playerdata");
      final File var3 = new File(var1.getParentFile(), "unknownplayers");
      if (var1.exists() && var1.isDirectory()) {
         File[] var4 = var1.listFiles();
         ArrayList var5 = Lists.newArrayList();
         File[] var6 = var4;
         int var7 = var4.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            File var9 = var6[var8];
            String var10 = var9.getName();
            if (var10.toLowerCase(Locale.ROOT).endsWith(".dat")) {
               String var11 = var10.substring(0, var10.length() - ".dat".length());
               if (!var11.isEmpty()) {
                  var5.add(var11);
               }
            }
         }

         try {
            final String[] var13 = (String[])var5.toArray(new String[var5.size()]);
            ProfileLookupCallback var14 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1x) {
                  var0.getProfileCache().add(var1x);
                  UUID var2x = var1x.getId();
                  this.movePlayerFile(var2, this.getFileNameForProfile(var1x.getName()), var2x.toString());
               }

               public void onProfileLookupFailed(String var1x, Exception var2x) {
                  OldUsersConverter.LOGGER.warn("Could not lookup user uuid for {}", var1x, var2x);
                  if (var2x instanceof ProfileNotFoundException) {
                     String var3x = this.getFileNameForProfile(var1x);
                     this.movePlayerFile(var3, var3x, var3x);
                  } else {
                     throw new ConversionError("Could not request user " + var1x + " from backend systems", var2x);
                  }
               }

               private void movePlayerFile(File var1x, String var2x, String var3x) {
                  File var4 = new File(var1, var2x + ".dat");
                  File var5 = new File(var1x, var3x + ".dat");
                  OldUsersConverter.ensureDirectoryExists(var1x);
                  if (!var4.renameTo(var5)) {
                     throw new ConversionError("Could not convert file for " + var2x);
                  }
               }

               private String getFileNameForProfile(String var1x) {
                  String var2x = null;
                  String[] var3x = var13;
                  int var4 = var3x.length;

                  for(int var5 = 0; var5 < var4; ++var5) {
                     String var6 = var3x[var5];
                     if (var6 != null && var6.equalsIgnoreCase(var1x)) {
                        var2x = var6;
                        break;
                     }
                  }

                  if (var2x == null) {
                     throw new ConversionError("Could not find the filename for " + var1x + " anymore");
                  } else {
                     return var2x;
                  }
               }
            };
            lookupPlayers(var0, Lists.newArrayList(var13), var14);
            return true;
         } catch (ConversionError var12) {
            LOGGER.error("Conversion failed, please try again later", var12);
            return false;
         }
      } else {
         return true;
      }
   }

   static void ensureDirectoryExists(File var0) {
      if (var0.exists()) {
         if (!var0.isDirectory()) {
            throw new ConversionError("Can't create directory " + var0.getName() + " in world save directory.");
         }
      } else if (!var0.mkdirs()) {
         throw new ConversionError("Can't create directory " + var0.getName() + " in world save directory.");
      }
   }

   public static boolean serverReadyAfterUserconversion(MinecraftServer var0) {
      boolean var1 = areOldUserlistsRemoved();
      var1 = var1 && areOldPlayersConverted(var0);
      return var1;
   }

   private static boolean areOldUserlistsRemoved() {
      boolean var0 = false;
      if (OLD_USERBANLIST.exists() && OLD_USERBANLIST.isFile()) {
         var0 = true;
      }

      boolean var1 = false;
      if (OLD_IPBANLIST.exists() && OLD_IPBANLIST.isFile()) {
         var1 = true;
      }

      boolean var2 = false;
      if (OLD_OPLIST.exists() && OLD_OPLIST.isFile()) {
         var2 = true;
      }

      boolean var3 = false;
      if (OLD_WHITELIST.exists() && OLD_WHITELIST.isFile()) {
         var3 = true;
      }

      if (!var0 && !var1 && !var2 && !var3) {
         return true;
      } else {
         LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
         LOGGER.warn("** please remove the following files and restart the server:");
         if (var0) {
            LOGGER.warn("* {}", OLD_USERBANLIST.getName());
         }

         if (var1) {
            LOGGER.warn("* {}", OLD_IPBANLIST.getName());
         }

         if (var2) {
            LOGGER.warn("* {}", OLD_OPLIST.getName());
         }

         if (var3) {
            LOGGER.warn("* {}", OLD_WHITELIST.getName());
         }

         return false;
      }
   }

   private static boolean areOldPlayersConverted(MinecraftServer var0) {
      File var1 = getWorldPlayersDirectory(var0);
      if (!var1.exists() || !var1.isDirectory() || var1.list().length <= 0 && var1.delete()) {
         return true;
      } else {
         LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
         LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
         LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", var1.getPath());
         return false;
      }
   }

   private static File getWorldPlayersDirectory(MinecraftServer var0) {
      return var0.getWorldPath(LevelResource.PLAYER_OLD_DATA_DIR).toFile();
   }

   private static void renameOldFile(File var0) {
      File var1 = new File(var0.getName() + ".converted");
      var0.renameTo(var1);
   }

   static Date parseDate(String var0, Date var1) {
      Date var2;
      try {
         var2 = BanListEntry.DATE_FORMAT.parse(var0);
      } catch (ParseException var4) {
         var2 = var1;
      }

      return var2;
   }

   private static class ConversionError extends RuntimeException {
      ConversionError(String var1, Throwable var2) {
         super(var1, var2);
      }

      ConversionError(String var1) {
         super(var1);
      }
   }
}
