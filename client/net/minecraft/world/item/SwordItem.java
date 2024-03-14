package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SwordItem extends TieredItem {
   public SwordItem(Tier var1, Item.Properties var2) {
      super(var1, var2);
   }

   public static ItemAttributeModifiers createAttributes(Tier var0, int var1, float var2) {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(
               BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)((float)var1 + var0.getAttackDamageBonus()), AttributeModifier.Operation.ADD_VALUE
            ),
            EquipmentSlotGroup.MAINHAND
         )
         .add(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)var2, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .build();
   }

   @Override
   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return !var4.isCreative();
   }

   @Override
   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      if (var2.is(Blocks.COBWEB)) {
         return 15.0F;
      } else {
         return var2.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
      }
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
      return true;
   }

   @Override
   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (var3.getDestroySpeed(var2, var4) != 0.0F) {
         var1.hurtAndBreak(2, var5, EquipmentSlot.MAINHAND);
      }

      return true;
   }

   @Override
   public boolean isCorrectToolForDrops(BlockState var1) {
      return var1.is(Blocks.COBWEB);
   }
}
