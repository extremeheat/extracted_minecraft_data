package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Pattern;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int INLINE_LIST_THRESHOLD = 8;
   private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList((byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6));
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_KEY = ChatFormatting.AQUA;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_STRING = ChatFormatting.GREEN;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER = ChatFormatting.GOLD;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE = ChatFormatting.RED;
   private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
   private static final String NAME_VALUE_SEPARATOR = String.valueOf(':');
   private static final String ELEMENT_SEPARATOR = String.valueOf(',');
   private static final String LIST_OPEN = "[";
   private static final String LIST_CLOSE = "]";
   private static final String LIST_TYPE_SEPARATOR = ";";
   private static final String ELEMENT_SPACING = " ";
   private static final String STRUCT_OPEN = "{";
   private static final String STRUCT_CLOSE = "}";
   private static final String NEWLINE = "\n";
   private final String indentation;
   private final int depth;
   private Component result = CommonComponents.EMPTY;

   public TextComponentTagVisitor(String var1, int var2) {
      super();
      this.indentation = var1;
      this.depth = var2;
   }

   public Component visit(Tag var1) {
      var1.accept(this);
      return this.result;
   }

   @Override
   public void visitString(StringTag var1) {
      String var2 = StringTag.quoteAndEscape(var1.getAsString());
      String var3 = var2.substring(0, 1);
      MutableComponent var4 = Component.literal(var2.substring(1, var2.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      this.result = Component.literal(var3).append(var4).append(var3);
   }

   @Override
   public void visitByte(ByteTag var1) {
      MutableComponent var2 = Component.literal("b").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsNumber())).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   @Override
   public void visitShort(ShortTag var1) {
      MutableComponent var2 = Component.literal("s").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsNumber())).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   @Override
   public void visitInt(IntTag var1) {
      this.result = Component.literal(String.valueOf(var1.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   @Override
   public void visitLong(LongTag var1) {
      MutableComponent var2 = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsNumber())).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   @Override
   public void visitFloat(FloatTag var1) {
      MutableComponent var2 = Component.literal("f").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsFloat())).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   @Override
   public void visitDouble(DoubleTag var1) {
      MutableComponent var2 = Component.literal("d").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsDouble())).append(var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   @Override
   public void visitByteArray(ByteArrayTag var1) {
      MutableComponent var2 = Component.literal("B").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = Component.literal("[").append(var2).append(";");
      byte[] var4 = var1.getAsByteArray();

      for (int var5 = 0; var5 < var4.length; var5++) {
         MutableComponent var6 = Component.literal(String.valueOf(var4[var5])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         var3.append(" ").append(var6).append(var2);
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3;
   }

   @Override
   public void visitIntArray(IntArrayTag var1) {
      MutableComponent var2 = Component.literal("I").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = Component.literal("[").append(var2).append(";");
      int[] var4 = var1.getAsIntArray();

      for (int var5 = 0; var5 < var4.length; var5++) {
         var3.append(" ").append(Component.literal(String.valueOf(var4[var5])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3;
   }

   @Override
   public void visitLongArray(LongArrayTag var1) {
      MutableComponent var2 = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = Component.literal("[").append(var2).append(";");
      long[] var4 = var1.getAsLongArray();

      for (int var5 = 0; var5 < var4.length; var5++) {
         MutableComponent var6 = Component.literal(String.valueOf(var4[var5])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         var3.append(" ").append(var6).append(var2);
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3;
   }

   @Override
   public void visitList(ListTag var1) {
      if (var1.isEmpty()) {
         this.result = Component.literal("[]");
      } else if (INLINE_ELEMENT_TYPES.contains(var1.getElementType()) && var1.size() <= 8) {
         String var5 = ELEMENT_SEPARATOR + " ";
         MutableComponent var6 = Component.literal("[");

         for (int var7 = 0; var7 < var1.size(); var7++) {
            if (var7 != 0) {
               var6.append(var5);
            }

            var6.append(new TextComponentTagVisitor(this.indentation, this.depth).visit(var1.get(var7)));
         }

         var6.append("]");
         this.result = var6;
      } else {
         MutableComponent var2 = Component.literal("[");
         if (!this.indentation.isEmpty()) {
            var2.append("\n");
         }

         for (int var3 = 0; var3 < var1.size(); var3++) {
            MutableComponent var4 = Component.literal(Strings.repeat(this.indentation, this.depth + 1));
            var4.append(new TextComponentTagVisitor(this.indentation, this.depth + 1).visit(var1.get(var3)));
            if (var3 != var1.size() - 1) {
               var4.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
            }

            var2.append(var4);
         }

         if (!this.indentation.isEmpty()) {
            var2.append("\n").append(Strings.repeat(this.indentation, this.depth));
         }

         var2.append("]");
         this.result = var2;
      }
   }

   @Override
   public void visitCompound(CompoundTag var1) {
      if (var1.isEmpty()) {
         this.result = Component.literal("{}");
      } else {
         MutableComponent var2 = Component.literal("{");
         Object var3 = var1.getAllKeys();
         if (LOGGER.isDebugEnabled()) {
            ArrayList var4 = Lists.newArrayList(var1.getAllKeys());
            Collections.sort(var4);
            var3 = var4;
         }

         if (!this.indentation.isEmpty()) {
            var2.append("\n");
         }

         Iterator var7 = var3.iterator();

         while (var7.hasNext()) {
            String var5 = (String)var7.next();
            MutableComponent var6 = Component.literal(Strings.repeat(this.indentation, this.depth + 1))
               .append(handleEscapePretty(var5))
               .append(NAME_VALUE_SEPARATOR)
               .append(" ")
               .append(new TextComponentTagVisitor(this.indentation, this.depth + 1).visit(var1.get(var5)));
            if (var7.hasNext()) {
               var6.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
            }

            var2.append(var6);
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
         return Component.literal(var0).withStyle(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         String var1 = StringTag.quoteAndEscape(var0);
         String var2 = var1.substring(0, 1);
         MutableComponent var3 = Component.literal(var1.substring(1, var1.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
         return Component.literal(var2).append(var3).append(var2);
      }
   }

   @Override
   public void visitEnd(EndTag var1) {
      this.result = CommonComponents.EMPTY;
   }
}
