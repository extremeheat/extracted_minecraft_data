package net.minecraft.world.item.equipment;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ArmorMaterial(int durability, Map<ArmorType, Integer> defense, int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, TagKey<Item> repairIngredient, ResourceKey<EquipmentAsset> assetId) {
   public ArmorMaterial(int var1, Map<ArmorType, Integer> var2, int var3, Holder<SoundEvent> var4, float var5, float var6, TagKey<Item> var7, ResourceKey<EquipmentAsset> var8) {
      super();
      this.durability = var1;
      this.defense = var2;
      this.enchantmentValue = var3;
      this.equipSound = var4;
      this.toughness = var5;
      this.knockbackResistance = var6;
      this.repairIngredient = var7;
      this.assetId = var8;
   }

   public Item.Properties humanoidProperties(Item.Properties var1, ArmorType var2) {
      return var1.durability(var2.getDurability(this.durability)).attributes(this.createAttributes(var2)).enchantable(this.enchantmentValue).component(DataComponents.EQUIPPABLE, Equippable.builder(var2.getSlot()).setEquipSound(this.equipSound).setAsset(this.assetId).build()).repairable(this.repairIngredient);
   }

   public Item.Properties animalProperties(Item.Properties var1, HolderSet<EntityType<?>> var2) {
      return var1.durability(ArmorType.BODY.getDurability(this.durability)).attributes(this.createAttributes(ArmorType.BODY)).repairable(this.repairIngredient).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.BODY).setEquipSound(this.equipSound).setAsset(this.assetId).setAllowedEntities(var2).build());
   }

   public Item.Properties animalProperties(Item.Properties var1, Holder<SoundEvent> var2, boolean var3, HolderSet<EntityType<?>> var4) {
      if (var3) {
         var1 = var1.durability(ArmorType.BODY.getDurability(this.durability)).repairable(this.repairIngredient);
      }

      return var1.attributes(this.createAttributes(ArmorType.BODY)).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.BODY).setEquipSound(var2).setAsset(this.assetId).setAllowedEntities(var4).setDamageOnHurt(var3).build());
   }

   private ItemAttributeModifiers createAttributes(ArmorType var1) {
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

   public int durability() {
      return this.durability;
   }

   public Map<ArmorType, Integer> defense() {
      return this.defense;
   }

   public int enchantmentValue() {
      return this.enchantmentValue;
   }

   public Holder<SoundEvent> equipSound() {
      return this.equipSound;
   }

   public float toughness() {
      return this.toughness;
   }

   public float knockbackResistance() {
      return this.knockbackResistance;
   }

   public TagKey<Item> repairIngredient() {
      return this.repairIngredient;
   }

   public ResourceKey<EquipmentAsset> assetId() {
      return this.assetId;
   }
}
