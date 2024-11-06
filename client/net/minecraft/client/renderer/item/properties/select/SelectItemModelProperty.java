package net.minecraft.client.renderer.item.properties.select;

import com.google.common.collect.HashMultiset;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface SelectItemModelProperty<T> {
   @Nullable
   T get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5);

   Type<? extends SelectItemModelProperty<T>, T> type();

   public static record Type<P extends SelectItemModelProperty<T>, T>(MapCodec<SelectItemModel.UnbakedSwitch<P, T>> switchCodec) {
      public Type(MapCodec<SelectItemModel.UnbakedSwitch<P, T>> var1) {
         super();
         this.switchCodec = var1;
      }

      public static <P extends SelectItemModelProperty<T>, T> Type<P, T> create(MapCodec<P> var0, Codec<T> var1) {
         Codec var2 = SelectItemModel.SwitchCase.codec(var1).listOf().validate((var0x) -> {
            if (var0x.isEmpty()) {
               return DataResult.error(() -> {
                  return "Empty case list";
               });
            } else {
               HashMultiset var1 = HashMultiset.create();
               Iterator var2 = var0x.iterator();

               while(var2.hasNext()) {
                  SelectItemModel.SwitchCase var3 = (SelectItemModel.SwitchCase)var2.next();
                  var1.addAll(var3.values());
               }

               return var1.size() != var1.entrySet().size() ? DataResult.error(() -> {
                  Stream var10000 = var1.entrySet().stream().filter((var0) -> {
                     return var0.getCount() > 1;
                  }).map((var0) -> {
                     return var0.getElement().toString();
                  });
                  return "Duplicate case conditions: " + (String)var10000.collect(Collectors.joining(", "));
               }) : DataResult.success(var0x);
            }
         });
         MapCodec var3 = RecordCodecBuilder.mapCodec((var2x) -> {
            return var2x.group(var0.forGetter(SelectItemModel.UnbakedSwitch::property), var2.fieldOf("cases").forGetter(SelectItemModel.UnbakedSwitch::cases)).apply(var2x, SelectItemModel.UnbakedSwitch::new);
         });
         return new Type(var3);
      }

      public MapCodec<SelectItemModel.UnbakedSwitch<P, T>> switchCodec() {
         return this.switchCodec;
      }
   }
}
