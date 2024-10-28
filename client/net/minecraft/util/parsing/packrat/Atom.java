package net.minecraft.util.parsing.packrat;

public record Atom<T>(String name) {
   public Atom(String var1) {
      super();
      this.name = var1;
   }

   public String toString() {
      return "<" + this.name + ">";
   }

   public static <T> Atom<T> of(String var0) {
      return new Atom(var0);
   }

   public String name() {
      return this.name;
   }
}
