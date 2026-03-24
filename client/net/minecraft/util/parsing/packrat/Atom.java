package net.minecraft.util.parsing.packrat;

public record Atom<T>(String name) {
   public Atom {
      super();
   }

   public String toString() {
      return "<" + this.name + ">";
   }

   public static <T> Atom<T> of(final String name) {
      return new Atom<T>(name);
   }
}
