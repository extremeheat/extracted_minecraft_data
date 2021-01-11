package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandKill extends CommandBase {
   public CommandKill() {
      super();
   }

   public String func_71517_b() {
      return "kill";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.kill.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length == 0) {
         EntityPlayerMP var4 = func_71521_c(var1);
         var4.func_174812_G();
         func_152373_a(var1, this, "commands.kill.successful", new Object[]{var4.func_145748_c_()});
      } else {
         Entity var3 = func_175768_b(var1, var2[0]);
         var3.func_174812_G();
         func_152373_a(var1, this, "commands.kill.successful", new Object[]{var3.func_145748_c_()});
      }
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length == 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
   }
}
