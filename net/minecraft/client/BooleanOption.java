package net.minecraft.client;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.resources.language.I18n;

public class BooleanOption extends Option {
   private final Predicate getter;
   private final BiConsumer setter;

   public BooleanOption(String var1, Predicate var2, BiConsumer var3) {
      super(var1);
      this.getter = var2;
      this.setter = var3;
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
      return new OptionButton(var2, var3, var4, 20, this, this.getMessage(var1), (var2x) -> {
         this.toggle(var1);
         var2x.setMessage(this.getMessage(var1));
      });
   }

   public String getMessage(Options var1) {
      return this.getCaption() + I18n.get(this.get(var1) ? "options.on" : "options.off");
   }
}
