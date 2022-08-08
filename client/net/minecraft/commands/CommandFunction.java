package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CommandFunction {
   private final Entry[] entries;
   final ResourceLocation id;

   public CommandFunction(ResourceLocation var1, Entry[] var2) {
      super();
      this.id = var1;
      this.entries = var2;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public Entry[] getEntries() {
      return this.entries;
   }

   public static CommandFunction fromLines(ResourceLocation var0, CommandDispatcher<CommandSourceStack> var1, CommandSourceStack var2, List<String> var3) {
      ArrayList var4 = Lists.newArrayListWithCapacity(var3.size());

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         int var6 = var5 + 1;
         String var7 = ((String)var3.get(var5)).trim();
         StringReader var8 = new StringReader(var7);
         if (var8.canRead() && var8.peek() != '#') {
            if (var8.peek() == '/') {
               var8.skip();
               if (var8.peek() == '/') {
                  throw new IllegalArgumentException("Unknown or invalid command '" + var7 + "' on line " + var6 + " (if you intended to make a comment, use '#' not '//')");
               }

               String var11 = var8.readUnquotedString();
               throw new IllegalArgumentException("Unknown or invalid command '" + var7 + "' on line " + var6 + " (did you mean '" + var11 + "'? Do not use a preceding forwards slash.)");
            }

            try {
               ParseResults var9 = var1.parse(var8, var2);
               if (var9.getReader().canRead()) {
                  throw Commands.getParseException(var9);
               }

               var4.add(new CommandEntry(var9));
            } catch (CommandSyntaxException var10) {
               throw new IllegalArgumentException("Whilst parsing command on line " + var6 + ": " + var10.getMessage());
            }
         }
      }

      return new CommandFunction(var0, (Entry[])var4.toArray(new Entry[0]));
   }

   @FunctionalInterface
   public interface Entry {
      void execute(ServerFunctionManager var1, CommandSourceStack var2, Deque<ServerFunctionManager.QueuedCommand> var3, int var4, int var5, @Nullable ServerFunctionManager.TraceCallbacks var6) throws CommandSyntaxException;
   }

   public static class CommandEntry implements Entry {
      private final ParseResults<CommandSourceStack> parse;

      public CommandEntry(ParseResults<CommandSourceStack> var1) {
         super();
         this.parse = var1;
      }

      public void execute(ServerFunctionManager var1, CommandSourceStack var2, Deque<ServerFunctionManager.QueuedCommand> var3, int var4, int var5, @Nullable ServerFunctionManager.TraceCallbacks var6) throws CommandSyntaxException {
         if (var6 != null) {
            String var7 = this.parse.getReader().getString();
            var6.onCommand(var5, var7);
            int var8 = this.execute(var1, var2);
            var6.onReturn(var5, var7, var8);
         } else {
            this.execute(var1, var2);
         }

      }

      private int execute(ServerFunctionManager var1, CommandSourceStack var2) throws CommandSyntaxException {
         return var1.getDispatcher().execute(new ParseResults(this.parse.getContext().withSource(var2), this.parse.getReader(), this.parse.getExceptions()));
      }

      public String toString() {
         return this.parse.getReader().getString();
      }
   }

   public static class CacheableFunction {
      public static final CacheableFunction NONE = new CacheableFunction((ResourceLocation)null);
      @Nullable
      private final ResourceLocation id;
      private boolean resolved;
      private Optional<CommandFunction> function = Optional.empty();

      public CacheableFunction(@Nullable ResourceLocation var1) {
         super();
         this.id = var1;
      }

      public CacheableFunction(CommandFunction var1) {
         super();
         this.resolved = true;
         this.id = null;
         this.function = Optional.of(var1);
      }

      public Optional<CommandFunction> get(ServerFunctionManager var1) {
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

   public static class FunctionEntry implements Entry {
      private final CacheableFunction function;

      public FunctionEntry(CommandFunction var1) {
         super();
         this.function = new CacheableFunction(var1);
      }

      public void execute(ServerFunctionManager var1, CommandSourceStack var2, Deque<ServerFunctionManager.QueuedCommand> var3, int var4, int var5, @Nullable ServerFunctionManager.TraceCallbacks var6) {
         Util.ifElse(this.function.get(var1), (var5x) -> {
            Entry[] var6x = var5x.getEntries();
            if (var6 != null) {
               var6.onCall(var5, var5x.getId(), var6x.length);
            }

            int var7 = var4 - var3.size();
            int var8 = Math.min(var6x.length, var7);

            for(int var9 = var8 - 1; var9 >= 0; --var9) {
               var3.addFirst(new ServerFunctionManager.QueuedCommand(var2, var5 + 1, var6x[var9]));
            }

         }, () -> {
            if (var6 != null) {
               var6.onCall(var5, this.function.getId(), -1);
            }

         });
      }

      public String toString() {
         return "function " + this.function.getId();
      }
   }
}
