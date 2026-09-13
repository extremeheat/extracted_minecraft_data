package net.minecraft.command.server;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.util.IChatComponent;

public class CommandBanIp extends CommandBase {
   public static final Pattern field_147211_a = Pattern.compile(
      "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
   );

   public CommandBanIp() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "ban-ip";
   }

   @Override
   public int func_82362_a() {
      return 3;
   }

   @Override
   public boolean func_71519_b(ICommandSender var1) {
      return MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152689_b() && super.func_71519_b(var1);
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.banip.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length >= 1 && var2[0].length() > 1) {
         Matcher var3 = field_147211_a.matcher(var2[0]);
         IChatComponent var4 = null;
         if (var2.length >= 2) {
            var4 = func_147178_a(var1, var2, 1);
         }

         if (var3.matches()) {
            this.func_147210_a(var1, var2[0], var4 == null ? null : var4.func_150260_c());
         } else {
            EntityPlayerMP var5 = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(var2[0]);
            if (var5 == null) {
               throw new PlayerNotFoundException("commands.banip.invalid");
            }

            this.func_147210_a(var1, var5.func_71114_r(), var4 == null ? null : var4.func_150260_c());
         }
      } else {
         throw new WrongUsageException("commands.banip.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return var2.length == 1 ? func_71530_a(var2, MinecraftServer.func_71276_C().func_71213_z()) : null;
   }

   protected void func_147210_a(ICommandSender var1, String var2, String var3) {
      IPBanEntry var4 = new IPBanEntry(var2, null, var1.func_70005_c_(), null, var3);
      MinecraftServer.func_71276_C().func_71203_ab().func_72363_f().func_152687_a(var4);
      List var5 = MinecraftServer.func_71276_C().func_71203_ab().func_72382_j(var2);
      String[] var6 = new String[var5.size()];
      int var7 = 0;

      for(EntityPlayerMP var9 : var5) {
         var9.field_71135_a.func_147360_c("You have been IP banned.");
         var6[var7++] = var9.func_70005_c_();
      }

      if (var5.isEmpty()) {
         func_152373_a(var1, this, "commands.banip.success", new Object[]{var2});
      } else {
         func_152373_a(var1, this, "commands.banip.success.players", new Object[]{var2, func_71527_a(var6)});
      }
   }
}
