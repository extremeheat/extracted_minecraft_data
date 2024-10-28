package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;

public record DamageType(String msgId, DamageScaling scaling, float exhaustion, DamageEffects effects, DeathMessageType deathMessageType) {
   public static final Codec<DamageType> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.STRING.fieldOf("message_id").forGetter(DamageType::msgId), DamageScaling.CODEC.fieldOf("scaling").forGetter(DamageType::scaling), Codec.FLOAT.fieldOf("exhaustion").forGetter(DamageType::exhaustion), DamageEffects.CODEC.optionalFieldOf("effects", DamageEffects.HURT).forGetter(DamageType::effects), DeathMessageType.CODEC.optionalFieldOf("death_message_type", DeathMessageType.DEFAULT).forGetter(DamageType::deathMessageType)).apply(var0, DamageType::new);
   });
   public static final Codec<Holder<DamageType>> CODEC;

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

   public DamageType(String msgId, DamageScaling scaling, float exhaustion, DamageEffects effects, DeathMessageType deathMessageType) {
      super();
      this.msgId = msgId;
      this.scaling = scaling;
      this.exhaustion = exhaustion;
      this.effects = effects;
      this.deathMessageType = deathMessageType;
   }

   public String msgId() {
      return this.msgId;
   }

   public DamageScaling scaling() {
      return this.scaling;
   }

   public float exhaustion() {
      return this.exhaustion;
   }

   public DamageEffects effects() {
      return this.effects;
   }

   public DeathMessageType deathMessageType() {
      return this.deathMessageType;
   }

   static {
      CODEC = RegistryFixedCodec.create(Registries.DAMAGE_TYPE);
   }
}
