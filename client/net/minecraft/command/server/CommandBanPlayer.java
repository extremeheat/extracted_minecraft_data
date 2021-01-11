package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.BlockPos;

public class CommandBanPlayer extends CommandBase {
   public CommandBanPlayer() {
      super();
   }

   public String func_71517_b() {
      return "ban";
   }

   public int func_82362_a() {
      return 3;
   }

   public String func_71518_a(ICommandSender var1) {
      return "commands.ban.usage";
   }

   public boolean func_71519_b(ICommandSender var1) {
      return MinecraftServer.func_71276_C().func_71203_ab().func_152608_h().func_152689_b() && super.func_71519_b(var1);
   }

   public void func_71515_b(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length >= 1 && var2[0].length() > 0) {
         MinecraftServer var3 = MinecraftServer.func_71276_C();
         GameProfile var4 = var3.func_152358_ax().func_152655_a(var2[0]);
         if (var4 == null) {
            throw new CommandException("commands.ban.failed", new Object[]{var2[0]});
         } else {
            String var5 = null;
            if (var2.length >= 2) {
               var5 = func_147178_a(var1, var2, 1).func_150260_c();
            }

            UserListBansEntry var6 = new UserListBansEntry(var4, (Date)null, var1.func_70005_c_(), (Date)null, var5);
            var3.func_71203_ab().func_152608_h().func_152687_a(var6);
            EntityPlayerMP var7 = var3.func_71203_ab().func_152612_a(var2[0]);
            if (var7 != null) {
               var7.field_71135_a.func_147360_c("You are banned from this server.");
            }

            func_152373_a(var1, this, "commands.ban.success", new Object[]{var2[0]});
         }
      } else {
         throw new WrongUsageException("commands.ban.usage", new Object[0]);
      }
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length >= 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
   }
}
