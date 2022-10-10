package net.minecraft.util.text;

import java.util.function.Function;
import java.util.function.Supplier;

public class TextComponentKeybind extends TextComponentBase {
   public static Function<String, Supplier<String>> field_193637_b = (var0) -> {
      return () -> {
         return var0;
      };
   };
   private final String field_193638_c;
   private Supplier<String> field_193639_d;

   public TextComponentKeybind(String var1) {
      super();
      this.field_193638_c = var1;
   }

   public String func_150261_e() {
      if (this.field_193639_d == null) {
         this.field_193639_d = (Supplier)field_193637_b.apply(this.field_193638_c);
      }

      return (String)this.field_193639_d.get();
   }

   public TextComponentKeybind func_150259_f() {
      return new TextComponentKeybind(this.field_193638_c);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TextComponentKeybind)) {
         return false;
      } else {
         TextComponentKeybind var2 = (TextComponentKeybind)var1;
         return this.field_193638_c.equals(var2.field_193638_c) && super.equals(var1);
      }
   }

   public String toString() {
      return "KeybindComponent{keybind='" + this.field_193638_c + '\'' + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
   }

   public String func_193633_h() {
      return this.field_193638_c;
   }

   // $FF: synthetic method
   public ITextComponent func_150259_f() {
      return this.func_150259_f();
   }
}
