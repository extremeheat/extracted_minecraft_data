package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SwingAnimationArgument;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.SwingAnimation;

public class SwingCommand {
   private static final SimpleCommandExceptionType ERROR_NO_LIVING_ENTITY = new SimpleCommandExceptionType(Component.translatable("commands.swing.failed.notliving"));
   private static final CommandResponseTracker.Messages<LivingEntity> RESPONSE_SWING;

   public SwingCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("swing").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).executes((c) -> swing((CommandSourceStack)c.getSource(), List.of(((CommandSourceStack)c.getSource()).getEntityOrException()), InteractionHand.MAIN_HAND))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes((c) -> swing((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), InteractionHand.MAIN_HAND))).then(handSwing("mainhand", InteractionHand.MAIN_HAND))).then(handSwing("offhand", InteractionHand.OFF_HAND))));
   }

   private static LiteralArgumentBuilder<CommandSourceStack> handSwing(final String name, final InteractionHand hand) {
      return (LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(name).executes((c) -> swing((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), hand))).then(((RequiredArgumentBuilder)Commands.argument("animation", SwingAnimationArgument.swingAnimationType()).executes((c) -> swing((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), hand, SwingAnimationArgument.getSwingAnimationType(c, "animation"), SwingAnimation.DEFAULT.duration()))).then(Commands.argument("duration", TimeArgument.time(1)).executes((c) -> swing((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), hand, SwingAnimationArgument.getSwingAnimationType(c, "animation"), IntegerArgumentType.getInteger(c, "duration")))));
   }

   private static int swing(final CommandSourceStack source, final Collection<? extends Entity> targets, final InteractionHand hand) throws CommandSyntaxException {
      return swing(source, targets, hand, SwingAnimation.DEFAULT.type(), SwingAnimation.DEFAULT.duration());
   }

   private static int swing(final CommandSourceStack source, final Collection<? extends Entity> targets, final InteractionHand hand, final SwingAnimationType animationType, final int duration) throws CommandSyntaxException {
      CommandResponseTracker<LivingEntity> tracker = CommandResponseTracker.<LivingEntity>create();
      SwingAnimation animation = new SwingAnimation(animationType, duration);

      for(Entity entity : targets) {
         if (entity instanceof LivingEntity livingEntity) {
            livingEntity.swing(hand, animation, true);
            tracker.track(livingEntity);
         }
      }

      return tracker.sendFeedback(source, true, RESPONSE_SWING);
   }

   static {
      RESPONSE_SWING = CommandResponseTracker.messages((SimpleCommandExceptionType)ERROR_NO_LIVING_ENTITY, (CommandResponseTracker.SingleHandler)((entity, var1) -> Component.translatable("commands.swing.success.single", entity.getDisplayName())), (CommandResponseTracker.MultipleHandler)((entityCount, var1) -> Component.translatable("commands.swing.success.multiple", entityCount)));
   }
}
