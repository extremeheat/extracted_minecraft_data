package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemContainerContents;

public interface ContainerComponentManipulators {
   ContainerComponentManipulator<ItemContainerContents> CONTAINER = new ContainerComponentManipulator<ItemContainerContents>() {
      public DataComponentType<ItemContainerContents> type() {
         return DataComponents.CONTAINER;
      }

      public Stream<ItemStack> getContents(ItemContainerContents var1) {
         return var1.stream();
      }

      public ItemContainerContents empty() {
         return ItemContainerContents.EMPTY;
      }

      public ItemContainerContents setContents(ItemContainerContents var1, Stream<ItemStack> var2) {
         return ItemContainerContents.fromItems(var2.toList());
      }

      // $FF: synthetic method
      public Object empty() {
         return this.empty();
      }
   };
   ContainerComponentManipulator<BundleContents> BUNDLE_CONTENTS = new ContainerComponentManipulator<BundleContents>() {
      public DataComponentType<BundleContents> type() {
         return DataComponents.BUNDLE_CONTENTS;
      }

      public BundleContents empty() {
         return BundleContents.EMPTY;
      }

      public Stream<ItemStack> getContents(BundleContents var1) {
         return var1.itemCopyStream();
      }

      public BundleContents setContents(BundleContents var1, Stream<ItemStack> var2) {
         BundleContents.Mutable var3 = (new BundleContents.Mutable(var1)).clearItems();
         Objects.requireNonNull(var3);
         var2.forEach(var3::tryInsert);
         return var3.toImmutable();
      }

      // $FF: synthetic method
      public Object empty() {
         return this.empty();
      }
   };
   ContainerComponentManipulator<ChargedProjectiles> CHARGED_PROJECTILES = new ContainerComponentManipulator<ChargedProjectiles>() {
      public DataComponentType<ChargedProjectiles> type() {
         return DataComponents.CHARGED_PROJECTILES;
      }

      public ChargedProjectiles empty() {
         return ChargedProjectiles.EMPTY;
      }

      public Stream<ItemStack> getContents(ChargedProjectiles var1) {
         return var1.getItems().stream();
      }

      public ChargedProjectiles setContents(ChargedProjectiles var1, Stream<ItemStack> var2) {
         return ChargedProjectiles.of(var2.toList());
      }

      // $FF: synthetic method
      public Object empty() {
         return this.empty();
      }
   };
   Map<DataComponentType<?>, ContainerComponentManipulator<?>> ALL_MANIPULATORS = (Map)Stream.of(CONTAINER, BUNDLE_CONTENTS, CHARGED_PROJECTILES).collect(Collectors.toMap(ContainerComponentManipulator::type, (var0) -> {
      return var0;
   }));
   Codec<ContainerComponentManipulator<?>> CODEC = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().comapFlatMap((var0) -> {
      ContainerComponentManipulator var1 = (ContainerComponentManipulator)ALL_MANIPULATORS.get(var0);
      return var1 != null ? DataResult.success(var1) : DataResult.error(() -> {
         return "No items in component";
      });
   }, ContainerComponentManipulator::type);
}
