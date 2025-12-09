package net.minecraft.client.renderer.item.properties.select;

import com.google.common.collect.HashMultiset;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface SelectItemModelProperty<T> {
   @Nullable T get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5);

   Codec<T> valueCodec();

   Type<? extends SelectItemModelProperty<T>, T> type();

   public static record Type<P extends SelectItemModelProperty<T>, T>(MapCodec<SelectItemModel.UnbakedSwitch<P, T>> switchCodec) {
      public Type(MapCodec<SelectItemModel.UnbakedSwitch<P, T>> var1) {
         super();
         this.switchCodec = var1;
      }

      public static <P extends SelectItemModelProperty<T>, T> Type<P, T> create(MapCodec<P> var0, Codec<T> var1) {
         MapCodec var2 = RecordCodecBuilder.mapCodec((var2x) -> var2x.group(var0.forGetter(SelectItemModel.UnbakedSwitch::property), createCasesFieldCodec(var1).forGetter(SelectItemModel.UnbakedSwitch::cases)).apply(var2x, SelectItemModel.UnbakedSwitch::new));
         return new Type<P, T>(var2);
      }

      public static <T> MapCodec<List<SelectItemModel.SwitchCase<T>>> createCasesFieldCodec(Codec<T> var0) {
         return SelectItemModel.SwitchCase.codec(var0).listOf().validate(Type::validateCases).fieldOf("cases");
      }

      private static <T> DataResult<List<SelectItemModel.SwitchCase<T>>> validateCases(List<SelectItemModel.SwitchCase<T>> var0) {
         if (var0.isEmpty()) {
            return DataResult.error(() -> "Empty case list");
         } else {
            HashMultiset var1 = HashMultiset.create();

            for(SelectItemModel.SwitchCase var3 : var0) {
               var1.addAll(var3.values());
            }

            return var1.size() != var1.entrySet().size() ? DataResult.error(() -> {
               Stream var10000 = var1.entrySet().stream().filter((var0) -> var0.getCount() > 1).map((var0) -> var0.getElement().toString());
               return "Duplicate case conditions: " + (String)var10000.collect(Collectors.joining(", "));
            }) : DataResult.success(var0);
         }
      }
   }
}
