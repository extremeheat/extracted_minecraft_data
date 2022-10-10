package net.minecraft.util.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.util.text.translation.LanguageMap;

public class TextComponentTranslation extends TextComponentBase {
   private static final LanguageMap field_200526_d = new LanguageMap();
   private static final LanguageMap field_200527_e = LanguageMap.func_74808_a();
   private final String field_150276_d;
   private final Object[] field_150277_e;
   private final Object field_150274_f = new Object();
   private long field_150275_g = -1L;
   @VisibleForTesting
   List<ITextComponent> field_150278_b = Lists.newArrayList();
   public static final Pattern field_150279_c = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TextComponentTranslation(String var1, Object... var2) {
      super();
      this.field_150276_d = var1;
      this.field_150277_e = var2;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Object var4 = var2[var3];
         if (var4 instanceof ITextComponent) {
            ITextComponent var5 = ((ITextComponent)var4).func_212638_h();
            this.field_150277_e[var3] = var5;
            var5.func_150256_b().func_150221_a(this.func_150256_b());
         } else if (var4 == null) {
            this.field_150277_e[var3] = "null";
         }
      }

   }

   @VisibleForTesting
   synchronized void func_150270_g() {
      synchronized(this.field_150274_f) {
         long var2 = field_200527_e.func_150510_c();
         if (var2 == this.field_150275_g) {
            return;
         }

         this.field_150275_g = var2;
         this.field_150278_b.clear();
      }

      try {
         this.func_150269_b(field_200527_e.func_74805_b(this.field_150276_d));
      } catch (TextComponentTranslationFormatException var6) {
         this.field_150278_b.clear();

         try {
            this.func_150269_b(field_200526_d.func_74805_b(this.field_150276_d));
         } catch (TextComponentTranslationFormatException var5) {
            throw var6;
         }
      }

   }

   protected void func_150269_b(String var1) {
      Matcher var2 = field_150279_c.matcher(var1);

      try {
         int var3 = 0;

         int var4;
         int var6;
         for(var4 = 0; var2.find(var4); var4 = var6) {
            int var5 = var2.start();
            var6 = var2.end();
            if (var5 > var4) {
               TextComponentString var7 = new TextComponentString(String.format(var1.substring(var4, var5)));
               var7.func_150256_b().func_150221_a(this.func_150256_b());
               this.field_150278_b.add(var7);
            }

            String var13 = var2.group(2);
            String var8 = var1.substring(var5, var6);
            if ("%".equals(var13) && "%%".equals(var8)) {
               TextComponentString var14 = new TextComponentString("%");
               var14.func_150256_b().func_150221_a(this.func_150256_b());
               this.field_150278_b.add(var14);
            } else {
               if (!"s".equals(var13)) {
                  throw new TextComponentTranslationFormatException(this, "Unsupported format: '" + var8 + "'");
               }

               String var9 = var2.group(1);
               int var10 = var9 != null ? Integer.parseInt(var9) - 1 : var3++;
               if (var10 < this.field_150277_e.length) {
                  this.field_150278_b.add(this.func_150272_a(var10));
               }
            }
         }

         if (var4 < var1.length()) {
            TextComponentString var12 = new TextComponentString(String.format(var1.substring(var4)));
            var12.func_150256_b().func_150221_a(this.func_150256_b());
            this.field_150278_b.add(var12);
         }

      } catch (IllegalFormatException var11) {
         throw new TextComponentTranslationFormatException(this, var11);
      }
   }

   private ITextComponent func_150272_a(int var1) {
      if (var1 >= this.field_150277_e.length) {
         throw new TextComponentTranslationFormatException(this, var1);
      } else {
         Object var2 = this.field_150277_e[var1];
         Object var3;
         if (var2 instanceof ITextComponent) {
            var3 = (ITextComponent)var2;
         } else {
            var3 = new TextComponentString(var2 == null ? "null" : var2.toString());
            ((ITextComponent)var3).func_150256_b().func_150221_a(this.func_150256_b());
         }

         return (ITextComponent)var3;
      }
   }

   public ITextComponent func_150255_a(Style var1) {
      super.func_150255_a(var1);
      Object[] var2 = this.field_150277_e;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 instanceof ITextComponent) {
            ((ITextComponent)var5).func_150256_b().func_150221_a(this.func_150256_b());
         }
      }

      if (this.field_150275_g > -1L) {
         Iterator var6 = this.field_150278_b.iterator();

         while(var6.hasNext()) {
            ITextComponent var7 = (ITextComponent)var6.next();
            var7.func_150256_b().func_150221_a(var1);
         }
      }

      return this;
   }

   public Stream<ITextComponent> func_212640_c() {
      this.func_150270_g();
      return Streams.concat(new Stream[]{this.field_150278_b.stream(), this.field_150264_a.stream()}).flatMap(ITextComponent::func_212640_c);
   }

   public String func_150261_e() {
      this.func_150270_g();
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.field_150278_b.iterator();

      while(var2.hasNext()) {
         ITextComponent var3 = (ITextComponent)var2.next();
         var1.append(var3.func_150261_e());
      }

      return var1.toString();
   }

   public TextComponentTranslation func_150259_f() {
      Object[] var1 = new Object[this.field_150277_e.length];

      for(int var2 = 0; var2 < this.field_150277_e.length; ++var2) {
         if (this.field_150277_e[var2] instanceof ITextComponent) {
            var1[var2] = ((ITextComponent)this.field_150277_e[var2]).func_212638_h();
         } else {
            var1[var2] = this.field_150277_e[var2];
         }
      }

      return new TextComponentTranslation(this.field_150276_d, var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TextComponentTranslation)) {
         return false;
      } else {
         TextComponentTranslation var2 = (TextComponentTranslation)var1;
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
   public ITextComponent func_150259_f() {
      return this.func_150259_f();
   }
}
