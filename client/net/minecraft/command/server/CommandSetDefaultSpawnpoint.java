package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandSetDefaultSpawnpoint extends CommandBase {
   public CommandSetDefaultSpawnpoint() {
      super();
   }

   public String func_71517_b() {
      return "setworldspawn";
   }

   public int func_82362_a() {
      return 2;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.setworldspawn.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      BlockPos var3;
      if (var2.length == 0) {
         var3 = func_71521_c(var1).func_180425_c();
      } else {
         if (var2.length != 3 || var1.func_130014_f_() == null) {
            throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
         }

         var3 = func_175757_a(var1, var2, 0, true);
      }

      var1.func_130014_f_().func_175652_B(var3);
      MinecraftServer.func_71276_C().func_71203_ab().func_148540_a(new S05PacketSpawnPosition(var3));
      func_152373_a(var1, this, "commands.setworldspawn.success", new Object[]{var3.func_177958_n(), var3.func_177956_o(), var3.func_177952_p()});
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length > 0 && var2.length <= 3 ? func_175771_a(var2, 0, var3) : null;
   }
}
