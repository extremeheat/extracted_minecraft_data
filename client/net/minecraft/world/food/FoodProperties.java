package net.minecraft.world.food;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;

public record FoodProperties(int nutrition, float saturation, boolean canAlwaysEat) implements ConsumableListener {
   public static final Codec<FoodProperties> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodProperties::nutrition), Codec.FLOAT.fieldOf("saturation").forGetter(FoodProperties::saturation), Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(FoodProperties::canAlwaysEat)).apply(var0, FoodProperties::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, FoodProperties> DIRECT_STREAM_CODEC;

   public FoodProperties(int var1, float var2, boolean var3) {
      super();
      this.nutrition = var1;
      this.saturation = var2;
      this.canAlwaysEat = var3;
   }

   public void onConsume(Level var1, LivingEntity var2, ItemStack var3, Consumable var4) {
      RandomSource var5 = var2.getRandom();
      var1.playSound((Player)null, var2.getX(), var2.getY(), var2.getZ(), (SoundEvent)((SoundEvent)var4.sound().value()), SoundSource.NEUTRAL, 1.0F, var5.triangle(1.0F, 0.4F));
      if (var2 instanceof Player var6) {
         var6.getFoodData().eat(this);
         var1.playSound((Player)null, var6.getX(), var6.getY(), var6.getZ(), (SoundEvent)SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, Mth.randomBetween(var5, 0.9F, 1.0F));
      }

   }

   public int nutrition() {
      return this.nutrition;
   }

   public float saturation() {
      return this.saturation;
   }

   public boolean canAlwaysEat() {
      return this.canAlwaysEat;
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, FoodProperties::nutrition, ByteBufCodecs.FLOAT, FoodProperties::saturation, ByteBufCodecs.BOOL, FoodProperties::canAlwaysEat, FoodProperties::new);
   }

   public static class Builder {
      private int nutrition;
      private float saturationModifier;
      private boolean canAlwaysEat;

      public Builder() {
         super();
      }

      public Builder nutrition(int var1) {
         this.nutrition = var1;
         return this;
      }

      public Builder saturationModifier(float var1) {
         this.saturationModifier = var1;
         return this;
      }

      public Builder alwaysEdible() {
         this.canAlwaysEat = true;
         return this;
      }

      public FoodProperties build() {
         float var1 = FoodConstants.saturationByModifier(this.nutrition, this.saturationModifier);
         return new FoodProperties(this.nutrition, var1, this.canAlwaysEat);
      }
   }
}
