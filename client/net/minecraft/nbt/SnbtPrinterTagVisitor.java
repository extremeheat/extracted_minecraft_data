package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.Util;

public class SnbtPrinterTagVisitor implements TagVisitor {
   private static final Map<String, List<String>> KEY_ORDER = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put("{}", Lists.newArrayList(new String[]{"DataVersion", "author", "size", "data", "entities", "palette", "palettes"}));
      var0.put("{}.data.[].{}", Lists.newArrayList(new String[]{"pos", "state", "nbt"}));
      var0.put("{}.entities.[].{}", Lists.newArrayList(new String[]{"blockPos", "pos"}));
   });
   private static final Set<String> NO_INDENTATION = Sets.newHashSet(new String[]{"{}.size.[]", "{}.data.[].{}", "{}.palette.[].{}", "{}.entities.[].{}"});
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
   private final List<String> path;
   private String result;

   public SnbtPrinterTagVisitor() {
      this("    ", 0, Lists.newArrayList());
   }

   public SnbtPrinterTagVisitor(String var1, int var2, List<String> var3) {
      super();
      this.result = "";
      this.indentation = var1;
      this.depth = var2;
      this.path = var3;
   }

   public String visit(Tag var1) {
      var1.accept((TagVisitor)this);
      return this.result;
   }

   public void visitString(StringTag var1) {
      this.result = StringTag.quoteAndEscape(var1.getAsString());
   }

   public void visitByte(ByteTag var1) {
      this.result = String.valueOf(var1.getAsNumber()) + "b";
   }

   public void visitShort(ShortTag var1) {
      this.result = String.valueOf(var1.getAsNumber()) + "s";
   }

   public void visitInt(IntTag var1) {
      this.result = String.valueOf(var1.getAsNumber());
   }

   public void visitLong(LongTag var1) {
      this.result = String.valueOf(var1.getAsNumber()) + "L";
   }

   public void visitFloat(FloatTag var1) {
      this.result = var1.getAsFloat() + "f";
   }

   public void visitDouble(DoubleTag var1) {
      this.result = var1.getAsDouble() + "d";
   }

   public void visitByteArray(ByteArrayTag var1) {
      StringBuilder var2 = (new StringBuilder("[")).append("B").append(";");
      byte[] var3 = var1.getAsByteArray();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var2.append(" ").append(var3[var4]).append("B");
         if (var4 != var3.length - 1) {
            var2.append(ELEMENT_SEPARATOR);
         }
      }

      var2.append("]");
      this.result = var2.toString();
   }

   public void visitIntArray(IntArrayTag var1) {
      StringBuilder var2 = (new StringBuilder("[")).append("I").append(";");
      int[] var3 = var1.getAsIntArray();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var2.append(" ").append(var3[var4]);
         if (var4 != var3.length - 1) {
            var2.append(ELEMENT_SEPARATOR);
         }
      }

      var2.append("]");
      this.result = var2.toString();
   }

   public void visitLongArray(LongArrayTag var1) {
      String var2 = "L";
      StringBuilder var3 = (new StringBuilder("[")).append("L").append(";");
      long[] var4 = var1.getAsLongArray();

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var3.append(" ").append(var4[var5]).append("L");
         if (var5 != var4.length - 1) {
            var3.append(ELEMENT_SEPARATOR);
         }
      }

      var3.append("]");
      this.result = var3.toString();
   }

   public void visitList(ListTag var1) {
      if (var1.isEmpty()) {
         this.result = "[]";
      } else {
         StringBuilder var2 = new StringBuilder("[");
         this.pushPath("[]");
         String var3 = NO_INDENTATION.contains(this.pathString()) ? "" : this.indentation;
         if (!var3.isEmpty()) {
            var2.append("\n");
         }

         for(int var4 = 0; var4 < var1.size(); ++var4) {
            var2.append(Strings.repeat(var3, this.depth + 1));
            var2.append((new SnbtPrinterTagVisitor(var3, this.depth + 1, this.path)).visit(var1.get(var4)));
            if (var4 != var1.size() - 1) {
               var2.append(ELEMENT_SEPARATOR).append(var3.isEmpty() ? " " : "\n");
            }
         }

         if (!var3.isEmpty()) {
            var2.append("\n").append(Strings.repeat(var3, this.depth));
         }

         var2.append("]");
         this.result = var2.toString();
         this.popPath();
      }
   }

   public void visitCompound(CompoundTag var1) {
      if (var1.isEmpty()) {
         this.result = "{}";
      } else {
         StringBuilder var2 = new StringBuilder("{");
         this.pushPath("{}");
         String var3 = NO_INDENTATION.contains(this.pathString()) ? "" : this.indentation;
         if (!var3.isEmpty()) {
            var2.append("\n");
         }

         List var4 = this.getKeys(var1);
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            Tag var7 = var1.get(var6);
            this.pushPath(var6);
            var2.append(Strings.repeat(var3, this.depth + 1)).append(handleEscapePretty(var6)).append(NAME_VALUE_SEPARATOR).append(" ").append((new SnbtPrinterTagVisitor(var3, this.depth + 1, this.path)).visit(var7));
            this.popPath();
            if (var5.hasNext()) {
               var2.append(ELEMENT_SEPARATOR).append(var3.isEmpty() ? " " : "\n");
            }
         }

         if (!var3.isEmpty()) {
            var2.append("\n").append(Strings.repeat(var3, this.depth));
         }

         var2.append("}");
         this.result = var2.toString();
         this.popPath();
      }
   }

   private void popPath() {
      this.path.remove(this.path.size() - 1);
   }

   private void pushPath(String var1) {
      this.path.add(var1);
   }

   protected List<String> getKeys(CompoundTag var1) {
      HashSet var2 = Sets.newHashSet(var1.getAllKeys());
      ArrayList var3 = Lists.newArrayList();
      List var4 = (List)KEY_ORDER.get(this.pathString());
      if (var4 != null) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            if (var2.remove(var6)) {
               var3.add(var6);
            }
         }

         if (!var2.isEmpty()) {
            Stream var10000 = var2.stream().sorted();
            Objects.requireNonNull(var3);
            var10000.forEach(var3::add);
         }
      } else {
         var3.addAll(var2);
         Collections.sort(var3);
      }

      return var3;
   }

   public String pathString() {
      return String.join(".", this.path);
   }

   protected static String handleEscapePretty(String var0) {
      return SIMPLE_VALUE.matcher(var0).matches() ? var0 : StringTag.quoteAndEscape(var0);
   }

   public void visitEnd(EndTag var1) {
   }
}
