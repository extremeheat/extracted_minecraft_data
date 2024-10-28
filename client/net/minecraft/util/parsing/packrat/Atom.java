package net.minecraft.util.parsing.packrat;

public record Atom<T>(String name) {
   public Atom(String name) {
      super();
      this.name = name;
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
