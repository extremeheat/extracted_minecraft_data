package net.minecraft.world.item.equipment;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ArmorMaterial(int durability, Map<ArmorType, Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, TagKey<Item> repairIngredient, ResourceKey<EquipmentAsset> assetId, float experienceExchangeValue) {
   public ArmorMaterial(int var1, Map<ArmorType, Integer> var2, int var3, Holder<SoundEvent> var4, float var5, float var6, TagKey<Item> var7, ResourceKey<EquipmentAsset> var8, float var9) {
      super();
      this.durability = var1;
      this.defense = var2;
      this.enchantmentValue = var3;
      this.equipSound = var4;
      this.toughness = var5;
      this.knockbackResistance = var6;
      this.repairIngredient = var7;
      this.assetId = var8;
      this.experienceExchangeValue = var9;
   }

   public ItemAttributeModifiers createAttributes(ArmorType var1) {
      int var2 = (Integer)this.defense.getOrDefault(var1, 0);
      ItemAttributeModifiers.Builder var3 = ItemAttributeModifiers.builder();
      EquipmentSlotGroup var4 = EquipmentSlotGroup.bySlot(var1.getSlot());
      ResourceLocation var5 = ResourceLocation.withDefaultNamespace("armor." + var1.getName());
      var3.add(Attributes.ARMOR, new AttributeModifier(var5, (double)var2, AttributeModifier.Operation.ADD_VALUE), var4);
      var3.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(var5, (double)this.toughness, AttributeModifier.Operation.ADD_VALUE), var4);
      if (this.knockbackResistance > 0.0F) {
         var3.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(var5, (double)this.knockbackResistance, AttributeModifier.Operation.ADD_VALUE), var4);
      }

      return var3.build();
   }
}
