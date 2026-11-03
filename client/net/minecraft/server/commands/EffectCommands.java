package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public class EffectCommands {
   private static final Dynamic2CommandExceptionType ERROR_GIVE_FAILED = new Dynamic2CommandExceptionType((effectCount, name) -> Component.translatableEscape("commands.effect.give.failed.single", effectCount, name));
   private static final Dynamic2CommandExceptionType ERROR_GIVE_FAILED_MULTIPLE = new Dynamic2CommandExceptionType((effectCount, entityCount) -> Component.translatableEscape("commands.effect.give.failed.multiple", effectCount, entityCount));
   private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.effect.clear.everything.failed"));
   private static final DynamicCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new DynamicCommandExceptionType((name) -> Component.translatableEscape("commands.effect.clear.specific.failed.single", name));
   private static final DynamicCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED_MULTIPLE = new DynamicCommandExceptionType((entityCount) -> Component.translatableEscape("commands.effect.clear.specific.failed.multiple", entityCount));
   private static final CommandResponseTracker.Messages<LivingEntity> RESPONSE_GIVE = CommandResponseTracker.messages((CommandResponseTracker.SingleHandler)((entity, effectCount) -> Component.translatable("commands.effect.give.success.single", effectCount, entity.getDisplayName())), (CommandResponseTracker.MultipleHandler)((entityCount, effectCount) -> Component.translatable("commands.effect.give.success.multiple", effectCount, entityCount)));
   private static final CommandResponseTracker.Messages<Entity> RESPONSE_CLEAR_ALL;
   private static final CommandResponseTracker.Messages<Entity> RESPONSE_CLEAR_SINGLE;
   private static final CommandResponseTracker.Dispatch<CommandSyntaxException, LivingEntity> ERROR_DISPATCH_GIVE;
   private static final CommandResponseTracker.Dispatch<CommandSyntaxException, Entity> ERROR_DISPATCH_CLEAR;

   public EffectCommands() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher, final CommandBuildContext context) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("effect").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).then(((LiteralArgumentBuilder)Commands.literal("clear").executes((c) -> clearEffects((CommandSourceStack)c.getSource(), ImmutableList.of(((CommandSourceStack)c.getSource()).getEntityOrException())))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes((c) -> clearEffects((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets")))).then(Commands.argument("effect", ResourceOrTagArgument.resourceOrTag(context, Registries.MOB_EFFECT)).executes((c) -> clearEffect((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), getMobEffect(c, "effect"))))))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("effect", ResourceOrTagArgument.resourceOrTag(context, Registries.MOB_EFFECT)).executes((c) -> giveEffect((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), getMobEffect(c, "effect"), (Integer)null, 0, true))).then(((RequiredArgumentBuilder)Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((c) -> giveEffect((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), getMobEffect(c, "effect"), IntegerArgumentType.getInteger(c, "seconds"), 0, true))).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((c) -> giveEffect((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), getMobEffect(c, "effect"), IntegerArgumentType.getInteger(c, "seconds"), IntegerArgumentType.getInteger(c, "amplifier"), true))).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((c) -> giveEffect((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), getMobEffect(c, "effect"), IntegerArgumentType.getInteger(c, "seconds"), IntegerArgumentType.getInteger(c, "amplifier"), !BoolArgumentType.getBool(c, "hideParticles"))))))).then(((LiteralArgumentBuilder)Commands.literal("infinite").executes((c) -> giveEffect((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), getMobEffect(c, "effect"), -1, 0, true))).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((c) -> giveEffect((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), getMobEffect(c, "effect"), -1, IntegerArgumentType.getInteger(c, "amplifier"), true))).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes((c) -> giveEffect((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), getMobEffect(c, "effect"), -1, IntegerArgumentType.getInteger(c, "amplifier"), !BoolArgumentType.getBool(c, "hideParticles"))))))))));
   }

   private static int giveEffect(final CommandSourceStack source, final Collection<? extends Entity> entities, final ResourceOrTagArgument.Result<MobEffect> elementOrTag, final @Nullable Integer seconds, final int amplifier, final boolean particles) throws CommandSyntaxException {
      Iterable<Holder<MobEffect>> effects = iterableResult(elementOrTag);
      CommandResponseTracker<LivingEntity> tracker = CommandResponseTracker.<LivingEntity>create();

      for(Entity entity : entities) {
         if (entity instanceof LivingEntity livingEntity) {
            tracker.track(livingEntity, addEffects(effects, livingEntity, source.getEntity(), seconds, amplifier, particles));
         }
      }

      if (tracker.totalValue() == 0) {
         throw (CommandSyntaxException)tracker.dispatch(CommandResponseTracker.ElementType.ANY, ERROR_DISPATCH_GIVE);
      } else {
         return tracker.sendFeedback(source, true, RESPONSE_GIVE);
      }
   }

   private static int addEffects(final Iterable<Holder<MobEffect>> effects, final LivingEntity target, final @Nullable Entity source, final @Nullable Integer seconds, final int amplifier, final boolean particles) {
      int applied = 0;

      for(Holder<MobEffect> effect : effects) {
         int duration = computeDurationInTicks(seconds, effect.value());
         MobEffectInstance instance = new MobEffectInstance(effect, duration, amplifier, false, particles);
         if (target.addEffect(instance, source)) {
            ++applied;
         }
      }

      return applied;
   }

   private static int computeDurationInTicks(final @Nullable Integer seconds, final MobEffect effect) {
      if (seconds != null) {
         if (effect.isInstantaneous()) {
            return seconds;
         } else {
            return seconds == -1 ? -1 : seconds * 20;
         }
      } else {
         return effect.isInstantaneous() ? 1 : 600;
      }
   }

   private static int clearEffects(final CommandSourceStack source, final Collection<? extends Entity> entities) throws CommandSyntaxException {
      CommandResponseTracker<LivingEntity> tracker = CommandResponseTracker.<LivingEntity>create();

      for(Entity entity : entities) {
         if (entity instanceof LivingEntity livingEntity) {
            tracker.track(livingEntity, livingEntity.removeAllEffects());
         }
      }

      return tracker.sendFeedback(source, true, RESPONSE_CLEAR_ALL);
   }

   private static int clearEffect(final CommandSourceStack source, final Collection<? extends Entity> entities, final ResourceOrTagArgument.Result<MobEffect> elementOrTag) throws CommandSyntaxException {
      Iterable<Holder<MobEffect>> effects = iterableResult(elementOrTag);
      CommandResponseTracker<LivingEntity> tracker = CommandResponseTracker.<LivingEntity>create();

      for(Entity entity : entities) {
         if (entity instanceof LivingEntity livingEntity) {
            tracker.track(livingEntity, removeEffects(effects, livingEntity));
         }
      }

      if (tracker.totalValue() == 0) {
         throw (CommandSyntaxException)tracker.dispatch(CommandResponseTracker.ElementType.ANY, ERROR_DISPATCH_CLEAR);
      } else {
         return tracker.sendFeedback(source, true, RESPONSE_CLEAR_SINGLE);
      }
   }

   private static int removeEffects(final Iterable<Holder<MobEffect>> effects, final LivingEntity target) {
      int removed = 0;

      for(Holder<MobEffect> effect : effects) {
         if (target.removeEffect(effect)) {
            ++removed;
         }
      }

      return removed;
   }

   private static ResourceOrTagArgument.Result<MobEffect> getMobEffect(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return ResourceOrTagArgument.<MobEffect>getResourceOrTag(context, name, Registries.MOB_EFFECT);
   }

   private static Iterable<Holder<MobEffect>> iterableResult(final ResourceOrTagArgument.Result<MobEffect> elementOrTag) {
      return (Iterable)elementOrTag.unwrap().map(List::of, (set) -> set);
   }

   static {
      RESPONSE_CLEAR_ALL = CommandResponseTracker.messages((SimpleCommandExceptionType)ERROR_CLEAR_EVERYTHING_FAILED, (CommandResponseTracker.SingleHandler)((entity, var1) -> Component.translatable("commands.effect.clear.everything.success.single", entity.getDisplayName())), (CommandResponseTracker.MultipleHandler)((entityCount, var1) -> Component.translatable("commands.effect.clear.everything.success.multiple", entityCount)));
      RESPONSE_CLEAR_SINGLE = CommandResponseTracker.messages((CommandResponseTracker.SingleHandler)((entity, effectCount) -> Component.translatable("commands.effect.clear.specific.success.single", effectCount, entity.getDisplayName())), (CommandResponseTracker.MultipleHandler)((entityCount, effectCount) -> Component.translatable("commands.effect.clear.specific.success.multiple", effectCount, entityCount)));
      ERROR_DISPATCH_GIVE = new CommandResponseTracker.Dispatch<CommandSyntaxException, LivingEntity>((entity, effectCount) -> ERROR_GIVE_FAILED.create(effectCount, entity.getDisplayName()), (entityCount, effectCount) -> ERROR_GIVE_FAILED_MULTIPLE.create(effectCount, entityCount));
      ERROR_DISPATCH_CLEAR = new CommandResponseTracker.Dispatch<CommandSyntaxException, Entity>((entity, var1) -> ERROR_CLEAR_SPECIFIC_FAILED.create(entity.getDisplayName()), (entityCount, var1) -> ERROR_CLEAR_SPECIFIC_FAILED_MULTIPLE.create(entityCount));
   }
}
