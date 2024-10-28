package net.minecraft.commands.functions;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.Commands;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.commands.execution.tasks.BuildContexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface CommandFunction<T> {
   ResourceLocation id();

   InstantiatedFunction<T> instantiate(@Nullable CompoundTag var1, CommandDispatcher<T> var2) throws FunctionInstantiationException;

   private static boolean shouldConcatenateNextLine(CharSequence var0) {
      int var1 = var0.length();
      return var1 > 0 && var0.charAt(var1 - 1) == '\\';
   }

   static <T extends ExecutionCommandSource<T>> CommandFunction<T> fromLines(ResourceLocation var0, CommandDispatcher<T> var1, T var2, List<String> var3) {
      FunctionBuilder var4 = new FunctionBuilder();

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         int var6 = var5 + 1;
         String var7 = ((String)var3.get(var5)).trim();
         String var8;
         String var10;
         if (shouldConcatenateNextLine(var7)) {
            StringBuilder var9 = new StringBuilder(var7);

            while(true) {
               ++var5;
               if (var5 == var3.size()) {
                  throw new IllegalArgumentException("Line continuation at end of file");
               }

               var9.deleteCharAt(var9.length() - 1);
               var10 = ((String)var3.get(var5)).trim();
               var9.append(var10);
               checkCommandLineLength(var9);
               if (!shouldConcatenateNextLine(var9)) {
                  var8 = var9.toString();
                  break;
               }
            }
         } else {
            var8 = var7;
         }

         checkCommandLineLength(var8);
         StringReader var12 = new StringReader(var8);
         if (var12.canRead() && var12.peek() != '#') {
            if (var12.peek() == '/') {
               var12.skip();
               if (var12.peek() == '/') {
                  throw new IllegalArgumentException("Unknown or invalid command '" + var8 + "' on line " + var6 + " (if you intended to make a comment, use '#' not '//')");
               }

               var10 = var12.readUnquotedString();
               throw new IllegalArgumentException("Unknown or invalid command '" + var8 + "' on line " + var6 + " (did you mean '" + var10 + "'? Do not use a preceding forwards slash.)");
            }

            if (var12.peek() == '$') {
               var4.addMacro(var8.substring(1), var6, var2);
            } else {
               try {
                  var4.addCommand(parseCommand(var1, var2, var12));
               } catch (CommandSyntaxException var11) {
                  throw new IllegalArgumentException("Whilst parsing command on line " + var6 + ": " + var11.getMessage());
               }
            }
         }
      }

      return var4.build(var0);
   }

   static void checkCommandLineLength(CharSequence var0) {
      if (var0.length() > 2000000) {
         CharSequence var1 = var0.subSequence(0, Math.min(512, 2000000));
         int var10002 = var0.length();
         throw new IllegalStateException("Command too long: " + var10002 + " characters, contents: " + String.valueOf(var1) + "...");
      }
   }

   static <T extends ExecutionCommandSource<T>> UnboundEntryAction<T> parseCommand(CommandDispatcher<T> var0, T var1, StringReader var2) throws CommandSyntaxException {
      ParseResults var3 = var0.parse(var2, var1);
      Commands.validateParseResults(var3);
      Optional var4 = ContextChain.tryFlatten(var3.getContext().build(var2.getString()));
      if (var4.isEmpty()) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(var3.getReader());
      } else {
         return new BuildContexts.Unbound(var2.getString(), (ContextChain)var4.get());
      }
   }
}
