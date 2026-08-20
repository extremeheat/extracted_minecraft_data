package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class SetCustomModelDataFunction extends LootItemConditionalFunction {
   private static final Codec<Holder<NumberProvider>> COLOR_PROVIDER_CODEC;
   public static final MapCodec<SetCustomModelDataFunction> MAP_CODEC;
   private final Optional<ListOperation.StandAlone<Holder<NumberProvider>>> floats;
   private final Optional<ListOperation.StandAlone<Boolean>> flags;
   private final Optional<ListOperation.StandAlone<String>> strings;
   private final Optional<ListOperation.StandAlone<Holder<NumberProvider>>> colors;

   public SetCustomModelDataFunction(final Optional<Holder<LootItemCondition>> condition, final Optional<ListOperation.StandAlone<Holder<NumberProvider>>> floats, final Optional<ListOperation.StandAlone<Boolean>> flags, final Optional<ListOperation.StandAlone<String>> strings, final Optional<ListOperation.StandAlone<Holder<NumberProvider>>> colors) {
      super(condition);
      this.floats = floats;
      this.flags = flags;
      this.strings = strings;
      this.colors = colors;
   }

   public void validate(final ValidationContext context) {
      super.validate(context);
      this.floats.ifPresent((f) -> Validatable.validateHolder(context, "floats", f.value()));
      this.colors.ifPresent((c) -> Validatable.validateHolder(context, "colors", c.value()));
   }

   public MapCodec<SetCustomModelDataFunction> codec() {
      return MAP_CODEC;
   }

   private static <T> List<T> apply(final Optional<ListOperation.StandAlone<T>> operation, final List<T> current) {
      return (List)operation.map((o) -> o.apply(current)).orElse(current);
   }

   private static <T, E> List<E> apply(final Optional<ListOperation.StandAlone<T>> operation, final List<E> current, final Function<T, E> mapper) {
      return (List)operation.map((o) -> {
         List<E> transformedReplacement = o.value().stream().map(mapper).toList();
         return o.operation().apply(current, transformedReplacement);
      }).orElse(current);
   }

   public ItemStack run(final ItemStack itemStack, final LootContext context) {
      CustomModelData component = (CustomModelData)itemStack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.EMPTY);
      itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(apply(this.floats, component.floats(), (provider) -> ((NumberProvider)provider.value()).getFloat(context)), apply(this.flags, component.flags()), apply(this.strings, component.strings()), apply(this.colors, component.colors(), (provider) -> ((NumberProvider)provider.value()).getInt(context))));
      return itemStack;
   }

   static {
      COLOR_PROVIDER_CODEC = Codec.withAlternative(NumberProviders.CODEC, ExtraCodecs.RGB_COLOR_CODEC, ConstantValue::exactly);
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(ListOperation.StandAlone.codec(NumberProviders.CODEC, 2147483647).optionalFieldOf("floats").forGetter((o) -> o.floats), ListOperation.StandAlone.codec(Codec.BOOL, 2147483647).optionalFieldOf("flags").forGetter((o) -> o.flags), ListOperation.StandAlone.codec(Codec.STRING, 2147483647).optionalFieldOf("strings").forGetter((o) -> o.strings), ListOperation.StandAlone.codec(COLOR_PROVIDER_CODEC, 2147483647).optionalFieldOf("colors").forGetter((o) -> o.colors))).apply(i, SetCustomModelDataFunction::new));
   }
}
