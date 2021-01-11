package net.minecraft.command.server;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
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
import net.minecraft.util.BlockPos;

public class CommandAchievement extends CommandBase {
   public CommandAchievement() {
      super();
   }

   public String func_71517_b() {
      return "achievement";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.achievement.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 2) {
         throw new WrongUsageException("commands.achievement.usage", new Object[0]);
      } else {
         final StatBase var3 = StatList.func_151177_a(var2[1]);
         if (var3 == null && !var2[1].equals("*")) {
            throw new CommandException("commands.achievement.unknownAchievement", new Object[]{var2[1]});
         } else {
            final EntityPlayerMP var4 = var2.length >= 3 ? func_82359_c(var1, var2[2]) : func_71521_c(var1);
            boolean var5 = var2[0].equalsIgnoreCase("give");
            boolean var6 = var2[0].equalsIgnoreCase("take");
            if (var5 || var6) {
               if (var3 == null) {
                  Iterator var14;
                  Achievement var15;
                  if (var5) {
                     var14 = AchievementList.field_76007_e.iterator();

                     while(var14.hasNext()) {
                        var15 = (Achievement)var14.next();
                        var4.func_71029_a(var15);
                     }

                     func_152373_a(var1, this, "commands.achievement.give.success.all", new Object[]{var4.func_70005_c_()});
                  } else if (var6) {
                     var14 = Lists.reverse(AchievementList.field_76007_e).iterator();

                     while(var14.hasNext()) {
                        var15 = (Achievement)var14.next();
                        var4.func_175145_a(var15);
                     }

                     func_152373_a(var1, this, "commands.achievement.take.success.all", new Object[]{var4.func_70005_c_()});
                  }

               } else {
                  if (var3 instanceof Achievement) {
                     Achievement var7 = (Achievement)var3;
                     ArrayList var8;
                     if (var5) {
                        if (var4.func_147099_x().func_77443_a(var7)) {
                           throw new CommandException("commands.achievement.alreadyHave", new Object[]{var4.func_70005_c_(), var3.func_150955_j()});
                        }

                        for(var8 = Lists.newArrayList(); var7.field_75992_c != null && !var4.func_147099_x().func_77443_a(var7.field_75992_c); var7 = var7.field_75992_c) {
                           var8.add(var7.field_75992_c);
                        }

                        Iterator var9 = Lists.reverse(var8).iterator();

                        while(var9.hasNext()) {
                           Achievement var10 = (Achievement)var9.next();
                           var4.func_71029_a(var10);
                        }
                     } else if (var6) {
                        if (!var4.func_147099_x().func_77443_a(var7)) {
                           throw new CommandException("commands.achievement.dontHave", new Object[]{var4.func_70005_c_(), var3.func_150955_j()});
                        }

                        var8 = Lists.newArrayList(Iterators.filter(AchievementList.field_76007_e.iterator(), new Predicate<Achievement>() {
                           public boolean apply(Achievement var1) {
                              return var4.func_147099_x().func_77443_a(var1) && var1 != var3;
                           }

                           // $FF: synthetic method
                           public boolean apply(Object var1) {
                              return this.apply((Achievement)var1);
                           }
                        }));
                        ArrayList var16 = Lists.newArrayList(var8);
                        Iterator var17 = var8.iterator();

                        label118:
                        while(true) {
                           Achievement var11;
                           Achievement var12;
                           boolean var13;
                           do {
                              if (!var17.hasNext()) {
                                 var17 = var16.iterator();

                                 while(var17.hasNext()) {
                                    var11 = (Achievement)var17.next();
                                    var4.func_175145_a(var11);
                                 }
                                 break label118;
                              }

                              var11 = (Achievement)var17.next();
                              var12 = var11;

                              for(var13 = false; var12 != null; var12 = var12.field_75992_c) {
                                 if (var12 == var3) {
                                    var13 = true;
                                 }
                              }
                           } while(var13);

                           for(var12 = var11; var12 != null; var12 = var12.field_75992_c) {
                              var16.remove(var11);
                           }
                        }
                     }
                  }

                  if (var5) {
                     var4.func_71029_a(var3);
                     func_152373_a(var1, this, "commands.achievement.give.success.one", new Object[]{var4.func_70005_c_(), var3.func_150955_j()});
                  } else if (var6) {
                     var4.func_175145_a(var3);
                     func_152373_a(var1, this, "commands.achievement.take.success.one", new Object[]{var3.func_150955_j(), var4.func_70005_c_()});
                  }

               }
            }
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"give", "take"});
      } else if (var2.length != 2) {
         return var2.length == 3 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
      } else {
         ArrayList var4 = Lists.newArrayList();
         Iterator var5 = StatList.field_75940_b.iterator();

         while(var5.hasNext()) {
            StatBase var6 = (StatBase)var5.next();
            var4.add(var6.field_75975_e);
         }

         return func_175762_a(var2, var4);
      }
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 2;
   }
}
