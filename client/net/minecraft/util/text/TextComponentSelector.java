package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextComponentSelector extends TextComponentBase {
   private static final Logger field_197669_b = LogManager.getLogger();
   private final String field_179993_b;
   @Nullable
   private final EntitySelector field_197670_d;

   public TextComponentSelector(String var1) {
      super();
      this.field_179993_b = var1;
      EntitySelector var2 = null;

      try {
         EntitySelectorParser var3 = new EntitySelectorParser(new StringReader(var1));
         var2 = var3.func_201345_m();
      } catch (CommandSyntaxException var4) {
         field_197669_b.warn("Invalid selector component: {}", var1, var4.getMessage());
      }

      this.field_197670_d = var2;
   }

   public String func_179992_g() {
      return this.field_179993_b;
   }

   public ITextComponent func_197668_a(CommandSource var1) throws CommandSyntaxException {
      return (ITextComponent)(this.field_197670_d == null ? new TextComponentString("") : EntitySelector.func_197350_a(this.field_197670_d.func_197341_b(var1)));
   }

   public String func_150261_e() {
      return this.field_179993_b;
   }

   public TextComponentSelector func_150259_f() {
      return new TextComponentSelector(this.field_179993_b);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TextComponentSelector)) {
         return false;
      } else {
         TextComponentSelector var2 = (TextComponentSelector)var1;
         return this.field_179993_b.equals(var2.field_179993_b) && super.equals(var1);
      }
   }

   public String toString() {
      return "SelectorComponent{pattern='" + this.field_179993_b + '\'' + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
   }

   // $FF: synthetic method
   public ITextComponent func_150259_f() {
      return this.func_150259_f();
   }
}
