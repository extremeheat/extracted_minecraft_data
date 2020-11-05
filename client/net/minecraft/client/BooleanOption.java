package net.minecraft.client;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BooleanOption extends Option {
   private final Predicate<Options> getter;
   private final BiConsumer<Options, Boolean> setter;
   @Nullable
   private final Component tooltipText;

   public BooleanOption(String var1, Predicate<Options> var2, BiConsumer<Options, Boolean> var3) {
      this(var1, (Component)null, var2, var3);
   }

   public BooleanOption(String var1, @Nullable Component var2, Predicate<Options> var3, BiConsumer<Options, Boolean> var4) {
      super(var1);
      this.getter = var3;
      this.setter = var4;
      this.tooltipText = var2;
   }

   public void set(Options var1, String var2) {
      this.set(var1, "true".equals(var2));
   }

   public void toggle(Options var1) {
      this.set(var1, !this.get(var1));
      var1.save();
   }

   private void set(Options var1, boolean var2) {
      this.setter.accept(var1, var2);
   }

   public boolean get(Options var1) {
      return this.getter.test(var1);
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4) {
      if (this.tooltipText != null) {
         this.setTooltip(Minecraft.getInstance().font.split(this.tooltipText, 200));
      }

      return new OptionButton(var2, var3, var4, 20, this, this.getMessage(var1), (var2x) -> {
         this.toggle(var1);
         var2x.setMessage(this.getMessage(var1));
      });
   }

   public Component getMessage(Options var1) {
      return CommonComponents.optionStatus(this.getCaption(), this.get(var1));
   }
}
