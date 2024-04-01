package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PotatoPeelsBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PotatoPeelerItem extends Item {
   public PotatoPeelerItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      return !var4.isCreative();
   }

   @Override
   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, EquipmentSlot.MAINHAND);
      return true;
   }

   public static ItemAttributeModifiers createAttributes(int var0, float var1) {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)var0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(
            Attributes.ATTACK_SPEED,
            new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)var1, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .build();
   }

   private static float getPeelSoundPitch(Level var0) {
      return var0.random.nextFloat(0.8F, 1.2F);
   }

   public static void playPeelSound(Level var0, @Nullable Player var1, BlockPos var2, SoundSource var3) {
      var0.playSound(var1, var2, SoundEvents.ENTITY_POTATO_PEEL, var3, 1.0F, getPeelSoundPitch(var0));
   }

   public static void playPeelSound(Level var0, Entity var1) {
      var1.playSound(SoundEvents.ENTITY_POTATO_PEEL, 1.0F, getPeelSoundPitch(var0));
   }

   public static void playPeelSound(Level var0, Entity var1, SoundSource var2) {
      var0.playSound(null, var1, SoundEvents.ENTITY_POTATO_PEEL, var2, 1.0F, getPeelSoundPitch(var0));
   }

   private static InteractionResult peelBlock(UseOnContext var0, ItemStack var1, BlockState var2) {
      Level var3 = var0.getLevel();
      BlockPos var4 = var0.getClickedPos();
      Player var5 = var0.getPlayer();
      ItemStack var6 = var0.getItemInHand();
      playPeelSound(var3, var5, var4, SoundSource.BLOCKS);
      var3.setBlockAndUpdate(var4, var2);
      if (var3 instanceof ServerLevel) {
         if (var2.isAir()) {
            Block.popResource(var3, var4, var1);
         } else {
            Block.popResourceFromFace(var3, var4, var0.getClickedFace(), var1);
         }
      }

      if (var5 != null) {
         var6.hurtAndBreak(1, var5, LivingEntity.getSlotForHand(var0.getHand()));
      }

      if (var5 instanceof ServerPlayer var7) {
         CriteriaTriggers.PEEL_BLOCK.trigger((ServerPlayer)var7);
      }

      return InteractionResult.sidedSuccess(var3.isClientSide);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      Block var5 = var4.getBlock();
      if (var5 instanceof PotatoPeelsBlock var6) {
         return peelBlock(var1, new ItemStack(var6.getPeelsItem(), 9), Blocks.AIR.defaultBlockState());
      } else if (var5 == Blocks.PEELGRASS_BLOCK && var1.getClickedFace() == Direction.UP) {
         return peelBlock(
            var1, ((Item)Items.POTATO_PEELS_MAP.get(PotatoPeelItem.PEELGRASS_PEEL_COLOR)).getDefaultInstance(), Blocks.TERREDEPOMME.defaultBlockState()
         );
      } else if (var5 == Blocks.CORRUPTED_PEELGRASS_BLOCK && var1.getClickedFace() == Direction.UP) {
         return peelBlock(var1, Items.CORRUPTED_POTATO_PEELS.getDefaultInstance(), Blocks.TERREDEPOMME.defaultBlockState());
      } else {
         return var5 == Blocks.POISONOUS_POTATO_BLOCK
            ? peelBlock(var1, new ItemStack(Items.POISONOUS_POTATO, 9), Blocks.POTATO_PEELS_BLOCK_MAP.get(DyeColor.WHITE).defaultBlockState())
            : super.useOn(var1);
      }
   }
}
