package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

public enum CloudStatus implements OptionEnum, StringRepresentable {
   OFF(0, "false", "options.off"),
   FAST(1, "fast", "options.clouds.fast"),
   FANCY(2, "true", "options.clouds.fancy");

   public static final Codec<CloudStatus> CODEC = StringRepresentable.<CloudStatus>fromEnum(CloudStatus::values);
   private final int id;
   private final String legacyName;
   private final String key;

   private CloudStatus(final int var3, final String var4, final String var5) {
      this.id = var3;
      this.legacyName = var4;
      this.key = var5;
   }

   public String getSerializedName() {
      return this.legacyName;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   // $FF: synthetic method
   private static CloudStatus[] $values() {
      return new CloudStatus[]{OFF, FAST, FANCY};
   }
}
