package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class CommandSaveOff extends CommandBase {
   public CommandSaveOff() {
      super();
   }

   public String func_71517_b() {
      return "save-off";
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.save-off.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      MinecraftServer var3 = MinecraftServer.func_71276_C();
      boolean var4 = false;

      for(int var5 = 0; var5 < var3.field_71305_c.length; ++var5) {
         if (var3.field_71305_c[var5] != null) {
            WorldServer var6 = var3.field_71305_c[var5];
            if (!var6.field_73058_d) {
               var6.field_73058_d = true;
               var4 = true;
            }
         }
      }

      if (var4) {
         func_152373_a(var1, this, "commands.save.disabled", new Object[0]);
      } else {
         throw new CommandException("commands.save-off.alreadyOff", new Object[0]);
      }
   }
}
