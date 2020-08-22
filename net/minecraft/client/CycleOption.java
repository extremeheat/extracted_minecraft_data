package net.minecraft.client;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionButton;

public class CycleOption extends Option {
   private final BiConsumer setter;
   private final BiFunction toString;

   public CycleOption(String var1, BiConsumer var2, BiFunction var3) {
      super(var1);
      this.setter = var2;
      this.toString = var3;
   }

   public void toggle(Options var1, int var2) {
      this.setter.accept(var1, var2);
      var1.save();
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4) {
      return new OptionButton(var2, var3, var4, 20, this, this.getMessage(var1), (var2x) -> {
         this.toggle(var1, 1);
         var2x.setMessage(this.getMessage(var1));
      });
   }

   public String getMessage(Options var1) {
      return (String)this.toString.apply(var1, this);
   }
}
