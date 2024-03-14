package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MaceItem extends Item {
   private static final int DEFAULT_ATTACK_DAMAGE = 6;
   private static final float DEFAULT_ATTACK_SPEED = -2.4F;
   public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
   private static final ImmutableMultimap<Holder<Attribute>, AttributeModifier> ATTRIBUTES = ImmutableMultimap.builder()
      .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 6.0, AttributeModifier.Operation.ADD_VALUE))
      .put(
         Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4000000953674316, AttributeModifier.Operation.ADD_VALUE)
      )
      .build();

   public MaceItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return !var4.isCreative();
   }

   @Override
   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      return var2.is(Blocks.COBWEB) ? 15.0F : 1.5F;
   }

   @Override
   public boolean isEnchantable(ItemStack var1) {
      return false;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
      if (var3 instanceof ServerPlayer var4 && var4.fallDistance > 1.5F) {
         ServerLevel var5 = (ServerLevel)var3.level();
         if (var4.ignoreFallDamageAboveY == null || var4.ignoreFallDamageAboveY > var4.getY()) {
            var4.ignoreFallDamageAboveY = var4.getY();
         }

         if (var2.onGround()) {
            var4.setSpawnExtraParticlesOnFall(true);
            var5.playSound(null, var4.getX(), var4.getY(), var4.getZ(), SoundEvents.MACE_SMASH_GROUND, SoundSource.NEUTRAL, 1.0F, 1.0F);
         } else {
            var5.playSound(null, var4.getX(), var4.getY(), var4.getZ(), SoundEvents.MACE_SMASH_AIR, SoundSource.NEUTRAL, 1.0F, 1.0F);
         }
      }

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

   @Override
   public Multimap<Holder<Attribute>, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot var1) {
      return (Multimap<Holder<Attribute>, AttributeModifier>)(var1 == EquipmentSlot.MAINHAND ? ATTRIBUTES : super.getDefaultAttributeModifiers(var1));
   }

   @Override
   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return var2.is(Items.BREEZE_ROD);
   }
}
