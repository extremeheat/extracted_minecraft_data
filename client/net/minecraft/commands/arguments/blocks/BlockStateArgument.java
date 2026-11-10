package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;

public class BlockStateArgument implements SerializableArgument<BlockInput> {
   private static final BlockStateArgument INSTANCE = new BlockStateArgument();
   public static final SerializableArgument.Type<BlockStateArgument> TYPE;
   private static final Collection<String> EXAMPLES;

   private BlockStateArgument() {
      super();
   }

   public SerializableArgument.Type<BlockStateArgument> type() {
      return TYPE;
   }

   public static ArgumentType<BlockInput> block() {
      return INSTANCE.toBrigadier();
   }

   public BlockInput parse(final StringReader reader, final ParseContext context) throws CommandSyntaxException {
      HolderGetter<Block> blocks = context.holderGetters().<Block>lookupOrThrow(Registries.BLOCK);
      BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(blocks, reader, true);
      return new BlockInput(result.blockState(), result.properties().keySet(), result.nbt());
   }

   public static BlockInput getBlock(final CommandContext<?> context, final String name) {
      return (BlockInput)context.getArgument(name, BlockInput.class);
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
      return BlockStateParser.fillSuggestions(context, builder, false, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<BlockStateArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");
   }
}
