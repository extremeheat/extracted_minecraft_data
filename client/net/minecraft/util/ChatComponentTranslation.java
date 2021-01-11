package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatComponentTranslation extends ChatComponentStyle {
   private final String field_150276_d;
   private final Object[] field_150277_e;
   private final Object field_150274_f = new Object();
   private long field_150275_g = -1L;
   List<IChatComponent> field_150278_b = Lists.newArrayList();
   public static final Pattern field_150279_c = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public ChatComponentTranslation(String var1, Object... var2) {
      super();
      this.field_150276_d = var1;
      this.field_150277_e = var2;
      Object[] var3 = var2;
      int var4 = var2.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Object var6 = var3[var5];
         if (var6 instanceof IChatComponent) {
            ((IChatComponent)var6).func_150256_b().func_150221_a(this.func_150256_b());
         }
      }

   }

   synchronized void func_150270_g() {
      synchronized(this.field_150274_f) {
         long var2 = StatCollector.func_150827_a();
         if (var2 == this.field_150275_g) {
            return;
         }

         this.field_150275_g = var2;
         this.field_150278_b.clear();
      }

      try {
         this.func_150269_b(StatCollector.func_74838_a(this.field_150276_d));
      } catch (ChatComponentTranslationFormatException var6) {
         this.field_150278_b.clear();

         try {
            this.func_150269_b(StatCollector.func_150826_b(this.field_150276_d));
         } catch (ChatComponentTranslationFormatException var5) {
            throw var6;
         }
      }

   }

   protected void func_150269_b(String var1) {
      boolean var2 = false;
      Matcher var3 = field_150279_c.matcher(var1);
      int var4 = 0;
      int var5 = 0;

      try {
         int var7;
         for(; var3.find(var5); var5 = var7) {
            int var6 = var3.start();
            var7 = var3.end();
            if (var6 > var5) {
               ChatComponentText var8 = new ChatComponentText(String.format(var1.substring(var5, var6)));
               var8.func_150256_b().func_150221_a(this.func_150256_b());
               this.field_150278_b.add(var8);
            }

            String var14 = var3.group(2);
            String var9 = var1.substring(var6, var7);
            if ("%".equals(var14) && "%%".equals(var9)) {
               ChatComponentText var15 = new ChatComponentText("%");
               var15.func_150256_b().func_150221_a(this.func_150256_b());
               this.field_150278_b.add(var15);
            } else {
               if (!"s".equals(var14)) {
                  throw new ChatComponentTranslationFormatException(this, "Unsupported format: '" + var9 + "'");
               }

               String var10 = var3.group(1);
               int var11 = var10 != null ? Integer.parseInt(var10) - 1 : var4++;
               if (var11 < this.field_150277_e.length) {
                  this.field_150278_b.add(this.func_150272_a(var11));
               }
            }
         }

         if (var5 < var1.length()) {
            ChatComponentText var13 = new ChatComponentText(String.format(var1.substring(var5)));
            var13.func_150256_b().func_150221_a(this.func_150256_b());
            this.field_150278_b.add(var13);
         }

      } catch (IllegalFormatException var12) {
         throw new ChatComponentTranslationFormatException(this, var12);
      }
   }

   private IChatComponent func_150272_a(int var1) {
      if (var1 >= this.field_150277_e.length) {
         throw new ChatComponentTranslationFormatException(this, var1);
      } else {
         Object var2 = this.field_150277_e[var1];
         Object var3;
         if (var2 instanceof IChatComponent) {
            var3 = (IChatComponent)var2;
         } else {
            var3 = new ChatComponentText(var2 == null ? "null" : var2.toString());
            ((IChatComponent)var3).func_150256_b().func_150221_a(this.func_150256_b());
         }

         return (IChatComponent)var3;
      }
   }

   public IChatComponent func_150255_a(ChatStyle var1) {
      super.func_150255_a(var1);
      Object[] var2 = this.field_150277_e;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 instanceof IChatComponent) {
            ((IChatComponent)var5).func_150256_b().func_150221_a(this.func_150256_b());
         }
      }

      if (this.field_150275_g > -1L) {
         Iterator var6 = this.field_150278_b.iterator();

         while(var6.hasNext()) {
            IChatComponent var7 = (IChatComponent)var6.next();
            var7.func_150256_b().func_150221_a(var1);
         }
      }

      return this;
   }

   public Iterator<IChatComponent> iterator() {
      this.func_150270_g();
      return Iterators.concat(func_150262_a(this.field_150278_b), func_150262_a(this.field_150264_a));
   }

   public String func_150261_e() {
      this.func_150270_g();
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.field_150278_b.iterator();

      while(var2.hasNext()) {
         IChatComponent var3 = (IChatComponent)var2.next();
         var1.append(var3.func_150261_e());
      }

      return var1.toString();
   }

   public ChatComponentTranslation func_150259_f() {
      Object[] var1 = new Object[this.field_150277_e.length];

      for(int var2 = 0; var2 < this.field_150277_e.length; ++var2) {
         if (this.field_150277_e[var2] instanceof IChatComponent) {
            var1[var2] = ((IChatComponent)this.field_150277_e[var2]).func_150259_f();
         } else {
            var1[var2] = this.field_150277_e[var2];
         }
      }

      ChatComponentTranslation var5 = new ChatComponentTranslation(this.field_150276_d, var1);
      var5.func_150255_a(this.func_150256_b().func_150232_l());
      Iterator var3 = this.func_150253_a().iterator();

      while(var3.hasNext()) {
         IChatComponent var4 = (IChatComponent)var3.next();
         var5.func_150257_a(var4.func_150259_f());
      }

      return var5;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChatComponentTranslation)) {
         return false;
      } else {
         ChatComponentTranslation var2 = (ChatComponentTranslation)var1;
         return Arrays.equals(this.field_150277_e, var2.field_150277_e) && this.field_150276_d.equals(var2.field_150276_d) && super.equals(var1);
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.field_150276_d.hashCode();
      var1 = 31 * var1 + Arrays.hashCode(this.field_150277_e);
      return var1;
   }

   public String toString() {
      return "TranslatableComponent{key='" + this.field_150276_d + '\'' + ", args=" + Arrays.toString(this.field_150277_e) + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
   }

   public String func_150268_i() {
      return this.field_150276_d;
   }

   public Object[] func_150271_j() {
      return this.field_150277_e;
   }

   // $FF: synthetic method
   public IChatComponent func_150259_f() {
      return this.func_150259_f();
   }
}
