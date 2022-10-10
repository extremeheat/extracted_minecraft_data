package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class ExperienceCommand {
   private static final SimpleCommandExceptionType field_198449_a = new SimpleCommandExceptionType(new TextComponentTranslation("commands.experience.set.points.invalid", new Object[0]));

   public static void func_198437_a(CommandDispatcher<CommandSource> var0) {
      LiteralCommandNode var1 = var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("experience").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).then(Commands.func_197057_a("add").then(Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("amount", IntegerArgumentType.integer()).executes((var0x) -> {
         return func_198448_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "amount"), ExperienceCommand.Type.POINTS);
      })).then(Commands.func_197057_a("points").executes((var0x) -> {
         return func_198448_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "amount"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.func_197057_a("levels").executes((var0x) -> {
         return func_198448_a((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "amount"), ExperienceCommand.Type.LEVELS);
      })))))).then(Commands.func_197057_a("set").then(Commands.func_197056_a("targets", EntityArgument.func_197094_d()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.func_197056_a("amount", IntegerArgumentType.integer(0)).executes((var0x) -> {
         return func_198438_b((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "amount"), ExperienceCommand.Type.POINTS);
      })).then(Commands.func_197057_a("points").executes((var0x) -> {
         return func_198438_b((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "amount"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.func_197057_a("levels").executes((var0x) -> {
         return func_198438_b((CommandSource)var0x.getSource(), EntityArgument.func_197090_e(var0x, "targets"), IntegerArgumentType.getInteger(var0x, "amount"), ExperienceCommand.Type.LEVELS);
      })))))).then(Commands.func_197057_a("query").then(((RequiredArgumentBuilder)Commands.func_197056_a("targets", EntityArgument.func_197096_c()).then(Commands.func_197057_a("points").executes((var0x) -> {
         return func_198443_a((CommandSource)var0x.getSource(), EntityArgument.func_197089_d(var0x, "targets"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.func_197057_a("levels").executes((var0x) -> {
         return func_198443_a((CommandSource)var0x.getSource(), EntityArgument.func_197089_d(var0x, "targets"), ExperienceCommand.Type.LEVELS);
      })))));
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.func_197057_a("xp").requires((var0x) -> {
         return var0x.func_197034_c(2);
      })).redirect(var1));
   }

   private static int func_198443_a(CommandSource var0, EntityPlayerMP var1, ExperienceCommand.Type var2) {
      int var3 = var2.field_198433_f.applyAsInt(var1);
      var0.func_197030_a(new TextComponentTranslation("commands.experience.query." + var2.field_198432_e, new Object[]{var1.func_145748_c_(), var3}), false);
      return var3;
   }

   private static int func_198448_a(CommandSource var0, Collection<? extends EntityPlayerMP> var1, int var2, ExperienceCommand.Type var3) {
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         EntityPlayerMP var5 = (EntityPlayerMP)var4.next();
         var3.field_198430_c.accept(var5, var2);
      }

      if (var1.size() == 1) {
         var0.func_197030_a(new TextComponentTranslation("commands.experience.add." + var3.field_198432_e + ".success.single", new Object[]{var2, ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
      } else {
         var0.func_197030_a(new TextComponentTranslation("commands.experience.add." + var3.field_198432_e + ".success.multiple", new Object[]{var2, var1.size()}), true);
      }

      return var1.size();
   }

   private static int func_198438_b(CommandSource var0, Collection<? extends EntityPlayerMP> var1, int var2, ExperienceCommand.Type var3) throws CommandSyntaxException {
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         EntityPlayerMP var6 = (EntityPlayerMP)var5.next();
         if (var3.field_198431_d.test(var6, var2)) {
            ++var4;
         }
      }

      if (var4 == 0) {
         throw field_198449_a.create();
      } else {
         if (var1.size() == 1) {
            var0.func_197030_a(new TextComponentTranslation("commands.experience.set." + var3.field_198432_e + ".success.single", new Object[]{var2, ((EntityPlayerMP)var1.iterator().next()).func_145748_c_()}), true);
         } else {
            var0.func_197030_a(new TextComponentTranslation("commands.experience.set." + var3.field_198432_e + ".success.multiple", new Object[]{var2, var1.size()}), true);
         }

         return var1.size();
      }
   }

   static enum Type {
      POINTS("points", EntityPlayer::func_195068_e, (var0, var1) -> {
         if (var1 >= var0.func_71050_bK()) {
            return false;
         } else {
            var0.func_195394_a(var1);
            return true;
         }
      }, (var0) -> {
         return MathHelper.func_76141_d(var0.field_71106_cc * (float)var0.func_71050_bK());
      }),
      LEVELS("levels", EntityPlayerMP::func_82242_a, (var0, var1) -> {
         var0.func_195399_b(var1);
         return true;
      }, (var0) -> {
         return var0.field_71068_ca;
      });

      public final BiConsumer<EntityPlayerMP, Integer> field_198430_c;
      public final BiPredicate<EntityPlayerMP, Integer> field_198431_d;
      public final String field_198432_e;
      private final ToIntFunction<EntityPlayerMP> field_198433_f;

      private Type(String var3, BiConsumer<EntityPlayerMP, Integer> var4, BiPredicate<EntityPlayerMP, Integer> var5, ToIntFunction<EntityPlayerMP> var6) {
         this.field_198430_c = var4;
         this.field_198432_e = var3;
         this.field_198431_d = var5;
         this.field_198433_f = var6;
      }
   }
}
