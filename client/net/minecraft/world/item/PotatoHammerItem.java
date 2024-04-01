package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PotatoHammerItem extends Item {
   public PotatoHammerItem(Item.Properties var1) {
      super(var1);
   }

   public static ItemAttributeModifiers createAttributes() {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 10.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", 2.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .build();
   }

   public static ItemEnchantments createDefaultEnchantments() {
      ItemEnchantments.Mutable var0 = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
      var0.set(Enchantments.KNOCKBACK, 10);
      return var0.toImmutable();
   }

   @Override
   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return !var4.isCreative();
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      Level var4 = var3.level();
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
      if (var4.getRandom().nextFloat() < 0.3F) {
         ThrownPotion var5 = new ThrownPotion(var4, var3);
         var5.setItem(PotionContents.createItemStack(Items.LINGERING_POTION, Potions.LONG_POISON));
         var5.shootFromRotation(var2, var2.getXRot(), var2.getYRot(), -1.0F, 0.0F, 0.0F);
         var4.addFreshEntity(var5);
      }

      return true;
   }
}
