package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;

public record DamageType(String msgId, DamageScaling scaling, float exhaustion, DamageEffects effects, DeathMessageType deathMessageType) {
   public static final Codec<DamageType> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.fieldOf("message_id").forGetter(DamageType::msgId), DamageScaling.CODEC.fieldOf("scaling").forGetter(DamageType::scaling), Codec.FLOAT.fieldOf("exhaustion").forGetter(DamageType::exhaustion), DamageEffects.CODEC.optionalFieldOf("effects", DamageEffects.HURT).forGetter(DamageType::effects), DeathMessageType.CODEC.optionalFieldOf("death_message_type", DeathMessageType.DEFAULT).forGetter(DamageType::deathMessageType)).apply(var0, DamageType::new));
   public static final Codec<Holder<DamageType>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DamageType>> STREAM_CODEC;

   public DamageType(String var1, DamageScaling var2, float var3) {
      this(var1, var2, var3, DamageEffects.HURT, DeathMessageType.DEFAULT);
   }

   public DamageType(String var1, DamageScaling var2, float var3, DamageEffects var4) {
      this(var1, var2, var3, var4, DeathMessageType.DEFAULT);
   }

   public DamageType(String var1, float var2, DamageEffects var3) {
      this(var1, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, var2, var3);
   }

   public DamageType(String var1, float var2) {
      this(var1, DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, var2);
   }

   public DamageType(String var1, DamageScaling var2, float var3, DamageEffects var4, DeathMessageType var5) {
      super();
      this.msgId = var1;
      this.scaling = var2;
      this.exhaustion = var3;
      this.effects = var4;
      this.deathMessageType = var5;
   }

   static {
      CODEC = RegistryFixedCodec.<Holder<DamageType>>create(Registries.DAMAGE_TYPE);
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.DAMAGE_TYPE);
   }
}
