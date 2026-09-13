package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

public class CommandSaveAll extends CommandBase {
   public CommandSaveAll() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "save-all";
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.save.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      MinecraftServer var3 = MinecraftServer.func_71276_C();
      var1.func_145747_a(new ChatComponentTranslation("commands.save.start"));
      if (var3.func_71203_ab() != null) {
         var3.func_71203_ab().func_72389_g();
      }

      try {
         for(int var4 = 0; var4 < var3.field_71305_c.length; ++var4) {
            if (var3.field_71305_c[var4] != null) {
               WorldServer var5 = var3.field_71305_c[var4];
               boolean var6 = var5.field_73058_d;
               var5.field_73058_d = false;
               var5.func_73044_a(true, null);
               var5.field_73058_d = var6;
            }
         }

         if (var2.length > 0 && "flush".equals(var2[0])) {
            var1.func_145747_a(new ChatComponentTranslation("commands.save.flushStart"));

            for(int var8 = 0; var8 < var3.field_71305_c.length; ++var8) {
               if (var3.field_71305_c[var8] != null) {
                  WorldServer var9 = var3.field_71305_c[var8];
                  boolean var10 = var9.field_73058_d;
                  var9.field_73058_d = false;
                  var9.func_104140_m();
                  var9.field_73058_d = var10;
               }
            }

            var1.func_145747_a(new ChatComponentTranslation("commands.save.flushEnd"));
         }
      } catch (MinecraftException var7) {
         func_152373_a(var1, this, "commands.save.failed", new Object[]{var7.getMessage()});
         return;
      }

      func_152373_a(var1, this, "commands.save.success", new Object[0]);
   }
}
