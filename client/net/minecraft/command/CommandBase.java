package net.minecraft.command;

import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public abstract class CommandBase implements ICommand {
   private static IAdminCommand field_71533_a;

   public CommandBase() {
      super();
   }

   public int func_82362_a() {
      return 4;
   }

   @Override
   public List func_71514_a() {
      return null;
   }

   @Override
   public boolean func_71519_b(ICommandSender var1) {
      return var1.func_70003_b(this.func_82362_a(), this.func_71517_b());
   }

   @Override
   public List func_71516_a(ICommandSender var1, String[] var2) {
      return null;
   }

   public static int func_71526_a(ICommandSender var0, String var1) {
      try {
         return Integer.parseInt(var1);
      } catch (NumberFormatException var3) {
         throw new NumberInvalidException("commands.generic.num.invalid", var1);
      }
   }

   public static int func_71528_a(ICommandSender var0, String var1, int var2) {
      return func_71532_a(var0, var1, var2, 2147483647);
   }

   public static int func_71532_a(ICommandSender var0, String var1, int var2, int var3) {
      int var4 = func_71526_a(var0, var1);
      if (var4 < var2) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", var4, var2);
      } else if (var4 > var3) {
         throw new NumberInvalidException("commands.generic.num.tooBig", var4, var3);
      } else {
         return var4;
      }
   }

   public static double func_82363_b(ICommandSender var0, String var1) {
      try {
         double var2 = Double.parseDouble(var1);
         if (!Doubles.isFinite(var2)) {
            throw new NumberInvalidException("commands.generic.num.invalid", var1);
         } else {
            return var2;
         }
      } catch (NumberFormatException var4) {
         throw new NumberInvalidException("commands.generic.num.invalid", var1);
      }
   }

   public static double func_110664_a(ICommandSender var0, String var1, double var2) {
      return func_110661_a(var0, var1, var2, 1.7976931348623157E308);
   }

   public static double func_110661_a(ICommandSender var0, String var1, double var2, double var4) {
      double var6 = func_82363_b(var0, var1);
      if (var6 < var2) {
         throw new NumberInvalidException("commands.generic.double.tooSmall", var6, var2);
      } else if (var6 > var4) {
         throw new NumberInvalidException("commands.generic.double.tooBig", var6, var4);
      } else {
         return var6;
      }
   }

   public static boolean func_110662_c(ICommandSender var0, String var1) {
      if (var1.equals("true") || var1.equals("1")) {
         return true;
      } else if (!var1.equals("false") && !var1.equals("0")) {
         throw new CommandException("commands.generic.boolean.invalid", var1);
      } else {
         return false;
      }
   }

   public static EntityPlayerMP func_71521_c(ICommandSender var0) {
      if (var0 instanceof EntityPlayerMP) {
         return (EntityPlayerMP)var0;
      } else {
         throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.");
      }
   }

   public static EntityPlayerMP func_82359_c(ICommandSender var0, String var1) {
      EntityPlayerMP var2 = PlayerSelector.func_82386_a(var0, var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(var1);
         if (var2 == null) {
            throw new PlayerNotFoundException();
         } else {
            return var2;
         }
      }
   }

   public static String func_96332_d(ICommandSender var0, String var1) {
      EntityPlayerMP var2 = PlayerSelector.func_82386_a(var0, var1);
      if (var2 != null) {
         return var2.func_70005_c_();
      } else if (PlayerSelector.func_82378_b(var1)) {
         throw new PlayerNotFoundException();
      } else {
         return var1;
      }
   }

   public static IChatComponent func_147178_a(ICommandSender var0, String[] var1, int var2) {
      return func_147176_a(var0, var1, var2, false);
   }

   public static IChatComponent func_147176_a(ICommandSender var0, String[] var1, int var2, boolean var3) {
      ChatComponentText var4 = new ChatComponentText("");

      for(int var5 = var2; var5 < var1.length; ++var5) {
         if (var5 > var2) {
            var4.func_150258_a(" ");
         }

         Object var6 = new ChatComponentText(var1[var5]);
         if (var3) {
            IChatComponent var7 = PlayerSelector.func_150869_b(var0, var1[var5]);
            if (var7 != null) {
               var6 = var7;
            } else if (PlayerSelector.func_82378_b(var1[var5])) {
               throw new PlayerNotFoundException();
            }
         }

         var4.func_150257_a((IChatComponent)var6);
      }

      return var4;
   }

   public static String func_82360_a(ICommandSender var0, String[] var1, int var2) {
      StringBuilder var3 = new StringBuilder();

      for(int var4 = var2; var4 < var1.length; ++var4) {
         if (var4 > var2) {
            var3.append(" ");
         }

         String var5 = var1[var4];
         var3.append(var5);
      }

      return var3.toString();
   }

   public static double func_110666_a(ICommandSender var0, double var1, String var3) {
      return func_110665_a(var0, var1, var3, -30000000, 30000000);
   }

   public static double func_110665_a(ICommandSender var0, double var1, String var3, int var4, int var5) {
      boolean var6 = var3.startsWith("~");
      if (var6 && Double.isNaN(var1)) {
         throw new NumberInvalidException("commands.generic.num.invalid", var1);
      } else {
         double var7 = var6 ? var1 : 0.0;
         if (!var6 || var3.length() > 1) {
            boolean var9 = var3.contains(".");
            if (var6) {
               var3 = var3.substring(1);
            }

            var7 += func_82363_b(var0, var3);
            if (!var9 && !var6) {
               var7 += 0.5;
            }
         }

         if (var4 != 0 || var5 != 0) {
            if (var7 < (double)var4) {
               throw new NumberInvalidException("commands.generic.double.tooSmall", var7, var4);
            }

            if (var7 > (double)var5) {
               throw new NumberInvalidException("commands.generic.double.tooBig", var7, var5);
            }
         }

         return var7;
      }
   }

   public static Item func_147179_f(ICommandSender var0, String var1) {
      Item var2 = (Item)Item.field_150901_e.func_82594_a(var1);
      if (var2 == null) {
         try {
            Item var3 = Item.func_150899_d(Integer.parseInt(var1));
            if (var3 != null) {
               ChatComponentTranslation var4 = new ChatComponentTranslation("commands.generic.deprecatedId", Item.field_150901_e.func_148750_c(var3));
               var4.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
               var0.func_145747_a(var4);
            }

            var2 = var3;
         } catch (NumberFormatException var5) {
         }
      }

      if (var2 == null) {
         throw new NumberInvalidException("commands.give.notFound", var1);
      } else {
         return var2;
      }
   }

   public static Block func_147180_g(ICommandSender var0, String var1) {
      if (Block.field_149771_c.func_148741_d(var1)) {
         return (Block)Block.field_149771_c.func_82594_a(var1);
      } else {
         try {
            int var2 = Integer.parseInt(var1);
            if (Block.field_149771_c.func_148753_b(var2)) {
               Block var3 = Block.func_149729_e(var2);
               ChatComponentTranslation var4 = new ChatComponentTranslation("commands.generic.deprecatedId", Block.field_149771_c.func_148750_c(var3));
               var4.func_150256_b().func_150238_a(EnumChatFormatting.GRAY);
               var0.func_145747_a(var4);
               return var3;
            }
         } catch (NumberFormatException var5) {
         }

         throw new NumberInvalidException("commands.give.notFound", var1);
      }
   }

   public static String func_71527_a(Object[] var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         String var3 = var0[var2].toString();
         if (var2 > 0) {
            if (var2 == var0.length - 1) {
               var1.append(" and ");
            } else {
               var1.append(", ");
            }
         }

         var1.append(var3);
      }

      return var1.toString();
   }

   public static IChatComponent func_147177_a(IChatComponent[] var0) {
      ChatComponentText var1 = new ChatComponentText("");

      for(int var2 = 0; var2 < var0.length; ++var2) {
         if (var2 > 0) {
            if (var2 == var0.length - 1) {
               var1.func_150258_a(" and ");
            } else if (var2 > 0) {
               var1.func_150258_a(", ");
            }
         }

         var1.func_150257_a(var0[var2]);
      }

      return var1;
   }

   public static String func_96333_a(Collection var0) {
      return func_71527_a(var0.toArray(new String[var0.size()]));
   }

   public static boolean func_71523_a(String var0, String var1) {
      return var1.regionMatches(true, 0, var0, 0, var0.length());
   }

   public static List func_71530_a(String[] var0, String... var1) {
      String var2 = var0[var0.length - 1];
      ArrayList var3 = new ArrayList();

      for(String var7 : var1) {
         if (func_71523_a(var2, var7)) {
            var3.add(var7);
         }
      }

      return var3;
   }

   public static List func_71531_a(String[] var0, Iterable var1) {
      String var2 = var0[var0.length - 1];
      ArrayList var3 = new ArrayList();

      for(String var5 : var1) {
         if (func_71523_a(var2, var5)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   @Override
   public boolean func_82358_a(String[] var1, int var2) {
      return false;
   }

   public static void func_152373_a(ICommandSender var0, ICommand var1, String var2, Object... var3) {
      func_152374_a(var0, var1, 0, var2, var3);
   }

   public static void func_152374_a(ICommandSender var0, ICommand var1, int var2, String var3, Object... var4) {
      if (field_71533_a != null) {
         field_71533_a.func_152372_a(var0, var1, var2, var3, var4);
      }
   }

   public static void func_71529_a(IAdminCommand var0) {
      field_71533_a = var0;
   }

   public int compareTo(ICommand var1) {
      return this.func_71517_b().compareTo(var1.func_71517_b());
   }
}
