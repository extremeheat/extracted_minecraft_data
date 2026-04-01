package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingBlockCommand;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.Vec3;

public class MoveAction extends CommandActionItem {
   public static final Identifier MOVE_RANGE_MODIFIER_ID = Identifier.withDefaultNamespace("move_range");

   public MoveAction(final Item.Properties properties) {
      super(properties, LivingBlockCommand.Type.TYPE_MOVE);
   }

   public static ItemAttributeModifiers createAttributes() {
      return ItemAttributeModifiers.builder().add(Attributes.BLOCK_INTERACTION_RANGE, new AttributeModifier(MOVE_RANGE_MODIFIER_ID, 50.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(MOVE_RANGE_MODIFIER_ID, 50.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
   }

   public void command(final LivingBlock block, final ServerPlayer player, final Vec3 pos, final BlockPos blockPos, final Direction direction) {
      block.interrupt();
      block.hopesAndDreams.desire(Desires.APPROACH, Target.near(pos, 0.1));
   }

   public boolean attackBlock(final Player player, final LivingBlock target) {
      if (player.isSpectator()) {
         return false;
      } else {
         this.actionOnBlock(player, target.blockPosition(), Direction.UP);
         return false;
      }
   }
}
