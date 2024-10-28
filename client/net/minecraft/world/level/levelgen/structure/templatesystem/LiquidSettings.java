package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum LiquidSettings implements StringRepresentable {
   IGNORE_WATERLOGGING("ignore_waterlogging"),
   APPLY_WATERLOGGING("apply_waterlogging");

   public static Codec<LiquidSettings> CODEC = StringRepresentable.fromValues(LiquidSettings::values);
   private final String name;

   private LiquidSettings(final String var3) {
      this.name = var3;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static LiquidSettings[] $values() {
      return new LiquidSettings[]{IGNORE_WATERLOGGING, APPLY_WATERLOGGING};
   }
}
