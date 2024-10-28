package net.minecraft.world.level;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public interface Spawner {
   void setEntityId(EntityType<?> var1, RandomSource var2);

   static void appendHoverText(ItemStack var0, List<Component> var1, String var2) {
      Component var3 = getSpawnEntityDisplayName(var0, var2);
      if (var3 != null) {
         var1.add(var3);
      } else {
         var1.add(CommonComponents.EMPTY);
         var1.add(Component.translatable("block.minecraft.spawner.desc1").withStyle(ChatFormatting.GRAY));
         var1.add(CommonComponents.space().append((Component)Component.translatable("block.minecraft.spawner.desc2").withStyle(ChatFormatting.BLUE)));
      }

   }

   @Nullable
   static Component getSpawnEntityDisplayName(ItemStack var0, String var1) {
      CompoundTag var2 = ((CustomData)var0.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY)).getUnsafe();
      ResourceLocation var3 = getEntityKey(var2, var1);
      return var3 != null ? (Component)BuiltInRegistries.ENTITY_TYPE.getOptional(var3).map((var0x) -> {
         return Component.translatable(var0x.getDescriptionId()).withStyle(ChatFormatting.GRAY);
      }).orElse((Object)null) : null;
   }

   @Nullable
   private static ResourceLocation getEntityKey(CompoundTag var0, String var1) {
      if (var0.contains(var1, 10)) {
         String var2 = var0.getCompound(var1).getCompound("entity").getString("id");
         return ResourceLocation.tryParse(var2);
      } else {
         return null;
      }
   }
}
