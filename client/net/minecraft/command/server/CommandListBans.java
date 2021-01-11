package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListBans extends CommandBase {
   public CommandListBans() {
      super();
   }

   public String func_71517_b() {
      return "banlist";
   }

   public int func_82362_a() {
      return 3;
   }

   public boolean func_71519_b(ICommandSender var1) {
      return (MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152689_b() || MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152689_b()) && super.func_71519_b(var1);
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.banlist.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length >= 1 && var2[0].equalsIgnoreCase("ips")) {
         var1.func_145747_a(new ChatComponentTranslation("commands.banlist.ips", new Object[]{MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152685_a().length}));
         var1.func_145747_a(new ChatComponentText(func_71527_a(MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152685_a())));
      } else {
         var1.func_145747_a(new ChatComponentTranslation("commands.banlist.players", new Object[]{MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152685_a().length}));
         var1.func_145747_a(new ChatComponentText(func_71527_a(MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152685_a())));
      }

   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? func_71530_a(var2, new String[]{"players", "ips"}) : null;
   }
}
