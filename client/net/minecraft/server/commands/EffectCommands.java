package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EffectCommands {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.give.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.clear.everything.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.effect.clear.specific.failed", new Object[0]));

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("effect").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(Commands.literal("clear").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes((var0x) -> {
         return clearEffects((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"));
      })).then(Commands.argument("effect", MobEffectArgument.effect()).executes((var0x) -> {
         return clearEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), MobEffectArgument.getEffect(var0x, "effect"));
      }))))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("effect", MobEffectArgument.effect()).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), MobEffectArgument.getEffect(var0x, "effect"), (Integer)null, 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), MobEffectArgument.getEffect(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), MobEffectArgument.getEffect(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), IntegerArgumentType.getInteger(var0x, "amplifier"), true);
      })).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), MobEffectArgument.getEffect(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), IntegerArgumentType.getInteger(var0x, "amplifier"), !BoolArgumentType.getBool(var0x, "hideParticles"));
      }))))))));
   }

   private static int giveEffect(CommandSourceStack var0, Collection<? extends Entity> var1, MobEffect var2, @Nullable Integer var3, int var4, boolean var5) throws CommandSyntaxException {
      int var6 = 0;
      int var7;
      if (var3 != null) {
         if (var2.isInstantenous()) {
            var7 = var3;
         } else {
            var7 = var3 * 20;
         }
      } else if (var2.isInstantenous()) {
         var7 = 1;
      } else {
         var7 = 600;
      }

      Iterator var8 = var1.iterator();

      while(var8.hasNext()) {
         Entity var9 = (Entity)var8.next();
         if (var9 instanceof LivingEntity) {
            MobEffectInstance var10 = new MobEffectInstance(var2, var7, var4, false, var5);
            if (((LivingEntity)var9).addEffect(var10)) {
               ++var6;
            }
         }
      }

      if (var6 == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(new TranslatableComponent("commands.effect.give.success.single", new Object[]{var2.getDisplayName(), ((Entity)var1.iterator().next()).getDisplayName(), var7 / 20}), true);
         } else {
            var0.sendSuccess(new TranslatableComponent("commands.effect.give.success.multiple", new Object[]{var2.getDisplayName(), var1.size(), var7 / 20}), true);
         }

         return var6;
      }
   }

   private static int clearEffects(CommandSourceStack var0, Collection<? extends Entity> var1) throws CommandSyntaxException {
      int var2 = 0;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Entity var4 = (Entity)var3.next();
         if (var4 instanceof LivingEntity && ((LivingEntity)var4).removeAllEffects()) {
            ++var2;
         }
      }

      if (var2 == 0) {
         throw ERROR_CLEAR_EVERYTHING_FAILED.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.single", new Object[]{((Entity)var1.iterator().next()).getDisplayName()}), true);
         } else {
            var0.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.multiple", new Object[]{var1.size()}), true);
         }

         return var2;
      }
   }

   private static int clearEffect(CommandSourceStack var0, Collection<? extends Entity> var1, MobEffect var2) throws CommandSyntaxException {
      int var3 = 0;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Entity var5 = (Entity)var4.next();
         if (var5 instanceof LivingEntity && ((LivingEntity)var5).removeEffect(var2)) {
            ++var3;
         }
      }

      if (var3 == 0) {
         throw ERROR_CLEAR_SPECIFIC_FAILED.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.single", new Object[]{var2.getDisplayName(), ((Entity)var1.iterator().next()).getDisplayName()}), true);
         } else {
            var0.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.multiple", new Object[]{var2.getDisplayName(), var1.size()}), true);
         }

         return var3;
      }
   }
}
