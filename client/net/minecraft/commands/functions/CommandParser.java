package net.minecraft.commands.functions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.resources.Identifier;

@FunctionalInterface
public interface CommandParser<Runtime> extends AutoCloseable {
   ContextChain<Runtime> parse(StringReader input) throws CommandSyntaxException;

   default void close() {
   }

   public interface Provider<Runtime> {
      CommandParser<Runtime> create(Identifier functionId);
   }
}
