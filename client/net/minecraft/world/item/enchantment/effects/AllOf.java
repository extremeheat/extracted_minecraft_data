package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;

public interface AllOf {
   static <T, A extends T> MapCodec<A> codec(final Codec<T> topLevelCodec, final Function<List<T>, A> constructor, final Function<A, List<T>> accessor) {
      return RecordCodecBuilder.mapCodec((i) -> i.group(topLevelCodec.listOf().fieldOf("effects").forGetter(accessor)).apply(i, constructor));
   }

   static EntityEffects entityEffects(final EnchantmentEntityEffect... effects) {
      return new EntityEffects(List.of(effects));
   }

   static LocationBasedEffects locationBasedEffects(final EnchantmentLocationBasedEffect... effects) {
      return new LocationBasedEffects(List.of(effects));
   }

   static ValueEffects valueEffects(final EnchantmentValueEffect... effects) {
      return new ValueEffects(List.of(effects));
   }

   public static record EntityEffects(List<EnchantmentEntityEffect> effects) implements EnchantmentEntityEffect {
      public static final MapCodec<EntityEffects> CODEC;

      public EntityEffects {
         super();
      }

      public void apply(final ServerLevel serverLevel, final int enchantmentLevel, final EnchantedItemInUse item, final Entity entity, final Vec3 position) {
         for(EnchantmentEntityEffect effect : this.effects) {
            effect.apply(serverLevel, enchantmentLevel, item, entity, position);
         }

      }

      public MapCodec<EntityEffects> codec() {
         return CODEC;
      }

      static {
         CODEC = AllOf.codec(EnchantmentEntityEffect.CODEC, EntityEffects::new, EntityEffects::effects);
      }
   }

   public static record LocationBasedEffects(List<EnchantmentLocationBasedEffect> effects) implements EnchantmentLocationBasedEffect {
      public static final MapCodec<LocationBasedEffects> CODEC;

      public LocationBasedEffects {
         super();
      }

      public void onChangedBlock(final ServerLevel serverLevel, final int enchantmentLevel, final EnchantedItemInUse item, final Entity entity, final Vec3 position, final boolean becameActive) {
         for(EnchantmentLocationBasedEffect effect : this.effects) {
            effect.onChangedBlock(serverLevel, enchantmentLevel, item, entity, position, becameActive);
         }

      }

      public void onDeactivated(final EnchantedItemInUse item, final Entity entity, final Vec3 position, final int level) {
         for(EnchantmentLocationBasedEffect effect : this.effects) {
            effect.onDeactivated(item, entity, position, level);
         }

      }

      public MapCodec<LocationBasedEffects> codec() {
         return CODEC;
      }

      static {
         CODEC = AllOf.codec(EnchantmentLocationBasedEffect.CODEC, LocationBasedEffects::new, LocationBasedEffects::effects);
      }
   }

   public static record ValueEffects(List<EnchantmentValueEffect> effects) implements EnchantmentValueEffect {
      public static final MapCodec<ValueEffects> CODEC;

      public ValueEffects {
         super();
      }

      public float process(final int enchantmentLevel, final RandomSource random, float value) {
         for(EnchantmentValueEffect effect : this.effects) {
            value = effect.process(enchantmentLevel, random, value);
         }

         return value;
      }

      public MapCodec<ValueEffects> codec() {
         return CODEC;
      }

      static {
         CODEC = AllOf.codec(EnchantmentValueEffect.CODEC, ValueEffects::new, ValueEffects::effects);
      }
   }
}
