package net.minecraft.util;

import java.util.Iterator;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.Entity;

public class ChatComponentProcessor {
   public static IChatComponent func_179985_a(ICommandSender var0, IChatComponent var1, Entity var2) throws CommandException {
      Object var3 = null;
      if (var1 instanceof ChatComponentScore) {
         ChatComponentScore var4 = (ChatComponentScore)var1;
         String var5 = var4.func_179995_g();
         if (PlayerSelector.func_82378_b(var5)) {
            List var6 = PlayerSelector.func_179656_b(var0, var5, Entity.class);
            if (var6.size() != 1) {
               throw new EntityNotFoundException();
            }

            var5 = ((Entity)var6.get(0)).func_70005_c_();
         }

         var3 = var2 != null && var5.equals("*") ? new ChatComponentScore(var2.func_70005_c_(), var4.func_179994_h()) : new ChatComponentScore(var5, var4.func_179994_h());
         ((ChatComponentScore)var3).func_179997_b(var4.func_150261_e());
      } else if (var1 instanceof ChatComponentSelector) {
         String var7 = ((ChatComponentSelector)var1).func_179992_g();
         var3 = PlayerSelector.func_150869_b(var0, var7);
         if (var3 == null) {
            var3 = new ChatComponentText("");
         }
      } else if (var1 instanceof ChatComponentText) {
         var3 = new ChatComponentText(((ChatComponentText)var1).func_150265_g());
      } else {
         if (!(var1 instanceof ChatComponentTranslation)) {
            return var1;
         }

         Object[] var8 = ((ChatComponentTranslation)var1).func_150271_j();

         for(int var10 = 0; var10 < var8.length; ++var10) {
            Object var11 = var8[var10];
            if (var11 instanceof IChatComponent) {
               var8[var10] = func_179985_a(var0, (IChatComponent)var11, var2);
            }
         }

         var3 = new ChatComponentTranslation(((ChatComponentTranslation)var1).func_150268_i(), var8);
      }

      ChatStyle var9 = var1.func_150256_b();
      if (var9 != null) {
         ((IChatComponent)var3).func_150255_a(var9.func_150232_l());
      }

      Iterator var13 = var1.func_150253_a().iterator();

      while(var13.hasNext()) {
         IChatComponent var12 = (IChatComponent)var13.next();
         ((IChatComponent)var3).func_150257_a(func_179985_a(var0, var12, var2));
      }

      return (IChatComponent)var3;
   }
}
