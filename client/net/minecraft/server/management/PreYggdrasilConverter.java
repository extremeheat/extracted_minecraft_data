package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {
   private static final Logger field_152732_e = LogManager.getLogger();
   public static final File field_152728_a = new File("banned-ips.txt");
   public static final File field_152729_b = new File("banned-players.txt");
   public static final File field_152730_c = new File("ops.txt");
   public static final File field_152731_d = new File("white-list.txt");

   static List<String> func_152721_a(File var0, Map<String, String[]> var1) throws IOException {
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

   private static void func_152717_a(MinecraftServer var0, Collection<String> var1, ProfileLookupCallback var2) {
      String[] var3 = (String[])var1.stream().filter((var0x) -> {
         return !StringUtils.func_151246_b(var0x);
      }).toArray((var0x) -> {
         return new String[var0x];
      });
      if (var0.func_71266_T()) {
         var0.func_152359_aw().findProfilesByNames(var3, Agent.MINECRAFT, var2);
      } else {
         String[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String var7 = var4[var6];
            UUID var8 = EntityPlayer.func_146094_a(new GameProfile((UUID)null, var7));
            GameProfile var9 = new GameProfile(var8, var7);
            var2.onProfileLookupSucceeded(var9);
         }
      }

   }

   public static boolean func_152724_a(final MinecraftServer var0) {
      final UserListBans var1 = new UserListBans(PlayerList.field_152613_a);
      if (field_152729_b.exists() && field_152729_b.isFile()) {
         if (var1.func_152691_c().exists()) {
            try {
               var1.func_152679_g();
            } catch (FileNotFoundException var6) {
               field_152732_e.warn("Could not load existing file {}", var1.func_152691_c().getName(), var6);
            }
         }

         try {
            final HashMap var2 = Maps.newHashMap();
            func_152721_a(field_152729_b, var2);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1x) {
                  var0.func_152358_ax().func_152649_a(var1x);
                  String[] var2x = (String[])var2.get(var1x.getName().toLowerCase(Locale.ROOT));
                  if (var2x == null) {
                     PreYggdrasilConverter.field_152732_e.warn("Could not convert user banlist entry for {}", var1x.getName());
                     throw new PreYggdrasilConverter.ConversionError("Profile not in the conversionlist");
                  } else {
                     Date var3 = var2x.length > 1 ? PreYggdrasilConverter.func_152713_b(var2x[1], (Date)null) : null;
                     String var4 = var2x.length > 2 ? var2x[2] : null;
                     Date var5 = var2x.length > 3 ? PreYggdrasilConverter.func_152713_b(var2x[3], (Date)null) : null;
                     String var6 = var2x.length > 4 ? var2x[4] : null;
                     var1.func_152687_a(new UserListBansEntry(var1x, var3, var4, var5, var6));
                  }
               }

               public void onProfileLookupFailed(GameProfile var1x, Exception var2x) {
                  PreYggdrasilConverter.field_152732_e.warn("Could not lookup user banlist entry for {}", var1x.getName(), var2x);
                  if (!(var2x instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + var1x.getName() + " from backend systems", var2x);
                  }
               }
            };
            func_152717_a(var0, var2.keySet(), var3);
            var1.func_152678_f();
            func_152727_c(field_152729_b);
            return true;
         } catch (IOException var4) {
            field_152732_e.warn("Could not read old user banlist to convert it!", var4);
            return false;
         } catch (PreYggdrasilConverter.ConversionError var5) {
            field_152732_e.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean func_152722_b(MinecraftServer var0) {
      UserListIPBans var1 = new UserListIPBans(PlayerList.field_152614_b);
      if (field_152728_a.exists() && field_152728_a.isFile()) {
         if (var1.func_152691_c().exists()) {
            try {
               var1.func_152679_g();
            } catch (FileNotFoundException var11) {
               field_152732_e.warn("Could not load existing file {}", var1.func_152691_c().getName(), var11);
            }
         }

         try {
            HashMap var2 = Maps.newHashMap();
            func_152721_a(field_152728_a, var2);
            Iterator var3 = var2.keySet().iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
               String[] var5 = (String[])var2.get(var4);
               Date var6 = var5.length > 1 ? func_152713_b(var5[1], (Date)null) : null;
               String var7 = var5.length > 2 ? var5[2] : null;
               Date var8 = var5.length > 3 ? func_152713_b(var5[3], (Date)null) : null;
               String var9 = var5.length > 4 ? var5[4] : null;
               var1.func_152687_a(new UserListIPBansEntry(var4, var6, var7, var8, var9));
            }

            var1.func_152678_f();
            func_152727_c(field_152728_a);
            return true;
         } catch (IOException var10) {
            field_152732_e.warn("Could not parse old ip banlist to convert it!", var10);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean func_152718_c(final MinecraftServer var0) {
      final UserListOps var1 = new UserListOps(PlayerList.field_152615_c);
      if (field_152730_c.exists() && field_152730_c.isFile()) {
         if (var1.func_152691_c().exists()) {
            try {
               var1.func_152679_g();
            } catch (FileNotFoundException var6) {
               field_152732_e.warn("Could not load existing file {}", var1.func_152691_c().getName(), var6);
            }
         }

         try {
            List var2 = Files.readLines(field_152730_c, StandardCharsets.UTF_8);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1x) {
                  var0.func_152358_ax().func_152649_a(var1x);
                  var1.func_152687_a(new UserListOpsEntry(var1x, var0.func_110455_j(), false));
               }

               public void onProfileLookupFailed(GameProfile var1x, Exception var2) {
                  PreYggdrasilConverter.field_152732_e.warn("Could not lookup oplist entry for {}", var1x.getName(), var2);
                  if (!(var2 instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + var1x.getName() + " from backend systems", var2);
                  }
               }
            };
            func_152717_a(var0, var2, var3);
            var1.func_152678_f();
            func_152727_c(field_152730_c);
            return true;
         } catch (IOException var4) {
            field_152732_e.warn("Could not read old oplist to convert it!", var4);
            return false;
         } catch (PreYggdrasilConverter.ConversionError var5) {
            field_152732_e.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean func_152710_d(final MinecraftServer var0) {
      final UserListWhitelist var1 = new UserListWhitelist(PlayerList.field_152616_d);
      if (field_152731_d.exists() && field_152731_d.isFile()) {
         if (var1.func_152691_c().exists()) {
            try {
               var1.func_152679_g();
            } catch (FileNotFoundException var6) {
               field_152732_e.warn("Could not load existing file {}", var1.func_152691_c().getName(), var6);
            }
         }

         try {
            List var2 = Files.readLines(field_152731_d, StandardCharsets.UTF_8);
            ProfileLookupCallback var3 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1x) {
                  var0.func_152358_ax().func_152649_a(var1x);
                  var1.func_152687_a(new UserListWhitelistEntry(var1x));
               }

               public void onProfileLookupFailed(GameProfile var1x, Exception var2) {
                  PreYggdrasilConverter.field_152732_e.warn("Could not lookup user whitelist entry for {}", var1x.getName(), var2);
                  if (!(var2 instanceof ProfileNotFoundException)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + var1x.getName() + " from backend systems", var2);
                  }
               }
            };
            func_152717_a(var0, var2, var3);
            var1.func_152678_f();
            func_152727_c(field_152731_d);
            return true;
         } catch (IOException var4) {
            field_152732_e.warn("Could not read old whitelist to convert it!", var4);
            return false;
         } catch (PreYggdrasilConverter.ConversionError var5) {
            field_152732_e.error("Conversion failed, please try again later", var5);
            return false;
         }
      } else {
         return true;
      }
   }

   public static String func_187473_a(final MinecraftServer var0, String var1) {
      if (!StringUtils.func_151246_b(var1) && var1.length() <= 16) {
         GameProfile var2 = var0.func_152358_ax().func_152655_a(var1);
         if (var2 != null && var2.getId() != null) {
            return var2.getId().toString();
         } else if (!var0.func_71264_H() && var0.func_71266_T()) {
            final ArrayList var3 = Lists.newArrayList();
            ProfileLookupCallback var4 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1) {
                  var0.func_152358_ax().func_152649_a(var1);
                  var3.add(var1);
               }

               public void onProfileLookupFailed(GameProfile var1, Exception var2) {
                  PreYggdrasilConverter.field_152732_e.warn("Could not lookup user whitelist entry for {}", var1.getName(), var2);
               }
            };
            func_152717_a(var0, Lists.newArrayList(new String[]{var1}), var4);
            return !var3.isEmpty() && ((GameProfile)var3.get(0)).getId() != null ? ((GameProfile)var3.get(0)).getId().toString() : "";
         } else {
            return EntityPlayer.func_146094_a(new GameProfile((UUID)null, var1)).toString();
         }
      } else {
         return var1;
      }
   }

   public static boolean func_152723_a(final DedicatedServer var0, PropertyManager var1) {
      final File var2 = func_152725_d(var1);
      final File var3 = new File(var2.getParentFile(), "playerdata");
      final File var4 = new File(var2.getParentFile(), "unknownplayers");
      if (var2.exists() && var2.isDirectory()) {
         File[] var5 = var2.listFiles();
         ArrayList var6 = Lists.newArrayList();
         File[] var7 = var5;
         int var8 = var5.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            File var10 = var7[var9];
            String var11 = var10.getName();
            if (var11.toLowerCase(Locale.ROOT).endsWith(".dat")) {
               String var12 = var11.substring(0, var11.length() - ".dat".length());
               if (!var12.isEmpty()) {
                  var6.add(var12);
               }
            }
         }

         try {
            final String[] var14 = (String[])var6.toArray(new String[var6.size()]);
            ProfileLookupCallback var15 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1) {
                  var0.func_152358_ax().func_152649_a(var1);
                  UUID var2x = var1.getId();
                  if (var2x == null) {
                     throw new PreYggdrasilConverter.ConversionError("Missing UUID for user profile " + var1.getName());
                  } else {
                     this.func_152743_a(var3, this.func_152744_a(var1), var2x.toString());
                  }
               }

               public void onProfileLookupFailed(GameProfile var1, Exception var2x) {
                  PreYggdrasilConverter.field_152732_e.warn("Could not lookup user uuid for {}", var1.getName(), var2x);
                  if (var2x instanceof ProfileNotFoundException) {
                     String var3x = this.func_152744_a(var1);
                     this.func_152743_a(var4, var3x, var3x);
                  } else {
                     throw new PreYggdrasilConverter.ConversionError("Could not request user " + var1.getName() + " from backend systems", var2x);
                  }
               }

               private void func_152743_a(File var1, String var2x, String var3x) {
                  File var4x = new File(var2, var2x + ".dat");
                  File var5 = new File(var1, var3x + ".dat");
                  PreYggdrasilConverter.func_152711_b(var1);
                  if (!var4x.renameTo(var5)) {
                     throw new PreYggdrasilConverter.ConversionError("Could not convert file for " + var2x);
                  }
               }

               private String func_152744_a(GameProfile var1) {
                  String var2x = null;
                  String[] var3x = var14;
                  int var4x = var3x.length;

                  for(int var5 = 0; var5 < var4x; ++var5) {
                     String var6 = var3x[var5];
                     if (var6 != null && var6.equalsIgnoreCase(var1.getName())) {
                        var2x = var6;
                        break;
                     }
                  }

                  if (var2x == null) {
                     throw new PreYggdrasilConverter.ConversionError("Could not find the filename for " + var1.getName() + " anymore");
                  } else {
                     return var2x;
                  }
               }
            };
            func_152717_a(var0, Lists.newArrayList(var14), var15);
            return true;
         } catch (PreYggdrasilConverter.ConversionError var13) {
            field_152732_e.error("Conversion failed, please try again later", var13);
            return false;
         }
      } else {
         return true;
      }
   }

   private static void func_152711_b(File var0) {
      if (var0.exists()) {
         if (!var0.isDirectory()) {
            throw new PreYggdrasilConverter.ConversionError("Can't create directory " + var0.getName() + " in world save directory.");
         }
      } else if (!var0.mkdirs()) {
         throw new PreYggdrasilConverter.ConversionError("Can't create directory " + var0.getName() + " in world save directory.");
      }
   }

   public static boolean func_152714_a(PropertyManager var0) {
      boolean var1 = func_152712_b(var0);
      var1 = var1 && func_152715_c(var0);
      return var1;
   }

   private static boolean func_152712_b(PropertyManager var0) {
      boolean var1 = false;
      if (field_152729_b.exists() && field_152729_b.isFile()) {
         var1 = true;
      }

      boolean var2 = false;
      if (field_152728_a.exists() && field_152728_a.isFile()) {
         var2 = true;
      }

      boolean var3 = false;
      if (field_152730_c.exists() && field_152730_c.isFile()) {
         var3 = true;
      }

      boolean var4 = false;
      if (field_152731_d.exists() && field_152731_d.isFile()) {
         var4 = true;
      }

      if (!var1 && !var2 && !var3 && !var4) {
         return true;
      } else {
         field_152732_e.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
         field_152732_e.warn("** please remove the following files and restart the server:");
         if (var1) {
            field_152732_e.warn("* {}", field_152729_b.getName());
         }

         if (var2) {
            field_152732_e.warn("* {}", field_152728_a.getName());
         }

         if (var3) {
            field_152732_e.warn("* {}", field_152730_c.getName());
         }

         if (var4) {
            field_152732_e.warn("* {}", field_152731_d.getName());
         }

         return false;
      }
   }

   private static boolean func_152715_c(PropertyManager var0) {
      File var1 = func_152725_d(var0);
      if (!var1.exists() || !var1.isDirectory() || var1.list().length <= 0 && var1.delete()) {
         return true;
      } else {
         field_152732_e.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
         field_152732_e.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
         field_152732_e.warn("** please restart the server and if the problem persists, remove the directory '{}'", var1.getPath());
         return false;
      }
   }

   private static File func_152725_d(PropertyManager var0) {
      String var1 = var0.func_73671_a("level-name", "world");
      File var2 = new File(var1);
      return new File(var2, "players");
   }

   private static void func_152727_c(File var0) {
      File var1 = new File(var0.getName() + ".converted");
      var0.renameTo(var1);
   }

   private static Date func_152713_b(String var0, Date var1) {
      Date var2;
      try {
         var2 = UserListEntryBan.field_73698_a.parse(var0);
      } catch (ParseException var4) {
         var2 = var1;
      }

      return var2;
   }

   static class ConversionError extends RuntimeException {
      private ConversionError(String var1, Throwable var2) {
         super(var1, var2);
      }

      private ConversionError(String var1) {
         super(var1);
      }

      // $FF: synthetic method
      ConversionError(String var1, Object var2) {
         this(var1);
      }

      // $FF: synthetic method
      ConversionError(String var1, Throwable var2, Object var3) {
         this(var1, var2);
      }
   }
}
