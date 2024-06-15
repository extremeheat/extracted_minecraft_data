package net.minecraft.commands.functions;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MacroFunction<T extends ExecutionCommandSource<T>> implements CommandFunction<T> {
   private static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("#"), var0 -> {
      var0.setMaximumFractionDigits(15);
      var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
   });
   private static final int MAX_CACHE_ENTRIES = 8;
   private final List<String> parameters;
   private final Object2ObjectLinkedOpenHashMap<List<String>, InstantiatedFunction<T>> cache = new Object2ObjectLinkedOpenHashMap(8, 0.25F);
   private final ResourceLocation id;
   private final List<MacroFunction.Entry<T>> entries;

   public MacroFunction(ResourceLocation var1, List<MacroFunction.Entry<T>> var2, List<String> var3) {
      super();
      this.id = var1;
      this.entries = var2;
      this.parameters = var3;
   }

   @Override
   public ResourceLocation id() {
      return this.id;
   }

   @Override
   public InstantiatedFunction<T> instantiate(@Nullable CompoundTag var1, CommandDispatcher<T> var2) throws FunctionInstantiationException {
      if (var1 == null) {
         throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_arguments", Component.translationArg(this.id())));
      } else {
         ArrayList var3 = new ArrayList(this.parameters.size());

         for (String var5 : this.parameters) {
            Tag var6 = var1.get(var5);
            if (var6 == null) {
               throw new FunctionInstantiationException(
                  Component.translatable("commands.function.error.missing_argument", Component.translationArg(this.id()), var5)
               );
            }

            var3.add(stringify(var6));
         }

         InstantiatedFunction var7 = (InstantiatedFunction)this.cache.getAndMoveToLast(var3);
         if (var7 != null) {
            return var7;
         } else {
            if (this.cache.size() >= 8) {
               this.cache.removeFirst();
            }

            InstantiatedFunction var8 = this.substituteAndParse(this.parameters, var3, var2);
            this.cache.put(var3, var8);
            return var8;
         }
      }
   }

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

   private static void lookupValues(List<String> var0, IntList var1, List<String> var2) {
      var2.clear();
      var1.forEach(var2x -> var2.add((String)var0.get(var2x)));
   }

   private InstantiatedFunction<T> substituteAndParse(List<String> var1, List<String> var2, CommandDispatcher<T> var3) throws FunctionInstantiationException {
      ArrayList var4 = new ArrayList(this.entries.size());
      ArrayList var5 = new ArrayList(var2.size());

      for (MacroFunction.Entry var7 : this.entries) {
         lookupValues(var2, var7.parameters(), var5);
         var4.add(var7.instantiate(var5, var3, this.id));
      }

      return new PlainTextFunction<>(this.id().withPath(var1x -> var1x + "/" + var1.hashCode()), var4);
   }

   interface Entry<T> {
      IntList parameters();

      UnboundEntryAction<T> instantiate(List<String> var1, CommandDispatcher<T> var2, ResourceLocation var3) throws FunctionInstantiationException;
   }

   static class MacroEntry<T extends ExecutionCommandSource<T>> implements MacroFunction.Entry<T> {
      private final StringTemplate template;
      private final IntList parameters;
      private final T compilationContext;

      public MacroEntry(StringTemplate var1, IntList var2, T var3) {
         super();
         this.template = var1;
         this.parameters = var2;
         this.compilationContext = (T)var3;
      }

      @Override
      public IntList parameters() {
         return this.parameters;
      }

      @Override
      public UnboundEntryAction<T> instantiate(List<String> var1, CommandDispatcher<T> var2, ResourceLocation var3) throws FunctionInstantiationException {
         String var4 = this.template.substitute(var1);

         try {
            return CommandFunction.parseCommand(var2, this.compilationContext, new StringReader(var4));
         } catch (CommandSyntaxException var6) {
            throw new FunctionInstantiationException(
               Component.translatable("commands.function.error.parse", Component.translationArg(var3), var4, var6.getMessage())
            );
         }
      }
   }

   static class PlainTextEntry<T> implements MacroFunction.Entry<T> {
      private final UnboundEntryAction<T> compiledAction;

      public PlainTextEntry(UnboundEntryAction<T> var1) {
         super();
         this.compiledAction = var1;
      }

      @Override
      public IntList parameters() {
         return IntLists.emptyList();
      }

      @Override
      public UnboundEntryAction<T> instantiate(List<String> var1, CommandDispatcher<T> var2, ResourceLocation var3) {
         return this.compiledAction;
      }
   }
}
