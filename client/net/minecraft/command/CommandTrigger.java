package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandTrigger extends CommandBase {
   public CommandTrigger() {
      super();
   }

   public String func_71517_b() {
      return "trigger";
   }

   public int func_82362_a() {
      return 0;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.trigger.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 3) {
         throw new WrongUsageException("commands.trigger.usage", new Object[0]);
      } else {
         EntityPlayerMP var3;
         if (var1 instanceof EntityPlayerMP) {
            var3 = (EntityPlayerMP)var1;
         } else {
            Entity var4 = var1.func_174793_f();
            if (!(var4 instanceof EntityPlayerMP)) {
               throw new CommandException("commands.trigger.invalidPlayer", new Object[0]);
            }

            var3 = (EntityPlayerMP)var4;
         }

         Scoreboard var8 = MinecraftServer.func_71276_C().func_71218_a(0).func_96441_U();
         ScoreObjective var5 = var8.func_96518_b(var2[0]);
         if (var5 != null && var5.func_96680_c() == IScoreObjectiveCriteria.field_178791_c) {
            int var6 = func_175755_a(var2[2]);
            if (!var8.func_178819_b(var3.func_70005_c_(), var5)) {
               throw new CommandException("commands.trigger.invalidObjective", new Object[]{var2[0]});
            } else {
               Score var7 = var8.func_96529_a(var3.func_70005_c_(), var5);
               if (var7.func_178816_g()) {
                  throw new CommandException("commands.trigger.disabled", new Object[]{var2[0]});
               } else {
                  if ("set".equals(var2[1])) {
                     var7.func_96647_c(var6);
                  } else {
                     if (!"add".equals(var2[1])) {
                        throw new CommandException("commands.trigger.invalidMode", new Object[]{var2[1]});
                     }

                     var7.func_96649_a(var6);
                  }

                  var7.func_178815_a(true);
                  if (var3.field_71134_c.func_73083_d()) {
                     func_152373_a(var1, this, "commands.trigger.success", new Object[]{var2[0], var2[1], var2[2]});
                  }

               }
            }
         } else {
            throw new CommandException("commands.trigger.invalidObjective", new Object[]{var2[0]});
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         Scoreboard var4 = MinecraftServer.func_71276_C().func_71218_a(0).func_96441_U();
         ArrayList var5 = Lists.newArrayList();
         Iterator var6 = var4.func_96514_c().iterator();

         while(var6.hasNext()) {
            ScoreObjective var7 = (ScoreObjective)var6.next();
            if (var7.func_96680_c() == IScoreObjectiveCriteria.field_178791_c) {
               var5.add(var7.func_96679_b());
            }
         }

         return func_71530_a(var2, (String[])var5.toArray(new String[var5.size()]));
      } else {
         return var2.length == 2 ? func_71530_a(var2, new String[]{"add", "set"}) : null;
      }
   }
}
