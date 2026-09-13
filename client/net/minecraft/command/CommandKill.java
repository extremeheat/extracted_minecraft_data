package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;

public class CommandKill extends CommandBase {
   public CommandKill() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "kill";
   }

   @Override
   public int func_82362_a() {
      return 0;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.kill.usage";
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      EntityPlayerMP var3 = func_71521_c(var1);
      var3.func_70097_a(DamageSource.field_76380_i, 3.4028235E38F);
      var1.func_145747_a(new ChatComponentTranslation("commands.kill.success"));
   }
}
