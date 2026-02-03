package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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

   public TextComponentTagVisitor(final String indentation) {
      super();
      this.indentation = indentation;
   }

   public Component visit(final Tag tag) {
      tag.accept((TagVisitor)this);
      return this.result;
   }

   public void visitString(final StringTag tag) {
      String quoted = StringTag.quoteAndEscape(tag.value());
      String quote = quoted.substring(0, 1);
      Component inner = Component.literal(quoted.substring(1, quoted.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      this.result.append(quote).append(inner).append(quote);
   }

   public void visitByte(final ByteTag tag) {
      this.result.append((Component)Component.literal(String.valueOf(tag.value())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(BYTE_TYPE);
   }

   public void visitShort(final ShortTag tag) {
      this.result.append((Component)Component.literal(String.valueOf(tag.value())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(SHORT_TYPE);
   }

   public void visitInt(final IntTag tag) {
      this.result.append((Component)Component.literal(String.valueOf(tag.value())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
   }

   public void visitLong(final LongTag tag) {
      this.result.append((Component)Component.literal(String.valueOf(tag.value())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(LONG_TYPE);
   }

   public void visitFloat(final FloatTag tag) {
      this.result.append((Component)Component.literal(String.valueOf(tag.value())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(FLOAT_TYPE);
   }

   public void visitDouble(final DoubleTag tag) {
      this.result.append((Component)Component.literal(String.valueOf(tag.value())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER)).append(DOUBLE_TYPE);
   }

   public void visitByteArray(final ByteArrayTag tag) {
      this.result.append("[").append(BYTE_ARRAY_TYPE).append(";");
      byte[] data = tag.getAsByteArray();

      for(int i = 0; i < data.length && i < 128; ++i) {
         MutableComponent line = Component.literal(String.valueOf(data[i])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         this.result.append(" ").append((Component)line).append(BYTE_ARRAY_TYPE);
         if (i != data.length - 1) {
            this.result.append(ELEMENT_SEPARATOR);
         }
      }

      if (data.length > 128) {
         this.result.append(FOLDED);
      }

      this.result.append("]");
   }

   public void visitIntArray(final IntArrayTag tag) {
      this.result.append("[").append(INT_TYPE).append(";");
      int[] data = tag.getAsIntArray();

      for(int i = 0; i < data.length && i < 128; ++i) {
         this.result.append(" ").append((Component)Component.literal(String.valueOf(data[i])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (i != data.length - 1) {
            this.result.append(ELEMENT_SEPARATOR);
         }
      }

      if (data.length > 128) {
         this.result.append(FOLDED);
      }

      this.result.append("]");
   }

   public void visitLongArray(final LongArrayTag tag) {
      this.result.append("[").append(LONG_TYPE).append(";");
      long[] data = tag.getAsLongArray();

      for(int i = 0; i < data.length && i < 128; ++i) {
         Component line = Component.literal(String.valueOf(data[i])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         this.result.append(" ").append(line).append(LONG_TYPE);
         if (i != data.length - 1) {
            this.result.append(ELEMENT_SEPARATOR);
         }
      }

      if (data.length > 128) {
         this.result.append(FOLDED);
      }

      this.result.append("]");
   }

   private static boolean shouldWrapListElements(final ListTag list) {
      if (list.size() >= 8) {
         return false;
      } else {
         for(Tag element : list) {
            if (!(element instanceof NumericTag)) {
               return true;
            }
         }

         return false;
      }
   }

   public void visitList(final ListTag tag) {
      if (tag.isEmpty()) {
         this.result.append("[]");
      } else if (this.depth >= 64) {
         this.result.append("[").append(FOLDED).append("]");
      } else if (!shouldWrapListElements(tag)) {
         this.result.append("[");

         for(int i = 0; i < tag.size(); ++i) {
            if (i != 0) {
               this.result.append(SPACED_ELEMENT_SEPARATOR);
            }

            this.appendSubTag(tag.get(i), false);
         }

         this.result.append("]");
      } else {
         this.result.append("[");
         if (!this.indentation.isEmpty()) {
            this.result.append("\n");
         }

         String entryIndent = Strings.repeat(this.indentation, this.indentDepth + 1);

         for(int i = 0; i < tag.size() && i < 128; ++i) {
            this.result.append(entryIndent);
            this.appendSubTag(tag.get(i), true);
            if (i != tag.size() - 1) {
               this.result.append(this.indentation.isEmpty() ? SPACED_ELEMENT_SEPARATOR : WRAPPED_ELEMENT_SEPARATOR);
            }
         }

         if (tag.size() > 128) {
            this.result.append(entryIndent).append(FOLDED);
         }

         if (!this.indentation.isEmpty()) {
            this.result.append("\n" + Strings.repeat(this.indentation, this.indentDepth));
         }

         this.result.append("]");
      }
   }

   public void visitCompound(final CompoundTag tag) {
      if (tag.isEmpty()) {
         this.result.append("{}");
      } else if (this.depth >= 64) {
         this.result.append("{").append(FOLDED).append("}");
      } else {
         this.result.append("{");
         Collection<String> strings = tag.keySet();
         if (LOGGER.isDebugEnabled()) {
            List<String> keys = Lists.newArrayList(tag.keySet());
            Collections.sort(keys);
            strings = keys;
         }

         if (!this.indentation.isEmpty()) {
            this.result.append("\n");
         }

         String entryIndent = Strings.repeat(this.indentation, this.indentDepth + 1);
         Iterator<String> iterator = strings.iterator();

         while(iterator.hasNext()) {
            String key = (String)iterator.next();
            this.result.append(entryIndent).append(handleEscapePretty(key)).append(": ");
            this.appendSubTag(tag.get(key), true);
            if (iterator.hasNext()) {
               this.result.append(this.indentation.isEmpty() ? SPACED_ELEMENT_SEPARATOR : WRAPPED_ELEMENT_SEPARATOR);
            }
         }

         if (!this.indentation.isEmpty()) {
            this.result.append("\n" + Strings.repeat(this.indentation, this.indentDepth));
         }

         this.result.append("}");
      }
   }

   private void appendSubTag(final Tag tag, final boolean indent) {
      if (indent) {
         ++this.indentDepth;
      }

      ++this.depth;

      try {
         tag.accept((TagVisitor)this);
      } finally {
         if (indent) {
            --this.indentDepth;
         }

         --this.depth;
      }

   }

   protected static Component handleEscapePretty(final String input) {
      if (SIMPLE_VALUE.matcher(input).matches()) {
         return Component.literal(input).withStyle(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         String quoted = StringTag.quoteAndEscape(input);
         String quote = quoted.substring(0, 1);
         Component inner = Component.literal(quoted.substring(1, quoted.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_KEY);
         return Component.literal(quote).append(inner).append(quote);
      }
   }

   public void visitEnd(final EndTag tag) {
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
