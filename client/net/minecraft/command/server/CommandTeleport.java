package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandTeleport extends CommandBase {
   public CommandTeleport() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "tp";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.tp.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length < 1) {
         throw new WrongUsageException("commands.tp.usage");
      } else {
         EntityPlayerMP var3;
         if (var2.length != 2 && var2.length != 4) {
            var3 = func_71521_c(var1);
         } else {
            var3 = func_82359_c(var1, var2[0]);
            if (var3 == null) {
               throw new PlayerNotFoundException();
            }
         }

         if (var2.length != 3 && var2.length != 4) {
            if (var2.length == 1 || var2.length == 2) {
               EntityPlayerMP var14 = func_82359_c(var1, var2[var2.length - 1]);
               if (var14 == null) {
                  throw new PlayerNotFoundException();
               }

               if (var14.field_70170_p != var3.field_70170_p) {
                  func_152373_a(var1, this, "commands.tp.notSameDimension", new Object[0]);
                  return;
               }

               var3.func_70078_a(null);
               var3.field_71135_a.func_147364_a(var14.field_70165_t, var14.field_70163_u, var14.field_70161_v, var14.field_70177_z, var14.field_70125_A);
               func_152373_a(var1, this, "commands.tp.success", new Object[]{var3.func_70005_c_(), var14.func_70005_c_()});
            }
         } else if (var3.field_70170_p != null) {
            int var4 = var2.length - 3;
            double var5 = func_110666_a(var1, var3.field_70165_t, var2[var4++]);
            double var7 = func_110665_a(var1, var3.field_70163_u, var2[var4++], 0, 0);
            double var9 = func_110666_a(var1, var3.field_70161_v, var2[var4++]);
            var3.func_70078_a(null);
            var3.func_70634_a(var5, var7, var9);
            func_152373_a(var1, this, "commands.tp.success.coordinates", new Object[]{var3.func_70005_c_(), var5, var7, var9});
         }
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length != 1 && var2.length != 2 ? null : func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z());
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
