package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;

public enum AttackIndicatorStatus {
   OFF(0, "options.off"),
   CROSSHAIR(1, "options.attack.crosshair"),
   HOTBAR(2, "options.attack.hotbar");

   private static final AttackIndicatorStatus[] BY_ID = (AttackIndicatorStatus[])Arrays.stream(values()).sorted(Comparator.comparingInt(AttackIndicatorStatus::getId)).toArray((var0) -> {
      return new AttackIndicatorStatus[var0];
   });
   // $FF: renamed from: id int
   private final int field_420;
   private final String key;

   private AttackIndicatorStatus(int var3, String var4) {
      this.field_420 = var3;
      this.key = var4;
   }

   public int getId() {
      return this.field_420;
   }

   public String getKey() {
      return this.key;
   }

   public static AttackIndicatorStatus byId(int var0) {
      return BY_ID[Mth.positiveModulo(var0, BY_ID.length)];
   }

   // $FF: synthetic method
   private static AttackIndicatorStatus[] $values() {
      return new AttackIndicatorStatus[]{OFF, CROSSHAIR, HOTBAR};
   }
}
