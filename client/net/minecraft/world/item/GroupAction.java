package net.minecraft.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.LivingBlockGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class GroupAction extends ActionItem {
   public static final Identifier GROUP_RANGE_MODIFIER_ID = Identifier.withDefaultNamespace("group_range");

   public GroupAction(final Item.Properties properties) {
      super(properties);
   }

   public boolean actionOnEntity(final Player player, final Entity entity) {
      if (player.isSpectator()) {
         return false;
      } else if (entity instanceof LivingBlock) {
         LivingBlock target = (LivingBlock)entity;
         return setBlockGroup(player, target);
      } else {
         return false;
      }
   }

   public boolean attackBlock(final Player player, final LivingBlock target) {
      return player.isSpectator() ? false : setBlockGroup(player, target);
   }

   public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
      return this.toggle(player);
   }

   public InteractionResult useOn(final UseOnContext context) {
      Player player = context.getPlayer();
      return (InteractionResult)(player != null ? this.toggle(player) : InteractionResult.PASS);
   }

   public InteractionResult interactLivingBlock(final Player player, final LivingBlock target) {
      return this.toggle(player);
   }

   public InteractionResult toggle(final Player player) {
      LivingBlockGroup group = player.getSelectedGroup();
      LivingBlockGroup nextGroup = (LivingBlockGroup)LivingBlockGroup.BY_ID.apply(group.id() + (player.isShiftKeyDown() ? -1 : 1));
      player.setSelectedGroup(nextGroup);
      ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
      itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(nextGroup.color()));
      Component name = Component.translatable("item.minecraft.select_group_action.details", Component.translatable("living_blocks.group." + nextGroup.getSerializedName()));
      if (!(player instanceof ServerPlayer)) {
         player.sendOverlayMessage(name);
      }

      return InteractionResult.CONSUME;
   }

   private static boolean setBlockGroup(final Player player, final LivingBlock target) {
      LivingBlockGroup currentGroup = target.getGroup();
      LivingBlockGroup selectedGroup = player.getSelectedGroup();
      if (target.canBeControlledBy(player) && selectedGroup != LivingBlockGroup.ALL) {
         target.setOwner(player);
         if (currentGroup != selectedGroup) {
            target.setGroup(selectedGroup);
            target.setSelected(false);
            return true;
         } else if (currentGroup != LivingBlockGroup.NONE) {
            target.setGroup(LivingBlockGroup.NONE);
            if (!target.isSelected()) {
               target.setOwner((Player)null);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static ItemAttributeModifiers createGroupAttributes() {
      return ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND).add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(GROUP_RANGE_MODIFIER_ID, 50.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build();
   }
}
