package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;

public enum GraphicsStatus implements OptionEnum {
   FAST(0, "options.graphics.fast"),
   FANCY(1, "options.graphics.fancy"),
   FABULOUS(2, "options.graphics.fabulous");

   private static final GraphicsStatus[] BY_ID = (GraphicsStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(GraphicsStatus::getId)).toArray((var0) -> {
      return new GraphicsStatus[var0];
   });
   private final int id;
   private final String key;

   private GraphicsStatus(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public String toString() {
      switch (this) {
         case FAST:
            return "fast";
         case FANCY:
            return "fancy";
         case FABULOUS:
            return "fabulous";
         default:
            throw new IllegalArgumentException();
      }
   }

   public static GraphicsStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   // $FF: synthetic method
   private static GraphicsStatus[] $values() {
      return new GraphicsStatus[]{FAST, FANCY, FABULOUS};
   }
}
