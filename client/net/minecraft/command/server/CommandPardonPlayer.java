package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandPardonPlayer extends CommandBase {
   public CommandPardonPlayer() {
      super();
   }

   public String func_71517_b() {
      return "pardon";
   }

   public int func_82362_a() {
      return 3;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.unban.usage";
   }

   public boolean func_71519_b(ICommandSender var1) {
      return MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152689_b() && super.func_71519_b(var1);
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length == 1 && var2[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.func_71276_C();
         GameProfile var4 = var3.func_71203_ab().func_152608_h().func_152703_a(var2[0]);
         if (var4 == null) {
            throw new CommandException("commands.unban.failed", new Object[]{var2[0]});
         } else {
            var3.func_71203_ab().func_152608_h().func_152684_c(var4);
            func_152373_a(var1, this, "commands.unban.success", new Object[]{var2[0]});
         }
      } else {
         throw new WrongUsageException("commands.unban.usage", new Object[0]);
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152685_a()) : null;
   }
}
