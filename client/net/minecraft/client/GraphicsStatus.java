package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum GraphicsStatus implements OptionEnum {
   FAST(0, "options.graphics.fast"),
   FANCY(1, "options.graphics.fancy"),
   FABULOUS(2, "options.graphics.fabulous");

   private static final IntFunction<GraphicsStatus> BY_ID = ByIdMap.continuous(GraphicsStatus::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
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
      String var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = "fast";
         case 1 -> var10000 = "fancy";
         case 2 -> var10000 = "fabulous";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static GraphicsStatus byId(int var0) {
      return (GraphicsStatus)BY_ID.apply(var0);
   }

   // $FF: synthetic method
   private static GraphicsStatus[] $values() {
      return new GraphicsStatus[]{FAST, FANCY, FABULOUS};
   }
}
