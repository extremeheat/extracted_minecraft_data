package net.minecraft.client;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.FormattedText;

public class ComponentCollector {
   private final List<FormattedText> parts = Lists.newArrayList();

   public ComponentCollector() {
      super();
   }

   public void append(FormattedText var1) {
      this.parts.add(var1);
   }

   @Nullable
   public FormattedText getResult() {
      if (this.parts.isEmpty()) {
         return null;
      } else {
         return this.parts.size() == 1 ? (FormattedText)this.parts.get(0) : FormattedText.composite(this.parts);
      }
   }

   public FormattedText getResultOrEmpty() {
      FormattedText var1 = this.getResult();
      return var1 != null ? var1 : FormattedText.EMPTY;
   }

   public void reset() {
      this.parts.clear();
   }
}
