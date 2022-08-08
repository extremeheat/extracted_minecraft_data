package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

public class BlockStateArgument implements ArgumentType<BlockInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");
   private final HolderLookup<Block> blocks;

   public BlockStateArgument(CommandBuildContext var1) {
      super();
      this.blocks = var1.holderLookup(Registry.BLOCK_REGISTRY);
   }

   public static BlockStateArgument block(CommandBuildContext var0) {
      return new BlockStateArgument(var0);
   }

   public BlockInput parse(StringReader var1) throws CommandSyntaxException {
      BlockStateParser.BlockResult var2 = BlockStateParser.parseForBlock(this.blocks, var1, true);
      return new BlockInput(var2.blockState(), var2.properties().keySet(), var2.nbt());
   }

   public static BlockInput getBlock(CommandContext<CommandSourceStack> var0, String var1) {
      return (BlockInput)var0.getArgument(var1, BlockInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return BlockStateParser.fillSuggestions(this.blocks, var2, false, true);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
