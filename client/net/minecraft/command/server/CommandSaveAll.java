package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase {
   public CommandSaveAll() {
      super();
   }

   public String func_71517_b() {
      return "save-all";
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.save.usage";
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      MinecraftServer var3 = MinecraftServer.func_71276_C();
      var1.func_145747_a(new ChatComponentTranslation("commands.save.start", new Object[0]));
      if (var3.func_71203_ab() != null) {
         var3.func_71203_ab().func_72389_g();
      }

      try {
         int var4;
         WorldServer var5;
         boolean var6;
         for(var4 = 0; var4 < var3.field_71305_c.length; ++var4) {
            if (var3.field_71305_c[var4] != null) {
               var5 = var3.field_71305_c[var4];
               var6 = var5.field_73058_d;
               var5.field_73058_d = false;
               var5.func_73044_a(true, (IProgressUpdate)null);
               var5.field_73058_d = var6;
            }
         }

         if (var2.length > 0 && "flush".equals(var2[0])) {
            var1.func_145747_a(new ChatComponentTranslation("commands.save.flushStart", new Object[0]));

            for(var4 = 0; var4 < var3.field_71305_c.length; ++var4) {
               if (var3.field_71305_c[var4] != null) {
                  var5 = var3.field_71305_c[var4];
                  var6 = var5.field_73058_d;
                  var5.field_73058_d = false;
                  var5.func_104140_m();
                  var5.field_73058_d = var6;
               }
            }

            var1.func_145747_a(new ChatComponentTranslation("commands.save.flushEnd", new Object[0]));
         }
      } catch (MinecraftException var7) {
         func_152373_a(var1, this, "commands.save.failed", new Object[]{var7.getMessage()});
         return;
      }

      func_152373_a(var1, this, "commands.save.success", new Object[0]);
   }
}
