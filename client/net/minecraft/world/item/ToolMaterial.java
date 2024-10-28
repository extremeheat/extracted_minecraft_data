package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record ToolMaterial(TagKey<Block> incorrectBlocksForDrops, int durability, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Item> repairItems) {
   public static final ToolMaterial WOOD;
   public static final ToolMaterial STONE;
   public static final ToolMaterial IRON;
   public static final ToolMaterial DIAMOND;
   public static final ToolMaterial GOLD;
   public static final ToolMaterial NETHERITE;

   public ToolMaterial(TagKey<Block> var1, int var2, float var3, float var4, int var5, TagKey<Item> var6) {
      super();
      this.incorrectBlocksForDrops = var1;
      this.durability = var2;
      this.speed = var3;
      this.attackDamageBonus = var4;
      this.enchantmentValue = var5;
      this.repairItems = var6;
   }

   private Item.Properties applyCommonProperties(Item.Properties var1) {
      return var1.durability(this.durability).repairable(this.repairItems).enchantable(this.enchantmentValue);
   }

   public Item.Properties applyToolProperties(Item.Properties var1, TagKey<Block> var2, float var3, float var4) {
      HolderGetter var5 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
      return this.applyCommonProperties(var1).component(DataComponents.TOOL, new Tool(List.of(Tool.Rule.deniesDrops(var5.getOrThrow(this.incorrectBlocksForDrops)), Tool.Rule.minesAndDrops(var5.getOrThrow(var2), this.speed)), 1.0F, 1)).attributes(this.createToolAttributes(var3, var4));
   }

   private ItemAttributeModifiers createToolAttributes(float var1, float var2) {
      return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, (double)(var1 + this.attackDamageBonus), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double)var2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
   }

   public Item.Properties applySwordProperties(Item.Properties var1, float var2, float var3) {
      HolderGetter var4 = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
      return this.applyCommonProperties(var1).component(DataComponents.TOOL, new Tool(List.of(Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0F), Tool.Rule.overrideSpeed(var4.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5F)), 1.0F, 2)).attributes(this.createSwordAttributes(var2, var3));
   }

   private ItemAttributeModifiers createSwordAttributes(float var1, float var2) {
      return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, (double)(var1 + this.attackDamageBonus), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (double)var2, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
   }

   public TagKey<Block> incorrectBlocksForDrops() {
      return this.incorrectBlocksForDrops;
   }

   public int durability() {
      return this.durability;
   }

   public float speed() {
      return this.speed;
   }

   public float attackDamageBonus() {
      return this.attackDamageBonus;
   }

   public int enchantmentValue() {
      return this.enchantmentValue;
   }

   public TagKey<Item> repairItems() {
      return this.repairItems;
   }

   static {
      WOOD = new ToolMaterial(BlockTags.INCORRECT_FOR_WOODEN_TOOL, 59, 2.0F, 0.0F, 15, ItemTags.WOODEN_TOOL_MATERIALS);
      STONE = new ToolMaterial(BlockTags.INCORRECT_FOR_STONE_TOOL, 131, 4.0F, 1.0F, 5, ItemTags.STONE_TOOL_MATERIALS);
      IRON = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6.0F, 2.0F, 14, ItemTags.IRON_TOOL_MATERIALS);
      DIAMOND = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 8.0F, 3.0F, 10, ItemTags.DIAMOND_TOOL_MATERIALS);
      GOLD = new ToolMaterial(BlockTags.INCORRECT_FOR_GOLD_TOOL, 32, 12.0F, 0.0F, 22, ItemTags.GOLD_TOOL_MATERIALS);
      NETHERITE = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2031, 9.0F, 4.0F, 15, ItemTags.NETHERITE_TOOL_MATERIALS);
   }
}
