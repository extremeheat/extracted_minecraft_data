package net.minecraft.world.entity.ai.attributes;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class Attribute {
   public static final Codec<Holder<Attribute>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Attribute>> STREAM_CODEC;
   private final double defaultValue;
   private boolean syncable;
   private final String descriptionId;
   private Sentiment sentiment;

   protected Attribute(String var1, double var2) {
      super();
      this.sentiment = Attribute.Sentiment.POSITIVE;
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

   public Attribute setSentiment(Sentiment var1) {
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

   static {
      CODEC = BuiltInRegistries.ATTRIBUTE.holderByNameCodec();
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE);
   }

   public static enum Sentiment {
      POSITIVE,
      NEUTRAL,
      NEGATIVE;

      private Sentiment() {
      }

      public ChatFormatting getStyle(boolean var1) {
         ChatFormatting var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1 ? ChatFormatting.BLUE : ChatFormatting.RED;
            case 1 -> var10000 = ChatFormatting.GRAY;
            case 2 -> var10000 = var1 ? ChatFormatting.RED : ChatFormatting.BLUE;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Sentiment[] $values() {
         return new Sentiment[]{POSITIVE, NEUTRAL, NEGATIVE};
      }
   }
}
