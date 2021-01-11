package net.minecraft.server.management;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {
   private static final Logger field_152732_e = LogManager.getLogger();
   public static final File field_152728_a = new File("banned-ips.txt");
   public static final File field_152729_b = new File("banned-players.txt");
   public static final File field_152730_c = new File("ops.txt");
   public static final File field_152731_d = new File("white-list.txt");

   private static void func_152717_a(MinecraftServer var0, Collection<String> var1, ProfileLookupCallback var2) {
      String[] var3 = (String[])Iterators.toArray(Iterators.filter(var1.iterator(), new Predicate<String>() {
         public boolean apply(String var1) {
            return !StringUtils.func_151246_b(var1);
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((String)var1);
         }
      }), String.class);
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

   public static String func_152719_a(String var0) {
      if (!StringUtils.func_151246_b(var0) && var0.length() <= 16) {
         final MinecraftServer var1 = MinecraftServer.func_71276_C();
         GameProfile var2 = var1.func_152358_ax().func_152655_a(var0);
         if (var2 != null && var2.getId() != null) {
            return var2.getId().toString();
         } else if (!var1.func_71264_H() && var1.func_71266_T()) {
            final ArrayList var3 = Lists.newArrayList();
            ProfileLookupCallback var4 = new ProfileLookupCallback() {
               public void onProfileLookupSucceeded(GameProfile var1x) {
                  var1.func_152358_ax().func_152649_a(var1x);
                  var3.add(var1x);
               }

               public void onProfileLookupFailed(GameProfile var1x, Exception var2) {
                  PreYggdrasilConverter.field_152732_e.warn("Could not lookup user whitelist entry for " + var1x.getName(), var2);
               }
            };
            func_152717_a(var1, Lists.newArrayList(new String[]{var0}), var4);
            return var3.size() > 0 && ((GameProfile)var3.get(0)).getId() != null ? ((GameProfile)var3.get(0)).getId().toString() : "";
         } else {
            return EntityPlayer.func_146094_a(new GameProfile((UUID)null, var0)).toString();
         }
      } else {
         return var0;
      }
   }
}
