package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandWhitelist extends CommandBase {
   public CommandWhitelist() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "whitelist";
   }

   @Override
   public int func_82362_a() {
      return 3;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.whitelist.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length >= 1) {
         MinecraftServer var3 = MinecraftServer.func_71276_C();
         if (var2[0].equals("on")) {
            var3.func_71203_ab().func_72371_a(true);
            func_152373_a(var1, this, "commands.whitelist.enabled", new Object[0]);
            return;
         }

         if (var2[0].equals("off")) {
            var3.func_71203_ab().func_72371_a(false);
            func_152373_a(var1, this, "commands.whitelist.disabled", new Object[0]);
            return;
         }

         if (var2[0].equals("list")) {
            var1.func_145747_a(
               new ChatComponentTranslation("commands.whitelist.list", var3.func_71203_ab().func_152598_l().length, var3.func_71203_ab().func_72373_m().length)
            );
            String[] var6 = var3.func_71203_ab().func_152598_l();
            var1.func_145747_a(new ChatComponentText(func_71527_a(var6)));
            return;
         }

         if (var2[0].equals("add")) {
            if (var2.length < 2) {
               throw new WrongUsageException("commands.whitelist.add.usage");
            }

            GameProfile var5 = var3.func_152358_ax().func_152655_a(var2[1]);
            if (var5 == null) {
               throw new CommandException("commands.whitelist.add.failed", var2[1]);
            }

            var3.func_71203_ab().func_152601_d(var5);
            func_152373_a(var1, this, "commands.whitelist.add.success", new Object[]{var2[1]});
            return;
         }

         if (var2[0].equals("remove")) {
            if (var2.length < 2) {
               throw new WrongUsageException("commands.whitelist.remove.usage");
            }

            GameProfile var4 = var3.func_71203_ab().func_152599_k().func_152706_a(var2[1]);
            if (var4 == null) {
               throw new CommandException("commands.whitelist.remove.failed", var2[1]);
            }

            var3.func_71203_ab().func_152597_c(var4);
            func_152373_a(var1, this, "commands.whitelist.remove.success", new Object[]{var2[1]});
            return;
         }

         if (var2[0].equals("reload")) {
            var3.func_71203_ab().func_72362_j();
            func_152373_a(var1, this, "commands.whitelist.reloaded", new Object[0]);
            return;
         }
      }

      throw new WrongUsageException("commands.whitelist.usage");
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return func_71530_a(var2, new String[]{"on", "off", "list", "add", "remove", "reload"});
      } else {
         if (var2.length == 2) {
            if (var2[0].equals("remove")) {
               return func_71530_a(var2, MinecraftServer.func_71276_C().func_71203_ab().func_152598_l());
            }

            if (var2[0].equals("add")) {
               return func_71530_a(var2, MinecraftServer.func_71276_C().func_152358_ax().func_152654_a());
            }
         }

         return null;
      }
   }
}
