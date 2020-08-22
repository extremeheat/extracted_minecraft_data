package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CommandFunction {
   private final CommandFunction.Entry[] entries;
   private final ResourceLocation id;

   public CommandFunction(ResourceLocation var1, CommandFunction.Entry[] var2) {
      this.id = var1;
      this.entries = var2;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public CommandFunction.Entry[] getEntries() {
      return this.entries;
   }

   public static CommandFunction fromLines(ResourceLocation var0, ServerFunctionManager var1, List var2) {
      ArrayList var3 = Lists.newArrayListWithCapacity(var2.size());

      for(int var4 = 0; var4 < var2.size(); ++var4) {
         int var5 = var4 + 1;
         String var6 = ((String)var2.get(var4)).trim();
         StringReader var7 = new StringReader(var6);
         if (var7.canRead() && var7.peek() != '#') {
            if (var7.peek() == '/') {
               var7.skip();
               if (var7.peek() == '/') {
                  throw new IllegalArgumentException("Unknown or invalid command '" + var6 + "' on line " + var5 + " (if you intended to make a comment, use '#' not '//')");
               }

               String var10 = var7.readUnquotedString();
               throw new IllegalArgumentException("Unknown or invalid command '" + var6 + "' on line " + var5 + " (did you mean '" + var10 + "'? Do not use a preceding forwards slash.)");
            }

            try {
               ParseResults var8 = var1.getServer().getCommands().getDispatcher().parse(var7, var1.getCompilationContext());
               if (var8.getReader().canRead()) {
                  throw Commands.getParseException(var8);
               }

               var3.add(new CommandFunction.CommandEntry(var8));
            } catch (CommandSyntaxException var9) {
               throw new IllegalArgumentException("Whilst parsing command on line " + var5 + ": " + var9.getMessage());
            }
         }
      }

      return new CommandFunction(var0, (CommandFunction.Entry[])var3.toArray(new CommandFunction.Entry[0]));
   }

   public static class CacheableFunction {
      public static final CommandFunction.CacheableFunction NONE = new CommandFunction.CacheableFunction((ResourceLocation)null);
      @Nullable
      private final ResourceLocation id;
      private boolean resolved;
      private Optional function = Optional.empty();

      public CacheableFunction(@Nullable ResourceLocation var1) {
         this.id = var1;
      }

      public CacheableFunction(CommandFunction var1) {
         this.resolved = true;
         this.id = null;
         this.function = Optional.of(var1);
      }

      public Optional get(ServerFunctionManager var1) {
         if (!this.resolved) {
            if (this.id != null) {
               this.function = var1.get(this.id);
            }

            this.resolved = true;
         }

         return this.function;
      }

      @Nullable
      public ResourceLocation getId() {
         return (ResourceLocation)this.function.map((var0) -> {
            return var0.id;
         }).orElse(this.id);
      }
   }

   public static class FunctionEntry implements CommandFunction.Entry {
      private final CommandFunction.CacheableFunction function;

      public FunctionEntry(CommandFunction var1) {
         this.function = new CommandFunction.CacheableFunction(var1);
      }

      public void execute(ServerFunctionManager var1, CommandSourceStack var2, ArrayDeque var3, int var4) {
         this.function.get(var1).ifPresent((var4x) -> {
            CommandFunction.Entry[] var5 = var4x.getEntries();
            int var6 = var4 - var3.size();
            int var7 = Math.min(var5.length, var6);

            for(int var8 = var7 - 1; var8 >= 0; --var8) {
               var3.addFirst(new ServerFunctionManager.QueuedCommand(var1, var2, var5[var8]));
            }

         });
      }

      public String toString() {
         return "function " + this.function.getId();
      }
   }

   public static class CommandEntry implements CommandFunction.Entry {
      private final ParseResults parse;

      public CommandEntry(ParseResults var1) {
         this.parse = var1;
      }

      public void execute(ServerFunctionManager var1, CommandSourceStack var2, ArrayDeque var3, int var4) throws CommandSyntaxException {
         var1.getDispatcher().execute(new ParseResults(this.parse.getContext().withSource(var2), this.parse.getReader(), this.parse.getExceptions()));
      }

      public String toString() {
         return this.parse.getReader().getString();
      }
   }

   public interface Entry {
      void execute(ServerFunctionManager var1, CommandSourceStack var2, ArrayDeque var3, int var4) throws CommandSyntaxException;
   }
}
