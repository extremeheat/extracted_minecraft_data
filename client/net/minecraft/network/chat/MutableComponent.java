package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

public final class MutableComponent implements Component {
   private final ComponentContents contents;
   private final List<Component> siblings;
   private Style style;
   private FormattedCharSequence visualOrderText;
   private @Nullable Language decomposedWith;

   MutableComponent(final ComponentContents contents, final List<Component> siblings, final Style style) {
      super();
      this.visualOrderText = FormattedCharSequence.EMPTY;
      this.contents = contents;
      this.siblings = siblings;
      this.style = style;
   }

   public static MutableComponent create(final ComponentContents contents) {
      return new MutableComponent(contents, Lists.newArrayList(), Style.EMPTY);
   }

   public ComponentContents getContents() {
      return this.contents;
   }

   public List<Component> getSiblings() {
      return this.siblings;
   }

   public MutableComponent setStyle(final Style style) {
      this.style = style;
      return this;
   }

   public Style getStyle() {
      return this.style;
   }

   public MutableComponent append(final String text) {
      return text.isEmpty() ? this : this.append((Component)Component.literal(text));
   }

   public MutableComponent append(final Component component) {
      this.siblings.add(component);
      return this;
   }

   public MutableComponent withStyle(final UnaryOperator<Style> updater) {
      this.setStyle((Style)updater.apply(this.getStyle()));
      return this;
   }

   public MutableComponent withStyle(final Style patch) {
      this.setStyle(patch.applyTo(this.getStyle()));
      return this;
   }

   public MutableComponent withStyle(final ChatFormatting... formats) {
      this.setStyle(this.getStyle().applyFormats(formats));
      return this;
   }

   public MutableComponent withStyle(final ChatFormatting format) {
      this.setStyle(this.getStyle().applyFormat(format));
      return this;
   }

   public MutableComponent withColor(final int color) {
      this.setStyle(this.getStyle().withColor(color));
      return this;
   }

   public MutableComponent withoutShadow() {
      this.setStyle(this.getStyle().withoutShadow());
      return this;
   }

   public FormattedCharSequence getVisualOrderText() {
      Language currentLanguage = Language.getInstance();
      if (this.decomposedWith != currentLanguage) {
         this.visualOrderText = currentLanguage.getVisualOrder(this);
         this.decomposedWith = currentLanguage;
      }

      return this.visualOrderText;
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof MutableComponent) {
            MutableComponent that = (MutableComponent)o;
            if (this.contents.equals(that.contents) && this.style.equals(that.style) && this.siblings.equals(that.siblings)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      int result = 1;
      result = 31 * result + this.contents.hashCode();
      result = 31 * result + this.style.hashCode();
      result = 31 * result + this.siblings.hashCode();
      return result;
   }

   public String toString() {
      StringBuilder result = new StringBuilder(this.contents.toString());
      boolean hasStyle = !this.style.isEmpty();
      boolean hasSiblings = !this.siblings.isEmpty();
      if (hasStyle || hasSiblings) {
         result.append('[');
         if (hasStyle) {
            result.append("style=");
            result.append(this.style);
         }

         if (hasStyle && hasSiblings) {
            result.append(", ");
         }

         if (hasSiblings) {
            result.append("siblings=");
            result.append(this.siblings);
         }

         result.append(']');
      }

      return result.toString();
   }
}
