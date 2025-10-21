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
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MacroFunction<T extends ExecutionCommandSource<T>> implements CommandFunction<T> {
   private static final DecimalFormat DECIMAL_FORMAT;
   private static final int MAX_CACHE_ENTRIES = 8;
   private final List<String> parameters;
   private final Object2ObjectLinkedOpenHashMap<List<String>, InstantiatedFunction<T>> cache = new Object2ObjectLinkedOpenHashMap(8, 0.25F);
   private final ResourceLocation id;
   private final List<Entry<T>> entries;

   public MacroFunction(ResourceLocation var1, List<Entry<T>> var2, List<String> var3) {
      super();
      this.id = var1;
      this.entries = var2;
      this.parameters = var3;
   }

   public ResourceLocation id() {
      return this.id;
   }

   public InstantiatedFunction<T> instantiate(@Nullable CompoundTag var1, CommandDispatcher<T> var2) throws FunctionInstantiationException {
      if (var1 == null) {
         throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_arguments", Component.translationArg(this.id())));
      } else {
         ArrayList var3 = new ArrayList(this.parameters.size());

         for(String var5 : this.parameters) {
            Tag var6 = var1.get(var5);
            if (var6 == null) {
               throw new FunctionInstantiationException(Component.translatable("commands.function.error.missing_argument", Component.translationArg(this.id()), var5));
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
      Objects.requireNonNull(var0);
      byte var2 = 0;
      String var30;
      //$FF: var2->value
      //0->net/minecraft/nbt/FloatTag
      //1->net/minecraft/nbt/DoubleTag
      //2->net/minecraft/nbt/ByteTag
      //3->net/minecraft/nbt/ShortTag
      //4->net/minecraft/nbt/LongTag
      //5->net/minecraft/nbt/StringTag
      switch (var0.typeSwitch<invokedynamic>(var0, var2)) {
         case 0:
            FloatTag var3 = (FloatTag)var0;
            FloatTag var39 = var3;

            try {
               var40 = var39.value();
            } catch (Throwable var23) {
               throw new MatchException(var23.toString(), var23);
            }

            float var24 = var40;
            var30 = DECIMAL_FORMAT.format((double)var24);
            break;
         case 1:
            DoubleTag var5 = (DoubleTag)var0;
            DoubleTag var37 = var5;

            try {
               var38 = var37.value();
            } catch (Throwable var22) {
               throw new MatchException(var22.toString(), var22);
            }

            double var25 = var38;
            var30 = DECIMAL_FORMAT.format(var25);
            break;
         case 2:
            ByteTag var8 = (ByteTag)var0;
            ByteTag var35 = var8;

            try {
               var36 = var35.value();
            } catch (Throwable var21) {
               throw new MatchException(var21.toString(), var21);
            }

            byte var26 = var36;
            var30 = String.valueOf(var26);
            break;
         case 3:
            ShortTag var10 = (ShortTag)var0;
            ShortTag var33 = var10;

            try {
               var34 = var33.value();
            } catch (Throwable var20) {
               throw new MatchException(var20.toString(), var20);
            }

            short var27 = var34;
            var30 = String.valueOf(var27);
            break;
         case 4:
            LongTag var12 = (LongTag)var0;
            LongTag var31 = var12;

            try {
               var32 = var31.value();
            } catch (Throwable var19) {
               throw new MatchException(var19.toString(), var19);
            }

            long var28 = var32;
            var30 = String.valueOf(var28);
            break;
         case 5:
            StringTag var15 = (StringTag)var0;
            StringTag var10000 = var15;

            try {
               var29 = var10000.value();
            } catch (Throwable var18) {
               throw new MatchException(var18.toString(), var18);
            }

            String var17 = var29;
            var30 = var17;
            break;
         default:
            var30 = var0.toString();
      }

      return var30;
   }

   private static void lookupValues(List<String> var0, IntList var1, List<String> var2) {
      var2.clear();
      var1.forEach((var2x) -> var2.add((String)var0.get(var2x)));
   }

   private InstantiatedFunction<T> substituteAndParse(List<String> var1, List<String> var2, CommandDispatcher<T> var3) throws FunctionInstantiationException {
      ArrayList var4 = new ArrayList(this.entries.size());
      ArrayList var5 = new ArrayList(var2.size());

      for(Entry var7 : this.entries) {
         lookupValues(var2, var7.parameters(), var5);
         var4.add(var7.instantiate(var5, var3, this.id));
      }

      return new PlainTextFunction<T>(this.id().withPath((UnaryOperator)((var1x) -> var1x + "/" + var1.hashCode())), var4);
   }

   static {
      DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ROOT)), (var0) -> var0.setMaximumFractionDigits(15));
   }

   static class PlainTextEntry<T> implements Entry<T> {
      private final UnboundEntryAction<T> compiledAction;

      public PlainTextEntry(UnboundEntryAction<T> var1) {
         super();
         this.compiledAction = var1;
      }

      public IntList parameters() {
         return IntLists.emptyList();
      }

      public UnboundEntryAction<T> instantiate(List<String> var1, CommandDispatcher<T> var2, ResourceLocation var3) {
         return this.compiledAction;
      }
   }

   static class MacroEntry<T extends ExecutionCommandSource<T>> implements Entry<T> {
      private final StringTemplate template;
      private final IntList parameters;
      private final T compilationContext;

      public MacroEntry(StringTemplate var1, IntList var2, T var3) {
         super();
         this.template = var1;
         this.parameters = var2;
         this.compilationContext = var3;
      }

      public IntList parameters() {
         return this.parameters;
      }

      public UnboundEntryAction<T> instantiate(List<String> var1, CommandDispatcher<T> var2, ResourceLocation var3) throws FunctionInstantiationException {
         String var4 = this.template.substitute(var1);

         try {
            return CommandFunction.<T>parseCommand(var2, this.compilationContext, new StringReader(var4));
         } catch (CommandSyntaxException var6) {
            throw new FunctionInstantiationException(Component.translatable("commands.function.error.parse", Component.translationArg(var3), var4, var6.getMessage()));
         }
      }
   }

   interface Entry<T> {
      IntList parameters();

      UnboundEntryAction<T> instantiate(List<String> var1, CommandDispatcher<T> var2, ResourceLocation var3) throws FunctionInstantiationException;
   }
}
