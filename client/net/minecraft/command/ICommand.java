package net.minecraft.command;

import java.util.List;

public interface ICommand extends Comparable {
   String func_71517_b();

   String func_71518_a(ICommandSender var1);

   List func_71514_a();

   void func_71515_b(ICommandSender var1, String[] var2);

   boolean func_71519_b(ICommandSender var1);

   List func_71516_a(ICommandSender var1, String[] var2);

   boolean func_82358_a(String[] var1, int var2);
}
