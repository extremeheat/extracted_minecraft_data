package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DiggerItem extends TieredItem {
   private final TagKey<Block> blocks;
   protected final float speed;

   protected DiggerItem(Tier var1, TagKey<Block> var2, Item.Properties var3) {
      super(var1, var3);
      this.blocks = var2;
      this.speed = var1.getSpeed();
   }

   public static ItemAttributeModifiers createAttributes(Tier var0, float var1, float var2) {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double)(var1 + var0.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)var2, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .build();
   }

   @Override
   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      return var2.is(this.blocks) ? this.speed : 1.0F;
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(2, var3, EquipmentSlot.MAINHAND);
      return true;
   }

   @Override
   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (!var2.isClientSide && var3.getDestroySpeed(var2, var4) != 0.0F) {
         var1.hurtAndBreak(1, var5, EquipmentSlot.MAINHAND);
      }

      return true;
   }

   @Override
   public boolean isCorrectToolForDrops(BlockState var1) {
      int var2 = this.getTier().getLevel();
      if (var2 < 3 && var1.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
         return false;
      } else if (var2 < 2 && var1.is(BlockTags.NEEDS_IRON_TOOL)) {
         return false;
      } else {
         return var2 < 1 && var1.is(BlockTags.NEEDS_STONE_TOOL) ? false : var1.is(this.blocks);
      }
   }
}
