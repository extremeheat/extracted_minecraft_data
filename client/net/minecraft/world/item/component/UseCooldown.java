package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record UseCooldown(float seconds, Optional<ResourceLocation> cooldownGroup) {
   public static final Codec<UseCooldown> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.POSITIVE_FLOAT.fieldOf("seconds").forGetter(UseCooldown::seconds), ResourceLocation.CODEC.optionalFieldOf("cooldown_group").forGetter(UseCooldown::cooldownGroup)).apply(var0, UseCooldown::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, UseCooldown> STREAM_CODEC;

   public UseCooldown(float var1) {
      this(var1, Optional.empty());
   }

   public UseCooldown(float var1, Optional<ResourceLocation> var2) {
      super();
      this.seconds = var1;
      this.cooldownGroup = var2;
   }

   public int ticks() {
      return (int)(this.seconds * 20.0F);
   }

   public void apply(ItemStack var1, LivingEntity var2) {
      if (var2 instanceof Player var3) {
         var3.getCooldowns().addCooldown(var1, this.ticks());
      }

   }

   public float seconds() {
      return this.seconds;
   }

   public Optional<ResourceLocation> cooldownGroup() {
      return this.cooldownGroup;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, UseCooldown::seconds, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs::optional), UseCooldown::cooldownGroup, UseCooldown::new);
   }
}
