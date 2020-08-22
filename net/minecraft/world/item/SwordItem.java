package net.minecraft.world.item;

import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class SwordItem extends TieredItem {
   private final float attackDamage;
   private final float attackSpeed;

   public SwordItem(Tier var1, int var2, float var3, Item.Properties var4) {
      super(var1, var4);
      this.attackSpeed = var3;
      this.attackDamage = (float)var2 + var1.getAttackDamageBonus();
   }

   public float getDamage() {
      return this.attackDamage;
   }

   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return !var4.isCreative();
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      Block var3 = var2.getBlock();
      if (var3 == Blocks.COBWEB) {
         return 15.0F;
      } else {
         Material var4 = var2.getMaterial();
         return var4 != Material.PLANT && var4 != Material.REPLACEABLE_PLANT && var4 != Material.CORAL && !var2.is(BlockTags.LEAVES) && var4 != Material.VEGETABLE ? 1.0F : 1.5F;
      }
   }

   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, (var0) -> {
         var0.broadcastBreakEvent(EquipmentSlot.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (var3.getDestroySpeed(var2, var4) != 0.0F) {
         var1.hurtAndBreak(2, var5, (var0) -> {
            var0.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return true;
   }

   public boolean canDestroySpecial(BlockState var1) {
      return var1.getBlock() == Blocks.COBWEB;
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot var1) {
      Multimap var2 = super.getDefaultAttributeModifiers(var1);
      if (var1 == EquipmentSlot.MAINHAND) {
         var2.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
         var2.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
      }

      return var2;
   }
}
