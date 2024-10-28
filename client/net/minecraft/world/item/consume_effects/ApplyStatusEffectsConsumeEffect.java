package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record ApplyStatusEffectsConsumeEffect(List<MobEffectInstance> effects, float probability) implements ConsumeEffect {
   public static final MapCodec<ApplyStatusEffectsConsumeEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(ApplyStatusEffectsConsumeEffect::effects), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(ApplyStatusEffectsConsumeEffect::probability)).apply(var0, ApplyStatusEffectsConsumeEffect::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, ApplyStatusEffectsConsumeEffect> STREAM_CODEC;

   public ApplyStatusEffectsConsumeEffect(MobEffectInstance var1, float var2) {
      this(List.of(var1), var2);
   }

   public ApplyStatusEffectsConsumeEffect(List<MobEffectInstance> var1) {
      this(var1, 1.0F);
   }

   public ApplyStatusEffectsConsumeEffect(MobEffectInstance var1) {
      this(var1, 1.0F);
   }

   public ApplyStatusEffectsConsumeEffect(List<MobEffectInstance> var1, float var2) {
      super();
      this.effects = var1;
      this.probability = var2;
   }

   public ConsumeEffect.Type<ApplyStatusEffectsConsumeEffect> getType() {
      return ConsumeEffect.Type.APPLY_EFFECTS;
   }

   public boolean apply(Level var1, ItemStack var2, LivingEntity var3) {
      if (var3.getRandom().nextFloat() >= this.probability) {
         return false;
      } else {
         boolean var4 = false;
         Iterator var5 = this.effects.iterator();

         while(var5.hasNext()) {
            MobEffectInstance var6 = (MobEffectInstance)var5.next();
            if (var3.addEffect(new MobEffectInstance(var6))) {
               var4 = true;
            }
         }

         return var4;
      }
   }

   public List<MobEffectInstance> effects() {
      return this.effects;
   }

   public float probability() {
      return this.probability;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), ApplyStatusEffectsConsumeEffect::effects, ByteBufCodecs.FLOAT, ApplyStatusEffectsConsumeEffect::probability, ApplyStatusEffectsConsumeEffect::new);
   }
}
