package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.world.scores.PlayerTeam;

public record ConversionParams(ConversionType type, boolean keepEquipment, boolean preserveCanPickUpLoot, @Nullable PlayerTeam team) {
   public ConversionParams(ConversionType var1, boolean var2, boolean var3, @Nullable PlayerTeam var4) {
      super();
      this.type = var1;
      this.keepEquipment = var2;
      this.preserveCanPickUpLoot = var3;
      this.team = var4;
   }

   public static ConversionParams single(Mob var0, boolean var1, boolean var2) {
      return new ConversionParams(ConversionType.SINGLE, var1, var2, var0.getTeam());
   }

   @FunctionalInterface
   public interface AfterConversion<T extends Mob> {
      void finalizeConversion(T var1);
   }
}
