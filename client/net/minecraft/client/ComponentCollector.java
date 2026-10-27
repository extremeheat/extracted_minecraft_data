package net.minecraft.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.FormattedText;
import org.jspecify.annotations.Nullable;

public class ComponentCollector {
   private List<FormattedText> parts = new ArrayList();

   public ComponentCollector() {
      super();
   }

   public void append(final FormattedText component) {
      this.parts.add(component);
   }

   public @Nullable FormattedText getResult() {
      int size = this.parts.size();
      if (size == 0) {
         return null;
      } else {
         return size == 1 ? (FormattedText)this.parts.getFirst() : FormattedText.composite(this.parts);
      }
   }

   public FormattedText getResultOrEmpty() {
      FormattedText result = this.getResult();
      return result != null ? result : FormattedText.EMPTY;
   }

   public void reset() {
      int size = this.parts.size();
      if (size > 0) {
         if (size == 1) {
            this.parts.clear();
         } else {
            this.parts = new ArrayList();
         }
      }

   }
}
