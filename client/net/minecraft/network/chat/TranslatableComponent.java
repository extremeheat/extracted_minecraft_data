package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.world.entity.Entity;

public class TranslatableComponent extends BaseComponent implements ContextAwareComponent {
   private static final Object[] NO_ARGS = new Object[0];
   private static final FormattedText TEXT_PERCENT = FormattedText.of("%");
   private static final FormattedText TEXT_NULL = FormattedText.of("null");
   private final String key;
   private final Object[] args;
   @Nullable
   private Language decomposedWith;
   private final List<FormattedText> decomposedParts = Lists.newArrayList();
   private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

   public TranslatableComponent(String var1) {
      super();
      this.key = var1;
      this.args = NO_ARGS;
   }

   public TranslatableComponent(String var1, Object... var2) {
      super();
      this.key = var1;
      this.args = var2;
   }

   private void decompose() {
      Language var1 = Language.getInstance();
      if (var1 != this.decomposedWith) {
         this.decomposedWith = var1;
         this.decomposedParts.clear();
         String var2 = var1.getOrDefault(this.key);

         try {
            this.decomposeTemplate(var2);
         } catch (TranslatableFormatException var4) {
            this.decomposedParts.clear();
            this.decomposedParts.add(FormattedText.of(var2));
         }

      }
   }

   private void decomposeTemplate(String var1) {
      Matcher var2 = FORMAT_PATTERN.matcher(var1);

      try {
         int var3 = 0;

         int var4;
         int var6;
         for(var4 = 0; var2.find(var4); var4 = var6) {
            int var5 = var2.start();
            var6 = var2.end();
            String var7;
            if (var5 > var4) {
               var7 = var1.substring(var4, var5);
               if (var7.indexOf(37) != -1) {
                  throw new IllegalArgumentException();
               }

               this.decomposedParts.add(FormattedText.of(var7));
            }

            var7 = var2.group(2);
            String var8 = var1.substring(var5, var6);
            if ("%".equals(var7) && "%%".equals(var8)) {
               this.decomposedParts.add(TEXT_PERCENT);
            } else {
               if (!"s".equals(var7)) {
                  throw new TranslatableFormatException(this, "Unsupported format: '" + var8 + "'");
               }

               String var9 = var2.group(1);
               int var10 = var9 != null ? Integer.parseInt(var9) - 1 : var3++;
               if (var10 < this.args.length) {
                  this.decomposedParts.add(this.getArgument(var10));
               }
            }
         }

         if (var4 < var1.length()) {
            String var12 = var1.substring(var4);
            if (var12.indexOf(37) != -1) {
               throw new IllegalArgumentException();
            }

            this.decomposedParts.add(FormattedText.of(var12));
         }

      } catch (IllegalArgumentException var11) {
         throw new TranslatableFormatException(this, var11);
      }
   }

   private FormattedText getArgument(int var1) {
      if (var1 >= this.args.length) {
         throw new TranslatableFormatException(this, var1);
      } else {
         Object var2 = this.args[var1];
         if (var2 instanceof Component) {
            return (Component)var2;
         } else {
            return var2 == null ? TEXT_NULL : FormattedText.of(var2.toString());
         }
      }
   }

   public TranslatableComponent plainCopy() {
      return new TranslatableComponent(this.key, this.args);
   }

   public <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      this.decompose();
      Iterator var3 = this.decomposedParts.iterator();

      Optional var5;
      do {
         if (!var3.hasNext()) {
            return Optional.empty();
         }

         FormattedText var4 = (FormattedText)var3.next();
         var5 = var4.visit(var1, var2);
      } while(!var5.isPresent());

      return var5;
   }

   public <T> Optional<T> visitSelf(FormattedText.ContentConsumer<T> var1) {
      this.decompose();
      Iterator var2 = this.decomposedParts.iterator();

      Optional var4;
      do {
         if (!var2.hasNext()) {
            return Optional.empty();
         }

         FormattedText var3 = (FormattedText)var2.next();
         var4 = var3.visit(var1);
      } while(!var4.isPresent());

      return var4;
   }

   public MutableComponent resolve(@Nullable CommandSourceStack var1, @Nullable Entity var2, int var3) throws CommandSyntaxException {
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
   public BaseComponent plainCopy() {
      return this.plainCopy();
   }

   // $FF: synthetic method
   public MutableComponent plainCopy() {
      return this.plainCopy();
   }
}
