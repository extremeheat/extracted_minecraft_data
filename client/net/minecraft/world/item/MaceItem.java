package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.DensityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MaceItem extends Item {
   private static final int DEFAULT_ATTACK_DAMAGE = 6;
   private static final float DEFAULT_ATTACK_SPEED = -2.4F;
   private static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
   private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
   public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
   private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;
   private static final float SMASH_ATTACK_FALL_DISTANCE_MULTIPLIER = 3.0F;
   private static final ImmutableMultimap<Holder<Attribute>, AttributeModifier> ATTRIBUTES = ImmutableMultimap.builder()
      .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 6.0, AttributeModifier.Operation.ADD_VALUE))
      .put(
         Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4000000953674316, AttributeModifier.Operation.ADD_VALUE)
      )
      .build();

   public MaceItem(Item.Properties var1) {
      super(var1);
   }

   public static Tool createToolProperties() {
      return new Tool(List.of(), 1.0F, 2);
   }

   @Override
   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return !var4.isCreative();
   }

   @Override
   public int getEnchantmentValue() {
      return 15;
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
      if (var3 instanceof ServerPlayer var4 && canSmashAttack((Player)var4)) {
         ServerLevel var5 = (ServerLevel)var3.level();
         if (!((ServerPlayer)var4).ignoreFallDamageFromCurrentImpulse
            || ((ServerPlayer)var4).currentImpulseImpactPos == null
            || ((ServerPlayer)var4).currentImpulseImpactPos.y() > ((ServerPlayer)var4).getY()) {
            ((ServerPlayer)var4).currentImpulseImpactPos = ((ServerPlayer)var4).position();
            ((ServerPlayer)var4).ignoreFallDamageFromCurrentImpulse = true;
         }

         ((ServerPlayer)var4).setDeltaMovement(((ServerPlayer)var4).getDeltaMovement().with(Direction.Axis.Y, 0.0));
         ((ServerPlayer)var4).connection.send(new ClientboundSetEntityMotionPacket((Entity)var4));
         if (var2.onGround()) {
            ((ServerPlayer)var4).setSpawnExtraParticlesOnFall(true);
            SoundEvent var6 = ((ServerPlayer)var4).fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
            var5.playSound(
               null,
               ((ServerPlayer)var4).getX(),
               ((ServerPlayer)var4).getY(),
               ((ServerPlayer)var4).getZ(),
               var6,
               ((ServerPlayer)var4).getSoundSource(),
               1.0F,
               1.0F
            );
         } else {
            var5.playSound(
               null,
               ((ServerPlayer)var4).getX(),
               ((ServerPlayer)var4).getY(),
               ((ServerPlayer)var4).getZ(),
               SoundEvents.MACE_SMASH_AIR,
               ((ServerPlayer)var4).getSoundSource(),
               1.0F,
               1.0F
            );
         }

         knockback(var5, (Player)var4, var2);
      }

      return true;
   }

   @Override
   public Multimap<Holder<Attribute>, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot var1) {
      return (Multimap<Holder<Attribute>, AttributeModifier>)(var1 == EquipmentSlot.MAINHAND ? ATTRIBUTES : super.getDefaultAttributeModifiers(var1));
   }

   @Override
   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return var2.is(Items.BREEZE_ROD);
   }

   @Override
   public float getAttackDamageBonus(Player var1, float var2) {
      int var3 = EnchantmentHelper.getEnchantmentLevel(Enchantments.DENSITY, var1);
      float var4 = DensityEnchantment.calculateDamageAddition(var3, var1.fallDistance);
      return canSmashAttack(var1) ? 3.0F * var1.fallDistance + var4 : 0.0F;
   }

   private static void knockback(Level var0, Player var1, Entity var2) {
      var0.levelEvent(2013, var2.getOnPos(), 750);
      var0.getEntitiesOfClass(LivingEntity.class, var2.getBoundingBox().inflate(3.5), knockbackPredicate(var1, var2)).forEach(var2x -> {
         Vec3 var3 = var2x.position().subtract(var2.position());
         double var4 = getKnockbackPower(var1, var2x, var3);
         Vec3 var6 = var3.normalize().scale(var4);
         if (var4 > 0.0) {
            var2x.push(var6.x, 0.699999988079071, var6.z);
         }
      });
   }

   private static Predicate<LivingEntity> knockbackPredicate(Player var0, Entity var1) {
      return var2 -> {
         boolean var3;
         boolean var4;
         boolean var5;
         boolean var10000;
         label44: {
            var3 = !var2.isSpectator();
            var4 = var2 != var0 && var2 != var1;
            var5 = !var0.isAlliedTo(var2);
            if (var2 instanceof ArmorStand var7 && var7.isMarker()) {
               var10000 = false;
               break label44;
            }

            var10000 = true;
         }

         boolean var6 = var10000;
         boolean var8 = var1.distanceToSqr(var2) <= Math.pow(3.5, 2.0);
         return var3 && var4 && var5 && var6 && var8;
      };
   }

   private static double getKnockbackPower(Player var0, LivingEntity var1, Vec3 var2) {
      return (3.5 - var2.length())
         * 0.699999988079071
         * (double)(var0.fallDistance > 5.0F ? 2 : 1)
         * (1.0 - var1.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
   }

   public static boolean canSmashAttack(Player var0) {
      return var0.fallDistance > 1.5F && !var0.isFallFlying();
   }
}
