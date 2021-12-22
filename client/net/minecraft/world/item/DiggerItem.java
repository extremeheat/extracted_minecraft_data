package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DiggerItem extends TieredItem implements Vanishable {
   private final Tag<Block> blocks;
   protected final float speed;
   private final float attackDamageBaseline;
   private final Multimap<Attribute, AttributeModifier> defaultModifiers;

   protected DiggerItem(float var1, float var2, Tier var3, Tag<Block> var4, Item.Properties var5) {
      super(var3, var5);
      this.blocks = var4;
      this.speed = var3.getSpeed();
      this.attackDamageBaseline = var1 + var3.getAttackDamageBonus();
      Builder var6 = ImmutableMultimap.builder();
      var6.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double)this.attackDamageBaseline, AttributeModifier.Operation.ADDITION));
      var6.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)var2, AttributeModifier.Operation.ADDITION));
      this.defaultModifiers = var6.build();
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      return this.blocks.contains(var2.getBlock()) ? this.speed : 1.0F;
   }

   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(2, var3, (var0) -> {
         var0.broadcastBreakEvent(EquipmentSlot.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (!var2.isClientSide && var3.getDestroySpeed(var2, var4) != 0.0F) {
         var1.hurtAndBreak(1, var5, (var0) -> {
            var0.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return true;
   }

   public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot var1) {
      return var1 == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(var1);
   }

   public float getAttackDamage() {
      return this.attackDamageBaseline;
   }

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
