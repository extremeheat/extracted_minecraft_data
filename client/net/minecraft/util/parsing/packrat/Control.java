package net.minecraft.util.parsing.packrat;

public interface Control {
   Control UNBOUND = new Control() {
      public void cut() {
      }

      public boolean hasCut() {
         return false;
      }
   };

   void cut();

   boolean hasCut();
}
