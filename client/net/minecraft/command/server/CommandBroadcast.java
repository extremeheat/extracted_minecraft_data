package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandBroadcast extends CommandBase {
   public CommandBroadcast() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "say";
   }

   @Override
   public int func_82362_a() {
      return 1;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.say.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length > 0 && var2[0].length() > 0) {
         IChatComponent var3 = func_147176_a(var1, var2, 0, true);
         MinecraftServer.func_71276_C().func_71203_ab().func_148539_a(new ChatComponentTranslation("chat.type.announcement", var1.func_70005_c_(), var3));
      } else {
         throw new WrongUsageException("commands.say.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length >= 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
   }
}
