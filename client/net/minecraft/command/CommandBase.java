package net.minecraft.command;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class CommandBase implements ICommand {
   private static IAdminCommand field_71533_a;

   public CommandBase() {
      super();
   }

   public int func_82362_a() {
      return 4;
   }

   public List<String> func_71514_a() {
      return Collections.emptyList();
   }

   public boolean func_71519_b(ICommandSender var1) {
      return var1.func_70003_b(this.func_82362_a(), this.func_71517_b());
   }

   public List<String> func_180525_a(ICommandSender var1, String[] var2, BlockPos var3) {
      return null;
   }

   public static int func_175755_a(String var0) throws NumberInvalidException {
      try {
         return Integer.parseInt(var0);
      } catch (NumberFormatException var2) {
         throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{var0});
      }
   }

   public static int func_180528_a(String var0, int var1) throws NumberInvalidException {
      return func_175764_a(var0, var1, 2147483647);
   }

   public static int func_175764_a(String var0, int var1, int var2) throws NumberInvalidException {
      int var3 = func_175755_a(var0);
      if (var3 < var1) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[]{var3, var1});
      } else if (var3 > var2) {
         throw new NumberInvalidException("commands.generic.num.tooBig", new Object[]{var3, var2});
      } else {
         return var3;
      }
   }

   public static long func_175766_b(String var0) throws NumberInvalidException {
      try {
         return Long.parseLong(var0);
      } catch (NumberFormatException var2) {
         throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{var0});
      }
   }

   public static long func_175760_a(String var0, long var1, long var3) throws NumberInvalidException {
      long var5 = func_175766_b(var0);
      if (var5 < var1) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", new Object[]{var5, var1});
      } else if (var5 > var3) {
         throw new NumberInvalidException("commands.generic.num.tooBig", new Object[]{var5, var3});
      } else {
         return var5;
      }
   }

   public static BlockPos func_175757_a(ICommandSender var0, String[] var1, int var2, boolean var3) throws NumberInvalidException {
      BlockPos var4 = var0.func_180425_c();
      return new BlockPos(func_175769_b((double)var4.func_177958_n(), var1[var2], -30000000, 30000000, var3), func_175769_b((double)var4.func_177956_o(), var1[var2 + 1], 0, 256, false), func_175769_b((double)var4.func_177952_p(), var1[var2 + 2], -30000000, 30000000, var3));
   }

   public static double func_175765_c(String var0) throws NumberInvalidException {
      try {
         double var1 = Double.parseDouble(var0);
         if (!Doubles.isFinite(var1)) {
            throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{var0});
         } else {
            return var1;
         }
      } catch (NumberFormatException var3) {
         throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{var0});
      }
   }

   public static double func_180526_a(String var0, double var1) throws NumberInvalidException {
      return func_175756_a(var0, var1, 1.7976931348623157E308D);
   }

   public static double func_175756_a(String var0, double var1, double var3) throws NumberInvalidException {
      double var5 = func_175765_c(var0);
      if (var5 < var1) {
         throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[]{var5, var1});
      } else if (var5 > var3) {
         throw new NumberInvalidException("commands.generic.double.tooBig", new Object[]{var5, var3});
      } else {
         return var5;
      }
   }

   public static boolean func_180527_d(String var0) throws CommandException {
      if (!var0.equals("true") && !var0.equals("1")) {
         if (!var0.equals("false") && !var0.equals("0")) {
            throw new CommandException("commands.generic.boolean.invalid", new Object[]{var0});
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static EntityPlayerMP func_71521_c(ICommandSender var0) throws PlayerNotFoundException {
      if (var0 instanceof EntityPlayerMP) {
         return (EntityPlayerMP)var0;
      } else {
         throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.", new Object[0]);
      }
   }

   public static EntityPlayerMP func_82359_c(ICommandSender var0, String var1) throws PlayerNotFoundException {
      EntityPlayerMP var2 = PlayerSelector.func_82386_a(var0, var1);
      if (var2 == null) {
         try {
            var2 = MinecraftServer.func_71276_C().func_71203_ab().func_177451_a(UUID.fromString(var1));
         } catch (IllegalArgumentException var4) {
         }
      }

      if (var2 == null) {
         var2 = MinecraftServer.func_71276_C().func_71203_ab().func_152612_a(var1);
      }

      if (var2 == null) {
         throw new PlayerNotFoundException();
      } else {
         return var2;
      }
   }

   public static Entity func_175768_b(ICommandSender var0, String var1) throws EntityNotFoundException {
      return func_175759_a(var0, var1, Entity.class);
   }

   public static <T extends Entity> T func_175759_a(ICommandSender var0, String var1, Class<? extends T> var2) throws EntityNotFoundException {
      Object var3 = PlayerSelector.func_179652_a(var0, var1, var2);
      MinecraftServer var4 = MinecraftServer.func_71276_C();
      if (var3 == null) {
         var3 = var4.func_71203_ab().func_152612_a(var1);
      }

      if (var3 == null) {
         try {
            UUID var5 = UUID.fromString(var1);
            var3 = var4.func_175576_a(var5);
            if (var3 == null) {
               var3 = var4.func_71203_ab().func_177451_a(var5);
            }
         } catch (IllegalArgumentException var6) {
            throw new EntityNotFoundException("commands.generic.entity.invalidUuid", new Object[0]);
         }
      }

      if (var3 != null && var2.isAssignableFrom(var3.getClass())) {
         return (Entity)var3;
      } else {
         throw new EntityNotFoundException();
      }
   }

   public static List<Entity> func_175763_c(ICommandSender var0, String var1) throws EntityNotFoundException {
      return (List)(PlayerSelector.func_82378_b(var1) ? PlayerSelector.func_179656_b(var0, var1, Entity.class) : Lists.newArrayList(new Entity[]{func_175768_b(var0, var1)}));
   }

   public static String func_96332_d(ICommandSender var0, String var1) throws PlayerNotFoundException {
      try {
         return func_82359_c(var0, var1).func_70005_c_();
      } catch (PlayerNotFoundException var3) {
         if (PlayerSelector.func_82378_b(var1)) {
            throw var3;
         } else {
            return var1;
         }
      }
   }

   public static String func_175758_e(ICommandSender var0, String var1) throws EntityNotFoundException {
      try {
         return func_82359_c(var0, var1).func_70005_c_();
      } catch (PlayerNotFoundException var5) {
         try {
            return func_175768_b(var0, var1).func_110124_au().toString();
         } catch (EntityNotFoundException var4) {
            if (PlayerSelector.func_82378_b(var1)) {
               throw var4;
            } else {
               return var1;
            }
         }
      }
   }

   public static IChatComponent func_147178_a(ICommandSender var0, String[] var1, int var2) throws PlayerNotFoundException {
      return func_147176_a(var0, var1, var2, false);
   }

   public static IChatComponent func_147176_a(ICommandSender var0, String[] var1, int var2, boolean var3) throws PlayerNotFoundException {
      ChatComponentText var4 = new ChatComponentText("");

      for(int var5 = var2; var5 < var1.length; ++var5) {
         if (var5 > var2) {
            var4.func_150258_a(" ");
         }

         Object var6 = new ChatComponentText(var1[var5]);
         if (var3) {
            IChatComponent var7 = PlayerSelector.func_150869_b(var0, var1[var5]);
            if (var7 == null) {
               if (PlayerSelector.func_82378_b(var1[var5])) {
                  throw new PlayerNotFoundException();
               }
            } else {
               var6 = var7;
            }
         }

         var4.func_150257_a((IChatComponent)var6);
      }

      return var4;
   }

   public static String func_180529_a(String[] var0, int var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = var1; var3 < var0.length; ++var3) {
         if (var3 > var1) {
            var2.append(" ");
         }

         String var4 = var0[var3];
         var2.append(var4);
      }

      return var2.toString();
   }

   public static CommandBase.CoordinateArg func_175770_a(double var0, String var2, boolean var3) throws NumberInvalidException {
      return func_175767_a(var0, var2, -30000000, 30000000, var3);
   }

   public static CommandBase.CoordinateArg func_175767_a(double var0, String var2, int var3, int var4, boolean var5) throws NumberInvalidException {
      boolean var6 = var2.startsWith("~");
      if (var6 && Double.isNaN(var0)) {
         throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{var0});
      } else {
         double var7 = 0.0D;
         if (!var6 || var2.length() > 1) {
            boolean var9 = var2.contains(".");
            if (var6) {
               var2 = var2.substring(1);
            }

            var7 += func_175765_c(var2);
            if (!var9 && !var6 && var5) {
               var7 += 0.5D;
            }
         }

         if (var3 != 0 || var4 != 0) {
            if (var7 < (double)var3) {
               throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[]{var7, var3});
            }

            if (var7 > (double)var4) {
               throw new NumberInvalidException("commands.generic.double.tooBig", new Object[]{var7, var4});
            }
         }

         return new CommandBase.CoordinateArg(var7 + (var6 ? var0 : 0.0D), var7, var6);
      }
   }

   public static double func_175761_b(double var0, String var2, boolean var3) throws NumberInvalidException {
      return func_175769_b(var0, var2, -30000000, 30000000, var3);
   }

   public static double func_175769_b(double var0, String var2, int var3, int var4, boolean var5) throws NumberInvalidException {
      boolean var6 = var2.startsWith("~");
      if (var6 && Double.isNaN(var0)) {
         throw new NumberInvalidException("commands.generic.num.invalid", new Object[]{var0});
      } else {
         double var7 = var6 ? var0 : 0.0D;
         if (!var6 || var2.length() > 1) {
            boolean var9 = var2.contains(".");
            if (var6) {
               var2 = var2.substring(1);
            }

            var7 += func_175765_c(var2);
            if (!var9 && !var6 && var5) {
               var7 += 0.5D;
            }
         }

         if (var3 != 0 || var4 != 0) {
            if (var7 < (double)var3) {
               throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[]{var7, var3});
            }

            if (var7 > (double)var4) {
               throw new NumberInvalidException("commands.generic.double.tooBig", new Object[]{var7, var4});
            }
         }

         return var7;
      }
   }

   public static Item func_147179_f(ICommandSender var0, String var1) throws NumberInvalidException {
      ResourceLocation var2 = new ResourceLocation(var1);
      Item var3 = (Item)Item.field_150901_e.func_82594_a(var2);
      if (var3 == null) {
         throw new NumberInvalidException("commands.give.item.notFound", new Object[]{var2});
      } else {
         return var3;
      }
   }

   public static Block func_147180_g(ICommandSender var0, String var1) throws NumberInvalidException {
      ResourceLocation var2 = new ResourceLocation(var1);
      if (!Block.field_149771_c.func_148741_d(var2)) {
         throw new NumberInvalidException("commands.give.block.notFound", new Object[]{var2});
      } else {
         Block var3 = (Block)Block.field_149771_c.func_82594_a(var2);
         if (var3 == null) {
            throw new NumberInvalidException("commands.give.block.notFound", new Object[]{var2});
         } else {
            return var3;
         }
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

   public static IChatComponent func_180530_a(List<IChatComponent> var0) {
      ChatComponentText var1 = new ChatComponentText("");

      for(int var2 = 0; var2 < var0.size(); ++var2) {
         if (var2 > 0) {
            if (var2 == var0.size() - 1) {
               var1.func_150258_a(" and ");
            } else if (var2 > 0) {
               var1.func_150258_a(", ");
            }
         }

         var1.func_150257_a((IChatComponent)var0.get(var2));
      }

      return var1;
   }

   public static String func_96333_a(Collection<String> var0) {
      return func_71527_a(var0.toArray(new String[var0.size()]));
   }

   public static List<String> func_175771_a(String[] var0, int var1, BlockPos var2) {
      if (var2 == null) {
         return null;
      } else {
         int var4 = var0.length - 1;
         String var3;
         if (var4 == var1) {
            var3 = Integer.toString(var2.func_177958_n());
         } else if (var4 == var1 + 1) {
            var3 = Integer.toString(var2.func_177956_o());
         } else {
            if (var4 != var1 + 2) {
               return null;
            }

            var3 = Integer.toString(var2.func_177952_p());
         }

         return Lists.newArrayList(new String[]{var3});
      }
   }

   public static List<String> func_181043_b(String[] var0, int var1, BlockPos var2) {
      if (var2 == null) {
         return null;
      } else {
         int var4 = var0.length - 1;
         String var3;
         if (var4 == var1) {
            var3 = Integer.toString(var2.func_177958_n());
         } else {
            if (var4 != var1 + 1) {
               return null;
            }

            var3 = Integer.toString(var2.func_177952_p());
         }

         return Lists.newArrayList(new String[]{var3});
      }
   }

   public static boolean func_71523_a(String var0, String var1) {
      return var1.regionMatches(true, 0, var0, 0, var0.length());
   }

   public static List<String> func_71530_a(String[] var0, String... var1) {
      return func_175762_a(var0, Arrays.asList(var1));
   }

   public static List<String> func_175762_a(String[] var0, Collection<?> var1) {
      String var2 = var0[var0.length - 1];
      ArrayList var3 = Lists.newArrayList();
      if (!var1.isEmpty()) {
         Iterator var4 = Iterables.transform(var1, Functions.toStringFunction()).iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            if (func_71523_a(var2, var5)) {
               var3.add(var5);
            }
         }

         if (var3.isEmpty()) {
            var4 = var1.iterator();

            while(var4.hasNext()) {
               Object var6 = var4.next();
               if (var6 instanceof ResourceLocation && func_71523_a(var2, ((ResourceLocation)var6).func_110623_a())) {
                  var3.add(String.valueOf(var6));
               }
            }
         }
      }

      return var3;
   }

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

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((ICommand)var1);
   }

   public static class CoordinateArg {
      private final double field_179633_a;
      private final double field_179631_b;
      private final boolean field_179632_c;

      protected CoordinateArg(double var1, double var3, boolean var5) {
         super();
         this.field_179633_a = var1;
         this.field_179631_b = var3;
         this.field_179632_c = var5;
      }

      public double func_179628_a() {
         return this.field_179633_a;
      }

      public double func_179629_b() {
         return this.field_179631_b;
      }

      public boolean func_179630_c() {
         return this.field_179632_c;
      }
   }
}
