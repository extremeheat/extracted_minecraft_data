package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {
   public CommandGameRule() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "gamerule";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.gamerule.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      if (var2.length == 2) {
         String var7 = var2[0];
         String var8 = var2[1];
         GameRules var9 = this.func_82366_d();
         if (var9.func_82765_e(var7)) {
            var9.func_82764_b(var7, var8);
            func_152373_a(var1, this, "commands.gamerule.success", new Object[0]);
         } else {
            func_152373_a(var1, this, "commands.gamerule.norule", new Object[]{var7});
         }
      } else if (var2.length == 1) {
         String var6 = var2[0];
         GameRules var4 = this.func_82366_d();
         if (var4.func_82765_e(var6)) {
            String var5 = var4.func_82767_a(var6);
            var1.func_145747_a(new ChatComponentText(var6).func_150258_a(" = ").func_150258_a(var5));
         } else {
            func_152373_a(var1, this, "commands.gamerule.norule", new Object[]{var6});
         }
      } else if (var2.length == 0) {
         GameRules var3 = this.func_82366_d();
         var1.func_145747_a(new ChatComponentText(func_71527_a(var3.func_82763_b())));
      } else {
         throw new WrongUsageException("commands.gamerule.usage");
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return func_71530_a(var2, this.func_82366_d().func_82763_b());
      } else {
         return var2.length == 2 ? func_71530_a(var2, new String[]{"true", "false"}) : null;
      }
   }

   private GameRules func_82366_d() {
      return MinecraftServer.func_71276_C().func_71218_a(0).func_82736_K();
   }
}
