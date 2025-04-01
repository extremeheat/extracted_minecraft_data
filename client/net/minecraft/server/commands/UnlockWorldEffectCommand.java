package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceSelectorArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.mines.WorldEffect;

public class UnlockWorldEffectCommand {
   public UnlockWorldEffectCommand() {
      super();
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0, CommandBuildContext var1) {
      var0.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("unlock_world_effect").requires((var0x) -> var0x.hasPermission(2))).then(Commands.argument("effect", ResourceSelectorArgument.resourceSelector(var1, Registries.WORLD_EFFECT)).executes((var0x) -> unlock((CommandSourceStack)var0x.getSource(), ResourceSelectorArgument.getSelectedResources(var0x, "effect", Registries.WORLD_EFFECT)))));
   }

   private static int unlock(CommandSourceStack var0, Collection<Holder.Reference<WorldEffect>> var1) throws CommandSyntaxException {
      var1.forEach((var1x) -> var0.getLevel().unlockEffect((WorldEffect)var1x.value()));
      return 1;
   }
}
