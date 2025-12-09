package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum CloudStatus implements StringRepresentable {
   OFF("false", "options.off"),
   FAST("fast", "options.clouds.fast"),
   FANCY("true", "options.clouds.fancy");

   public static final Codec<CloudStatus> CODEC = StringRepresentable.<CloudStatus>fromEnum(CloudStatus::values);
   private final String legacyName;
   private final Component caption;

   private CloudStatus(final String var3, final String var4) {
      this.legacyName = var3;
      this.caption = Component.translatable(var4);
   }

   public Component caption() {
      return this.caption;
   }

   public String getSerializedName() {
      return this.legacyName;
   }

   // $FF: synthetic method
   private static CloudStatus[] $values() {
      return new CloudStatus[]{OFF, FAST, FANCY};
   }
}
