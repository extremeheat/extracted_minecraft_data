package net.minecraft.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.util.ResourceLocation;

public class FunctionObject {
   private final FunctionObject.Entry[] field_193530_b;
   private final ResourceLocation field_197002_b;

   public FunctionObject(ResourceLocation var1, FunctionObject.Entry[] var2) {
      super();
      this.field_197002_b = var1;
      this.field_193530_b = var2;
   }

   public ResourceLocation func_197001_a() {
      return this.field_197002_b;
   }

   public FunctionObject.Entry[] func_193528_a() {
      return this.field_193530_b;
   }

   public static FunctionObject func_197000_a(ResourceLocation var0, FunctionManager var1, List<String> var2) {
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
               ParseResults var8 = var1.func_195450_a().func_195571_aL().func_197054_a().parse(var7, var1.func_195448_f());
               if (var8.getReader().canRead()) {
                  if (var8.getExceptions().size() == 1) {
                     throw (CommandSyntaxException)var8.getExceptions().values().iterator().next();
                  }

                  if (var8.getContext().getRange().isEmpty()) {
                     throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(var8.getReader());
                  }

                  throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(var8.getReader());
               }

               var3.add(new FunctionObject.CommandEntry(var8));
            } catch (CommandSyntaxException var9) {
               throw new IllegalArgumentException("Whilst parsing command on line " + var5 + ": " + var9.getMessage());
            }
         }
      }

      return new FunctionObject(var0, (FunctionObject.Entry[])var3.toArray(new FunctionObject.Entry[0]));
   }

   public static class CacheableFunction {
      public static final FunctionObject.CacheableFunction field_193519_a = new FunctionObject.CacheableFunction((ResourceLocation)null);
      @Nullable
      private final ResourceLocation field_193520_b;
      private boolean field_193521_c;
      private FunctionObject field_193522_d;

      public CacheableFunction(@Nullable ResourceLocation var1) {
         super();
         this.field_193520_b = var1;
      }

      public CacheableFunction(FunctionObject var1) {
         super();
         this.field_193520_b = null;
         this.field_193522_d = var1;
      }

      @Nullable
      public FunctionObject func_193518_a(FunctionManager var1) {
         if (!this.field_193521_c) {
            if (this.field_193520_b != null) {
               this.field_193522_d = var1.func_193058_a(this.field_193520_b);
            }

            this.field_193521_c = true;
         }

         return this.field_193522_d;
      }

      @Nullable
      public ResourceLocation func_200376_a() {
         return this.field_193522_d != null ? this.field_193522_d.field_197002_b : this.field_193520_b;
      }
   }

   public static class FunctionEntry implements FunctionObject.Entry {
      private final FunctionObject.CacheableFunction field_193524_a;

      public FunctionEntry(FunctionObject var1) {
         super();
         this.field_193524_a = new FunctionObject.CacheableFunction(var1);
      }

      public void func_196998_a(FunctionManager var1, CommandSource var2, ArrayDeque<FunctionManager.QueuedCommand> var3, int var4) {
         FunctionObject var5 = this.field_193524_a.func_193518_a(var1);
         if (var5 != null) {
            FunctionObject.Entry[] var6 = var5.func_193528_a();
            int var7 = var4 - var3.size();
            int var8 = Math.min(var6.length, var7);

            for(int var9 = var8 - 1; var9 >= 0; --var9) {
               var3.addFirst(new FunctionManager.QueuedCommand(var1, var2, var6[var9]));
            }
         }

      }

      public String toString() {
         return "function " + this.field_193524_a.func_200376_a();
      }
   }

   public static class CommandEntry implements FunctionObject.Entry {
      private final ParseResults<CommandSource> field_196999_a;

      public CommandEntry(ParseResults<CommandSource> var1) {
         super();
         this.field_196999_a = var1;
      }

      public void func_196998_a(FunctionManager var1, CommandSource var2, ArrayDeque<FunctionManager.QueuedCommand> var3, int var4) throws CommandSyntaxException {
         var1.func_195446_d().execute(new ParseResults(this.field_196999_a.getContext().withSource(var2), this.field_196999_a.getStartIndex(), this.field_196999_a.getReader(), this.field_196999_a.getExceptions()));
      }

      public String toString() {
         return this.field_196999_a.getReader().getString();
      }
   }

   public interface Entry {
      void func_196998_a(FunctionManager var1, CommandSource var2, ArrayDeque<FunctionManager.QueuedCommand> var3, int var4) throws CommandSyntaxException;
   }
}
