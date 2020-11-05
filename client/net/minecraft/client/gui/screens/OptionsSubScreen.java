package net.minecraft.client.gui.screens;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class OptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;

   public OptionsSubScreen(Screen var1, Options var2, Component var3) {
      super(var3);
      this.lastScreen = var1;
      this.options = var2;
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   @Nullable
   public static List<FormattedCharSequence> tooltipAt(OptionsList var0, int var1, int var2) {
      Optional var3 = var0.getMouseOver((double)var1, (double)var2);
      if (var3.isPresent() && var3.get() instanceof TooltipAccessor) {
         Optional var4 = ((TooltipAccessor)var3.get()).getTooltip();
         return (List)var4.orElse((Object)null);
      } else {
         return null;
      }
   }
}
