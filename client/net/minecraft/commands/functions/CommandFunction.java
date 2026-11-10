package net.minecraft.commands.functions;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public interface CommandFunction<Runtime> {
   Identifier id();

   InstantiatedFunction<Runtime> instantiate(@Nullable CompoundTag arguments) throws FunctionInstantiationException;

   private static boolean shouldConcatenateNextLine(final CharSequence line) {
      int length = line.length();
      return length > 0 && line.charAt(length - 1) == '\\';
   }

   static <Runtime extends ExecutionCommandSource<Runtime>> CommandFunction<Runtime> fromLines(final Identifier id, final CommandParser.Provider<Runtime> parserProvider, final List<String> lines) {
      FunctionBuilder<Runtime> functionBuilder = new FunctionBuilder<Runtime>();

      for(int i = 0; i < lines.size(); ++i) {
         int lineNumber = i + 1;
         String inputLine = ((String)lines.get(i)).trim();
         String line;
         if (shouldConcatenateNextLine(inputLine)) {
            StringBuilder builder = new StringBuilder(inputLine);

            do {
               ++i;
               if (i == lines.size()) {
                  throw new IllegalArgumentException("Line continuation at end of file");
               }

               builder.deleteCharAt(builder.length() - 1);
               String innerLine = ((String)lines.get(i)).trim();
               builder.append(innerLine);
               checkCommandLineLength(builder);
            } while(shouldConcatenateNextLine(builder));

            line = builder.toString();
         } else {
            line = inputLine;
         }

         checkCommandLineLength(line);
         StringReader input = new StringReader(line);
         if (input.canRead() && input.peek() != '#') {
            try (CommandParser<Runtime> parser = parserProvider.create(id)) {
               if (input.peek() == '/') {
                  input.skip();
                  if (input.peek() == '/') {
                     throw new IllegalArgumentException("Unknown or invalid command '" + line + "' on line " + lineNumber + " (if you intended to make a comment, use '#' not '//')");
                  }

                  String name = input.readUnquotedString();
                  throw new IllegalArgumentException("Unknown or invalid command '" + line + "' on line " + lineNumber + " (did you mean '" + name + "'? Do not use a preceding forwards slash.)");
               }

               if (input.peek() == '$') {
                  functionBuilder.addMacro(line.substring(1), lineNumber);
               } else {
                  try {
                     functionBuilder.addCommand(parseCommand(parser, input));
                  } catch (CommandSyntaxException e) {
                     throw new IllegalArgumentException("Whilst parsing command on line " + lineNumber + ": " + e.getMessage());
                  }
               }
            }
         }
      }

      return functionBuilder.build(id, parserProvider);
   }

   static void checkCommandLineLength(final CharSequence line) {
      if (line.length() > 2000000) {
         CharSequence truncated = line.subSequence(0, Math.min(512, 2000000));
         int var10002 = line.length();
         throw new IllegalStateException("Command too long: " + var10002 + " characters, contents: " + String.valueOf(truncated) + "...");
      }
   }

   static <Runtime extends ExecutionCommandSource<Runtime>> UnboundEntryAction<Runtime> parseCommand(final CommandParser<Runtime> parser, final StringReader input) throws CommandSyntaxException {
      ContextChain<Runtime> parsedChain = parser.parse(input);
      return new BuildContexts.Unbound<Runtime>(input.getString(), parsedChain);
   }
}
