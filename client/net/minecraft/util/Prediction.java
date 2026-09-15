package net.minecraft.util;

public enum Prediction {
   PREDICTED,
   SERVER_ONLY;

   private Prediction() {
   }

   // $FF: synthetic method
   private static Prediction[] $values() {
      return new Prediction[]{PREDICTED, SERVER_ONLY};
   }
}
