package net.minecraft.network.chat;

import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;

public interface MutableComponent extends Component {
   MutableComponent setStyle(Style var1);

   default MutableComponent append(String var1) {
      return this.append((Component)(new TextComponent(var1)));
   }

   MutableComponent append(Component var1);

   default MutableComponent withStyle(UnaryOperator<Style> var1) {
      this.setStyle((Style)var1.apply(this.getStyle()));
      return this;
   }

   default MutableComponent withStyle(Style var1) {
      this.setStyle(var1.applyTo(this.getStyle()));
      return this;
   }

   default MutableComponent withStyle(ChatFormatting... var1) {
      this.setStyle(this.getStyle().applyFormats(var1));
      return this;
   }

   default MutableComponent withStyle(ChatFormatting var1) {
      this.setStyle(this.getStyle().applyFormat(var1));
      return this;
   }
}
