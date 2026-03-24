package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class SwingCommand {
   private static final SimpleCommandExceptionType ERROR_NO_LIVING_ENTITY = new SimpleCommandExceptionType(Component.translatable("commands.swing.failed.notliving"));

   public SwingCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("swing").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))).executes((c) -> swing((CommandSourceStack)c.getSource(), List.of(((CommandSourceStack)c.getSource()).getEntityOrException()), InteractionHand.MAIN_HAND))).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes((c) -> swing((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), InteractionHand.MAIN_HAND))).then(Commands.literal("mainhand").executes((c) -> swing((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), InteractionHand.MAIN_HAND)))).then(Commands.literal("offhand").executes((c) -> swing((CommandSourceStack)c.getSource(), EntityArgument.getEntities(c, "targets"), InteractionHand.OFF_HAND)))));
   }

   private static int swing(final CommandSourceStack source, final Collection<? extends Entity> targets, final InteractionHand hand) throws CommandSyntaxException {
      int livingEntitiesCount = 0;

      for(Entity entity : targets) {
         if (entity instanceof LivingEntity livingEntity) {
            livingEntity.swing(hand, true);
            ++livingEntitiesCount;
         }
      }

      if (livingEntitiesCount == 0) {
         throw ERROR_NO_LIVING_ENTITY.create();
      } else {
         if (livingEntitiesCount == 1) {
            source.sendSuccess(() -> Component.translatable("commands.swing.success.single", ((Entity)targets.iterator().next()).getDisplayName()), true);
         } else {
            source.sendSuccess(() -> Component.translatable("commands.swing.success.multiple", livingEntitiesCount), true);
         }

         return livingEntitiesCount;
      }
   }
}
