package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MaceItem extends Item {
   private static final int DEFAULT_ATTACK_DAMAGE = 6;
   private static final float DEFAULT_ATTACK_SPEED = -2.4F;
   private static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
   private static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 2.5F;
   private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.6F;
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
   public boolean isEnchantable(ItemStack var1) {
      return false;
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
      if (var3 instanceof ServerPlayer var4 && ((ServerPlayer)var4).fallDistance > 1.5F) {
         ServerLevel var5 = (ServerLevel)var3.level();
         if (!((ServerPlayer)var4).ignoreFallDamageFromCurrentImpulse
            || ((ServerPlayer)var4).currentImpulseImpactPos == null
            || ((ServerPlayer)var4).currentImpulseImpactPos.y() > ((ServerPlayer)var4).getY()) {
            ((ServerPlayer)var4).currentImpulseImpactPos = ((ServerPlayer)var4).position();
            ((ServerPlayer)var4).ignoreFallDamageFromCurrentImpulse = true;
         }

         if (var2.onGround()) {
            ((ServerPlayer)var4).setSpawnExtraParticlesOnFall(true);
            SoundEvent var6 = ((ServerPlayer)var4).fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
            var5.playSound(null, ((ServerPlayer)var4).getX(), ((ServerPlayer)var4).getY(), ((ServerPlayer)var4).getZ(), var6, SoundSource.NEUTRAL, 1.0F, 1.0F);
         } else {
            var5.playSound(
               null,
               ((ServerPlayer)var4).getX(),
               ((ServerPlayer)var4).getY(),
               ((ServerPlayer)var4).getZ(),
               SoundEvents.MACE_SMASH_AIR,
               SoundSource.NEUTRAL,
               1.0F,
               1.0F
            );
         }

         this.knockback(var5, (Player)var4, var2);
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
      return var1.fallDistance > 1.5F ? var2 * 0.5F * var1.fallDistance : 0.0F;
   }

   private void knockback(Level var1, Player var2, Entity var3) {
      var1.getEntitiesOfClass(
            LivingEntity.class,
            var3.getBoundingBox().inflate(2.5),
            var2x -> var2x != var2
                  && var2x != var3
                  && !var3.isAlliedTo(var2x)
                  && (!(var2x instanceof ArmorStand var3xx) || !var3xx.isMarker())
                  && var3.distanceToSqr(var2x) <= Math.pow(2.5, 2.0)
         )
         .forEach(
            var2x -> {
               Vec3 var3xx = var2x.position().subtract(var3.position());
               double var4 = (2.5 - var3xx.length()) * 0.6000000238418579 * (1.0 - var2x.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
               Vec3 var6 = var3xx.normalize().scale(var4);
               if (var4 > 0.0) {
                  var2x.push(var6.x, 0.6000000238418579, var6.z);
                  if (var1 instanceof ServerLevel var7) {
                     BlockPos var8 = var2x.getOnPos();
                     Vec3 var9 = var8.getCenter().add(0.0, 0.5, 0.0);
                     int var10 = (int)(100.0 * var4);
                     var7.sendParticles(
                        new BlockParticleOption(ParticleTypes.BLOCK, var7.getBlockState(var8)),
                        var9.x,
                        var9.y,
                        var9.z,
                        var10,
                        0.30000001192092896,
                        0.30000001192092896,
                        0.30000001192092896,
                        0.15000000596046448
                     );
                  }
               }
            }
         );
   }
}
