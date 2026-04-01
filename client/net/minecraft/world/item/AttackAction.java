package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingBlockCommand;
import net.minecraft.world.entity.Targetable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.Vec3;

public class AttackAction extends CommandActionItem {
   public static final Identifier ATTACK_RANGE_MODIFIER_ID = Identifier.withDefaultNamespace("attack_range");

   public AttackAction(final Item.Properties properties) {
      super(properties, LivingBlockCommand.Type.TYPE_ATTACK);
   }

   public static ItemAttributeModifiers createAttributes() {
      return ItemAttributeModifiers.builder().add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(ATTACK_RANGE_MODIFIER_ID, 50.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(ATTACK_RANGE_MODIFIER_ID, 50.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
   }

   public void command(final LivingBlock block, final ServerPlayer player, final Vec3 pos, final BlockPos blockPos, final Direction direction) {
      block.interrupt();
      block.hopesAndDreams.desire(Desires.PROTECT, pos);
      block.hopesAndDreams.desire(Desires.MINE, blockPos);
   }

   public void command(final LivingBlock block, final ServerPlayer player, final Targetable target) {
      block.interrupt();
      block.hopesAndDreams.desire(Desires.ATTACK, target);
   }
}
