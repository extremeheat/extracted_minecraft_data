package net.minecraft.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager {
   private static final Logger field_147175_a = LogManager.getLogger();
   private final Map field_71562_a = new HashMap();
   private final Set field_71561_b = new HashSet();

   public CommandHandler() {
      super();
   }

   @Override
   public int func_71556_a(ICommandSender var1, String var2) {
      var2 = var2.trim();
      if (var2.startsWith("/")) {
         var2 = var2.substring(1);
      }

      String[] var3 = var2.split(" ");
      String var4 = var3[0];
      var3 = func_71559_a(var3);
      ICommand var5 = (ICommand)this.field_71562_a.get(var4);
      int var6 = this.func_82370_a(var5, var3);
      int var7 = 0;

      try {
         if (var5 == null) {
            throw new CommandNotFoundException();
         }

         if (var5.func_71519_b(var1)) {
            if (var6 > -1) {
               EntityPlayerMP[] var8 = PlayerSelector.func_82380_c(var1, var3[var6]);
               String var26 = var3[var6];

               for(EntityPlayerMP var13 : var8) {
                  var3[var6] = var13.func_70005_c_();

                  try {
                     var5.func_71515_b(var1, var3);
                     ++var7;
                  } catch (CommandException var17) {
                     ChatComponentTranslation var15 = new ChatComponentTranslation(var17.getMessage(), var17.func_74844_a());
                     var15.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                     var1.func_145747_a(var15);
                  }
               }

               var3[var6] = var26;
            } else {
               try {
                  var5.func_71515_b(var1, var3);
                  ++var7;
               } catch (CommandException var16) {
                  ChatComponentTranslation var27 = new ChatComponentTranslation(var16.getMessage(), var16.func_74844_a());
                  var27.func_150256_b().func_150238_a(EnumChatFormatting.RED);
                  var1.func_145747_a(var27);
               }
            }
         } else {
            ChatComponentTranslation var23 = new ChatComponentTranslation("commands.generic.permission");
            var23.func_150256_b().func_150238_a(EnumChatFormatting.RED);
            var1.func_145747_a(var23);
         }
      } catch (WrongUsageException var18) {
         ChatComponentTranslation var25 = new ChatComponentTranslation(
            "commands.generic.usage", new ChatComponentTranslation(var18.getMessage(), var18.func_74844_a())
         );
         var25.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var1.func_145747_a(var25);
      } catch (CommandException var19) {
         ChatComponentTranslation var24 = new ChatComponentTranslation(var19.getMessage(), var19.func_74844_a());
         var24.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var1.func_145747_a(var24);
      } catch (Throwable var20) {
         ChatComponentTranslation var9 = new ChatComponentTranslation("commands.generic.exception");
         var9.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         var1.func_145747_a(var9);
         field_147175_a.error("Couldn't process command: '" + var2 + "'", var20);
      }

      return var7;
   }

   public ICommand func_71560_a(ICommand var1) {
      List var2 = var1.func_71514_a();
      this.field_71562_a.put(var1.func_71517_b(), var1);
      this.field_71561_b.add(var1);
      if (var2 != null) {
         for(String var4 : var2) {
            ICommand var5 = (ICommand)this.field_71562_a.get(var4);
            if (var5 == null || !var5.func_71517_b().equals(var4)) {
               this.field_71562_a.put(var4, var1);
            }
         }
      }

      return var1;
   }

   private static String[] func_71559_a(String[] var0) {
      String[] var1 = new String[var0.length - 1];

      for(int var2 = 1; var2 < var0.length; ++var2) {
         var1[var2 - 1] = var0[var2];
      }

      return var1;
   }

   @Override
   public List func_71558_b(ICommandSender var1, String var2) {
      String[] var3 = var2.split(" ", -1);
      String var4 = var3[0];
      if (var3.length == 1) {
         ArrayList var8 = new ArrayList();

         for(Entry var7 : this.field_71562_a.entrySet()) {
            if (CommandBase.func_71523_a(var4, (String)var7.getKey()) && ((ICommand)var7.getValue()).func_71519_b(var1)) {
               var8.add(var7.getKey());
            }
         }

         return var8;
      } else {
         if (var3.length > 1) {
            ICommand var5 = (ICommand)this.field_71562_a.get(var4);
            if (var5 != null) {
               return var5.func_71516_a(var1, func_71559_a(var3));
            }
         }

         return null;
      }
   }

   @Override
   public List func_71557_a(ICommandSender var1) {
      ArrayList var2 = new ArrayList();

      for(ICommand var4 : this.field_71561_b) {
         if (var4.func_71519_b(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   @Override
   public Map func_71555_a() {
      return this.field_71562_a;
   }

   private int func_82370_a(ICommand var1, String[] var2) {
      if (var1 == null) {
         return -1;
      } else {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.func_82358_a(var2, var3) && PlayerSelector.func_82377_a(var2[var3])) {
               return var3;
            }
         }

         return -1;
      }
   }
}
