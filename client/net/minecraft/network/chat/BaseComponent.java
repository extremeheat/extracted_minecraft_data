package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.locale.Language;
import net.minecraft.util.FormattedCharSequence;

public abstract class BaseComponent implements MutableComponent {
   protected final List<Component> siblings = Lists.newArrayList();
   private FormattedCharSequence visualOrderText;
   @Nullable
   private Language decomposedWith;
   private Style style;

   public BaseComponent() {
      super();
      this.visualOrderText = FormattedCharSequence.EMPTY;
      this.style = Style.EMPTY;
   }

   public MutableComponent append(Component var1) {
      this.siblings.add(var1);
      return this;
   }

   public String getContents() {
      return "";
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

   public abstract BaseComponent plainCopy();

   public final MutableComponent copy() {
      BaseComponent var1 = this.plainCopy();
      var1.siblings.addAll(this.siblings);
      var1.setStyle(this.style);
      return var1;
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
      } else if (!(var1 instanceof BaseComponent)) {
         return false;
      } else {
         BaseComponent var2 = (BaseComponent)var1;
         return this.siblings.equals(var2.siblings) && Objects.equals(this.getStyle(), var2.getStyle());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getStyle(), this.siblings});
   }

   public String toString() {
      return "BaseComponent{style=" + this.style + ", siblings=" + this.siblings + '}';
   }

   // $FF: synthetic method
   public MutableComponent plainCopy() {
      return this.plainCopy();
   }
}
