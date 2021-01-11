package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandSetSpawnpoint extends CommandBase {
   public CommandSetSpawnpoint() {
      super();
   }

   public String func_71517_b() {
      return "spawnpoint";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.spawnpoint.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length > 1 && var2.length < 4) {
         throw new WrongUsageException("commands.spawnpoint.usage", new Object[0]);
      } else {
         EntityPlayerMP var3 = var2.length > 0 ? func_82359_c(var1, var2[0]) : func_71521_c(var1);
         BlockPos var4 = var2.length > 3 ? func_175757_a(var1, var2, 1, true) : var3.func_180425_c();
         if (var3.field_70170_p != null) {
            var3.func_180473_a(var4, true);
            func_152373_a(var1, this, "commands.spawnpoint.success", new Object[]{var3.func_70005_c_(), var4.func_177958_n(), var4.func_177956_o(), var4.func_177952_p()});
         }

      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      if (var2.length == 1) {
         return func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
      } else {
         return var2.length > 1 && var2.length <= 4 ? func_175771_a(var2, 1, var3) : null;
      }
   }

   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
