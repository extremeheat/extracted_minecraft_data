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
   static <T, A extends T> MapCodec<A> codec(Codec<T> var0, Function<List<T>, A> var1, Function<A, List<T>> var2) {
      return RecordCodecBuilder.mapCodec((var3) -> var3.group(var0.listOf().fieldOf("effects").forGetter(var2)).apply(var3, var1));
   }

   static EntityEffects entityEffects(EnchantmentEntityEffect... var0) {
      return new EntityEffects(List.of(var0));
   }

   static LocationBasedEffects locationBasedEffects(EnchantmentLocationBasedEffect... var0) {
      return new LocationBasedEffects(List.of(var0));
   }

   static ValueEffects valueEffects(EnchantmentValueEffect... var0) {
      return new ValueEffects(List.of(var0));
   }

   public static record EntityEffects(List<EnchantmentEntityEffect> effects) implements EnchantmentEntityEffect {
      public static final MapCodec<EntityEffects> CODEC;

      public EntityEffects(List<EnchantmentEntityEffect> var1) {
         super();
         this.effects = var1;
      }

      public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
         for(EnchantmentEntityEffect var7 : this.effects) {
            var7.apply(var1, var2, var3, var4, var5);
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

      public LocationBasedEffects(List<EnchantmentLocationBasedEffect> var1) {
         super();
         this.effects = var1;
      }

      public void onChangedBlock(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5, boolean var6) {
         for(EnchantmentLocationBasedEffect var8 : this.effects) {
            var8.onChangedBlock(var1, var2, var3, var4, var5, var6);
         }

      }

      public void onDeactivated(EnchantedItemInUse var1, Entity var2, Vec3 var3, int var4) {
         for(EnchantmentLocationBasedEffect var6 : this.effects) {
            var6.onDeactivated(var1, var2, var3, var4);
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

      public ValueEffects(List<EnchantmentValueEffect> var1) {
         super();
         this.effects = var1;
      }

      public float process(int var1, RandomSource var2, float var3) {
         for(EnchantmentValueEffect var5 : this.effects) {
            var3 = var5.process(var1, var2, var3);
         }

         return var3;
      }

      public MapCodec<ValueEffects> codec() {
         return CODEC;
      }

      static {
         CODEC = AllOf.codec(EnchantmentValueEffect.CODEC, ValueEffects::new, ValueEffects::effects);
      }
   }
}
