package net.minecraft.world.entity.ai.attributes;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

public class Attribute {
   public static final Codec<Holder<Attribute>> CODEC = BuiltInRegistries.ATTRIBUTE.holderByNameCodec();
   private final double defaultValue;
   private boolean syncable;
   private final String descriptionId;
   private Attribute.Sentiment sentiment = Attribute.Sentiment.POSITIVE;

   protected Attribute(String var1, double var2) {
      super();
      this.defaultValue = var2;
      this.descriptionId = var1;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isClientSyncable() {
      return this.syncable;
   }

   public Attribute setSyncable(boolean var1) {
      this.syncable = var1;
      return this;
   }

   public Attribute setSentiment(Attribute.Sentiment var1) {
      this.sentiment = var1;
      return this;
   }

   public double sanitizeValue(double var1) {
      return var1;
   }

   public String getDescriptionId() {
      return this.descriptionId;
   }

   public ChatFormatting getStyle(boolean var1) {
      return this.sentiment.getStyle(var1);
   }

   public static enum Sentiment {
      POSITIVE,
      NEUTRAL,
      NEGATIVE;

      private Sentiment() {
      }

      public ChatFormatting getStyle(boolean var1) {
         return switch (this) {
            case POSITIVE -> var1 ? ChatFormatting.BLUE : ChatFormatting.RED;
            case NEUTRAL -> ChatFormatting.GRAY;
            case NEGATIVE -> var1 ? ChatFormatting.RED : ChatFormatting.BLUE;
         };
      }
   }
}
