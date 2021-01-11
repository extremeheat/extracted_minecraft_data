package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase {
   public CommandScoreboard() {
      super();
   }

   public String func_71517_b() {
      return "scoreboard";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.scoreboard.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (!this.func_175780_b(var1, var2)) {
         if (var2.length < 1) {
            throw new WrongUsageException("commands.scoreboard.usage", new Object[0]);
         } else {
            if (var2[0].equalsIgnoreCase("objectives")) {
               if (var2.length == 1) {
                  throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
               }

               if (var2[1].equalsIgnoreCase("list")) {
                  this.func_147196_d(var1);
               } else if (var2[1].equalsIgnoreCase("add")) {
                  if (var2.length < 4) {
                     throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
                  }

                  this.func_147193_c(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("remove")) {
                  if (var2.length != 3) {
                     throw new WrongUsageException("commands.scoreboard.objectives.remove.usage", new Object[0]);
                  }

                  this.func_147191_h(var1, var2[2]);
               } else {
                  if (!var2[1].equalsIgnoreCase("setdisplay")) {
                     throw new WrongUsageException("commands.scoreboard.objectives.usage", new Object[0]);
                  }

                  if (var2.length != 3 && var2.length != 4) {
                     throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage", new Object[0]);
                  }

                  this.func_147198_k(var1, var2, 2);
               }
            } else if (var2[0].equalsIgnoreCase("players")) {
               if (var2.length == 1) {
                  throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
               }

               if (var2[1].equalsIgnoreCase("list")) {
                  if (var2.length > 3) {
                     throw new WrongUsageException("commands.scoreboard.players.list.usage", new Object[0]);
                  }

                  this.func_147195_l(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("add")) {
                  if (var2.length < 5) {
                     throw new WrongUsageException("commands.scoreboard.players.add.usage", new Object[0]);
                  }

                  this.func_147197_m(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("remove")) {
                  if (var2.length < 5) {
                     throw new WrongUsageException("commands.scoreboard.players.remove.usage", new Object[0]);
                  }

                  this.func_147197_m(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("set")) {
                  if (var2.length < 5) {
                     throw new WrongUsageException("commands.scoreboard.players.set.usage", new Object[0]);
                  }

                  this.func_147197_m(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("reset")) {
                  if (var2.length != 3 && var2.length != 4) {
                     throw new WrongUsageException("commands.scoreboard.players.reset.usage", new Object[0]);
                  }

                  this.func_147187_n(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("enable")) {
                  if (var2.length != 4) {
                     throw new WrongUsageException("commands.scoreboard.players.enable.usage", new Object[0]);
                  }

                  this.func_175779_n(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("test")) {
                  if (var2.length != 5 && var2.length != 6) {
                     throw new WrongUsageException("commands.scoreboard.players.test.usage", new Object[0]);
                  }

                  this.func_175781_o(var1, var2, 2);
               } else {
                  if (!var2[1].equalsIgnoreCase("operation")) {
                     throw new WrongUsageException("commands.scoreboard.players.usage", new Object[0]);
                  }

                  if (var2.length != 7) {
                     throw new WrongUsageException("commands.scoreboard.players.operation.usage", new Object[0]);
                  }

                  this.func_175778_p(var1, var2, 2);
               }
            } else {
               if (!var2[0].equalsIgnoreCase("teams")) {
                  throw new WrongUsageException("commands.scoreboard.usage", new Object[0]);
               }

               if (var2.length == 1) {
                  throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
               }

               if (var2[1].equalsIgnoreCase("list")) {
                  if (var2.length > 3) {
                     throw new WrongUsageException("commands.scoreboard.teams.list.usage", new Object[0]);
                  }

                  this.func_147186_g(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("add")) {
                  if (var2.length < 3) {
                     throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
                  }

                  this.func_147185_d(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("remove")) {
                  if (var2.length != 3) {
                     throw new WrongUsageException("commands.scoreboard.teams.remove.usage", new Object[0]);
                  }

                  this.func_147194_f(var1, var2, 2);
               } else if (var2[1].equalsIgnoreCase("empty")) {
                  if (var2.length != 3) {
                     throw new WrongUsageException("commands.scoreboard.teams.empty.usage", new Object[0]);
                  }

                  this.func_147188_j(var1, var2, 2);
               } else if (!var2[1].equalsIgnoreCase("join")) {
                  if (var2[1].equalsIgnoreCase("leave")) {
                     if (var2.length < 3 && !(var1 instanceof EntityPlayer)) {
                        throw new WrongUsageException("commands.scoreboard.teams.leave.usage", new Object[0]);
                     }

                     this.func_147199_i(var1, var2, 2);
                  } else {
                     if (!var2[1].equalsIgnoreCase("option")) {
                        throw new WrongUsageException("commands.scoreboard.teams.usage", new Object[0]);
                     }

                     if (var2.length != 4 && var2.length != 5) {
                        throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
                     }

                     this.func_147200_e(var1, var2, 2);
                  }
               } else {
                  if (var2.length < 4 && (var2.length != 3 || !(var1 instanceof EntityPlayer))) {
                     throw new WrongUsageException("commands.scoreboard.teams.join.usage", new Object[0]);
                  }

                  this.func_147190_h(var1, var2, 2);
               }
            }

         }
      }
   }

   private boolean func_175780_b(ICommandSender var1, String[] var2) throws CommandException {
      int var3 = -1;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         if (this.func_82358_a(var2, var4) && "*".equals(var2[var4])) {
            if (var3 >= 0) {
               throw new CommandException("commands.scoreboard.noMultiWildcard", new Object[0]);
            }

            var3 = var4;
         }
      }

      if (var3 < 0) {
         return false;
      } else {
         ArrayList var12 = Lists.newArrayList(this.func_147192_d().func_96526_d());
         String var5 = var2[var3];
         ArrayList var6 = Lists.newArrayList();
         Iterator var7 = var12.iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();
            var2[var3] = var8;

            try {
               this.func_71515_b(var1, var2);
               var6.add(var8);
            } catch (CommandException var11) {
               ChatComponentTranslation var10 = new ChatComponentTranslation(var11.getMessage(), var11.func_74844_a());
               var10.func_150256_b().func_150238_a(EnumChatFormatting.RED);
               var1.func_145747_a(var10);
            }
         }

         var2[var3] = var5;
         var1.func_174794_a(CommandResultStats.Type.AFFECTED_ENTITIES, var6.size());
         if (var6.size() == 0) {
            throw new WrongUsageException("commands.scoreboard.allMatchesFailed", new Object[0]);
         } else {
            return true;
         }
      }
   }

   protected Scoreboard func_147192_d() {
      return MinecraftServer.func_71276_C().func_71218_a(0).func_96441_U();
   }

   protected ScoreObjective func_147189_a(String var1, boolean var2) throws CommandException {
      Scoreboard var3 = this.func_147192_d();
      ScoreObjective var4 = var3.func_96518_b(var1);
      if (var4 == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", new Object[]{var1});
      } else if (var2 && var4.func_96680_c().func_96637_b()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", new Object[]{var1});
      } else {
         return var4;
      }
   }

   protected ScorePlayerTeam func_147183_a(String var1) throws CommandException {
      Scoreboard var2 = this.func_147192_d();
      ScorePlayerTeam var3 = var2.func_96508_e(var1);
      if (var3 == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", new Object[]{var1});
      } else {
         return var3;
      }
   }

   protected void func_147193_c(ICommandSender var1, String[] var2, int var3) throws CommandException {
      String var4 = var2[var3++];
      String var5 = var2[var3++];
      Scoreboard var6 = this.func_147192_d();
      IScoreObjectiveCriteria var7 = (IScoreObjectiveCriteria)IScoreObjectiveCriteria.field_96643_a.get(var5);
      if (var7 == null) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", new Object[]{var5});
      } else if (var6.func_96518_b(var4) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", new Object[]{var4});
      } else if (var4.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", new Object[]{var4, 16});
      } else if (var4.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.usage", new Object[0]);
      } else {
         if (var2.length > var3) {
            String var8 = func_147178_a(var1, var2, var3).func_150260_c();
            if (var8.length() > 32) {
               throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", new Object[]{var8, 32});
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

   protected void func_147185_d(ICommandSender var1, String[] var2, int var3) throws CommandException {
      String var4 = var2[var3++];
      Scoreboard var5 = this.func_147192_d();
      if (var5.func_96508_e(var4) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", new Object[]{var4});
      } else if (var4.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", new Object[]{var4, 16});
      } else if (var4.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.teams.add.usage", new Object[0]);
      } else {
         if (var2.length > var3) {
            String var6 = func_147178_a(var1, var2, var3).func_150260_c();
            if (var6.length() > 32) {
               throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", new Object[]{var6, 32});
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

   protected void func_147200_e(ICommandSender var1, String[] var2, int var3) throws CommandException {
      ScorePlayerTeam var4 = this.func_147183_a(var2[var3++]);
      if (var4 != null) {
         String var5 = var2[var3++].toLowerCase();
         if (!var5.equalsIgnoreCase("color") && !var5.equalsIgnoreCase("friendlyfire") && !var5.equalsIgnoreCase("seeFriendlyInvisibles") && !var5.equalsIgnoreCase("nametagVisibility") && !var5.equalsIgnoreCase("deathMessageVisibility")) {
            throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
         } else if (var2.length == 4) {
            if (var5.equalsIgnoreCase("color")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(EnumChatFormatting.func_96296_a(true, false))});
            } else if (!var5.equalsIgnoreCase("friendlyfire") && !var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!var5.equalsIgnoreCase("nametagVisibility") && !var5.equalsIgnoreCase("deathMessageVisibility")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage", new Object[0]);
               } else {
                  throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_71527_a(Team.EnumVisible.func_178825_a())});
               }
            } else {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(Arrays.asList("true", "false"))});
            }
         } else {
            String var6 = var2[var3];
            if (var5.equalsIgnoreCase("color")) {
               EnumChatFormatting var7 = EnumChatFormatting.func_96300_b(var6);
               if (var7 == null || var7.func_96301_b()) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(EnumChatFormatting.func_96296_a(true, false))});
               }

               var4.func_178774_a(var7);
               var4.func_96666_b(var7.toString());
               var4.func_96662_c(EnumChatFormatting.RESET.toString());
            } else if (var5.equalsIgnoreCase("friendlyfire")) {
               if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(Arrays.asList("true", "false"))});
               }

               var4.func_96660_a(var6.equalsIgnoreCase("true"));
            } else if (var5.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!var6.equalsIgnoreCase("true") && !var6.equalsIgnoreCase("false")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_96333_a(Arrays.asList("true", "false"))});
               }

               var4.func_98300_b(var6.equalsIgnoreCase("true"));
            } else {
               Team.EnumVisible var8;
               if (var5.equalsIgnoreCase("nametagVisibility")) {
                  var8 = Team.EnumVisible.func_178824_a(var6);
                  if (var8 == null) {
                     throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_71527_a(Team.EnumVisible.func_178825_a())});
                  }

                  var4.func_178772_a(var8);
               } else if (var5.equalsIgnoreCase("deathMessageVisibility")) {
                  var8 = Team.EnumVisible.func_178824_a(var6);
                  if (var8 == null) {
                     throw new WrongUsageException("commands.scoreboard.teams.option.noValue", new Object[]{var5, func_71527_a(Team.EnumVisible.func_178825_a())});
                  }

                  var4.func_178773_b(var8);
               }
            }

            func_152373_a(var1, this, "commands.scoreboard.teams.option.success", new Object[]{var5, var4.func_96661_b(), var6});
         }
      }
   }

   protected void func_147194_f(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      ScorePlayerTeam var5 = this.func_147183_a(var2[var3]);
      if (var5 != null) {
         var4.func_96511_d(var5);
         func_152373_a(var1, this, "commands.scoreboard.teams.remove.success", new Object[]{var5.func_96661_b()});
      }
   }

   protected void func_147186_g(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      if (var2.length > var3) {
         ScorePlayerTeam var5 = this.func_147183_a(var2[var3]);
         if (var5 == null) {
            return;
         }

         Collection var6 = var5.func_96670_d();
         var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var6.size());
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", new Object[]{var5.func_96661_b()});
         }

         ChatComponentTranslation var7 = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", new Object[]{var6.size(), var5.func_96661_b()});
         var7.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var7);
         var1.func_145747_a(new ChatComponentText(func_71527_a(var6.toArray())));
      } else {
         Collection var9 = var4.func_96525_g();
         var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var9.size());
         if (var9.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty", new Object[0]);
         }

         ChatComponentTranslation var10 = new ChatComponentTranslation("commands.scoreboard.teams.list.count", new Object[]{var9.size()});
         var10.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var10);
         Iterator var11 = var9.iterator();

         while(var11.hasNext()) {
            ScorePlayerTeam var8 = (ScorePlayerTeam)var11.next();
            var1.func_145747_a(new ChatComponentTranslation("commands.scoreboard.teams.list.entry", new Object[]{var8.func_96661_b(), var8.func_96669_c(), var8.func_96670_d().size()}));
         }
      }

   }

   protected void func_147190_h(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      String var5 = var2[var3++];
      HashSet var6 = Sets.newHashSet();
      HashSet var7 = Sets.newHashSet();
      String var8;
      if (var1 instanceof EntityPlayer && var3 == var2.length) {
         var8 = func_71521_c(var1).func_70005_c_();
         if (var4.func_151392_a(var8, var5)) {
            var6.add(var8);
         } else {
            var7.add(var8);
         }
      } else {
         label50:
         while(true) {
            while(true) {
               if (var3 >= var2.length) {
                  break label50;
               }

               var8 = var2[var3++];
               if (var8.startsWith("@")) {
                  List var13 = func_175763_c(var1, var8);
                  Iterator var10 = var13.iterator();

                  while(var10.hasNext()) {
                     Entity var11 = (Entity)var10.next();
                     String var12 = func_175758_e(var1, var11.func_110124_au().toString());
                     if (var4.func_151392_a(var12, var5)) {
                        var6.add(var12);
                     } else {
                        var7.add(var12);
                     }
                  }
               } else {
                  String var9 = func_175758_e(var1, var8);
                  if (var4.func_151392_a(var9, var5)) {
                     var6.add(var9);
                  } else {
                     var7.add(var9);
                  }
               }
            }
         }
      }

      if (!var6.isEmpty()) {
         var1.func_174794_a(CommandResultStats.Type.AFFECTED_ENTITIES, var6.size());
         func_152373_a(var1, this, "commands.scoreboard.teams.join.success", new Object[]{var6.size(), var5, func_71527_a(var6.toArray(new String[var6.size()]))});
      }

      if (!var7.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.join.failure", new Object[]{var7.size(), var5, func_71527_a(var7.toArray(new String[var7.size()]))});
      }
   }

   protected void func_147199_i(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      HashSet var5 = Sets.newHashSet();
      HashSet var6 = Sets.newHashSet();
      String var7;
      if (var1 instanceof EntityPlayer && var3 == var2.length) {
         var7 = func_71521_c(var1).func_70005_c_();
         if (var4.func_96524_g(var7)) {
            var5.add(var7);
         } else {
            var6.add(var7);
         }
      } else {
         label50:
         while(true) {
            while(true) {
               if (var3 >= var2.length) {
                  break label50;
               }

               var7 = var2[var3++];
               if (var7.startsWith("@")) {
                  List var12 = func_175763_c(var1, var7);
                  Iterator var9 = var12.iterator();

                  while(var9.hasNext()) {
                     Entity var10 = (Entity)var9.next();
                     String var11 = func_175758_e(var1, var10.func_110124_au().toString());
                     if (var4.func_96524_g(var11)) {
                        var5.add(var11);
                     } else {
                        var6.add(var11);
                     }
                  }
               } else {
                  String var8 = func_175758_e(var1, var7);
                  if (var4.func_96524_g(var8)) {
                     var5.add(var8);
                  } else {
                     var6.add(var8);
                  }
               }
            }
         }
      }

      if (!var5.isEmpty()) {
         var1.func_174794_a(CommandResultStats.Type.AFFECTED_ENTITIES, var5.size());
         func_152373_a(var1, this, "commands.scoreboard.teams.leave.success", new Object[]{var5.size(), func_71527_a(var5.toArray(new String[var5.size()]))});
      }

      if (!var6.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", new Object[]{var6.size(), func_71527_a(var6.toArray(new String[var6.size()]))});
      }
   }

   protected void func_147188_j(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      ScorePlayerTeam var5 = this.func_147183_a(var2[var3]);
      if (var5 != null) {
         ArrayList var6 = Lists.newArrayList(var5.func_96670_d());
         var1.func_174794_a(CommandResultStats.Type.AFFECTED_ENTITIES, var6.size());
         if (var6.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", new Object[]{var5.func_96661_b()});
         } else {
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               String var8 = (String)var7.next();
               var4.func_96512_b(var8, var5);
            }

            func_152373_a(var1, this, "commands.scoreboard.teams.empty.success", new Object[]{var6.size(), var5.func_96661_b()});
         }
      }
   }

   protected void func_147191_h(ICommandSender var1, String var2) throws CommandException {
      Scoreboard var3 = this.func_147192_d();
      ScoreObjective var4 = this.func_147189_a(var2, false);
      var3.func_96519_k(var4);
      func_152373_a(var1, this, "commands.scoreboard.objectives.remove.success", new Object[]{var2});
   }

   protected void func_147196_d(ICommandSender var1) throws CommandException {
      Scoreboard var2 = this.func_147192_d();
      Collection var3 = var2.func_96514_c();
      if (var3.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty", new Object[0]);
      } else {
         ChatComponentTranslation var4 = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", new Object[]{var3.size()});
         var4.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var4);
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            ScoreObjective var6 = (ScoreObjective)var5.next();
            var1.func_145747_a(new ChatComponentTranslation("commands.scoreboard.objectives.list.entry", new Object[]{var6.func_96679_b(), var6.func_96678_d(), var6.func_96680_c().func_96636_a()}));
         }

      }
   }

   protected void func_147198_k(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      String var5 = var2[var3++];
      int var6 = Scoreboard.func_96537_j(var5);
      ScoreObjective var7 = null;
      if (var2.length == 4) {
         var7 = this.func_147189_a(var2[var3], false);
      }

      if (var6 < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", new Object[]{var5});
      } else {
         var4.func_96530_a(var6, var7);
         if (var7 != null) {
            func_152373_a(var1, this, "commands.scoreboard.objectives.setdisplay.successSet", new Object[]{Scoreboard.func_96517_b(var6), var7.func_96679_b()});
         } else {
            func_152373_a(var1, this, "commands.scoreboard.objectives.setdisplay.successCleared", new Object[]{Scoreboard.func_96517_b(var6)});
         }

      }
   }

   protected void func_147195_l(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      if (var2.length > var3) {
         String var5 = func_175758_e(var1, var2[var3]);
         Map var6 = var4.func_96510_d(var5);
         var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var6.size());
         if (var6.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", new Object[]{var5});
         }

         ChatComponentTranslation var7 = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", new Object[]{var6.size(), var5});
         var7.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var7);
         Iterator var8 = var6.values().iterator();

         while(var8.hasNext()) {
            Score var9 = (Score)var8.next();
            var1.func_145747_a(new ChatComponentTranslation("commands.scoreboard.players.list.player.entry", new Object[]{var9.func_96652_c(), var9.func_96645_d().func_96678_d(), var9.func_96645_d().func_96679_b()}));
         }
      } else {
         Collection var10 = var4.func_96526_d();
         var1.func_174794_a(CommandResultStats.Type.QUERY_RESULT, var10.size());
         if (var10.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty", new Object[0]);
         }

         ChatComponentTranslation var11 = new ChatComponentTranslation("commands.scoreboard.players.list.count", new Object[]{var10.size()});
         var11.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
         var1.func_145747_a(var11);
         var1.func_145747_a(new ChatComponentText(func_71527_a(var10.toArray())));
      }

   }

   protected void func_147197_m(ICommandSender var1, String[] var2, int var3) throws CommandException {
      String var4 = var2[var3 - 1];
      int var5 = var3;
      String var6 = func_175758_e(var1, var2[var3++]);
      if (var6.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[]{var6, 40});
      } else {
         ScoreObjective var7 = this.func_147189_a(var2[var3++], true);
         int var8 = var4.equalsIgnoreCase("set") ? func_175755_a(var2[var3++]) : func_180528_a(var2[var3++], 0);
         if (var2.length > var3) {
            Entity var9 = func_175768_b(var1, var2[var5]);

            try {
               NBTTagCompound var10 = JsonToNBT.func_180713_a(func_180529_a(var2, var3));
               NBTTagCompound var11 = new NBTTagCompound();
               var9.func_70109_d(var11);
               if (!NBTUtil.func_181123_a(var10, var11, true)) {
                  throw new CommandException("commands.scoreboard.players.set.tagMismatch", new Object[]{var6});
               }
            } catch (NBTException var12) {
               throw new CommandException("commands.scoreboard.players.set.tagError", new Object[]{var12.getMessage()});
            }
         }

         Scoreboard var13 = this.func_147192_d();
         Score var14 = var13.func_96529_a(var6, var7);
         if (var4.equalsIgnoreCase("set")) {
            var14.func_96647_c(var8);
         } else if (var4.equalsIgnoreCase("add")) {
            var14.func_96649_a(var8);
         } else {
            var14.func_96646_b(var8);
         }

         func_152373_a(var1, this, "commands.scoreboard.players.set.success", new Object[]{var7.func_96679_b(), var6, var14.func_96652_c()});
      }
   }

   protected void func_147187_n(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      String var5 = func_175758_e(var1, var2[var3++]);
      if (var2.length > var3) {
         ScoreObjective var6 = this.func_147189_a(var2[var3++], false);
         var4.func_178822_d(var5, var6);
         func_152373_a(var1, this, "commands.scoreboard.players.resetscore.success", new Object[]{var6.func_96679_b(), var5});
      } else {
         var4.func_178822_d(var5, (ScoreObjective)null);
         func_152373_a(var1, this, "commands.scoreboard.players.reset.success", new Object[]{var5});
      }

   }

   protected void func_175779_n(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      String var5 = func_96332_d(var1, var2[var3++]);
      if (var5.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[]{var5, 40});
      } else {
         ScoreObjective var6 = this.func_147189_a(var2[var3], false);
         if (var6.func_96680_c() != IScoreObjectiveCriteria.field_178791_c) {
            throw new CommandException("commands.scoreboard.players.enable.noTrigger", new Object[]{var6.func_96679_b()});
         } else {
            Score var7 = var4.func_96529_a(var5, var6);
            var7.func_178815_a(false);
            func_152373_a(var1, this, "commands.scoreboard.players.enable.success", new Object[]{var6.func_96679_b(), var5});
         }
      }
   }

   protected void func_175781_o(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      String var5 = func_175758_e(var1, var2[var3++]);
      if (var5.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[]{var5, 40});
      } else {
         ScoreObjective var6 = this.func_147189_a(var2[var3++], false);
         if (!var4.func_178819_b(var5, var6)) {
            throw new CommandException("commands.scoreboard.players.test.notFound", new Object[]{var6.func_96679_b(), var5});
         } else {
            int var7 = var2[var3].equals("*") ? -2147483648 : func_175755_a(var2[var3]);
            ++var3;
            int var8 = var3 < var2.length && !var2[var3].equals("*") ? func_180528_a(var2[var3], var7) : 2147483647;
            Score var9 = var4.func_96529_a(var5, var6);
            if (var9.func_96652_c() >= var7 && var9.func_96652_c() <= var8) {
               func_152373_a(var1, this, "commands.scoreboard.players.test.success", new Object[]{var9.func_96652_c(), var7, var8});
            } else {
               throw new CommandException("commands.scoreboard.players.test.failed", new Object[]{var9.func_96652_c(), var7, var8});
            }
         }
      }
   }

   protected void func_175778_p(ICommandSender var1, String[] var2, int var3) throws CommandException {
      Scoreboard var4 = this.func_147192_d();
      String var5 = func_175758_e(var1, var2[var3++]);
      ScoreObjective var6 = this.func_147189_a(var2[var3++], true);
      String var7 = var2[var3++];
      String var8 = func_175758_e(var1, var2[var3++]);
      ScoreObjective var9 = this.func_147189_a(var2[var3], false);
      if (var5.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[]{var5, 40});
      } else if (var8.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", new Object[]{var8, 40});
      } else {
         Score var10 = var4.func_96529_a(var5, var6);
         if (!var4.func_178819_b(var8, var9)) {
            throw new CommandException("commands.scoreboard.players.operation.notFound", new Object[]{var9.func_96679_b(), var8});
         } else {
            Score var11 = var4.func_96529_a(var8, var9);
            if (var7.equals("+=")) {
               var10.func_96647_c(var10.func_96652_c() + var11.func_96652_c());
            } else if (var7.equals("-=")) {
               var10.func_96647_c(var10.func_96652_c() - var11.func_96652_c());
            } else if (var7.equals("*=")) {
               var10.func_96647_c(var10.func_96652_c() * var11.func_96652_c());
            } else if (var7.equals("/=")) {
               if (var11.func_96652_c() != 0) {
                  var10.func_96647_c(var10.func_96652_c() / var11.func_96652_c());
               }
            } else if (var7.equals("%=")) {
               if (var11.func_96652_c() != 0) {
                  var10.func_96647_c(var10.func_96652_c() % var11.func_96652_c());
               }
            } else if (var7.equals("=")) {
               var10.func_96647_c(var11.func_96652_c());
            } else if (var7.equals("<")) {
               var10.func_96647_c(Math.min(var10.func_96652_c(), var11.func_96652_c()));
            } else if (var7.equals(">")) {
               var10.func_96647_c(Math.max(var10.func_96652_c(), var11.func_96652_c()));
            } else {
               if (!var7.equals("><")) {
                  throw new CommandException("commands.scoreboard.players.operation.invalidOperation", new Object[]{var7});
               }

               int var12 = var10.func_96652_c();
               var10.func_96647_c(var11.func_96652_c());
               var11.func_96647_c(var12);
            }

            func_152373_a(var1, this, "commands.scoreboard.players.operation.success", new Object[0]);
         }
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"objectives", "players", "teams"});
      } else {
         if (var2[0].equalsIgnoreCase("objectives")) {
            if (var2.length == 2) {
               return func_71530_a(var2, new String[]{"list", "add", "remove", "setdisplay"});
            }

            if (var2[1].equalsIgnoreCase("add")) {
               if (var2.length == 4) {
                  Set var4 = IScoreObjectiveCriteria.field_96643_a.keySet();
                  return func_175762_a(var2, var4);
               }
            } else if (var2[1].equalsIgnoreCase("remove")) {
               if (var2.length == 3) {
                  return func_175762_a(var2, this.func_147184_a(false));
               }
            } else if (var2[1].equalsIgnoreCase("setdisplay")) {
               if (var2.length == 3) {
                  return func_71530_a(var2, Scoreboard.func_178821_h());
               }

               if (var2.length == 4) {
                  return func_175762_a(var2, this.func_147184_a(false));
               }
            }
         } else if (var2[0].equalsIgnoreCase("players")) {
            if (var2.length == 2) {
               return func_71530_a(var2, new String[]{"set", "add", "remove", "reset", "list", "enable", "test", "operation"});
            }

            if (!var2[1].equalsIgnoreCase("set") && !var2[1].equalsIgnoreCase("add") && !var2[1].equalsIgnoreCase("remove") && !var2[1].equalsIgnoreCase("reset")) {
               if (var2[1].equalsIgnoreCase("enable")) {
                  if (var2.length == 3) {
                     return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
                  }

                  if (var2.length == 4) {
                     return func_175762_a(var2, this.func_175782_e());
                  }
               } else if (!var2[1].equalsIgnoreCase("list") && !var2[1].equalsIgnoreCase("test")) {
                  if (var2[1].equalsIgnoreCase("operation")) {
                     if (var2.length == 3) {
                        return func_175762_a(var2, this.func_147192_d().func_96526_d());
                     }

                     if (var2.length == 4) {
                        return func_175762_a(var2, this.func_147184_a(true));
                     }

                     if (var2.length == 5) {
                        return func_71530_a(var2, new String[]{"+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><"});
                     }

                     if (var2.length == 6) {
                        return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
                     }

                     if (var2.length == 7) {
                        return func_175762_a(var2, this.func_147184_a(false));
                     }
                  }
               } else {
                  if (var2.length == 3) {
                     return func_175762_a(var2, this.func_147192_d().func_96526_d());
                  }

                  if (var2.length == 4 && var2[1].equalsIgnoreCase("test")) {
                     return func_175762_a(var2, this.func_147184_a(false));
                  }
               }
            } else {
               if (var2.length == 3) {
                  return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
               }

               if (var2.length == 4) {
                  return func_175762_a(var2, this.func_147184_a(true));
               }
            }
         } else if (var2[0].equalsIgnoreCase("teams")) {
            if (var2.length == 2) {
               return func_71530_a(var2, new String[]{"add", "remove", "join", "leave", "empty", "list", "option"});
            }

            if (var2[1].equalsIgnoreCase("join")) {
               if (var2.length == 3) {
                  return func_175762_a(var2, this.func_147192_d().func_96531_f());
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
                        return func_175762_a(var2, this.func_147192_d().func_96531_f());
                     }

                     if (var2.length == 4) {
                        return func_71530_a(var2, new String[]{"color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"});
                     }

                     if (var2.length == 5) {
                        if (var2[3].equalsIgnoreCase("color")) {
                           return func_175762_a(var2, EnumChatFormatting.func_96296_a(true, false));
                        }

                        if (!var2[3].equalsIgnoreCase("nametagVisibility") && !var2[3].equalsIgnoreCase("deathMessageVisibility")) {
                           if (!var2[3].equalsIgnoreCase("friendlyfire") && !var2[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                              return null;
                           }

                           return func_71530_a(var2, new String[]{"true", "false"});
                        }

                        return func_71530_a(var2, Team.EnumVisible.func_178825_a());
                     }
                  }
               } else if (var2.length == 3) {
                  return func_175762_a(var2, this.func_147192_d().func_96531_f());
               }
            }
         }

         return null;
      }
   }

   protected List<String> func_147184_a(boolean var1) {
      Collection var2 = this.func_147192_d().func_96514_c();
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = var2.iterator();

      while(true) {
         ScoreObjective var5;
         do {
            if (!var4.hasNext()) {
               return var3;
            }

            var5 = (ScoreObjective)var4.next();
         } while(var1 && var5.func_96680_c().func_96637_b());

         var3.add(var5.func_96679_b());
      }
   }

   protected List<String> func_175782_e() {
      Collection var1 = this.func_147192_d().func_96514_c();
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         ScoreObjective var4 = (ScoreObjective)var3.next();
         if (var4.func_96680_c() == IScoreObjectiveCriteria.field_178791_c) {
            var2.add(var4.func_96679_b());
         }
      }

      return var2;
   }

   public boolean func_82358_a(String[] var1, int var2) {
      if (!var1[0].equalsIgnoreCase("players")) {
         if (var1[0].equalsIgnoreCase("teams")) {
            return var2 == 2;
         } else {
            return false;
         }
      } else if (var1.length > 1 && var1[1].equalsIgnoreCase("operation")) {
         return var2 == 2 || var2 == 5;
      } else {
         return var2 == 2;
      }
   }
}
