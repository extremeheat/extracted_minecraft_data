package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandToggleDownfall extends CommandBase {
   public CommandToggleDownfall() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "toggledownfall";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.downfall.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      this.func_71554_c();
      func_152373_a(var1, this, "commands.downfall.success", new Object[0]);
   }

   protected void func_71554_c() {
      WorldInfo var1 = MinecraftServer.func_71276_C().field_71305_c[0].func_72912_H();
      var1.func_76084_b(!var1.func_76059_o());
   }
}
