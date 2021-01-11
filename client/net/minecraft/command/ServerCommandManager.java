package net.minecraft.command;

import java.util.Iterator;
import net.minecraft.command.server.CommandAchievement;
import net.minecraft.command.server.CommandBanIp;
import net.minecraft.command.server.CommandBanPlayer;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandDeOp;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.command.server.CommandListBans;
import net.minecraft.command.server.CommandListPlayers;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.command.server.CommandMessageRaw;
import net.minecraft.command.server.CommandOp;
import net.minecraft.command.server.CommandPardonIp;
import net.minecraft.command.server.CommandPardonPlayer;
import net.minecraft.command.server.CommandPublishLocalServer;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.command.server.CommandSaveOff;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.command.server.CommandScoreboard;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.command.server.CommandSetDefaultSpawnpoint;
import net.minecraft.command.server.CommandStop;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.command.server.CommandTestFor;
import net.minecraft.command.server.CommandTestForBlock;
import net.minecraft.command.server.CommandWhitelist;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class ServerCommandManager extends CommandHandler implements IAdminCommand {
   public ServerCommandManager() {
      super();
      this.func_71560_a(new CommandTime());
      this.func_71560_a(new CommandGameMode());
      this.func_71560_a(new CommandDifficulty());
      this.func_71560_a(new CommandDefaultGameMode());
      this.func_71560_a(new CommandKill());
      this.func_71560_a(new CommandToggleDownfall());
      this.func_71560_a(new CommandWeather());
      this.func_71560_a(new CommandXP());
      this.func_71560_a(new CommandTeleport());
      this.func_71560_a(new CommandGive());
      this.func_71560_a(new CommandReplaceItem());
      this.func_71560_a(new CommandStats());
      this.func_71560_a(new CommandEffect());
      this.func_71560_a(new CommandEnchant());
      this.func_71560_a(new CommandParticle());
      this.func_71560_a(new CommandEmote());
      this.func_71560_a(new CommandShowSeed());
      this.func_71560_a(new CommandHelp());
      this.func_71560_a(new CommandDebug());
      this.func_71560_a(new CommandMessage());
      this.func_71560_a(new CommandBroadcast());
      this.func_71560_a(new CommandSetSpawnpoint());
      this.func_71560_a(new CommandSetDefaultSpawnpoint());
      this.func_71560_a(new CommandGameRule());
      this.func_71560_a(new CommandClearInventory());
      this.func_71560_a(new CommandTestFor());
      this.func_71560_a(new CommandSpreadPlayers());
      this.func_71560_a(new CommandPlaySound());
      this.func_71560_a(new CommandScoreboard());
      this.func_71560_a(new CommandExecuteAt());
      this.func_71560_a(new CommandTrigger());
      this.func_71560_a(new CommandAchievement());
      this.func_71560_a(new CommandSummon());
      this.func_71560_a(new CommandSetBlock());
      this.func_71560_a(new CommandFill());
      this.func_71560_a(new CommandClone());
      this.func_71560_a(new CommandCompare());
      this.func_71560_a(new CommandBlockData());
      this.func_71560_a(new CommandTestForBlock());
      this.func_71560_a(new CommandMessageRaw());
      this.func_71560_a(new CommandWorldBorder());
      this.func_71560_a(new CommandTitle());
      this.func_71560_a(new CommandEntityData());
      if (MinecraftServer.func_71276_C().func_71262_S()) {
         this.func_71560_a(new CommandOp());
         this.func_71560_a(new CommandDeOp());
         this.func_71560_a(new CommandStop());
         this.func_71560_a(new CommandSaveAll());
         this.func_71560_a(new CommandSaveOff());
         this.func_71560_a(new CommandSaveOn());
         this.func_71560_a(new CommandBanIp());
         this.func_71560_a(new CommandPardonIp());
         this.func_71560_a(new CommandBanPlayer());
         this.func_71560_a(new CommandListBans());
         this.func_71560_a(new CommandPardonPlayer());
         this.func_71560_a(new CommandServerKick());
         this.func_71560_a(new CommandListPlayers());
         this.func_71560_a(new CommandWhitelist());
         this.func_71560_a(new CommandSetPlayerTimeout());
      } else {
         this.func_71560_a(new CommandPublishLocalServer());
      }

      CommandBase.func_71529_a(this);
   }

   public void func_152372_a(ICommandSender var1, ICommand var2, int var3, String var4, Object... var5) {
      boolean var6 = true;
      MinecraftServer var7 = MinecraftServer.func_71276_C();
      if (!var1.func_174792_t_()) {
         var6 = false;
      }

      ChatComponentTranslation var8 = new ChatComponentTranslation("chat.type.admin", new Object[]{var1.func_70005_c_(), new ChatComponentTranslation(var4, var5)});
      var8.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
      var8.func_150256_b().func_150217_b(true);
      if (var6) {
         Iterator var9 = var7.func_71203_ab().func_181057_v().iterator();

         label85:
         while(true) {
            EntityPlayer var10;
            boolean var11;
            boolean var12;
            do {
               do {
                  do {
                     do {
                        if (!var9.hasNext()) {
                           break label85;
                        }

                        var10 = (EntityPlayer)var9.next();
                     } while(var10 == var1);
                  } while(!var7.func_71203_ab().func_152596_g(var10.func_146103_bH()));
               } while(!var2.func_71519_b(var1));

               var11 = var1 instanceof MinecraftServer && MinecraftServer.func_71276_C().func_183002_r();
               var12 = var1 instanceof RConConsoleSource && MinecraftServer.func_71276_C().func_181034_q();
            } while(!var11 && !var12 && (var1 instanceof RConConsoleSource || var1 instanceof MinecraftServer));

            var10.func_145747_a(var8);
         }
      }

      if (var1 != var7 && var7.field_71305_c[0].func_82736_K().func_82766_b("logAdminCommands")) {
         var7.func_145747_a(var8);
      }

      boolean var13 = var7.field_71305_c[0].func_82736_K().func_82766_b("sendCommandFeedback");
      if (var1 instanceof CommandBlockLogic) {
         var13 = ((CommandBlockLogic)var1).func_175571_m();
      }

      if ((var3 & 1) != 1 && var13 || var1 instanceof MinecraftServer) {
         var1.func_145747_a(new ChatComponentTranslation(var4, var5));
      }

   }
}
