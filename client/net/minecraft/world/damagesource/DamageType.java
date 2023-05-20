package net.minecraft.world.damagesource;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DamageType(String b, DamageScaling c, float d, DamageEffects e, DeathMessageType f) {
   private final String msgId;
   private final DamageScaling scaling;
   private final float exhaustion;
   private final DamageEffects effects;
   private final DeathMessageType deathMessageType;
   public static final Codec<DamageType> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.STRING.fieldOf("message_id").forGetter(DamageType::msgId),
               DamageScaling.CODEC.fieldOf("scaling").forGetter(DamageType::scaling),
               Codec.FLOAT.fieldOf("exhaustion").forGetter(DamageType::exhaustion),
               DamageEffects.CODEC.optionalFieldOf("effects", DamageEffects.HURT).forGetter(DamageType::effects),
               DeathMessageType.CODEC.optionalFieldOf("death_message_type", DeathMessageType.DEFAULT).forGetter(DamageType::deathMessageType)
            )
            .apply(var0, DamageType::new)
   );

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
}
