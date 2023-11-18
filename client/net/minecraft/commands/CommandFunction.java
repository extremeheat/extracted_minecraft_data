package net.minecraft.commands;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CommandFunction {
   private final CommandFunction.Entry[] entries;
   final ResourceLocation id;

   public CommandFunction(ResourceLocation var1, CommandFunction.Entry[] var2) {
      super();
      this.id = var1;
      this.entries = var2;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public CommandFunction.Entry[] getEntries() {
      return this.entries;
   }

   public CommandFunction instantiate(@Nullable CompoundTag var1, CommandDispatcher<CommandSourceStack> var2, CommandSourceStack var3) throws FunctionInstantiationException {
      return this;
   }

   private static boolean shouldConcatenateNextLine(CharSequence var0) {
      int var1 = var0.length();
      return var1 > 0 && var0.charAt(var1 - 1) == '\\';
   }

   public static CommandFunction fromLines(ResourceLocation var0, CommandDispatcher<CommandSourceStack> var1, CommandSourceStack var2, List<String> var3) {
      ArrayList var4 = new ArrayList(var3.size());
      ObjectArraySet var5 = new ObjectArraySet();

      for(int var6 = 0; var6 < var3.size(); ++var6) {
         int var7 = var6 + 1;
         String var8 = ((String)var3.get(var6)).trim();
         String var9;
         if (shouldConcatenateNextLine(var8)) {
            StringBuilder var10 = new StringBuilder(var8);

            do {
               if (++var6 == var3.size()) {
                  throw new IllegalArgumentException("Line continuation at end of file");
               }

               var10.deleteCharAt(var10.length() - 1);
               String var11 = ((String)var3.get(var6)).trim();
               var10.append(var11);
            } while(shouldConcatenateNextLine(var10));

            var9 = var10.toString();
         } else {
            var9 = var8;
         }

         StringReader var13 = new StringReader(var9);
         if (var13.canRead() && var13.peek() != '#') {
            if (var13.peek() == '/') {
               var13.skip();
               if (var13.peek() == '/') {
                  throw new IllegalArgumentException(
                     "Unknown or invalid command '" + var9 + "' on line " + var7 + " (if you intended to make a comment, use '#' not '//')"
                  );
               }

               String var16 = var13.readUnquotedString();
               throw new IllegalArgumentException(
                  "Unknown or invalid command '" + var9 + "' on line " + var7 + " (did you mean '" + var16 + "'? Do not use a preceding forwards slash.)"
               );
            }

            if (var13.peek() == '$') {
               CommandFunction.MacroEntry var14 = decomposeMacro(var9.substring(1), var7);
               var4.add(var14);
               var5.addAll(var14.parameters());
            } else {
               try {
                  ParseResults var15 = var1.parse(var13, var2);
                  if (var15.getReader().canRead()) {
                     throw Commands.getParseException(var15);
                  }

                  var4.add(new CommandFunction.CommandEntry(var15));
               } catch (CommandSyntaxException var12) {
                  throw new IllegalArgumentException("Whilst parsing command on line " + var7 + ": " + var12.getMessage());
               }
            }
         }
      }

      return (CommandFunction)(var5.isEmpty()
         ? new CommandFunction(var0, var4.toArray(var0x -> new CommandFunction.Entry[var0x]))
         : new CommandFunction.CommandMacro(var0, var4.toArray(var0x -> new CommandFunction.Entry[var0x]), List.copyOf(var5)));
   }

   @VisibleForTesting
   public static CommandFunction.MacroEntry decomposeMacro(String var0, int var1) {
      Builder var2 = ImmutableList.builder();
      Builder var3 = ImmutableList.builder();
      int var4 = var0.length();
      int var5 = 0;
      int var6 = var0.indexOf(36);

      while(var6 != -1) {
         if (var6 != var4 - 1 && var0.charAt(var6 + 1) == '(') {
            var2.add(var0.substring(var5, var6));
            int var7 = var0.indexOf(41, var6 + 1);
            if (var7 == -1) {
               throw new IllegalArgumentException("Unterminated macro variable in macro '" + var0 + "' on line " + var1);
            }

            String var8 = var0.substring(var6 + 2, var7);
            if (!isValidVariableName(var8)) {
               throw new IllegalArgumentException("Invalid macro variable name '" + var8 + "' on line " + var1);
            }

            var3.add(var8);
            var5 = var7 + 1;
            var6 = var0.indexOf(36, var5);
         } else {
            var6 = var0.indexOf(36, var6 + 1);
         }
      }

      if (var5 == 0) {
         throw new IllegalArgumentException("Macro without variables on line " + var1);
      } else {
         if (var5 != var4) {
            var2.add(var0.substring(var5));
         }

         return new CommandFunction.MacroEntry(var2.build(), var3.build());
      }
   }

   private static boolean isValidVariableName(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         char var2 = var0.charAt(var1);
         if (!Character.isLetterOrDigit(var2) && var2 != '_') {
            return false;
         }
      }

      return true;
   }

   public static class CacheableFunction {
      public static final CommandFunction.CacheableFunction NONE = new CommandFunction.CacheableFunction((ResourceLocation)null);
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
         return this.function.<ResourceLocation>map(var0 -> var0.id).orElse(this.id);
      }
   }

   public static class CommandEntry implements CommandFunction.Entry {
      private final ParseResults<CommandSourceStack> parse;

      public CommandEntry(ParseResults<CommandSourceStack> var1) {
         super();
         this.parse = var1;
      }

      @Override
      public void execute(
         ServerFunctionManager var1,
         CommandSourceStack var2,
         Deque<ServerFunctionManager.QueuedCommand> var3,
         int var4,
         int var5,
         @Nullable ServerFunctionManager.TraceCallbacks var6
      ) throws CommandSyntaxException {
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
         return var1.getDispatcher().execute(Commands.mapSource(this.parse, var1x -> var2));
      }

      @Override
      public String toString() {
         return this.parse.getReader().getString();
      }
   }

   static class CommandMacro extends CommandFunction {
      private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#");
      private final List<String> parameters;
      private static final int MAX_CACHE_ENTRIES = 8;
      private final Object2ObjectLinkedOpenHashMap<List<String>, CommandFunction> cache = new Object2ObjectLinkedOpenHashMap(8, 0.25F);

      public CommandMacro(ResourceLocation var1, CommandFunction.Entry[] var2, List<String> var3) {
         super(var1, var2);
         this.parameters = var3;
      }

      @Override
      public CommandFunction instantiate(@Nullable CompoundTag var1, CommandDispatcher<CommandSourceStack> var2, CommandSourceStack var3) throws FunctionInstantiationException {
         if (var1 == null) {
            throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_arguments", this.getId()));
         } else {
            ArrayList var4 = new ArrayList(this.parameters.size());

            for(String var6 : this.parameters) {
               if (!var1.contains(var6)) {
                  throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_argument", this.getId(), var6));
               }

               var4.add(stringify(var1.get(var6)));
            }

            CommandFunction var7 = (CommandFunction)this.cache.getAndMoveToLast(var4);
            if (var7 != null) {
               return var7;
            } else {
               if (this.cache.size() >= 8) {
                  this.cache.removeFirst();
               }

               CommandFunction var8 = this.substituteAndParse(var4, var2, var3);
               if (var8 != null) {
                  this.cache.put(var4, var8);
               }

               return var8;
            }
         }
      }

      // $QF: Could not properly define all variable types!
      // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
      private static String stringify(Tag var0) {
         if (var0 instanceof FloatTag var1) {
            return DECIMAL_FORMAT.format((double)var1.getAsFloat());
         } else if (var0 instanceof DoubleTag var2) {
            return DECIMAL_FORMAT.format(var2.getAsDouble());
         } else if (var0 instanceof ByteTag var3) {
            return String.valueOf(var3.getAsByte());
         } else if (var0 instanceof ShortTag var4) {
            return String.valueOf(var4.getAsShort());
         } else {
            return var0 instanceof LongTag var5 ? String.valueOf(var5.getAsLong()) : var0.getAsString();
         }
      }

      private CommandFunction substituteAndParse(List<String> var1, CommandDispatcher<CommandSourceStack> var2, CommandSourceStack var3) throws FunctionInstantiationException {
         CommandFunction.Entry[] var4 = this.getEntries();
         CommandFunction.Entry[] var5 = new CommandFunction.Entry[var4.length];

         for(int var6 = 0; var6 < var4.length; ++var6) {
            CommandFunction.Entry var7 = var4[var6];
            if (!(var7 instanceof CommandFunction.MacroEntry)) {
               var5[var6] = var7;
            } else {
               CommandFunction.MacroEntry var8 = (CommandFunction.MacroEntry)var7;
               List var9 = var8.parameters();
               ArrayList var10 = new ArrayList(var9.size());

               for(String var12 : var9) {
                  var10.add((String)var1.get(this.parameters.indexOf(var12)));
               }

               String var15 = var8.substitute(var10);

               try {
                  ParseResults var16 = var2.parse(var15, var3);
                  if (var16.getReader().canRead()) {
                     throw Commands.getParseException(var16);
                  }

                  var5[var6] = new CommandFunction.CommandEntry(var16);
               } catch (CommandSyntaxException var13) {
                  throw new FunctionInstantiationException(Component.translatable("commands.function.error.parse", this.getId(), var15, var13.getMessage()));
               }
            }
         }

         ResourceLocation var14 = this.getId();
         return new CommandFunction(new ResourceLocation(var14.getNamespace(), var14.getPath() + "/" + var1.hashCode()), var5);
      }

      static {
         DECIMAL_FORMAT.setMaximumFractionDigits(15);
         DECIMAL_FORMAT.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
      }
   }

   @FunctionalInterface
   public interface Entry {
      void execute(
         ServerFunctionManager var1,
         CommandSourceStack var2,
         Deque<ServerFunctionManager.QueuedCommand> var3,
         int var4,
         int var5,
         @Nullable ServerFunctionManager.TraceCallbacks var6
      ) throws CommandSyntaxException;
   }

   public static class FunctionEntry implements CommandFunction.Entry {
      private final CommandFunction.CacheableFunction function;

      public FunctionEntry(CommandFunction var1) {
         super();
         this.function = new CommandFunction.CacheableFunction(var1);
      }

      @Override
      public void execute(
         ServerFunctionManager var1,
         CommandSourceStack var2,
         Deque<ServerFunctionManager.QueuedCommand> var3,
         int var4,
         int var5,
         @Nullable ServerFunctionManager.TraceCallbacks var6
      ) {
         Util.ifElse(this.function.get(var1), var5x -> {
            CommandFunction.Entry[] var6x = var5x.getEntries();
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

      @Override
      public String toString() {
         return "function " + this.function.getId();
      }
   }

   public static class MacroEntry implements CommandFunction.Entry {
      private final List<String> segments;
      private final List<String> parameters;

      public MacroEntry(List<String> var1, List<String> var2) {
         super();
         this.segments = var1;
         this.parameters = var2;
      }

      public List<String> parameters() {
         return this.parameters;
      }

      public String substitute(List<String> var1) {
         StringBuilder var2 = new StringBuilder();

         for(int var3 = 0; var3 < this.parameters.size(); ++var3) {
            var2.append(this.segments.get(var3)).append((String)var1.get(var3));
         }

         if (this.segments.size() > this.parameters.size()) {
            var2.append(this.segments.get(this.segments.size() - 1));
         }

         return var2.toString();
      }

      @Override
      public void execute(
         ServerFunctionManager var1,
         CommandSourceStack var2,
         Deque<ServerFunctionManager.QueuedCommand> var3,
         int var4,
         int var5,
         @Nullable ServerFunctionManager.TraceCallbacks var6
      ) throws CommandSyntaxException {
         throw new IllegalStateException("Tried to execute an uninstantiated macro");
      }
   }
}
