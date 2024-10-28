package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int INLINE_LIST_THRESHOLD = 8;
   private static final int MAX_DEPTH = 64;
   private static final int MAX_LENGTH = 128;
   private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList(1, 2, 3, 4, 5, 6));
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_KEY;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_STRING;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE;
   private static final Pattern SIMPLE_VALUE;
   private static final String LIST_OPEN = "[";
   private static final String LIST_CLOSE = "]";
   private static final String LIST_TYPE_SEPARATOR = ";";
   private static final String ELEMENT_SPACING = " ";
   private static final String STRUCT_OPEN = "{";
   private static final String STRUCT_CLOSE = "}";
   private static final String NEWLINE = "\n";
   private static final String NAME_VALUE_SEPARATOR = ": ";
   private static final String ELEMENT_SEPARATOR;
   private static final String WRAPPED_ELEMENT_SEPARATOR;
   private static final String SPACED_ELEMENT_SEPARATOR;
   private static final Component FOLDED;
   private static final Component BYTE_TYPE;
   private static final Component SHORT_TYPE;
   private static final Component INT_TYPE;
   private static final Component LONG_TYPE;
   private static final Component FLOAT_TYPE;
   private static final Component DOUBLE_TYPE;
   private static final Component BYTE_ARRAY_TYPE;
   private final String indentation;
   private int indentDepth;
   private int depth;
   private final MutableComponent result = Component.empty();

   public TextComponentTagVisitor(String var1) {
      super();
      this.indentation = var1;
   }

   public Component visit(Tag var1) {
      var1.accept((TagVisitor)this);
      return this.result;
   }

   public void visitString(StringTag var1) {
      String var2 = StringTag.quoteAndEscape(var1.getAsString());
      String var3 = var2.substring(0, 1);
      MutableComponent var4 = Component.literal(var2.substring(1, var2.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      this.result.append(var3).append((Component)var4).append(var3);
   }

   public void visitByte(ByteTag var1) {
      this.result.append((Component)Component.literal(String.valueOf(var1.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(BYTE_TYPE);
   }

   public void visitShort(ShortTag var1) {
      this.result.append((Component)Component.literal(String.valueOf(var1.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(SHORT_TYPE);
   }

   public void visitInt(IntTag var1) {
      this.result.append((Component)Component.literal(String.valueOf(var1.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
   }

   public void visitLong(LongTag var1) {
      this.result.append((Component)Component.literal(String.valueOf(var1.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(LONG_TYPE);
   }

   public void visitFloat(FloatTag var1) {
      this.result.append((Component)Component.literal(String.valueOf(var1.getAsFloat())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(FLOAT_TYPE);
   }

   public void visitDouble(DoubleTag var1) {
      this.result.append((Component)Component.literal(String.valueOf(var1.getAsDouble())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(DOUBLE_TYPE);
   }

   public void visitByteArray(ByteArrayTag var1) {
      this.result.append("[").append(BYTE_ARRAY_TYPE).append(";");
      byte[] var2 = var1.getAsByteArray();

      for(int var3 = 0; var3 < var2.length && var3 < 128; ++var3) {
         MutableComponent var4 = Component.literal(String.valueOf(var2[var3])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         this.result.append(" ").append((Component)var4).append(BYTE_ARRAY_TYPE);
         if (var3 != var2.length - 1) {
            this.result.append(ELEMENT_SEPARATOR);
         }
      }

      if (var2.length > 128) {
         this.result.append(FOLDED);
      }

      this.result.append("]");
   }

   public void visitIntArray(IntArrayTag var1) {
      this.result.append("[").append(INT_TYPE).append(";");
      int[] var2 = var1.getAsIntArray();

      for(int var3 = 0; var3 < var2.length && var3 < 128; ++var3) {
         this.result.append(" ").append((Component)Component.literal(String.valueOf(var2[var3])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (var3 != var2.length - 1) {
            this.result.append(ELEMENT_SEPARATOR);
         }
      }

      if (var2.length > 128) {
         this.result.append(FOLDED);
      }

      this.result.append("]");
   }

   public void visitLongArray(LongArrayTag var1) {
      this.result.append("[").append(LONG_TYPE).append(";");
      long[] var2 = var1.getAsLongArray();

      for(int var3 = 0; var3 < var2.length && var3 < 128; ++var3) {
         MutableComponent var4 = Component.literal(String.valueOf(var2[var3])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         this.result.append(" ").append((Component)var4).append(LONG_TYPE);
         if (var3 != var2.length - 1) {
            this.result.append(ELEMENT_SEPARATOR);
         }
      }

      if (var2.length > 128) {
         this.result.append(FOLDED);
      }

      this.result.append("]");
   }

   public void visitList(ListTag var1) {
      if (var1.isEmpty()) {
         this.result.append("[]");
      } else if (this.depth >= 64) {
         this.result.append("[").append(FOLDED).append("]");
      } else if (INLINE_ELEMENT_TYPES.contains(var1.getElementType()) && var1.size() <= 8) {
         this.result.append("[");

         for(int var4 = 0; var4 < var1.size(); ++var4) {
            if (var4 != 0) {
               this.result.append(SPACED_ELEMENT_SEPARATOR);
            }

            this.appendSubTag(var1.get(var4), false);
         }

         this.result.append("]");
      } else {
         this.result.append("[");
         if (!this.indentation.isEmpty()) {
            this.result.append("\n");
         }

         String var2 = Strings.repeat(this.indentation, this.indentDepth + 1);

         for(int var3 = 0; var3 < var1.size() && var3 < 128; ++var3) {
            this.result.append(var2);
            this.appendSubTag(var1.get(var3), true);
            if (var3 != var1.size() - 1) {
               this.result.append(this.indentation.isEmpty() ? SPACED_ELEMENT_SEPARATOR : WRAPPED_ELEMENT_SEPARATOR);
            }
         }

         if (var1.size() > 128) {
            this.result.append(var2).append(FOLDED);
         }

         if (!this.indentation.isEmpty()) {
            this.result.append("\n" + Strings.repeat(this.indentation, this.indentDepth));
         }

         this.result.append("]");
      }
   }

   public void visitCompound(CompoundTag var1) {
      if (var1.isEmpty()) {
         this.result.append("{}");
      } else if (this.depth >= 64) {
         this.result.append("{").append(FOLDED).append("}");
      } else {
         this.result.append("{");
         Object var2 = var1.getAllKeys();
         if (LOGGER.isDebugEnabled()) {
            ArrayList var3 = Lists.newArrayList(var1.getAllKeys());
            Collections.sort(var3);
            var2 = var3;
         }

         if (!this.indentation.isEmpty()) {
            this.result.append("\n");
         }

         String var6 = Strings.repeat(this.indentation, this.indentDepth + 1);
         Iterator var4 = ((Collection)var2).iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            this.result.append(var6).append(handleEscapePretty(var5)).append(": ");
            this.appendSubTag(var1.get(var5), true);
            if (var4.hasNext()) {
               this.result.append(this.indentation.isEmpty() ? SPACED_ELEMENT_SEPARATOR : WRAPPED_ELEMENT_SEPARATOR);
            }
         }

         if (!this.indentation.isEmpty()) {
            this.result.append("\n" + Strings.repeat(this.indentation, this.indentDepth));
         }

         this.result.append("}");
      }
   }

   private void appendSubTag(Tag var1, boolean var2) {
      if (var2) {
         ++this.indentDepth;
      }

      ++this.depth;

      try {
         var1.accept((TagVisitor)this);
      } finally {
         if (var2) {
            --this.indentDepth;
         }

         --this.depth;
      }

   }

   protected static Component handleEscapePretty(String var0) {
      if (SIMPLE_VALUE.matcher(var0).matches()) {
         return Component.literal(var0).withStyle(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         String var1 = StringTag.quoteAndEscape(var0);
         String var2 = var1.substring(0, 1);
         MutableComponent var3 = Component.literal(var1.substring(1, var1.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
         return Component.literal(var2).append((Component)var3).append(var2);
      }
   }

   public void visitEnd(EndTag var1) {
   }

   static {
      SYNTAX_HIGHLIGHTING_KEY = ChatFormatting.AQUA;
      SYNTAX_HIGHLIGHTING_STRING = ChatFormatting.GREEN;
      SYNTAX_HIGHLIGHTING_NUMBER = ChatFormatting.GOLD;
      SYNTAX_HIGHLIGHTING_NUMBER_TYPE = ChatFormatting.RED;
      SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
      ELEMENT_SEPARATOR = String.valueOf(',');
      WRAPPED_ELEMENT_SEPARATOR = ELEMENT_SEPARATOR + "\n";
      SPACED_ELEMENT_SEPARATOR = ELEMENT_SEPARATOR + " ";
      FOLDED = Component.literal("<...>").withStyle(ChatFormatting.GRAY);
      BYTE_TYPE = Component.literal("b").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      SHORT_TYPE = Component.literal("s").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      INT_TYPE = Component.literal("I").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      LONG_TYPE = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      FLOAT_TYPE = Component.literal("f").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      DOUBLE_TYPE = Component.literal("d").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      BYTE_ARRAY_TYPE = Component.literal("B").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
   }
}
