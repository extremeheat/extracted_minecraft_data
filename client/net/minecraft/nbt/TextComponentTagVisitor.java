package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList(1, 2, 3, 4, 5, 6));
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_KEY;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_STRING;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE;
   private static final Pattern SIMPLE_VALUE;
   private static final String NAME_VALUE_SEPARATOR;
   private static final String ELEMENT_SEPARATOR;
   private final String indentation;
   private final int depth;
   private Component result;

   public TextComponentTagVisitor(String var1, int var2) {
      super();
      this.indentation = var1;
      this.depth = var2;
   }

   public Component visit(Tag var1) {
      var1.accept(this);
      return this.result;
   }

   public void visitString(StringTag var1) {
      String var2 = StringTag.quoteAndEscape(var1.getAsString());
      String var3 = var2.substring(0, 1);
      MutableComponent var4 = (new TextComponent(var2.substring(1, var2.length() - 1))).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      this.result = (new TextComponent(var3)).append(var4).append(var3);
   }

   public void visitByte(ByteTag var1) {
      MutableComponent var2 = (new TextComponent("b")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf(var1.getAsNumber()))).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitShort(ShortTag var1) {
      MutableComponent var2 = (new TextComponent("s")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf(var1.getAsNumber()))).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitInt(IntTag var1) {
      this.result = (new TextComponent(String.valueOf(var1.getAsNumber()))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitLong(LongTag var1) {
      MutableComponent var2 = (new TextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf(var1.getAsNumber()))).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitFloat(FloatTag var1) {
      MutableComponent var2 = (new TextComponent("f")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf(var1.getAsFloat()))).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitDouble(DoubleTag var1) {
      MutableComponent var2 = (new TextComponent("d")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = (new TextComponent(String.valueOf(var1.getAsDouble()))).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitByteArray(ByteArrayTag var1) {
      MutableComponent var2 = (new TextComponent("B")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = (new TextComponent("[")).append(var2).append(";");
      byte[] var4 = var1.getAsByteArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         MutableComponent var6 = (new TextComponent(String.valueOf(var4[var5]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         var3.append(" ").append((Component)var6).append((Component)var2);
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3;
   }

   public void visitIntArray(IntArrayTag var1) {
      MutableComponent var2 = (new TextComponent("I")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = (new TextComponent("[")).append(var2).append(";");
      int[] var4 = var1.getAsIntArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var3.append(" ").append((Component)(new TextComponent(String.valueOf(var4[var5]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3;
   }

   public void visitLongArray(LongArrayTag var1) {
      MutableComponent var2 = (new TextComponent("L")).withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = (new TextComponent("[")).append(var2).append(";");
      long[] var4 = var1.getAsLongArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         MutableComponent var6 = (new TextComponent(String.valueOf(var4[var5]))).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         var3.append(" ").append((Component)var6).append((Component)var2);
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3;
   }

   public void visitList(ListTag var1) {
      if (var1.isEmpty()) {
         this.result = new TextComponent("[]");
      } else if (INLINE_ELEMENT_TYPES.contains(var1.getElementType()) && var1.size() <= 8) {
         String var5 = ELEMENT_SEPARATOR + " ";
         TextComponent var6 = new TextComponent("[");

         for(int var7 = 0; var7 < var1.size(); ++var7) {
            if (var7 != 0) {
               var6.append(var5);
            }

            var6.append((new TextComponentTagVisitor(this.indentation, this.depth)).visit(var1.get(var7)));
         }

         var6.append("]");
         this.result = var6;
      } else {
         TextComponent var2 = new TextComponent("[");
         if (!this.indentation.isEmpty()) {
            var2.append("\n");
         }

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            TextComponent var4 = new TextComponent(Strings.repeat(this.indentation, this.depth + 1));
            var4.append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(var1.get(var3)));
            if (var3 != var1.size() - 1) {
               var4.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
            }

            var2.append((Component)var4);
         }

         if (!this.indentation.isEmpty()) {
            var2.append("\n").append(Strings.repeat(this.indentation, this.depth));
         }

         var2.append("]");
         this.result = var2;
      }
   }

   public void visitCompound(CompoundTag var1) {
      if (var1.isEmpty()) {
         this.result = new TextComponent("{}");
      } else {
         TextComponent var2 = new TextComponent("{");
         Object var3 = var1.getAllKeys();
         if (LOGGER.isDebugEnabled()) {
            ArrayList var4 = Lists.newArrayList(var1.getAllKeys());
            Collections.sort(var4);
            var3 = var4;
         }

         if (!this.indentation.isEmpty()) {
            var2.append("\n");
         }

         MutableComponent var6;
         for(Iterator var7 = ((Collection)var3).iterator(); var7.hasNext(); var2.append((Component)var6)) {
            String var5 = (String)var7.next();
            var6 = (new TextComponent(Strings.repeat(this.indentation, this.depth + 1))).append(handleEscapePretty(var5)).append(NAME_VALUE_SEPARATOR).append(" ").append((new TextComponentTagVisitor(this.indentation, this.depth + 1)).visit(var1.get(var5)));
            if (var7.hasNext()) {
               var6.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
            }
         }

         if (!this.indentation.isEmpty()) {
            var2.append("\n").append(Strings.repeat(this.indentation, this.depth));
         }

         var2.append("}");
         this.result = var2;
      }
   }

   protected static Component handleEscapePretty(String var0) {
      if (SIMPLE_VALUE.matcher(var0).matches()) {
         return (new TextComponent(var0)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         String var1 = StringTag.quoteAndEscape(var0);
         String var2 = var1.substring(0, 1);
         MutableComponent var3 = (new TextComponent(var1.substring(1, var1.length() - 1))).withStyle(SYNTAX_HIGHLIGHTING_KEY);
         return (new TextComponent(var2)).append(var3).append(var2);
      }
   }

   public void visitEnd(EndTag var1) {
      this.result = TextComponent.EMPTY;
   }

   static {
      SYNTAX_HIGHLIGHTING_KEY = ChatFormatting.AQUA;
      SYNTAX_HIGHLIGHTING_STRING = ChatFormatting.GREEN;
      SYNTAX_HIGHLIGHTING_NUMBER = ChatFormatting.GOLD;
      SYNTAX_HIGHLIGHTING_NUMBER_TYPE = ChatFormatting.RED;
      SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
      NAME_VALUE_SEPARATOR = String.valueOf(':');
      ELEMENT_SEPARATOR = String.valueOf(',');
   }
}
