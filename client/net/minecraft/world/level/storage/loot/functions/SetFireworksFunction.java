package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireworksFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetFireworksFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(ListOperation.StandAlone.codec(FireworkExplosion.CODEC, 256).optionalFieldOf("explosions").forGetter((var0x) -> {
         return var0x.explosions;
      }), ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("flight_duration").forGetter((var0x) -> {
         return var0x.flightDuration;
      }))).apply(var0, SetFireworksFunction::new);
   });
   public static final Fireworks DEFAULT_VALUE = new Fireworks(0, List.of());
   private final Optional<ListOperation.StandAlone<FireworkExplosion>> explosions;
   private final Optional<Integer> flightDuration;

   protected SetFireworksFunction(List<LootItemCondition> var1, Optional<ListOperation.StandAlone<FireworkExplosion>> var2, Optional<Integer> var3) {
      super(var1);
      this.explosions = var2;
      this.flightDuration = var3;
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.FIREWORKS, DEFAULT_VALUE, this::apply);
      return var1;
   }

   private Fireworks apply(Fireworks var1) {
      Optional var10002 = this.flightDuration;
      Objects.requireNonNull(var1);
      return new Fireworks((Integer)var10002.orElseGet(var1::flightDuration), (List)this.explosions.map((var1x) -> {
         return var1x.apply(var1.explosions());
      }).orElse(var1.explosions()));
   }

   public LootItemFunctionType<SetFireworksFunction> getType() {
      return LootItemFunctions.SET_FIREWORKS;
   }
}
