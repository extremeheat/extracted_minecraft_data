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
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.minecraft.commands.ExecutionCommandSource;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.UnboundEntryAction;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class MacroFunction<T extends ExecutionCommandSource<T>> implements CommandFunction<T> {
   private static final DecimalFormat DECIMAL_FORMAT;
   private static final int MAX_CACHE_ENTRIES = 8;
   private final List<String> parameters;
   private final Object2ObjectLinkedOpenHashMap<List<String>, InstantiatedFunction<T>> cache = new Object2ObjectLinkedOpenHashMap(8, 0.25F);
   private final Identifier id;
   private final List<Entry<T>> entries;

   public MacroFunction(final Identifier id, final List<Entry<T>> entries, final List<String> parameters) {
      super();
      this.id = id;
      this.entries = entries;
      this.parameters = parameters;
   }

   public Identifier id() {
      return this.id;
   }

   public InstantiatedFunction<T> instantiate(final @Nullable CompoundTag arguments, final CommandDispatcher<T> dispatcher) throws FunctionInstantiationException {
      if (arguments == null) {
         throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_arguments", Component.translationArg(this.id())));
      } else {
         List<String> parameterValues = new ArrayList(this.parameters.size());

         for(String argument : this.parameters) {
            Tag argumentValue = arguments.get(argument);
            if (argumentValue == null) {
               throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_argument", Component.translationArg(this.id()), argument));
            }

            parameterValues.add(stringify(argumentValue));
         }

         InstantiatedFunction<T> cachedFunction = (InstantiatedFunction)this.cache.getAndMoveToLast(parameterValues);
         if (cachedFunction != null) {
            return cachedFunction;
         } else {
            if (this.cache.size() >= 8) {
               this.cache.removeFirst();
            }

            InstantiatedFunction<T> function = this.substituteAndParse(this.parameters, parameterValues, dispatcher);
            this.cache.put(parameterValues, function);
            return function;
         }
      }
   }

   private static String stringify(final Tag tag) {
      Objects.requireNonNull(tag);
      Tag var1 = tag;
      byte var2 = 0;

      while(true) {
         String var31;
         //$FF: var2->value
         //0->net/minecraft/nbt/FloatTag
         //1->net/minecraft/nbt/DoubleTag
         //2->net/minecraft/nbt/ByteTag
         //3->net/minecraft/nbt/ShortTag
         //4->net/minecraft/nbt/LongTag
         //5->net/minecraft/nbt/StringTag
         switch (var1.typeSwitch<invokedynamic>(var1, var2)) {
            case 0:
               FloatTag var3 = (FloatTag)var1;
               FloatTag var40 = var3;

               try {
                  var41 = var40.value();
               } catch (Throwable var20) {
                  throw new MatchException(var20.toString(), var20);
               }

               float value = var41;
               if (true) {
                  var31 = DECIMAL_FORMAT.format((double)value);
                  return var31;
               }

               var2 = 1;
               break;
            case 1:
               DoubleTag value = (DoubleTag)var1;
               DoubleTag var38 = value;

               try {
                  var39 = var38.value();
               } catch (Throwable var24) {
                  throw new MatchException(var24.toString(), var24);
               }

               double value = var39;
               if (true) {
                  var31 = DECIMAL_FORMAT.format(value);
                  return var31;
               }

               var2 = 2;
               break;
            case 2:
               ByteTag value = (ByteTag)var1;
               ByteTag var36 = value;

               try {
                  var37 = var36.value();
               } catch (Throwable var23) {
                  throw new MatchException(var23.toString(), var23);
               }

               byte value = var37;
               if (true) {
                  var31 = String.valueOf(value);
                  return var31;
               }

               var2 = 3;
               break;
            case 3:
               ShortTag value = (ShortTag)var1;
               ShortTag var34 = value;

               try {
                  var35 = var34.value();
               } catch (Throwable var22) {
                  throw new MatchException(var22.toString(), var22);
               }

               short value = var35;
               if (true) {
                  var31 = String.valueOf(value);
                  return var31;
               }

               var2 = 4;
               break;
            case 4:
               LongTag value = (LongTag)var1;
               LongTag var32 = value;

               try {
                  var33 = var32.value();
               } catch (Throwable var21) {
                  throw new MatchException(var21.toString(), var21);
               }

               long value = var33;
               if (true) {
                  var31 = String.valueOf(value);
                  return var31;
               }

               var2 = 5;
               break;
            case 5:
               StringTag value = (StringTag)var1;
               StringTag var10000 = value;

               try {
                  var30 = var10000.value();
               } catch (Throwable var19) {
                  throw new MatchException(var19.toString(), var19);
               }

               String value = var30;
               var31 = value;
               return var31;
            default:
               var31 = tag.toString();
               return var31;
         }
      }
   }

   private static void lookupValues(final List<String> values, final IntList indicesToSelect, final List<String> selectedValuesOutput) {
      selectedValuesOutput.clear();
      indicesToSelect.forEach((index) -> selectedValuesOutput.add((String)values.get(index)));
   }

   private InstantiatedFunction<T> substituteAndParse(final List<String> keys, final List<String> values, final CommandDispatcher<T> dispatcher) throws FunctionInstantiationException {
      List<UnboundEntryAction<T>> newEntries = new ArrayList(this.entries.size());
      List<String> entryArguments = new ArrayList(values.size());

      for(Entry<T> entry : this.entries) {
         lookupValues(values, entry.parameters(), entryArguments);
         newEntries.add(entry.instantiate(entryArguments, dispatcher, this.id));
      }

      return new PlainTextFunction<T>(this.id().withPath((UnaryOperator)((id) -> id + "/" + keys.hashCode())), newEntries);
   }

   static {
      DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ROOT)), (format) -> format.setMaximumFractionDigits(15));
   }

   static class PlainTextEntry<T> implements Entry<T> {
      private final UnboundEntryAction<T> compiledAction;

      public PlainTextEntry(final UnboundEntryAction<T> compiledAction) {
         super();
         this.compiledAction = compiledAction;
      }

      public IntList parameters() {
         return IntLists.emptyList();
      }

      public UnboundEntryAction<T> instantiate(final List<String> substitutions, final CommandDispatcher<T> dispatcher, final Identifier functionId) {
         return this.compiledAction;
      }
   }

   static class MacroEntry<T extends ExecutionCommandSource<T>> implements Entry<T> {
      private final StringTemplate template;
      private final IntList parameters;
      private final T compilationContext;

      public MacroEntry(final StringTemplate template, final IntList parameters, final T compilationContext) {
         super();
         this.template = template;
         this.parameters = parameters;
         this.compilationContext = compilationContext;
      }

      public IntList parameters() {
         return this.parameters;
      }

      public UnboundEntryAction<T> instantiate(final List<String> substitutions, final CommandDispatcher<T> dispatcher, final Identifier functionId) throws FunctionInstantiationException {
         String command = this.template.substitute(substitutions);

         try {
            return CommandFunction.<T>parseCommand(dispatcher, this.compilationContext, new StringReader(command));
         } catch (CommandSyntaxException e) {
            throw new FunctionInstantiationException(Component.translatable("commands.function.error.parse", Component.translationArg(functionId), command, e.getMessage()));
         }
      }
   }

   interface Entry<T> {
      IntList parameters();

      UnboundEntryAction<T> instantiate(List<String> substitutions, CommandDispatcher<T> dispatcher, Identifier funtionId) throws FunctionInstantiationException;
   }
}
