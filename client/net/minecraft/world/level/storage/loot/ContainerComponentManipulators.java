package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.ItemContainerContents;

public interface ContainerComponentManipulators {
   ContainerComponentManipulator<ItemContainerContents> CONTAINER = new ContainerComponentManipulator<ItemContainerContents>(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
   ContainerComponentManipulator<BundleContents> BUNDLE_CONTENTS = new ContainerComponentManipulator<BundleContents>(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
   ContainerComponentManipulator<ChargedProjectiles> CHARGED_PROJECTILES = new ContainerComponentManipulator<ChargedProjectiles>(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
   Map<DataComponentType<?>, ContainerComponentManipulator<?>> ALL_MANIPULATORS = (Map)Stream.of(CONTAINER, BUNDLE_CONTENTS, CHARGED_PROJECTILES).collect(Collectors.toMap(ContainerComponentManipulator::type, (e) -> e));
   Codec<ContainerComponentManipulator<?>> CODEC = BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec().comapFlatMap((type) -> {
      ContainerComponentManipulator<?> manipulator = (ContainerComponentManipulator)ALL_MANIPULATORS.get(type);
      return manipulator != null ? DataResult.success(manipulator) : DataResult.error(() -> "No items in component");
   }, ContainerComponentManipulator::type);
}
