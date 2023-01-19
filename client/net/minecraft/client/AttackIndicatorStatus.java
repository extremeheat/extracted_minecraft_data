package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum AttackIndicatorStatus implements OptionEnum {
   OFF(0, "options.off"),
   CROSSHAIR(1, "options.attack.crosshair"),
   HOTBAR(2, "options.attack.hotbar");

   private static final IntFunction<AttackIndicatorStatus> BY_ID = ByIdMap.continuous(AttackIndicatorStatus::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
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
      return BY_ID.apply(var0);
   }
}
