package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JsonToNBT {
   private static final Logger field_150317_a = LogManager.getLogger();
   private static final Pattern field_179273_b = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

   public static NBTTagCompound func_180713_a(String var0) throws NBTException {
      var0 = var0.trim();
      if (!var0.startsWith("{")) {
         throw new NBTException("Invalid tag encountered, expected '{' as first char.");
      } else if (func_150310_b(var0) != 1) {
         throw new NBTException("Encountered multiple top tags, only one expected");
      } else {
         return (NBTTagCompound)func_150316_a("tag", var0).func_150489_a();
      }
   }

   static int func_150310_b(String var0) throws NBTException {
      int var1 = 0;
      boolean var2 = false;
      Stack var3 = new Stack();

      for(int var4 = 0; var4 < var0.length(); ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 == '"') {
            if (func_179271_b(var0, var4)) {
               if (!var2) {
                  throw new NBTException("Illegal use of \\\": " + var0);
               }
            } else {
               var2 = !var2;
            }
         } else if (!var2) {
            if (var5 != '{' && var5 != '[') {
               if (var5 == '}' && (var3.isEmpty() || (Character)var3.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + var0);
               }

               if (var5 == ']' && (var3.isEmpty() || (Character)var3.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + var0);
               }
            } else {
               if (var3.isEmpty()) {
                  ++var1;
               }

               var3.push(var5);
            }
         }
      }

      if (var2) {
         throw new NBTException("Unbalanced quotation: " + var0);
      } else if (!var3.isEmpty()) {
         throw new NBTException("Unbalanced brackets: " + var0);
      } else {
         if (var1 == 0 && !var0.isEmpty()) {
            var1 = 1;
         }

         return var1;
      }
   }

   static JsonToNBT.Any func_179272_a(String... var0) throws NBTException {
      return func_150316_a(var0[0], var0[1]);
   }

   static JsonToNBT.Any func_150316_a(String var0, String var1) throws NBTException {
      var1 = var1.trim();
      String var3;
      boolean var4;
      char var6;
      if (var1.startsWith("{")) {
         var1 = var1.substring(1, var1.length() - 1);

         JsonToNBT.Compound var5;
         for(var5 = new JsonToNBT.Compound(var0); var1.length() > 0; var1 = var1.substring(var3.length() + 1)) {
            var3 = func_150314_a(var1, true);
            if (var3.length() > 0) {
               var4 = false;
               var5.field_150491_b.add(func_179270_a(var3, var4));
            }

            if (var1.length() < var3.length() + 1) {
               break;
            }

            var6 = var1.charAt(var3.length());
            if (var6 != ',' && var6 != '{' && var6 != '}' && var6 != '[' && var6 != ']') {
               throw new NBTException("Unexpected token '" + var6 + "' at: " + var1.substring(var3.length()));
            }
         }

         return var5;
      } else if (var1.startsWith("[") && !field_179273_b.matcher(var1).matches()) {
         var1 = var1.substring(1, var1.length() - 1);

         JsonToNBT.List var2;
         for(var2 = new JsonToNBT.List(var0); var1.length() > 0; var1 = var1.substring(var3.length() + 1)) {
            var3 = func_150314_a(var1, false);
            if (var3.length() > 0) {
               var4 = true;
               var2.field_150492_b.add(func_179270_a(var3, var4));
            }

            if (var1.length() < var3.length() + 1) {
               break;
            }

            var6 = var1.charAt(var3.length());
            if (var6 != ',' && var6 != '{' && var6 != '}' && var6 != '[' && var6 != ']') {
               throw new NBTException("Unexpected token '" + var6 + "' at: " + var1.substring(var3.length()));
            }
         }

         return var2;
      } else {
         return new JsonToNBT.Primitive(var0, var1);
      }
   }

   private static JsonToNBT.Any func_179270_a(String var0, boolean var1) throws NBTException {
      String var2 = func_150313_b(var0, var1);
      String var3 = func_150311_c(var0, var1);
      return func_179272_a(var2, var3);
   }

   private static String func_150314_a(String var0, boolean var1) throws NBTException {
      int var2 = func_150312_a(var0, ':');
      int var3 = func_150312_a(var0, ',');
      if (var1) {
         if (var2 == -1) {
            throw new NBTException("Unable to locate name/value separator for string: " + var0);
         }

         if (var3 != -1 && var3 < var2) {
            throw new NBTException("Name error at: " + var0);
         }
      } else if (var2 == -1 || var2 > var3) {
         var2 = -1;
      }

      return func_179269_a(var0, var2);
   }

   private static String func_179269_a(String var0, int var1) throws NBTException {
      Stack var2 = new Stack();
      int var3 = var1 + 1;
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;

      for(int var7 = 0; var3 < var0.length(); ++var3) {
         char var8 = var0.charAt(var3);
         if (var8 == '"') {
            if (func_179271_b(var0, var3)) {
               if (!var4) {
                  throw new NBTException("Illegal use of \\\": " + var0);
               }
            } else {
               var4 = !var4;
               if (var4 && !var6) {
                  var5 = true;
               }

               if (!var4) {
                  var7 = var3;
               }
            }
         } else if (!var4) {
            if (var8 != '{' && var8 != '[') {
               if (var8 == '}' && (var2.isEmpty() || (Character)var2.pop() != '{')) {
                  throw new NBTException("Unbalanced curly brackets {}: " + var0);
               }

               if (var8 == ']' && (var2.isEmpty() || (Character)var2.pop() != '[')) {
                  throw new NBTException("Unbalanced square brackets []: " + var0);
               }

               if (var8 == ',' && var2.isEmpty()) {
                  return var0.substring(0, var3);
               }
            } else {
               var2.push(var8);
            }
         }

         if (!Character.isWhitespace(var8)) {
            if (!var4 && var5 && var7 != var3) {
               return var0.substring(0, var7 + 1);
            }

            var6 = true;
         }
      }

      return var0.substring(0, var3);
   }

   private static String func_150313_b(String var0, boolean var1) throws NBTException {
      if (var1) {
         var0 = var0.trim();
         if (var0.startsWith("{") || var0.startsWith("[")) {
            return "";
         }
      }

      int var2 = func_150312_a(var0, ':');
      if (var2 == -1) {
         if (var1) {
            return "";
         } else {
            throw new NBTException("Unable to locate name/value separator for string: " + var0);
         }
      } else {
         return var0.substring(0, var2).trim();
      }
   }

   private static String func_150311_c(String var0, boolean var1) throws NBTException {
      if (var1) {
         var0 = var0.trim();
         if (var0.startsWith("{") || var0.startsWith("[")) {
            return var0;
         }
      }

      int var2 = func_150312_a(var0, ':');
      if (var2 == -1) {
         if (var1) {
            return var0;
         } else {
            throw new NBTException("Unable to locate name/value separator for string: " + var0);
         }
      } else {
         return var0.substring(var2 + 1).trim();
      }
   }

   private static int func_150312_a(String var0, char var1) {
      int var2 = 0;

      for(boolean var3 = true; var2 < var0.length(); ++var2) {
         char var4 = var0.charAt(var2);
         if (var4 == '"') {
            if (!func_179271_b(var0, var2)) {
               var3 = !var3;
            }
         } else if (var3) {
            if (var4 == var1) {
               return var2;
            }

            if (var4 == '{' || var4 == '[') {
               return -1;
            }
         }
      }

      return -1;
   }

   private static boolean func_179271_b(String var0, int var1) {
      return var1 > 0 && var0.charAt(var1 - 1) == '\\' && !func_179271_b(var0, var1 - 1);
   }

   static class Primitive extends JsonToNBT.Any {
      private static final Pattern field_179265_c = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
      private static final Pattern field_179263_d = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
      private static final Pattern field_179264_e = Pattern.compile("[-+]?[0-9]+[b|B]");
      private static final Pattern field_179261_f = Pattern.compile("[-+]?[0-9]+[l|L]");
      private static final Pattern field_179262_g = Pattern.compile("[-+]?[0-9]+[s|S]");
      private static final Pattern field_179267_h = Pattern.compile("[-+]?[0-9]+");
      private static final Pattern field_179268_i = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
      private static final Splitter field_179266_j = Splitter.on(',').omitEmptyStrings();
      protected String field_150493_b;

      public Primitive(String var1, String var2) {
         super();
         this.field_150490_a = var1;
         this.field_150493_b = var2;
      }

      public NBTBase func_150489_a() throws NBTException {
         try {
            if (field_179265_c.matcher(this.field_150493_b).matches()) {
               return new NBTTagDouble(Double.parseDouble(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
            }

            if (field_179263_d.matcher(this.field_150493_b).matches()) {
               return new NBTTagFloat(Float.parseFloat(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
            }

            if (field_179264_e.matcher(this.field_150493_b).matches()) {
               return new NBTTagByte(Byte.parseByte(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
            }

            if (field_179261_f.matcher(this.field_150493_b).matches()) {
               return new NBTTagLong(Long.parseLong(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
            }

            if (field_179262_g.matcher(this.field_150493_b).matches()) {
               return new NBTTagShort(Short.parseShort(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
            }

            if (field_179267_h.matcher(this.field_150493_b).matches()) {
               return new NBTTagInt(Integer.parseInt(this.field_150493_b));
            }

            if (field_179268_i.matcher(this.field_150493_b).matches()) {
               return new NBTTagDouble(Double.parseDouble(this.field_150493_b));
            }

            if (this.field_150493_b.equalsIgnoreCase("true") || this.field_150493_b.equalsIgnoreCase("false")) {
               return new NBTTagByte((byte)(Boolean.parseBoolean(this.field_150493_b) ? 1 : 0));
            }
         } catch (NumberFormatException var6) {
            this.field_150493_b = this.field_150493_b.replaceAll("\\\\\"", "\"");
            return new NBTTagString(this.field_150493_b);
         }

         if (this.field_150493_b.startsWith("[") && this.field_150493_b.endsWith("]")) {
            String var7 = this.field_150493_b.substring(1, this.field_150493_b.length() - 1);
            String[] var8 = (String[])Iterables.toArray(field_179266_j.split(var7), String.class);

            try {
               int[] var3 = new int[var8.length];

               for(int var4 = 0; var4 < var8.length; ++var4) {
                  var3[var4] = Integer.parseInt(var8[var4].trim());
               }

               return new NBTTagIntArray(var3);
            } catch (NumberFormatException var5) {
               return new NBTTagString(this.field_150493_b);
            }
         } else {
            if (this.field_150493_b.startsWith("\"") && this.field_150493_b.endsWith("\"")) {
               this.field_150493_b = this.field_150493_b.substring(1, this.field_150493_b.length() - 1);
            }

            this.field_150493_b = this.field_150493_b.replaceAll("\\\\\"", "\"");
            StringBuilder var1 = new StringBuilder();

            for(int var2 = 0; var2 < this.field_150493_b.length(); ++var2) {
               if (var2 < this.field_150493_b.length() - 1 && this.field_150493_b.charAt(var2) == '\\' && this.field_150493_b.charAt(var2 + 1) == '\\') {
                  var1.append('\\');
                  ++var2;
               } else {
                  var1.append(this.field_150493_b.charAt(var2));
               }
            }

            return new NBTTagString(var1.toString());
         }
      }
   }

   static class List extends JsonToNBT.Any {
      protected java.util.List<JsonToNBT.Any> field_150492_b = Lists.newArrayList();

      public List(String var1) {
         super();
         this.field_150490_a = var1;
      }

      public NBTBase func_150489_a() throws NBTException {
         NBTTagList var1 = new NBTTagList();
         Iterator var2 = this.field_150492_b.iterator();

         while(var2.hasNext()) {
            JsonToNBT.Any var3 = (JsonToNBT.Any)var2.next();
            var1.func_74742_a(var3.func_150489_a());
         }

         return var1;
      }
   }

   static class Compound extends JsonToNBT.Any {
      protected java.util.List<JsonToNBT.Any> field_150491_b = Lists.newArrayList();

      public Compound(String var1) {
         super();
         this.field_150490_a = var1;
      }

      public NBTBase func_150489_a() throws NBTException {
         NBTTagCompound var1 = new NBTTagCompound();
         Iterator var2 = this.field_150491_b.iterator();

         while(var2.hasNext()) {
            JsonToNBT.Any var3 = (JsonToNBT.Any)var2.next();
            var1.func_74782_a(var3.field_150490_a, var3.func_150489_a());
         }

         return var1;
      }
   }

   abstract static class Any {
      protected String field_150490_a;

      Any() {
         super();
      }

      public abstract NBTBase func_150489_a() throws NBTException;
   }
}
