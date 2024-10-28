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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class TextComponentTagVisitor implements TagVisitor {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int INLINE_LIST_THRESHOLD = 8;
   private static final int MAX_DEPTH = 64;
   private static final ByteCollection INLINE_ELEMENT_TYPES = new ByteOpenHashSet(Arrays.asList(1, 2, 3, 4, 5, 6));
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_KEY;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_STRING;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER;
   private static final ChatFormatting SYNTAX_HIGHLIGHTING_NUMBER_TYPE;
   private static final Pattern SIMPLE_VALUE;
   private static final String NAME_VALUE_SEPARATOR;
   private static final String ELEMENT_SEPARATOR;
   private static final String LIST_OPEN = "[";
   private static final String LIST_CLOSE = "]";
   private static final String LIST_TYPE_SEPARATOR = ";";
   private static final String ELEMENT_SPACING = " ";
   private static final String STRUCT_OPEN = "{";
   private static final String STRUCT_CLOSE = "}";
   private static final String NEWLINE = "\n";
   private static final Component TOO_DEEP;
   private final String indentation;
   private final int indentDepth;
   private final int depth;
   private Component result;

   public TextComponentTagVisitor(String var1) {
      this(var1, 0, 0);
   }

   private TextComponentTagVisitor(String var1, int var2, int var3) {
      super();
      this.result = CommonComponents.EMPTY;
      this.indentation = var1;
      this.indentDepth = var2;
      this.depth = var3;
   }

   public Component visit(Tag var1) {
      var1.accept((TagVisitor)this);
      return this.result;
   }

   public void visitString(StringTag var1) {
      String var2 = StringTag.quoteAndEscape(var1.getAsString());
      String var3 = var2.substring(0, 1);
      MutableComponent var4 = Component.literal(var2.substring(1, var2.length() - 1)).withStyle(SYNTAX_HIGHLIGHTING_STRING);
      this.result = Component.literal(var3).append((Component)var4).append(var3);
   }

   public void visitByte(ByteTag var1) {
      MutableComponent var2 = Component.literal("b").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsNumber())).append((Component)var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitShort(ShortTag var1) {
      MutableComponent var2 = Component.literal("s").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsNumber())).append((Component)var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitInt(IntTag var1) {
      this.result = Component.literal(String.valueOf(var1.getAsNumber())).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitLong(LongTag var1) {
      MutableComponent var2 = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsNumber())).append((Component)var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitFloat(FloatTag var1) {
      MutableComponent var2 = Component.literal("f").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsFloat())).append((Component)var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitDouble(DoubleTag var1) {
      MutableComponent var2 = Component.literal("d").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      this.result = Component.literal(String.valueOf(var1.getAsDouble())).append((Component)var2).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
   }

   public void visitByteArray(ByteArrayTag var1) {
      MutableComponent var2 = Component.literal("B").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = Component.literal("[").append((Component)var2).append(";");
      byte[] var4 = var1.getAsByteArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         MutableComponent var6 = Component.literal(String.valueOf(var4[var5])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
         var3.append(" ").append((Component)var6).append((Component)var2);
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3;
   }

   public void visitIntArray(IntArrayTag var1) {
      MutableComponent var2 = Component.literal("I").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = Component.literal("[").append((Component)var2).append(";");
      int[] var4 = var1.getAsIntArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var3.append(" ").append((Component)Component.literal(String.valueOf(var4[var5])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER));
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3;
   }

   public void visitLongArray(LongArrayTag var1) {
      MutableComponent var2 = Component.literal("L").withStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
      MutableComponent var3 = Component.literal("[").append((Component)var2).append(";");
      long[] var4 = var1.getAsLongArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         MutableComponent var6 = Component.literal(String.valueOf(var4[var5])).withStyle(SYNTAX_HIGHLIGHTING_NUMBER);
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
         this.result = Component.literal("[]");
      } else if (this.depth >= 64) {
         this.result = Component.literal("[").append(TOO_DEEP).append("]");
      } else {
         int var4;
         if (INLINE_ELEMENT_TYPES.contains(var1.getElementType()) && var1.size() <= 8) {
            String var6 = ELEMENT_SEPARATOR + " ";
            MutableComponent var7 = Component.literal("[");

            for(var4 = 0; var4 < var1.size(); ++var4) {
               if (var4 != 0) {
                  var7.append(var6);
               }

               var7.append(this.buildSubTag(var1.get(var4), false));
            }

            var7.append("]");
            this.result = var7;
         } else {
            MutableComponent var2 = Component.literal("[");
            if (!this.indentation.isEmpty()) {
               var2.append("\n");
            }

            String var3 = Strings.repeat(this.indentation, this.indentDepth + 1);

            for(var4 = 0; var4 < var1.size(); ++var4) {
               MutableComponent var5 = Component.literal(var3);
               var5.append(this.buildSubTag(var1.get(var4), true));
               if (var4 != var1.size() - 1) {
                  var5.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
               }

               var2.append((Component)var5);
            }

            if (!this.indentation.isEmpty()) {
               var2.append("\n").append(Strings.repeat(this.indentation, this.indentDepth));
            }

            var2.append("]");
            this.result = var2;
         }
      }
   }

   public void visitCompound(CompoundTag var1) {
      if (var1.isEmpty()) {
         this.result = Component.literal("{}");
      } else if (this.depth >= 64) {
         this.result = Component.literal("{").append(TOO_DEEP).append("}");
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

         String var8 = Strings.repeat(this.indentation, this.indentDepth + 1);

         MutableComponent var7;
         for(Iterator var5 = ((Collection)var3).iterator(); var5.hasNext(); var2.append((Component)var7)) {
            String var6 = (String)var5.next();
            var7 = Component.literal(var8).append(handleEscapePretty(var6)).append(NAME_VALUE_SEPARATOR).append(" ").append(this.buildSubTag(var1.get(var6), true));
            if (var5.hasNext()) {
               var7.append(ELEMENT_SEPARATOR).append(this.indentation.isEmpty() ? " " : "\n");
            }
         }

         if (!this.indentation.isEmpty()) {
            var2.append("\n").append(Strings.repeat(this.indentation, this.indentDepth));
         }

         var2.append("}");
         this.result = var2;
      }
   }

   private Component buildSubTag(Tag var1, boolean var2) {
      return (new TextComponentTagVisitor(this.indentation, var2 ? this.indentDepth + 1 : this.indentDepth, this.depth + 1)).visit(var1);
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
      this.result = CommonComponents.EMPTY;
   }

   static {
      SYNTAX_HIGHLIGHTING_KEY = ChatFormatting.AQUA;
      SYNTAX_HIGHLIGHTING_STRING = ChatFormatting.GREEN;
      SYNTAX_HIGHLIGHTING_NUMBER = ChatFormatting.GOLD;
      SYNTAX_HIGHLIGHTING_NUMBER_TYPE = ChatFormatting.RED;
      SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
      NAME_VALUE_SEPARATOR = String.valueOf(':');
      ELEMENT_SEPARATOR = String.valueOf(',');
      TOO_DEEP = Component.literal("<...>").withStyle(ChatFormatting.GRAY);
   }
}
