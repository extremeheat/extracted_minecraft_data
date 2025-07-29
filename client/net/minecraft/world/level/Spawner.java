package net.minecraft.world.level;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface Spawner {
   void setEntityId(EntityType<?> var1, RandomSource var2);

   static void appendHoverText(@Nullable TypedEntityData<BlockEntityType<?>> var0, Consumer<Component> var1, String var2) {
      Component var3 = getSpawnEntityDisplayName(var0, var2);
      if (var3 != null) {
         var1.accept(var3);
      } else {
         var1.accept(CommonComponents.EMPTY);
         var1.accept(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
         var1.accept(CommonComponents.space().append((Component)Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
      }

   }

   @Nullable
   static Component getSpawnEntityDisplayName(@Nullable TypedEntityData<BlockEntityType<?>> var0, String var1) {
      return var0 == null ? null : (Component)var0.getUnsafe().getCompound(var1).flatMap((var0x) -> var0x.getCompound("entity")).flatMap((var0x) -> var0x.read("id", EntityType.CODEC)).map((var0x) -> Component.translatable(var0x.getDescriptionId()).withStyle(ChatFormatting.GRAY)).orElse((Object)null);
   }
}
