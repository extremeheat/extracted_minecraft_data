package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;

public class MutableComponent implements Component {
   private final ComponentContents contents;
   private final List<Component> siblings;
   private Style style;
   private FormattedCharSequence visualOrderText;
   @Nullable
   private Language decomposedWith;

   MutableComponent(ComponentContents var1, List<Component> var2, Style var3) {
      super();
      this.visualOrderText = FormattedCharSequence.EMPTY;
      this.contents = var1;
      this.siblings = var2;
      this.style = var3;
   }

   public static MutableComponent create(ComponentContents var0) {
      return new MutableComponent(var0, Lists.newArrayList(), Style.EMPTY);
   }

   public ComponentContents getContents() {
      return this.contents;
   }

   public List<Component> getSiblings() {
      return this.siblings;
   }

   public MutableComponent setStyle(Style var1) {
      this.style = var1;
      return this;
   }

   public Style getStyle() {
      return this.style;
   }

   public MutableComponent append(String var1) {
      return var1.isEmpty() ? this : this.append((Component)Component.literal(var1));
   }

   public MutableComponent append(Component var1) {
      this.siblings.add(var1);
      return this;
   }

   public MutableComponent withStyle(UnaryOperator<Style> var1) {
      this.setStyle((Style)var1.apply(this.getStyle()));
      return this;
   }

   public MutableComponent withStyle(Style var1) {
      this.setStyle(var1.applyTo(this.getStyle()));
      return this;
   }

   public MutableComponent withStyle(ChatFormatting... var1) {
      this.setStyle(this.getStyle().applyFormats(var1));
      return this;
   }

   public MutableComponent withStyle(ChatFormatting var1) {
      this.setStyle(this.getStyle().applyFormat(var1));
      return this;
   }

   public MutableComponent withColor(int var1) {
      this.setStyle(this.getStyle().withColor(var1));
      return this;
   }

   public FormattedCharSequence getVisualOrderText() {
      Language var1 = Language.getInstance();
      if (this.decomposedWith != var1) {
         this.visualOrderText = var1.getVisualOrder((FormattedText)this);
         this.decomposedWith = var1;
      }

      return this.visualOrderText;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof MutableComponent)) {
         return false;
      } else {
         MutableComponent var2 = (MutableComponent)var1;
         return this.contents.equals(var2.contents) && this.style.equals(var2.style) && this.siblings.equals(var2.siblings);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.contents, this.style, this.siblings});
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.contents.toString());
      boolean var2 = !this.style.isEmpty();
      boolean var3 = !this.siblings.isEmpty();
      if (var2 || var3) {
         var1.append('[');
         if (var2) {
            var1.append("style=");
            var1.append(this.style);
         }

         if (var2 && var3) {
            var1.append(", ");
         }

         if (var3) {
            var1.append("siblings=");
            var1.append(this.siblings);
         }

         var1.append(']');
      }

      return var1.toString();
   }
}
