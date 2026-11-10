package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeDebugFormat;
import net.minecraft.world.attribute.EnvironmentAttributeDebugger;
import net.minecraft.world.attribute.EnvironmentAttributeLayerValues;
import net.minecraft.world.phys.Vec3;

public class EnvironmentAttributeCommand {
   public static final PermissionCheck PERMISSION_CHECK;

   public EnvironmentAttributeCommand() {
      super();
   }

   public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("environmentattribute").requires(Commands.hasPermission(PERMISSION_CHECK))).then(Commands.argument("attribute", ResourceArgument.resource(Registries.ENVIRONMENT_ATTRIBUTE)).then(((LiteralArgumentBuilder)Commands.literal("get").executes((c) -> getValue((CommandSourceStack)c.getSource(), getAttribute(c), ((CommandSourceStack)c.getSource()).getPosition()))).then(Commands.argument("pos", Vec3Argument.vec3()).executes((c) -> getValue((CommandSourceStack)c.getSource(), getAttribute(c), Vec3Argument.getVec3(c, "pos")))))));
   }

   private static Holder.Reference<EnvironmentAttribute<?>> getAttribute(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      return ResourceArgument.<EnvironmentAttribute<?>>getResource(context, "attribute", Registries.ENVIRONMENT_ATTRIBUTE);
   }

   private static int getValue(final CommandSourceStack source, final Holder.Reference<EnvironmentAttribute<?>> attributeHolder, final Vec3 pos) {
      ServerLevel level = source.getLevel();
      EnvironmentAttributeLayerValues layerValues = EnvironmentAttributeDebugger.getValuePerLayer(level, attributeHolder.value(), pos);
      source.sendSuccess(() -> {
         String var10000 = String.valueOf(attributeHolder.key().identifier());
         return Component.literal(var10000 + " = " + EnvironmentAttributeDebugFormat.describeValue(layerValues.finalValue()));
      }, false);

      for(EnvironmentAttributeLayerValues.LayerValue layer : layerValues.layers()) {
         String prefix = layer.disabled() ? "  [disabled] " : "  ";
         String index = layer.layerIndex() >= 0 ? layer.layerIndex() + ". " : "";
         String line = prefix + index + layer.kind() + (layer.modifier() != null ? " " + layer.modifier() : "") + " -> " + EnvironmentAttributeDebugFormat.describeValue(layer.value());
         source.sendSuccess(() -> Component.literal(line), false);
      }

      return 1;
   }

   static {
      PERMISSION_CHECK = new PermissionCheck.Require(Permissions.COMMANDS_GAMEMASTER);
   }
}
