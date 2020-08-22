package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum Difficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final Difficulty[] BY_ID = (Difficulty[])Arrays.stream(values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray((var0) -> {
      return new Difficulty[var0];
   });
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
      return new TranslatableComponent("options.difficulty." + this.key, new Object[0]);
   }

   public static Difficulty byId(int var0) {
      return BY_ID[var0 % BY_ID.length];
   }

   @Nullable
   public static Difficulty byName(String var0) {
      Difficulty[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Difficulty var4 = var1[var3];
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
