package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetFireworksFunction extends LootItemConditionalFunction {
   public static final Codec<SetFireworksFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  ExtraCodecs.strictOptionalField(ExtraCodecs.sizeLimitedList(FireworkExplosion.CODEC.listOf(), 256), "explosions", List.of())
                     .forGetter(var0x -> var0x.explosions),
                  ListOperation.Type.OPERATION_MAP_CODEC.forGetter(var0x -> var0x.explosionsOperation),
                  ExtraCodecs.strictOptionalField(ExtraCodecs.UNSIGNED_BYTE, "flight_duration").forGetter(var0x -> var0x.flightDuration)
               )
            )
            .apply(var0, SetFireworksFunction::new)
   );
   public static final Fireworks DEFAULT_VALUE = new Fireworks(0, List.of());
   private final List<FireworkExplosion> explosions;
   private final ListOperation explosionsOperation;
   private final Optional<Integer> flightDuration;

   protected SetFireworksFunction(List<LootItemCondition> var1, List<FireworkExplosion> var2, ListOperation var3, Optional<Integer> var4) {
      super(var1);
      this.explosions = var2;
      this.explosionsOperation = var3;
      this.flightDuration = var4;
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.FIREWORKS, DEFAULT_VALUE, this::apply);
      return var1;
   }

   private Fireworks apply(Fireworks var1) {
      List var2 = this.explosionsOperation.apply(var1.explosions(), this.explosions, 256);
      return new Fireworks(this.flightDuration.orElseGet(var1::flightDuration), var2);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_FIREWORKS;
   }
}
