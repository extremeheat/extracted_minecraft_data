package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SwordItem extends TieredItem implements Vanishable {
   private final float attackDamage;
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   public SwordItem(Tier var1, int var2, float var3, Item.Properties var4) {
      super(var1, var4);
      this.attackDamage = (float)var2 + var1.getAttackDamageBonus();
      Builder var5 = ImmutableMultimap.builder();
      var5.put(
         Attributes.ATTACK_DAMAGE,
         new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION)
      );
      var5.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)var3, AttributeModifier.Operation.ADDITION));
      this.defaultModifiers = var5.build();
   }

   public float getDamage() {
      return this.attackDamage;
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
      var1.hurtAndBreak(1, var3, var0 -> var0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
      return true;
   }

   @Override
   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (var3.getDestroySpeed(var2, var4) != 0.0F) {
         var1.hurtAndBreak(2, var5, var0 -> var0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
      }

      return true;
   }

   @Override
   public boolean isCorrectToolForDrops(BlockState var1) {
      return var1.is(Blocks.COBWEB);
   }

   @Override
   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot var1) {
      return var1 == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(var1);
   }
}
