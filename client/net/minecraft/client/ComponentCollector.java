package net.minecraft.client;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.FormattedText;
import org.jspecify.annotations.Nullable;

public class ComponentCollector {
   private final List<FormattedText> parts = new ArrayList();

   public ComponentCollector() {
      super();
   }

   public void append(final FormattedText component) {
      this.parts.add(component);
   }

   public @Nullable FormattedText getResult() {
      if (this.parts.isEmpty()) {
         return null;
      } else {
         return this.parts.size() == 1 ? (FormattedText)this.parts.get(0) : FormattedText.composite(this.parts);
      }
   }

   public FormattedText getResultOrEmpty() {
      FormattedText result = this.getResult();
      return result != null ? result : FormattedText.EMPTY;
   }

   public void reset() {
      this.parts.clear();
   }
}
