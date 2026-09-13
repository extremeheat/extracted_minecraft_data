package net.minecraft.command.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class CommandAchievement extends CommandBase {
   public CommandAchievement() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "achievement";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.achievement.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length >= 2) {
         StatBase var3 = StatList.func_151177_a(var2[1]);
         if (var3 == null && !var2[1].equals("*")) {
            throw new CommandException("commands.achievement.unknownAchievement", var2[1]);
         }

         EntityPlayerMP var4;
         if (var2.length >= 3) {
            var4 = func_82359_c(var1, var2[2]);
         } else {
            var4 = func_71521_c(var1);
         }

         if (var2[0].equalsIgnoreCase("give")) {
            if (var3 == null) {
               for(Achievement var6 : AchievementList.field_76007_e) {
                  var4.func_71029_a(var6);
               }

               func_152373_a(var1, this, "commands.achievement.give.success.all", new Object[]{var4.func_70005_c_()});
            } else {
               if (var3 instanceof Achievement) {
                  Achievement var9 = (Achievement)var3;

                  ArrayList var10;
                  for(var10 = Lists.newArrayList();
                     var9.field_75992_c != null && !var4.func_147099_x().func_77443_a(var9.field_75992_c);
                     var9 = var9.field_75992_c
                  ) {
                     var10.add(var9.field_75992_c);
                  }

                  for(Achievement var8 : Lists.reverse(var10)) {
                     var4.func_71029_a(var8);
                  }
               }

               var4.func_71029_a(var3);
               func_152373_a(var1, this, "commands.achievement.give.success.one", new Object[]{var4.func_70005_c_(), var3.func_150955_j()});
            }

            return;
         }
      }

      throw new WrongUsageException("commands.achievement.usage");
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"give"});
      } else if (var2.length != 2) {
         return var2.length == 3 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
      } else {
         ArrayList var3 = Lists.newArrayList();

         for(StatBase var5 : StatList.field_75940_b) {
            var3.add(var5.field_75975_e);
         }

         return func_71531_a(var2, var3);
      }
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 2;
   }
}
