package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.util.text.TextComponentTranslation;

public class JsonToNBT {
   public static final SimpleCommandExceptionType field_197657_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.nbt.trailing", new Object[0]));
   public static final SimpleCommandExceptionType field_197658_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.nbt.expected.key", new Object[0]));
   public static final SimpleCommandExceptionType field_197659_c = new SimpleCommandExceptionType(new TextComponentTranslation("argument.nbt.expected.value", new Object[0]));
   public static final Dynamic2CommandExceptionType field_197660_d = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.nbt.list.mixed", new Object[]{var0, var1});
   });
   public static final Dynamic2CommandExceptionType field_197661_e = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.nbt.array.mixed", new Object[]{var0, var1});
   });
   public static final DynamicCommandExceptionType field_197662_f = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.nbt.array.invalid", new Object[]{var0});
   });
   private static final Pattern field_193615_a = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
   private static final Pattern field_193616_b = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
   private static final Pattern field_193617_c = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
   private static final Pattern field_193618_d = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
   private static final Pattern field_193619_e = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
   private static final Pattern field_193620_f = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
   private static final Pattern field_193621_g = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
   private final StringReader field_197663_n;

   public static NBTTagCompound func_180713_a(String var0) throws CommandSyntaxException {
      return (new JsonToNBT(new StringReader(var0))).func_193609_a();
   }

   @VisibleForTesting
   NBTTagCompound func_193609_a() throws CommandSyntaxException {
      NBTTagCompound var1 = this.func_193593_f();
      this.field_197663_n.skipWhitespace();
      if (this.field_197663_n.canRead()) {
         throw field_197657_a.createWithContext(this.field_197663_n);
      } else {
         return var1;
      }
   }

   public JsonToNBT(StringReader var1) {
      super();
      this.field_197663_n = var1;
   }

   protected String func_193601_b() throws CommandSyntaxException {
      this.field_197663_n.skipWhitespace();
      if (!this.field_197663_n.canRead()) {
         throw field_197658_b.createWithContext(this.field_197663_n);
      } else {
         return this.field_197663_n.readString();
      }
   }

   protected INBTBase func_193611_c() throws CommandSyntaxException {
      this.field_197663_n.skipWhitespace();
      int var1 = this.field_197663_n.getCursor();
      if (this.field_197663_n.peek() == '"') {
         return new NBTTagString(this.field_197663_n.readQuotedString());
      } else {
         String var2 = this.field_197663_n.readUnquotedString();
         if (var2.isEmpty()) {
            this.field_197663_n.setCursor(var1);
            throw field_197659_c.createWithContext(this.field_197663_n);
         } else {
            return this.func_193596_c(var2);
         }
      }
   }

   private INBTBase func_193596_c(String var1) {
      try {
         if (field_193617_c.matcher(var1).matches()) {
            return new NBTTagFloat(Float.parseFloat(var1.substring(0, var1.length() - 1)));
         }

         if (field_193618_d.matcher(var1).matches()) {
            return new NBTTagByte(Byte.parseByte(var1.substring(0, var1.length() - 1)));
         }

         if (field_193619_e.matcher(var1).matches()) {
            return new NBTTagLong(Long.parseLong(var1.substring(0, var1.length() - 1)));
         }

         if (field_193620_f.matcher(var1).matches()) {
            return new NBTTagShort(Short.parseShort(var1.substring(0, var1.length() - 1)));
         }

         if (field_193621_g.matcher(var1).matches()) {
            return new NBTTagInt(Integer.parseInt(var1));
         }

         if (field_193616_b.matcher(var1).matches()) {
            return new NBTTagDouble(Double.parseDouble(var1.substring(0, var1.length() - 1)));
         }

         if (field_193615_a.matcher(var1).matches()) {
            return new NBTTagDouble(Double.parseDouble(var1));
         }

         if ("true".equalsIgnoreCase(var1)) {
            return new NBTTagByte((byte)1);
         }

         if ("false".equalsIgnoreCase(var1)) {
            return new NBTTagByte((byte)0);
         }
      } catch (NumberFormatException var3) {
      }

      return new NBTTagString(var1);
   }

   protected INBTBase func_193610_d() throws CommandSyntaxException {
      this.field_197663_n.skipWhitespace();
      if (!this.field_197663_n.canRead()) {
         throw field_197659_c.createWithContext(this.field_197663_n);
      } else {
         char var1 = this.field_197663_n.peek();
         if (var1 == '{') {
            return this.func_193593_f();
         } else {
            return var1 == '[' ? this.func_193605_e() : this.func_193611_c();
         }
      }
   }

   protected INBTBase func_193605_e() throws CommandSyntaxException {
      return this.field_197663_n.canRead(3) && this.field_197663_n.peek(1) != '"' && this.field_197663_n.peek(2) == ';' ? this.func_193606_k() : this.func_193600_j();
   }

   public NBTTagCompound func_193593_f() throws CommandSyntaxException {
      this.func_193604_b('{');
      NBTTagCompound var1 = new NBTTagCompound();
      this.field_197663_n.skipWhitespace();

      while(this.field_197663_n.canRead() && this.field_197663_n.peek() != '}') {
         int var2 = this.field_197663_n.getCursor();
         String var3 = this.func_193601_b();
         if (var3.isEmpty()) {
            this.field_197663_n.setCursor(var2);
            throw field_197658_b.createWithContext(this.field_197663_n);
         }

         this.func_193604_b(':');
         var1.func_74782_a(var3, this.func_193610_d());
         if (!this.func_193613_m()) {
            break;
         }

         if (!this.field_197663_n.canRead()) {
            throw field_197658_b.createWithContext(this.field_197663_n);
         }
      }

      this.func_193604_b('}');
      return var1;
   }

   private INBTBase func_193600_j() throws CommandSyntaxException {
      this.func_193604_b('[');
      this.field_197663_n.skipWhitespace();
      if (!this.field_197663_n.canRead()) {
         throw field_197659_c.createWithContext(this.field_197663_n);
      } else {
         NBTTagList var1 = new NBTTagList();
         byte var2 = -1;

         while(this.field_197663_n.peek() != ']') {
            int var3 = this.field_197663_n.getCursor();
            INBTBase var4 = this.func_193610_d();
            byte var5 = var4.func_74732_a();
            if (var2 < 0) {
               var2 = var5;
            } else if (var5 != var2) {
               this.field_197663_n.setCursor(var3);
               throw field_197660_d.createWithContext(this.field_197663_n, INBTBase.func_193581_j(var5), INBTBase.func_193581_j(var2));
            }

            var1.add(var4);
            if (!this.func_193613_m()) {
               break;
            }

            if (!this.field_197663_n.canRead()) {
               throw field_197659_c.createWithContext(this.field_197663_n);
            }
         }

         this.func_193604_b(']');
         return var1;
      }
   }

   private INBTBase func_193606_k() throws CommandSyntaxException {
      this.func_193604_b('[');
      int var1 = this.field_197663_n.getCursor();
      char var2 = this.field_197663_n.read();
      this.field_197663_n.read();
      this.field_197663_n.skipWhitespace();
      if (!this.field_197663_n.canRead()) {
         throw field_197659_c.createWithContext(this.field_197663_n);
      } else if (var2 == 'B') {
         return new NBTTagByteArray(this.func_193603_a((byte)7, (byte)1));
      } else if (var2 == 'L') {
         return new NBTTagLongArray(this.func_193603_a((byte)12, (byte)4));
      } else if (var2 == 'I') {
         return new NBTTagIntArray(this.func_193603_a((byte)11, (byte)3));
      } else {
         this.field_197663_n.setCursor(var1);
         throw field_197662_f.createWithContext(this.field_197663_n, String.valueOf(var2));
      }
   }

   private <T extends Number> List<T> func_193603_a(byte var1, byte var2) throws CommandSyntaxException {
      ArrayList var3 = Lists.newArrayList();

      while(true) {
         if (this.field_197663_n.peek() != ']') {
            int var4 = this.field_197663_n.getCursor();
            INBTBase var5 = this.func_193610_d();
            byte var6 = var5.func_74732_a();
            if (var6 != var2) {
               this.field_197663_n.setCursor(var4);
               throw field_197661_e.createWithContext(this.field_197663_n, INBTBase.func_193581_j(var6), INBTBase.func_193581_j(var1));
            }

            if (var2 == 1) {
               var3.add(((NBTPrimitive)var5).func_150290_f());
            } else if (var2 == 4) {
               var3.add(((NBTPrimitive)var5).func_150291_c());
            } else {
               var3.add(((NBTPrimitive)var5).func_150287_d());
            }

            if (this.func_193613_m()) {
               if (!this.field_197663_n.canRead()) {
                  throw field_197659_c.createWithContext(this.field_197663_n);
               }
               continue;
            }
         }

         this.func_193604_b(']');
         return var3;
      }
   }

   private boolean func_193613_m() {
      this.field_197663_n.skipWhitespace();
      if (this.field_197663_n.canRead() && this.field_197663_n.peek() == ',') {
         this.field_197663_n.skip();
         this.field_197663_n.skipWhitespace();
         return true;
      } else {
         return false;
      }
   }

   private void func_193604_b(char var1) throws CommandSyntaxException {
      this.field_197663_n.skipWhitespace();
      this.field_197663_n.expect(var1);
   }
}
