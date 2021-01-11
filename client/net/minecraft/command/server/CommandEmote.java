package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandEmote extends CommandBase {
   public CommandEmote() {
      super();
   }

   public String func_71517_b() {
      return "me";
   }

   public int func_82362_a() {
      return 0;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.me.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length <= 0) {
         throw new WrongUsageException("commands.me.usage", new Object[0]);
      } else {
         IChatComponent var3 = func_147176_a(var1, var2, 0, !(var1 instanceof EntityPlayer));
         MinecraftServer.func_71276_C().func_71203_ab().func_148539_a(new ChatComponentTranslation("chat.type.emote", new Object[]{var1.func_145748_c_(), var3}));
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
   }
}
