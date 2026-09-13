package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandListBans extends CommandBase {
   public CommandListBans() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "banlist";
   }

   @Override
   public int func_82362_a() {
      return 3;
   }

   @Override
   public boolean func_71519_b(ICommandSender var1) {
      return (
            MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152689_b()
               || MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152689_b()
         )
         && super.func_71519_b(var1);
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.banlist.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length >= 1 && var2[0].equalsIgnoreCase("ips")) {
         var1.func_145747_a(
            new ChatComponentTranslation("commands.banlist.ips", MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152685_a().length)
         );
         var1.func_145747_a(new ChatComponentText(func_71527_a(MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152685_a())));
      } else {
         var1.func_145747_a(
            new ChatComponentTranslation("commands.banlist.players", MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152685_a().length)
         );
         var1.func_145747_a(new ChatComponentText(func_71527_a(MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152685_a())));
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? func_71530_a(var2, new String[]{"players", "ips"}) : null;
   }
}
