package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ConsumeEffect {
   Codec<ConsumeEffect> CODEC = BuiltInRegistries.CONSUME_EFFECT_TYPE.byNameCodec().dispatch(ConsumeEffect::getType, Type::codec);
   StreamCodec<RegistryFriendlyByteBuf, ConsumeEffect> STREAM_CODEC = ByteBufCodecs.registry(Registries.CONSUME_EFFECT_TYPE).dispatch(ConsumeEffect::getType, Type::streamCodec);

   Type<? extends ConsumeEffect> getType();

   boolean apply(final Level level, final ItemStack stack, final LivingEntity user);

   public static record Type<T extends ConsumeEffect>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
      public static final Type<ApplyStatusEffectsConsumeEffect> APPLY_EFFECTS;
      public static final Type<RemoveStatusEffectsConsumeEffect> REMOVE_EFFECTS;
      public static final Type<ClearAllStatusEffectsConsumeEffect> CLEAR_ALL_EFFECTS;
      public static final Type<TeleportRandomlyConsumeEffect> TELEPORT_RANDOMLY;
      public static final Type<PlaySoundConsumeEffect> PLAY_SOUND;

      public Type {
         super();
      }

      private static <T extends ConsumeEffect> Type<T> register(final String name, final MapCodec<T> codec, final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
         return (Type)Registry.register(BuiltInRegistries.CONSUME_EFFECT_TYPE, (String)name, new Type(codec, streamCodec));
      }

      static {
         APPLY_EFFECTS = register("apply_effects", ApplyStatusEffectsConsumeEffect.CODEC, ApplyStatusEffectsConsumeEffect.STREAM_CODEC);
         REMOVE_EFFECTS = register("remove_effects", RemoveStatusEffectsConsumeEffect.CODEC, RemoveStatusEffectsConsumeEffect.STREAM_CODEC);
         CLEAR_ALL_EFFECTS = register("clear_all_effects", ClearAllStatusEffectsConsumeEffect.CODEC, ClearAllStatusEffectsConsumeEffect.STREAM_CODEC);
         TELEPORT_RANDOMLY = register("teleport_randomly", TeleportRandomlyConsumeEffect.CODEC, TeleportRandomlyConsumeEffect.STREAM_CODEC);
         PLAY_SOUND = register("play_sound", PlaySoundConsumeEffect.CODEC, PlaySoundConsumeEffect.STREAM_CODEC);
      }
   }
}
