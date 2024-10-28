package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulator;
import net.minecraft.world.level.storage.loot.ContainerComponentManipulators;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ModifyContainerContents extends LootItemConditionalFunction {
   public static final MapCodec<ModifyContainerContents> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(ContainerComponentManipulators.CODEC.fieldOf("component").forGetter((var0x) -> {
         return var0x.component;
      }), LootItemFunctions.ROOT_CODEC.fieldOf("modifier").forGetter((var0x) -> {
         return var0x.modifier;
      }))).apply(var0, ModifyContainerContents::new);
   });
   private final ContainerComponentManipulator<?> component;
   private final LootItemFunction modifier;

   private ModifyContainerContents(List<LootItemCondition> var1, ContainerComponentManipulator<?> var2, LootItemFunction var3) {
      super(var1);
      this.component = var2;
      this.modifier = var3;
   }

   public LootItemFunctionType<ModifyContainerContents> getType() {
      return LootItemFunctions.MODIFY_CONTENTS;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         this.component.modifyItems(var1, (var2x) -> {
            return (ItemStack)this.modifier.apply(var2x, var2);
         });
         return var1;
      }
   }

   public void validate(ValidationContext var1) {
      super.validate(var1);
      this.modifier.validate(var1.forChild(".modifier"));
   }
}
