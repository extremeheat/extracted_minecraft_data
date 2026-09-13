package net.minecraft.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings$GameType;

public class PlayerSelector {
   private static final Pattern field_82389_a = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");
   private static final Pattern field_82387_b = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
   private static final Pattern field_82388_c = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");

   public static EntityPlayerMP func_82386_a(ICommandSender var0, String var1) {
      EntityPlayerMP[] var2 = func_82380_c(var0, var1);
      return var2 != null && var2.length == 1 ? var2[0] : null;
   }

   public static IChatComponent func_150869_b(ICommandSender var0, String var1) {
      EntityPlayerMP[] var2 = func_82380_c(var0, var1);
      if (var2 != null && var2.length != 0) {
         IChatComponent[] var3 = new IChatComponent[var2.length];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = var2[var4].func_145748_c_();
         }

         return CommandBase.func_147177_a(var3);
      } else {
         return null;
      }
   }

   public static EntityPlayerMP[] func_82380_c(ICommandSender var0, String var1) {
      Matcher var2 = field_82389_a.matcher(var1);
      if (var2.matches()) {
         Map var3 = func_82381_h(var2.group(2));
         String var4 = var2.group(1);
         int var5 = func_82384_c(var4);
         int var6 = func_82379_d(var4);
         int var7 = func_82375_f(var4);
         int var8 = func_82376_e(var4);
         int var9 = func_82382_g(var4);
         int var10 = WorldSettings$GameType.NOT_SET.func_77148_a();
         ChunkCoordinates var11 = var0.func_82114_b();
         Map var12 = func_96560_a(var3);
         String var13 = null;
         String var14 = null;
         boolean var15 = false;
         if (var3.containsKey("rm")) {
            var5 = MathHelper.func_82715_a((String)var3.get("rm"), var5);
            var15 = true;
         }

         if (var3.containsKey("r")) {
            var6 = MathHelper.func_82715_a((String)var3.get("r"), var6);
            var15 = true;
         }

         if (var3.containsKey("lm")) {
            var7 = MathHelper.func_82715_a((String)var3.get("lm"), var7);
         }

         if (var3.containsKey("l")) {
            var8 = MathHelper.func_82715_a((String)var3.get("l"), var8);
         }

         if (var3.containsKey("x")) {
            var11.field_71574_a = MathHelper.func_82715_a((String)var3.get("x"), var11.field_71574_a);
            var15 = true;
         }

         if (var3.containsKey("y")) {
            var11.field_71572_b = MathHelper.func_82715_a((String)var3.get("y"), var11.field_71572_b);
            var15 = true;
         }

         if (var3.containsKey("z")) {
            var11.field_71573_c = MathHelper.func_82715_a((String)var3.get("z"), var11.field_71573_c);
            var15 = true;
         }

         if (var3.containsKey("m")) {
            var10 = MathHelper.func_82715_a((String)var3.get("m"), var10);
         }

         if (var3.containsKey("c")) {
            var9 = MathHelper.func_82715_a((String)var3.get("c"), var9);
         }

         if (var3.containsKey("team")) {
            var14 = (String)var3.get("team");
         }

         if (var3.containsKey("name")) {
            var13 = (String)var3.get("name");
         }

         World var16 = var15 ? var0.func_130014_f_() : null;
         if (var4.equals("p") || var4.equals("a")) {
            List var19 = MinecraftServer.func_71276_C().func_71203_ab().func_82449_a(var11, var5, var6, var9, var10, var7, var8, var12, var13, var14, var16);
            return var19.isEmpty() ? new EntityPlayerMP[0] : var19.toArray(new EntityPlayerMP[var19.size()]);
         } else if (var4.equals("r")) {
            List var17 = MinecraftServer.func_71276_C().func_71203_ab().func_82449_a(var11, var5, var6, 0, var10, var7, var8, var12, var13, var14, var16);
            Collections.shuffle(var17);
            var17 = var17.subList(0, Math.min(var9, var17.size()));
            return var17.isEmpty() ? new EntityPlayerMP[0] : var17.toArray(new EntityPlayerMP[var17.size()]);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static Map func_96560_a(Map var0) {
      HashMap var1 = new HashMap();

      for(String var3 : var0.keySet()) {
         if (var3.startsWith("score_") && var3.length() > "score_".length()) {
            String var4 = var3.substring("score_".length());
            var1.put(var4, MathHelper.func_82715_a((String)var0.get(var3), 1));
         }
      }

      return var1;
   }

   public static boolean func_82377_a(String var0) {
      Matcher var1 = field_82389_a.matcher(var0);
      if (var1.matches()) {
         Map var2 = func_82381_h(var1.group(2));
         String var3 = var1.group(1);
         int var4 = func_82382_g(var3);
         if (var2.containsKey("c")) {
            var4 = MathHelper.func_82715_a((String)var2.get("c"), var4);
         }

         return var4 != 1;
      } else {
         return false;
      }
   }

   public static boolean func_82383_a(String var0, String var1) {
      Matcher var2 = field_82389_a.matcher(var0);
      if (var2.matches()) {
         String var3 = var2.group(1);
         return var1 == null || var1.equals(var3);
      } else {
         return false;
      }
   }

   public static boolean func_82378_b(String var0) {
      return func_82383_a(var0, null);
   }

   private static final int func_82384_c(String var0) {
      return 0;
   }

   private static final int func_82379_d(String var0) {
      return 0;
   }

   private static final int func_82376_e(String var0) {
      return 2147483647;
   }

   private static final int func_82375_f(String var0) {
      return 0;
   }

   private static final int func_82382_g(String var0) {
      return var0.equals("a") ? 0 : 1;
   }

   private static Map func_82381_h(String var0) {
      HashMap var1 = new HashMap();
      if (var0 == null) {
         return var1;
      } else {
         Matcher var2 = field_82387_b.matcher(var0);
         int var3 = 0;

         int var4;
         for(var4 = -1; var2.find(); var4 = var2.end()) {
            String var5 = null;
            switch(var3++) {
               case 0:
                  var5 = "x";
                  break;
               case 1:
                  var5 = "y";
                  break;
               case 2:
                  var5 = "z";
                  break;
               case 3:
                  var5 = "r";
            }

            if (var5 != null && var2.group(1).length() > 0) {
               var1.put(var5, var2.group(1));
            }
         }

         if (var4 < var0.length()) {
            var2 = field_82388_c.matcher(var4 == -1 ? var0 : var0.substring(var4));

            while(var2.find()) {
               var1.put(var2.group(1), var2.group(2));
            }
         }

         return var1;
      }
   }
}
