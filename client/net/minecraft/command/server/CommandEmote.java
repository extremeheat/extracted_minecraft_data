package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandEmote extends CommandBase {
   public CommandEmote() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "me";
   }

   @Override
   public int func_82362_a() {
      return 0;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.me.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length > 0) {
         IChatComponent var3 = func_147176_a(var1, var2, 0, var1.func_70003_b(1, "me"));
         MinecraftServer.func_71276_C().func_71203_ab().func_148539_a(new ChatComponentTranslation("chat.type.emote", var1.func_145748_c_(), var3));
      } else {
         throw new WrongUsageException("commands.me.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
   }
}
