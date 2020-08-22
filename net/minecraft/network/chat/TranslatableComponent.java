package net.minecraft.network.chat;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.world.entity.Entity;

public class TranslatableComponent extends BaseComponent implements ContextAwareComponent {
   private static final Language DEFAULT_LANGUAGE = new Language();
   private static final Language LANGUAGE = Language.getInstance();
   private final String key;
   private final Object[] args;
   private final Object decomposeLock = new Object();
   private long decomposedLanguageTime = -1L;
   protected final List decomposedParts = Lists.newArrayList();
   public static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TranslatableComponent(String var1, Object... var2) {
      this.key = var1;
      this.args = var2;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         Object var4 = var2[var3];
         if (var4 instanceof Component) {
            Component var5 = ((Component)var4).deepCopy();
            this.args[var3] = var5;
            var5.getStyle().inheritFrom(this.getStyle());
         } else if (var4 == null) {
            this.args[var3] = "null";
         }
      }

   }

   @VisibleForTesting
   synchronized void decompose() {
      synchronized(this.decomposeLock) {
         long var2 = LANGUAGE.getLastUpdateTime();
         if (var2 == this.decomposedLanguageTime) {
            return;
         }

         this.decomposedLanguageTime = var2;
         this.decomposedParts.clear();
      }

      String var1 = LANGUAGE.getElement(this.key);

      try {
         this.decomposeTemplate(var1);
      } catch (TranslatableFormatException var5) {
         this.decomposedParts.clear();
         this.decomposedParts.add(new TextComponent(var1));
      }

   }

   protected void decomposeTemplate(String var1) {
      Matcher var2 = FORMAT_PATTERN.matcher(var1);

      try {
         int var3 = 0;

         int var4;
         int var6;
         for(var4 = 0; var2.find(var4); var4 = var6) {
            int var5 = var2.start();
            var6 = var2.end();
            if (var5 > var4) {
               TextComponent var7 = new TextComponent(String.format(var1.substring(var4, var5)));
               var7.getStyle().inheritFrom(this.getStyle());
               this.decomposedParts.add(var7);
            }

            String var13 = var2.group(2);
            String var8 = var1.substring(var5, var6);
            if ("%".equals(var13) && "%%".equals(var8)) {
               TextComponent var14 = new TextComponent("%");
               var14.getStyle().inheritFrom(this.getStyle());
               this.decomposedParts.add(var14);
            } else {
               if (!"s".equals(var13)) {
                  throw new TranslatableFormatException(this, "Unsupported format: '" + var8 + "'");
               }

               String var9 = var2.group(1);
               int var10 = var9 != null ? Integer.parseInt(var9) - 1 : var3++;
               if (var10 < this.args.length) {
                  this.decomposedParts.add(this.getComponent(var10));
               }
            }
         }

         if (var4 < var1.length()) {
            TextComponent var12 = new TextComponent(String.format(var1.substring(var4)));
            var12.getStyle().inheritFrom(this.getStyle());
            this.decomposedParts.add(var12);
         }

      } catch (IllegalFormatException var11) {
         throw new TranslatableFormatException(this, var11);
      }
   }

   private Component getComponent(int var1) {
      if (var1 >= this.args.length) {
         throw new TranslatableFormatException(this, var1);
      } else {
         Object var2 = this.args[var1];
         Object var3;
         if (var2 instanceof Component) {
            var3 = (Component)var2;
         } else {
            var3 = new TextComponent(var2 == null ? "null" : var2.toString());
            ((Component)var3).getStyle().inheritFrom(this.getStyle());
         }

         return (Component)var3;
      }
   }

   public Component setStyle(Style var1) {
      super.setStyle(var1);
      Object[] var2 = this.args;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Object var5 = var2[var4];
         if (var5 instanceof Component) {
            ((Component)var5).getStyle().inheritFrom(this.getStyle());
         }
      }

      if (this.decomposedLanguageTime > -1L) {
         Iterator var6 = this.decomposedParts.iterator();

         while(var6.hasNext()) {
            Component var7 = (Component)var6.next();
            var7.getStyle().inheritFrom(var1);
         }
      }

      return this;
   }

   public Stream stream() {
      this.decompose();
      return Streams.concat(new Stream[]{this.decomposedParts.stream(), this.siblings.stream()}).flatMap(Component::stream);
   }

   public String getContents() {
      this.decompose();
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.decomposedParts.iterator();

      while(var2.hasNext()) {
         Component var3 = (Component)var2.next();
         var1.append(var3.getContents());
      }

      return var1.toString();
   }

   public TranslatableComponent copy() {
      Object[] var1 = new Object[this.args.length];

      for(int var2 = 0; var2 < this.args.length; ++var2) {
         if (this.args[var2] instanceof Component) {
            var1[var2] = ((Component)this.args[var2]).deepCopy();
         } else {
            var1[var2] = this.args[var2];
         }
      }

      return new TranslatableComponent(this.key, var1);
   }

   public Component resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
      Object[] var4 = new Object[this.args.length];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         Object var6 = this.args[var5];
         if (var6 instanceof Component) {
            var4[var5] = ComponentUtils.updateForEntity(var1, (Component)var6, var2, var3);
         } else {
            var4[var5] = var6;
         }
      }

      return new TranslatableComponent(this.key, var4);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TranslatableComponent)) {
         return false;
      } else {
         TranslatableComponent var2 = (TranslatableComponent)var1;
         return Arrays.equals(this.args, var2.args) && this.key.equals(var2.key) && super.equals(var1);
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = 31 * var1 + this.key.hashCode();
      var1 = 31 * var1 + Arrays.hashCode(this.args);
      return var1;
   }

   public String toString() {
      return "TranslatableComponent{key='" + this.key + '\'' + ", args=" + Arrays.toString(this.args) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getKey() {
      return this.key;
   }

   public Object[] getArgs() {
      return this.args;
   }

   // $FF: synthetic method
   public Component copy() {
      return this.copy();
   }
}
