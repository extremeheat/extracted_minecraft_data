package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;

public class CommandClearInventory extends CommandBase {
   public CommandClearInventory() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "clear";
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.clear.usage";
   }

   @Override
   public int func_82362_a() {
      return 2;
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      EntityPlayerMP var3 = var2.length == 0 ? func_71521_c(var1) : func_82359_c(var1, var2[0]);
      Item var4 = var2.length >= 2 ? func_147179_f(var1, var2[1]) : null;
      int var5 = var2.length >= 3 ? func_71528_a(var1, var2[2], 0) : -1;
      if (var2.length >= 2 && var4 == null) {
         throw new CommandException("commands.clear.failure", var3.func_70005_c_());
      } else {
         int var6 = var3.field_71071_by.func_146027_a(var4, var5);
         var3.field_71069_bz.func_75142_b();
         if (!var3.field_71075_bZ.field_75098_d) {
            var3.func_71113_k();
         }

         if (var6 == 0) {
            throw new CommandException("commands.clear.failure", var3.func_70005_c_());
         } else {
            func_152373_a(var1, this, "commands.clear.success", new Object[]{var3.func_70005_c_(), var6});
         }
      }
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      if (var2.length == 1) {
         return func_71530_a(var2, this.func_147209_d());
      } else {
         return var2.length == 2 ? func_71531_a(var2, Item.field_150901_e.func_148742_b()) : null;
      }
   }

   protected String[] func_147209_d() {
      return MinecraftServer.func_71276_C().func_71213_z();
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return var2 == 0;
   }
}
