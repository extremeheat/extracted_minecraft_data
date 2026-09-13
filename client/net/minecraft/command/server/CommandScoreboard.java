package net.minecraft.command.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase {
   public CommandScoreboard() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "scoreboard";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.scoreboard.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length >= 1) {
         if (var2[0].equalsIgnoreCase("objectives")) {
            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.objectives.usage");
            }

            if (var2[1].equalsIgnoreCase("list")) {
               this.func_147196_d(var1);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length < 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
               }

               this.func_147193_c(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.objectives.remove.usage");
               }

               this.func_147191_h(var1, var2[2]);
            } else {
               if (!var2[1].equalsIgnoreCase("setdisplay")) {
                  throw new WrongUsageException("commands.scoreboard.objectives.usage");
               }

               if (var2.length != 3 && var2.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage");
               }

               this.func_147198_k(var1, var2, 2);
            }

            return;
         }

         if (var2[0].equalsIgnoreCase("players")) {
            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.players.usage");
            }

            if (var2[1].equalsIgnoreCase("list")) {
               if (var2.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.players.list.usage");
               }

               this.func_147195_l(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.players.add.usage");
               }

               this.func_147197_m(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.players.remove.usage");
               }

               this.func_147197_m(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("set")) {
               if (var2.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.players.set.usage");
               }

               this.func_147197_m(var1, var2, 2);
            } else {
               if (!var2[1].equalsIgnoreCase("reset")) {
                  throw new WrongUsageException("commands.scoreboard.players.usage");
               }

               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.players.reset.usage");
               }

               this.func_147187_n(var1, var2, 2);
            }

            return;
         }

         if (var2[0].equalsIgnoreCase("teams")) {
            if (var2.length == 1) {
               throw new WrongUsageException("commands.scoreboard.teams.usage");
            }

            if (var2[1].equalsIgnoreCase("list")) {
               if (var2.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.list.usage");
               }

               this.func_147186_g(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length < 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.add.usage");
               }

               this.func_147185_d(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.remove.usage");
               }

               this.func_147194_f(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("empty")) {
               if (var2.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.empty.usage");
               }

               this.func_147188_j(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("join")) {
               if (var2.length < 4 && (var2.length != 3 || !(var1 instanceof EntityPlayer))) {
                  throw new WrongUsageException("commands.scoreboard.teams.join.usage");
               }

               this.func_147190_h(var1, var2, 2);
            } else if (var2[1].equalsIgnoreCase("leave")) {
               if (var2.length < 3 && !(var1 instanceof EntityPlayer)) {
                  throw new WrongUsageException("commands.scoreboard.teams.leave.usage");
               }

               this.func_147199_i(var1, var2, 2);
            } else {
               if (!var2[1].equalsIgnoreCase("option")) {
                  throw new WrongUsageException("commands.scoreboard.teams.usage");
               }

               if (var2.length != 4 && var2.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               this.func_147200_e(var1, var2, 2);
            }

            return;
         }
      }

      throw new WrongUsageException("commands.scoreboard.usage");
   }

   protected Scoreboard func_147192_d() {
      return MinecraftServer.func_71276_C().func_71218_a(0).func_96441_U();
   }

   protected ScoreObjective func_147189_a(String var1, boolean var2) {
      Scoreboard var3 = this.func_147192_d();
      ScoreObjective var4 = var3.func_96518_b(var1);
      if (var4 == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", var1);
      } else if (var2 && var4.func_96680_c().func_96637_b()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", var1);
      } else {
         return var4;
      }
   }

   protected ScorePlayerTeam func_147183_a(String var1) {
      Scoreboard var2 = this.func_147192_d();
      ScorePlayerTeam var3 = var2.func_96508_e(var1);
      if (var3 == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", var1);
      } else {
         return var3;
      }
   }

   protected void func_147193_c(ICommandSender var1, String[] var2, int var3) {
      String var4 = var2[var3++];
      String var5 = var2[var3++];
      Scoreboard var6 = this.func_147192_d();
      IScoreObjectiveCriteria var7 = (IScoreObjectiveCriteria)IScoreObjectiveCriteria.field_96643_a.get(var5);
      if (var7 == null) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", var5);
      } else if (var6.func_96518_b(var4) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", var4);
      } else if (var4.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", var4, 16);
      } else if (var4.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
      } else {
         if (var2.length > var3) {
            String var8 = func_147178_a(var1, var2, var3).func_150260_c();
            if (var8.length() > 32) {
               throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", var8, 32);
            }

            if (var8.length() > 0) {
               var6.func_96535_a(var4, var7).func_96681_a(var8);
            } else {
               var6.func_96535_a(var4, var7);
            }
         } else {
            var6.func_96535_a(var4, var7);
         }

         func_152373_a(var1, this, "commands.scoreboard.objectives.add.success", new Object[]{var4});
      }
   }

   protected void func_147185_d(ICommandSender var1, String[] var2, int var3) {
      String var4 = var2[var3++];
      Scoreboard var5 = this.func_147192_d();
      if (var5.func_96508_e(var4) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", var4);
      } else if (var4.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", var4, 16);
      } else if (var4.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.teams.add.usage");
      } else {
         if (var2.length > var3) {
            String var6 = func_147178_a(var1, var2, var3).func_150260_c();
            if (var6.length() > 32) {
               throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", var6, 32);
            }

            if (var6.length() > 0) {
               var5.func_96527_f(var4).func_96664_a(var6);
            } else {
               var5.func_96527_f(var4);
            }
         } else {
            var5.func_96527_f(var4);
         }

         func_152373_a(var1, this, "commands.scoreboard.teams.add.success", new Object[]{var4});
      }
   }

   protected void func_147200_e(ICommandSender var1, String[] var2, int var3) {
      ScorePlayerTeam var4 = this.func_147183_a(var2[var3++]);
      if (var4 != null) {
         String var5 = var2[var3++].toLowerCase();
         if (!var5.equalsIgnoreCase("color") && !var5.equalsIgnoreCase("friendlyfire") && !var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
            throw new WrongUsageException("commands.scoreboard.teams.option.usage");
         } else if (var2.length == 4) {
            if (var5.equalsIgnoreCase("color")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var5, func_96333_a(EnumChatFormatting.func_96296_a(true, false)));
            } else if (!var5.equalsIgnoreCase("friendlyfire") && !var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.usage");
            } else {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var5, func_96333_a(Arrays.asList("true", "false")));
            }
         } else {
            String var6 = var2[var3++];
            if (var5.equalsIgnoreCase("color")) {
               EnumChatFormatting var7 = EnumChatFormatting.func_96300_b(var6);
               if (var7 == null || var7.func_96301_b()) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var5, func_96333_a(EnumChatFormatting.func_96296_a(true, false)));
               }

               var4.func_96666_b(var7.toString());
               var4.func_96662_c(EnumChatFormatting.RESET.toString());
            } else if (var5.equalsIgnoreCase("friendlyfire")) {
               if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var5, func_96333_a(Arrays.asList("true", "false")));
               }

               var4.func_96660_a(var6.equalsIgnoreCase("true"));
            } else if (var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.noValue", var5, func_96333_a(Arrays.asList("true", "false")));
               }

               var4.func_98300_b(var6.equalsIgnoreCase("true"));
            }

            func_152373_a(var1, this, "commands.scoreboard.teams.option.success", new Object[]{var5, var4.func_96661_b(), var6});
         }
      }
   }

   protected void func_147194_f(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.func_147192_d();
      ScorePlayerTeam var5 = this.func_147183_a(var2[var3++]);
      if (var5 != null) {
         var4.func_96511_d(var5);
         func_152373_a(var1, this, "commands.scoreboard.teams.remove.success", new Object[]{var5.func_96661_b()});
      }
   }

   protected void func_147186_g(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.func_147192_d();
      if (var2.length > var3) {
         ScorePlayerTeam var5 = this.func_147183_a(var2[var3++]);
         if (var5 == null) {
            return;
         }

         Collection var6 = var5.func_96670_d();
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", var5.func_96661_b());
         }

         ChatComponentTranslation var7 = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", var6.size(), var5.func_96661_b());
         var7.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var7);
         var1.func_145747_a(new ChatComponentText(func_71527_a(var6.toArray())));
      } else {
         Collection var10 = var4.func_96525_g();
         if (var10.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty");
         }

         ChatComponentTranslation var11 = new ChatComponentTranslation("commands.scoreboard.teams.list.count", var10.size());
         var11.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var11);

         for(ScorePlayerTeam var8 : var10) {
            var1.func_145747_a(
               new ChatComponentTranslation("commands.scoreboard.teams.list.entry", var8.func_96661_b(), var8.func_96669_c(), var8.func_96670_d().size())
            );
         }
      }
   }

   protected void func_147190_h(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.func_147192_d();
      String var5 = var2[var3++];
      HashSet var6 = new HashSet();
      HashSet var7 = new HashSet();
      if (var1 instanceof EntityPlayer && var3 == var2.length) {
         String var10 = func_71521_c(var1).func_70005_c_();
         if (var4.func_151392_a(var10, var5)) {
            var6.add(var10);
         } else {
            var7.add(var10);
         }
      } else {
         while(var3 < var2.length) {
            String var8 = func_96332_d(var1, var2[var3++]);
            if (var4.func_151392_a(var8, var5)) {
               var6.add(var8);
            } else {
               var7.add(var8);
            }
         }
      }

      if (!var6.isEmpty()) {
         func_152373_a(var1, this, "commands.scoreboard.teams.join.success", new Object[]{var6.size(), var5, func_71527_a(var6.toArray(new String[0]))});
      }

      if (!var7.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.join.failure", var7.size(), var5, func_71527_a(var7.toArray(new String[0])));
      }
   }

   protected void func_147199_i(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.func_147192_d();
      HashSet var5 = new HashSet();
      HashSet var6 = new HashSet();
      if (var1 instanceof EntityPlayer && var3 == var2.length) {
         String var8 = func_71521_c(var1).func_70005_c_();
         if (var4.func_96524_g(var8)) {
            var5.add(var8);
         } else {
            var6.add(var8);
         }
      } else {
         while(var3 < var2.length) {
            String var7 = func_96332_d(var1, var2[var3++]);
            if (var4.func_96524_g(var7)) {
               var5.add(var7);
            } else {
               var6.add(var7);
            }
         }
      }

      if (!var5.isEmpty()) {
         func_152373_a(var1, this, "commands.scoreboard.teams.leave.success", new Object[]{var5.size(), func_71527_a(var5.toArray(new String[0]))});
      }

      if (!var6.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", var6.size(), func_71527_a(var6.toArray(new String[0])));
      }
   }

   protected void func_147188_j(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.func_147192_d();
      ScorePlayerTeam var5 = this.func_147183_a(var2[var3++]);
      if (var5 != null) {
         ArrayList var6 = new ArrayList(var5.func_96670_d());
         if (var6.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", var5.func_96661_b());
         } else {
            for(String var8 : var6) {
               var4.func_96512_b(var8, var5);
            }

            func_152373_a(var1, this, "commands.scoreboard.teams.empty.success", new Object[]{var6.size(), var5.func_96661_b()});
         }
      }
   }

   protected void func_147191_h(ICommandSender var1, String var2) {
      Scoreboard var3 = this.func_147192_d();
      ScoreObjective var4 = this.func_147189_a(var2, false);
      var3.func_96519_k(var4);
      func_152373_a(var1, this, "commands.scoreboard.objectives.remove.success", new Object[]{var2});
   }

   protected void func_147196_d(ICommandSender var1) {
      Scoreboard var2 = this.func_147192_d();
      Collection var3 = var2.func_96514_c();
      if (var3.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty");
      } else {
         ChatComponentTranslation var4 = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", var3.size());
         var4.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var4);

         for(ScoreObjective var6 : var3) {
            var1.func_145747_a(
               new ChatComponentTranslation(
                  "commands.scoreboard.objectives.list.entry", var6.func_96679_b(), var6.func_96678_d(), var6.func_96680_c().func_96636_a()
               )
            );
         }
      }
   }

   protected void func_147198_k(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.func_147192_d();
      String var5 = var2[var3++];
      int var6 = Scoreboard.func_96537_j(var5);
      ScoreObjective var7 = null;
      if (var2.length == 4) {
         var7 = this.func_147189_a(var2[var3++], false);
      }

      if (var6 < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", var5);
      } else {
         var4.func_96530_a(var6, var7);
         if (var7 != null) {
            func_152373_a(var1, this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[]{Scoreboard.func_96517_b(var6), var7.func_96679_b()});
         } else {
            func_152373_a(var1, this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[]{Scoreboard.func_96517_b(var6)});
         }
      }
   }

   protected void func_147195_l(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.func_147192_d();
      if (var2.length > var3) {
         String var5 = func_96332_d(var1, var2[var3++]);
         Map var6 = var4.func_96510_d(var5);
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", var5);
         }

         ChatComponentTranslation var7 = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", var6.size(), var5);
         var7.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var7);

         for(Score var9 : var6.values()) {
            var1.func_145747_a(
               new ChatComponentTranslation(
                  "commands.scoreboard.players.list.player.entry", var9.func_96652_c(), var9.func_96645_d().func_96678_d(), var9.func_96645_d().func_96679_b()
               )
            );
         }
      } else {
         Collection var11 = var4.func_96526_d();
         if (var11.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty");
         }

         ChatComponentTranslation var12 = new ChatComponentTranslation("commands.scoreboard.players.list.count", var11.size());
         var12.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var12);
         var1.func_145747_a(new ChatComponentText(func_71527_a(var11.toArray())));
      }
   }

   protected void func_147197_m(ICommandSender var1, String[] var2, int var3) {
      String var4 = var2[var3 - 1];
      String var5 = func_96332_d(var1, var2[var3++]);
      ScoreObjective var6 = this.func_147189_a(var2[var3++], true);
      int var7 = var4.equalsIgnoreCase("set") ? func_71526_a(var1, var2[var3++]) : func_71528_a(var1, var2[var3++], 1);
      Scoreboard var8 = this.func_147192_d();
      Score var9 = var8.func_96529_a(var5, var6);
      if (var4.equalsIgnoreCase("set")) {
         var9.func_96647_c(var7);
      } else if (var4.equalsIgnoreCase("add")) {
         var9.func_96649_a(var7);
      } else {
         var9.func_96646_b(var7);
      }

      func_152373_a(var1, this, "commands.scoreboard.players.set.success", new Object[]{var6.func_96679_b(), var5, var9.func_96652_c()});
   }

   protected void func_147187_n(ICommandSender var1, String[] var2, int var3) {
      Scoreboard var4 = this.func_147192_d();
      String var5 = func_96332_d(var1, var2[var3++]);
      var4.func_96515_c(var5);
      func_152373_a(var1, this, "commands.scoreboard.players.reset.success", new Object[]{var5});
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"objectives", "players", "teams"});
      } else {
         if (var2[0].equalsIgnoreCase("objectives")) {
            if (var2.length == 2) {
               return func_71530_a(var2, new String[]{"list", "add", "remove", "setdisplay"});
            }

            if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length == 4) {
                  Set var3 = IScoreObjectiveCriteria.field_96643_a.keySet();
                  return func_71531_a(var2, var3);
               }
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length == 3) {
                  return func_71531_a(var2, this.func_147184_a(false));
               }
            } else if (var2[1].equalsIgnoreCase("setdisplay")) {
               if (var2.length == 3) {
                  return func_71530_a(var2, new String[]{"list", "sidebar", "belowName"});
               }

               if (var2.length == 4) {
                  return func_71531_a(var2, this.func_147184_a(false));
               }
            }
         } else if (var2[0].equalsIgnoreCase("players")) {
            if (var2.length == 2) {
               return func_71530_a(var2, new String[]{"set", "add", "remove", "reset", "list"});
            }

            if (!var2[1].equalsIgnoreCase("set") && !var2[1].equalsIgnoreCase("add") && !var2[1].equalsIgnoreCase("remove")) {
               if ((var2[1].equalsIgnoreCase("reset") || var2[1].equalsIgnoreCase("list")) && var2.length == 3) {
                  return func_71531_a(var2, this.func_147192_d().func_96526_d());
               }
            } else {
               if (var2.length == 3) {
                  return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
               }

               if (var2.length == 4) {
                  return func_71531_a(var2, this.func_147184_a(true));
               }
            }
         } else if (var2[0].equalsIgnoreCase("teams")) {
            if (var2.length == 2) {
               return func_71530_a(var2, new String[]{"add", "remove", "join", "leave", "empty", "list", "option"});
            }

            if (var2[1].equalsIgnoreCase("join")) {
               if (var2.length == 3) {
                  return func_71531_a(var2, this.func_147192_d().func_96531_f());
               }

               if (var2.length >= 4) {
                  return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
               }
            } else {
               if (var2[1].equalsIgnoreCase("leave")) {
                  return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
               }

               if (!var2[1].equalsIgnoreCase("empty") && !var2[1].equalsIgnoreCase("list") && !var2[1].equalsIgnoreCase("remove")) {
                  if (var2[1].equalsIgnoreCase("option")) {
                     if (var2.length == 3) {
                        return func_71531_a(var2, this.func_147192_d().func_96531_f());
                     }

                     if (var2.length == 4) {
                        return func_71530_a(var2, new String[]{"color", "friendlyfire", "seeFriendlyInvisibles"});
                     }

                     if (var2.length == 5) {
                        if (var2[3].equalsIgnoreCase("color")) {
                           return func_71531_a(var2, EnumChatFormatting.func_96296_a(true, false));
                        }

                        if (var2[3].equalsIgnoreCase("friendlyfire") || var2[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                           return func_71530_a(var2, new String[]{"true", "false"});
                        }
                     }
                  }
               } else if (var2.length == 3) {
                  return func_71531_a(var2, this.func_147192_d().func_96531_f());
               }
            }
         }

         return null;
      }
   }

   protected List func_147184_a(boolean var1) {
      Collection var2 = this.func_147192_d().func_96514_c();
      ArrayList var3 = new ArrayList();

      for(ScoreObjective var5 : var2) {
         if (!var1 || !var5.func_96680_c().func_96637_b()) {
            var3.add(var5.func_96679_b());
         }
      }

      return var3;
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      if (var1[0].equalsIgnoreCase("players")) {
         return var2 == 2;
      } else if (!var1[0].equalsIgnoreCase("teams")) {
         return false;
      } else {
         return var2 == 2 || var2 == 3;
      }
   }
}
