package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetCustomModelDataFunction extends LootItemConditionalFunction {
   private static final Codec<NumberProvider> COLOR_PROVIDER_CODEC;
   public static final MapCodec<SetCustomModelDataFunction> CODEC;
   private final Optional<ListOperation.StandAlone<NumberProvider>> floats;
   private final Optional<ListOperation.StandAlone<Boolean>> flags;
   private final Optional<ListOperation.StandAlone<String>> strings;
   private final Optional<ListOperation.StandAlone<NumberProvider>> colors;

   public SetCustomModelDataFunction(List<LootItemCondition> var1, Optional<ListOperation.StandAlone<NumberProvider>> var2, Optional<ListOperation.StandAlone<Boolean>> var3, Optional<ListOperation.StandAlone<String>> var4, Optional<ListOperation.StandAlone<NumberProvider>> var5) {
      super(var1);
      this.floats = var2;
      this.flags = var3;
      this.strings = var4;
      this.colors = var5;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return (Set)Stream.concat(this.floats.stream(), this.colors.stream()).flatMap((var0) -> var0.value().stream()).flatMap((var0) -> var0.getReferencedContextParams().stream()).collect(Collectors.toSet());
   }

   public LootItemFunctionType<SetCustomModelDataFunction> getType() {
      return LootItemFunctions.SET_CUSTOM_MODEL_DATA;
   }

   private static <T> List<T> apply(Optional<ListOperation.StandAlone<T>> var0, List<T> var1) {
      return (List)var0.map((var1x) -> var1x.apply(var1)).orElse(var1);
   }

   private static <T, E> List<E> apply(Optional<ListOperation.StandAlone<T>> var0, List<E> var1, Function<T, E> var2) {
      return (List)var0.map((var2x) -> {
         List var3 = var2x.value().stream().map(var2).toList();
         return var2x.operation().apply(var1, var3);
      }).orElse(var1);
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      CustomModelData var3 = (CustomModelData)var1.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.EMPTY);
      var1.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(apply(this.floats, var3.floats(), (var1x) -> var1x.getFloat(var2)), apply(this.flags, var3.flags()), apply(this.strings, var3.strings()), apply(this.colors, var3.colors(), (var1x) -> var1x.getInt(var2))));
      return var1;
   }

   static {
      COLOR_PROVIDER_CODEC = Codec.withAlternative(NumberProviders.CODEC, ExtraCodecs.RGB_COLOR_CODEC, ConstantValue::new);
      CODEC = RecordCodecBuilder.mapCodec((var0) -> commonFields(var0).and(var0.group(ListOperation.StandAlone.codec(NumberProviders.CODEC, 2147483647).optionalFieldOf("floats").forGetter((var0x) -> var0x.floats), ListOperation.StandAlone.codec(Codec.BOOL, 2147483647).optionalFieldOf("flags").forGetter((var0x) -> var0x.flags), ListOperation.StandAlone.codec(Codec.STRING, 2147483647).optionalFieldOf("strings").forGetter((var0x) -> var0x.strings), ListOperation.StandAlone.codec(COLOR_PROVIDER_CODEC, 2147483647).optionalFieldOf("colors").forGetter((var0x) -> var0x.colors))).apply(var0, SetCustomModelDataFunction::new));
   }
}
