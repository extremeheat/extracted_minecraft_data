package net.minecraft.command.impl.data;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.NBTArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class DataCommand {
   private static final SimpleCommandExceptionType field_198949_b = new SimpleCommandExceptionType(new TextComponentTranslation("commands.data.merge.failed", new Object[0]));
   private static final DynamicCommandExceptionType field_198950_c = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.data.get.invalid", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_201229_d = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("commands.data.get.unknown", new Object[]{var0});
   });
   public static final List<DataCommand.IDataProvider> field_198948_a;

   public static void func_198937_a(CommandDispatcher<CommandSource> var0) {
      LiteralArgumentBuilder var1 = (LiteralArgumentBuilder)Commands.func_197057_a("data").requires((var0x) -> {
         return var0x.func_197034_c(2);
      });
      Iterator var2 = field_198948_a.iterator();

      while(var2.hasNext()) {
         DataCommand.IDataProvider var3 = (DataCommand.IDataProvider)var2.next();
         ((LiteralArgumentBuilder)((LiteralArgumentBuilder)var1.then(var3.func_198920_a(Commands.func_197057_a("merge"), (var1x) -> {
            return var1x.then(Commands.func_197056_a("nbt", NBTArgument.func_197131_a()).executes((var1) -> {
               return func_198946_a((CommandSource)var1.getSource(), var3.func_198919_a(var1), NBTArgument.func_197130_a(var1, "nbt"));
            }));
         }))).then(var3.func_198920_a(Commands.func_197057_a("get"), (var1x) -> {
            return var1x.executes((var1) -> {
               return func_198947_a((CommandSource)var1.getSource(), var3.func_198919_a(var1));
            }).then(((RequiredArgumentBuilder)Commands.func_197056_a("path", NBTPathArgument.func_197149_a()).executes((var1) -> {
               return func_201228_b((CommandSource)var1.getSource(), var3.func_198919_a(var1), NBTPathArgument.func_197148_a(var1, "path"));
            })).then(Commands.func_197056_a("scale", DoubleArgumentType.doubleArg()).executes((var1) -> {
               return func_198938_a((CommandSource)var1.getSource(), var3.func_198919_a(var1), NBTPathArgument.func_197148_a(var1, "path"), DoubleArgumentType.getDouble(var1, "scale"));
            })));
         }))).then(var3.func_198920_a(Commands.func_197057_a("remove"), (var1x) -> {
            return var1x.then(Commands.func_197056_a("path", NBTPathArgument.func_197149_a()).executes((var1) -> {
               return func_198942_a((CommandSource)var1.getSource(), var3.func_198919_a(var1), NBTPathArgument.func_197148_a(var1, "path"));
            }));
         }));
      }

      var0.register(var1);
   }

   private static int func_198942_a(CommandSource var0, IDataAccessor var1, NBTPathArgument.NBTPath var2) throws CommandSyntaxException {
      NBTTagCompound var3 = var1.func_198923_a();
      NBTTagCompound var4 = var3.func_74737_b();
      var2.func_197140_b(var3);
      if (var4.equals(var3)) {
         throw field_198949_b.create();
      } else {
         var1.func_198925_a(var3);
         var0.func_197030_a(var1.func_198921_b(), true);
         return 1;
      }
   }

   private static int func_201228_b(CommandSource var0, IDataAccessor var1, NBTPathArgument.NBTPath var2) throws CommandSyntaxException {
      INBTBase var3 = var2.func_197143_a(var1.func_198923_a());
      int var4;
      if (var3 instanceof NBTPrimitive) {
         var4 = MathHelper.func_76128_c(((NBTPrimitive)var3).func_150286_g());
      } else if (var3 instanceof NBTTagCollection) {
         var4 = ((NBTTagCollection)var3).size();
      } else if (var3 instanceof NBTTagCompound) {
         var4 = ((NBTTagCompound)var3).func_186856_d();
      } else {
         if (!(var3 instanceof NBTTagString)) {
            throw field_201229_d.create(var2.toString());
         }

         var4 = ((NBTTagString)var3).func_150285_a_().length();
      }

      var0.func_197030_a(var1.func_198924_b(var3), false);
      return var4;
   }

   private static int func_198938_a(CommandSource var0, IDataAccessor var1, NBTPathArgument.NBTPath var2, double var3) throws CommandSyntaxException {
      INBTBase var5 = var2.func_197143_a(var1.func_198923_a());
      if (!(var5 instanceof NBTPrimitive)) {
         throw field_198950_c.create(var2.toString());
      } else {
         int var6 = MathHelper.func_76128_c(((NBTPrimitive)var5).func_150286_g() * var3);
         var0.func_197030_a(var1.func_198922_a(var2, var3, var6), false);
         return var6;
      }
   }

   private static int func_198947_a(CommandSource var0, IDataAccessor var1) throws CommandSyntaxException {
      var0.func_197030_a(var1.func_198924_b(var1.func_198923_a()), false);
      return 1;
   }

   private static int func_198946_a(CommandSource var0, IDataAccessor var1, NBTTagCompound var2) throws CommandSyntaxException {
      NBTTagCompound var3 = var1.func_198923_a();
      NBTTagCompound var4 = var3.func_74737_b().func_197643_a(var2);
      if (var3.equals(var4)) {
         throw field_198949_b.create();
      } else {
         var1.func_198925_a(var4);
         var0.func_197030_a(var1.func_198921_b(), true);
         return 1;
      }
   }

   static {
      field_198948_a = Lists.newArrayList(new DataCommand.IDataProvider[]{EntityDataAccessor.field_198926_a, BlockDataAccessor.field_198930_a});
   }

   public interface IDataProvider {
      IDataAccessor func_198919_a(CommandContext<CommandSource> var1) throws CommandSyntaxException;

      ArgumentBuilder<CommandSource, ?> func_198920_a(ArgumentBuilder<CommandSource, ?> var1, Function<ArgumentBuilder<CommandSource, ?>, ArgumentBuilder<CommandSource, ?>> var2);
   }
}
