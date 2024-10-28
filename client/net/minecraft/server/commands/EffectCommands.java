package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EffectCommands {
   private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.give.failed"));
   private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.clear.everything.failed"));
   private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.clear.specific.failed"));

   public EffectCommands() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("effect").requires((var0x) -> {
         return var0x.hasPermission(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("clear").executes((var0x) -> {
         return clearEffects((CommandSourceStack)var0x.getSource(), ImmutableList.of(((CommandSourceStack)var0x.getSource()).getEntityOrException()));
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes((var0x) -> {
         return clearEffects((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"));
      })).then(Commands.argument("effect", ResourceArgument.resource(var1, Registries.MOB_EFFECT)).executes((var0x) -> {
         return clearEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getMobEffect(var0x, "effect"));
      }))))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("effect", ResourceArgument.resource(var1, Registries.MOB_EFFECT)).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getMobEffect(var0x, "effect"), (Integer)null, 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getMobEffect(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getMobEffect(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), IntegerArgumentType.getInteger(var0x, "amplifier"), true);
      })).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getMobEffect(var0x, "effect"), IntegerArgumentType.getInteger(var0x, "seconds"), IntegerArgumentType.getInteger(var0x, "amplifier"), !BoolArgumentType.getBool(var0x, "hideParticles"));
      }))))).then(((LiteralArgumentBuilder)Commands.literal("infinite").executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getMobEffect(var0x, "effect"), -1, 0, true);
      })).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getMobEffect(var0x, "effect"), -1, IntegerArgumentType.getInteger(var0x, "amplifier"), true);
      })).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((var0x) -> {
         return giveEffect((CommandSourceStack)var0x.getSource(), EntityArgument.getEntities(var0x, "targets"), ResourceArgument.getMobEffect(var0x, "effect"), -1, IntegerArgumentType.getInteger(var0x, "amplifier"), !BoolArgumentType.getBool(var0x, "hideParticles"));
      }))))))));
   }

   private static int giveEffect(CommandSourceStack var0, Collection<? extends Entity> var1, Holder<MobEffect> var2, @Nullable Integer var3, int var4, boolean var5) throws CommandSyntaxException {
      MobEffect var6 = (MobEffect)var2.value();
      int var7 = 0;
      int var8;
      if (var3 != null) {
         if (var6.isInstantenous()) {
            var8 = var3;
         } else if (var3 == -1) {
            var8 = -1;
         } else {
            var8 = var3 * 20;
         }
      } else if (var6.isInstantenous()) {
         var8 = 1;
      } else {
         var8 = 600;
      }

      Iterator var9 = var1.iterator();

      while(var9.hasNext()) {
         Entity var10 = (Entity)var9.next();
         if (var10 instanceof LivingEntity) {
            MobEffectInstance var11 = new MobEffectInstance(var2, var8, var4, false, var5);
            if (((LivingEntity)var10).addEffect(var11, var0.getEntity())) {
               ++var7;
            }
         }
      }

      if (var7 == 0) {
         throw ERROR_GIVE_FAILED.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.effect.give.success.single", var6.getDisplayName(), ((Entity)var1.iterator().next()).getDisplayName(), var8 / 20);
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.effect.give.success.multiple", var6.getDisplayName(), var1.size(), var8 / 20);
            }, true);
         }

         return var7;
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
            var0.sendSuccess(() -> {
               return Component.translatable("commands.effect.clear.everything.success.single", ((Entity)var1.iterator().next()).getDisplayName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.effect.clear.everything.success.multiple", var1.size());
            }, true);
         }

         return var2;
      }
   }

   private static int clearEffect(CommandSourceStack var0, Collection<? extends Entity> var1, Holder<MobEffect> var2) throws CommandSyntaxException {
      MobEffect var3 = (MobEffect)var2.value();
      int var4 = 0;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Entity var6 = (Entity)var5.next();
         if (var6 instanceof LivingEntity && ((LivingEntity)var6).removeEffect(var2)) {
            ++var4;
         }
      }

      if (var4 == 0) {
         throw ERROR_CLEAR_SPECIFIC_FAILED.create();
      } else {
         if (var1.size() == 1) {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.effect.clear.specific.success.single", var3.getDisplayName(), ((Entity)var1.iterator().next()).getDisplayName());
            }, true);
         } else {
            var0.sendSuccess(() -> {
               return Component.translatable("commands.effect.clear.specific.success.multiple", var3.getDisplayName(), var1.size());
            }, true);
         }

         return var4;
      }
   }
}
