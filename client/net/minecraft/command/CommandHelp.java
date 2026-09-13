package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent$Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class CommandHelp extends CommandBase {
   public CommandHelp() {
      super();
   }

   @Override
   public String func_71517_b() {
      return "help";
   }

   @Override
   public int func_82362_a() {
      return 0;
   }

   @Override
   public String func_71518_a(ICommandSender var1) {
      return "commands.help.usage";
   }

   @Override
   public List func_71514_a() {
      return Arrays.asList("?");
   }

   @Override
   public void func_71515_b(ICommandSender var1, String[] var2) {
      List var3 = this.func_71534_d(var1);
      byte var4 = 7;
      int var5 = (var3.size() - 1) / var4;
      int var6 = 0;

      try {
         var6 = var2.length == 0 ? 0 : func_71532_a(var1, var2[0], 1, var5 + 1) - 1;
      } catch (NumberInvalidException var12) {
         Map var8 = this.func_71535_c();
         ICommand var9 = (ICommand)var8.get(var2[0]);
         if (var9 != null) {
            throw new WrongUsageException(var9.func_71518_a(var1));
         }

         if (MathHelper.func_82715_a(var2[0], -1) != -1) {
            throw var12;
         }

         throw new CommandNotFoundException();
      }

      int var7 = Math.min((var6 + 1) * var4, var3.size());
      ChatComponentTranslation var14 = new ChatComponentTranslation("commands.help.header", var6 + 1, var5 + 1);
      var14.func_150256_b().func_150238_a(EnumChatFormatting.DARK_GREEN);
      var1.func_145747_a(var14);

      for(int var15 = var6 * var4; var15 < var7; ++var15) {
         ICommand var10 = (ICommand)var3.get(var15);
         ChatComponentTranslation var11 = new ChatComponentTranslation(var10.func_71518_a(var1));
         var11.func_150256_b().func_150241_a(new ClickEvent(ClickEvent$Action.SUGGEST_COMMAND, "/" + var10.func_71517_b() + " "));
         var1.func_145747_a(var11);
      }

      if (var6 == 0 && var1 instanceof EntityPlayer) {
         ChatComponentTranslation var16 = new ChatComponentTranslation("commands.help.footer");
         var16.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
         var1.func_145747_a(var16);
      }
   }

   protected List func_71534_d(ICommandSender var1) {
      List var2 = MinecraftServer.func_71276_C().func_71187_D().func_71557_a(var1);
      Collections.sort(var2);
      return var2;
   }

   protected Map func_71535_c() {
      return MinecraftServer.func_71276_C().func_71187_D().func_71555_a();
   }
}
