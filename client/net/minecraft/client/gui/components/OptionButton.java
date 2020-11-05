package net.minecraft.client.gui.components;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.Option;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class OptionButton extends Button implements TooltipAccessor {
   private final Option option;

   public OptionButton(int var1, int var2, int var3, int var4, Option var5, Component var6, Button.OnPress var7) {
      super(var1, var2, var3, var4, var6, var7);
      this.option = var5;
   }

   public Option getOption() {
      return this.option;
   }

   public Optional<List<FormattedCharSequence>> getTooltip() {
      return this.option.getTooltip();
   }
}
