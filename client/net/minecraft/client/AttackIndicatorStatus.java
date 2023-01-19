package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;

public enum AttackIndicatorStatus implements OptionEnum {
   OFF(0, "options.off"),
   CROSSHAIR(1, "options.attack.crosshair"),
   HOTBAR(2, "options.attack.hotbar");

   private static final AttackIndicatorStatus[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(AttackIndicatorStatus::getId))
      .toArray(var0 -> new AttackIndicatorStatus[var0]);
   private final int id;
   private final String key;

   private AttackIndicatorStatus(int var3, String var4) {
      this.id = var3;
      this.key = var4;
   }

   @Override
   public int getId() {
      return this.id;
   }

   @Override
   public String getKey() {
      return this.key;
   }

   public static AttackIndicatorStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }
}
