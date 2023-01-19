package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public enum Difficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final Difficulty[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray(var0 -> new Difficulty[var0]);
   private final int id;
   private final String key;

   private Difficulty(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   public int getId() {
      return this.id;
   }

   public Component getDisplayName() {
      return Component.translatable("options.difficulty." + this.key);
   }

   public static Difficulty byId(int var0) {
      return BY_ID[var0 % BY_ID.length];
   }

   @Nullable
   public static Difficulty byName(String var0) {
      for(Difficulty var4 : values()) {
         if (var4.key.equals(var0)) {
            return var4;
         }
      }

      return null;
   }

   public String getKey() {
      return this.key;
   }
}
