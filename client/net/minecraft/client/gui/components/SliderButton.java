package net.minecraft.client.gui.components;

import java.util.List;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.util.FormattedCharSequence;

public class SliderButton extends AbstractOptionSliderButton implements TooltipAccessor {
   private final ProgressOption option;
   private final List<FormattedCharSequence> tooltip;

   public SliderButton(Options var1, int var2, int var3, int var4, int var5, ProgressOption var6, List<FormattedCharSequence> var7) {
      super(var1, var2, var3, var4, var5, (double)((float)var6.toPct(var6.get(var1))));
      this.option = var6;
      this.tooltip = var7;
      this.updateMessage();
   }

   protected void applyValue() {
      this.option.set(this.options, this.option.toValue(this.value));
      this.options.save();
   }

   protected void updateMessage() {
      this.setMessage(this.option.getMessage(this.options));
   }

   public List<FormattedCharSequence> getTooltip() {
      return this.tooltip;
   }
}
