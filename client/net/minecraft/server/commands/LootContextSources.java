package net.minecraft.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootContextSources {
   public LootContextSources() {
      super();
   }

   private static ArgumentBuilder<CommandSourceStack, ?> decorate(final ArgumentBuilder<CommandSourceStack, ?> node, final NodeVisitor nodeVisitor, final ContextDecorator decorator) {
      Objects.requireNonNull(node);
      nodeVisitor.visit(decorator, node::then);
      return node;
   }

   public static <T extends ArgumentBuilder<CommandSourceStack, T>> T addContextSources(final T node, final NodeVisitor factory) {
      return (T)node.then(decorate(Commands.literal("default"), factory, (var0, params) -> params.create(LootContextParamSets.COMMAND_COMPUTE_DEFAULT))).then(Commands.literal("block").then(decorate(Commands.argument("computePos", BlockPosArgument.blockPos()), factory, (context, params) -> {
         BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "computePos");
         CommandSourceStack source = (CommandSourceStack)context.getSource();
         ServerLevel level = source.getLevel();
         BlockState blockState = level.getBlockState(pos);
         BlockEntity blockEntity = level.getBlockEntity(pos);
         return params.withParameter(LootContextParams.BLOCK_STATE, blockState).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).create(LootContextParamSets.COMMAND_COMPUTE_POSITION);
      }))).then(Commands.literal("entity").then(decorate(Commands.argument("computeTarget", EntityArgument.entity()), factory, (context, params) -> {
         Entity target = EntityArgument.getEntity(context, "computeTarget");
         return params.withParameter(LootContextParams.TARGET_ENTITY, target).create(LootContextParamSets.COMMAND_COMPUTE_ENTITY);
      })));
   }

   @FunctionalInterface
   public interface ContextDecorator {
      LootParams customize(CommandContext<CommandSourceStack> context, LootParams.Builder params) throws CommandSyntaxException;

      default LootParams createParams(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
         CommandSourceStack source = (CommandSourceStack)context.getSource();
         ServerLevel level = source.getLevel();
         LootParams.Builder commonContext = (new LootParams.Builder(level)).withOptionalParameter(LootContextParams.THIS_ENTITY, source.getEntity()).withParameter(LootContextParams.ORIGIN, source.getPosition());
         return this.customize(context, commonContext);
      }

      default LootContext createContext(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
         LootParams params = this.createParams(context);
         return (new LootContext.Builder(params)).create(Optional.empty());
      }
   }

   @FunctionalInterface
   public interface NodeVisitor {
      void visit(ContextDecorator contextDecorator, Consumer<ArgumentBuilder<CommandSourceStack, ?>> output);
   }
}
