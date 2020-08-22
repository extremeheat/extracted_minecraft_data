package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HoeItem extends TieredItem {
   private final float attackSpeed;
   protected static final Map TILLABLES;

   public HoeItem(Tier var1, float var2, Item.Properties var3) {
      super(var1, var3);
      this.attackSpeed = var2;
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      if (var1.getClickedFace() != Direction.DOWN && var2.getBlockState(var3.above()).isAir()) {
         BlockState var4 = (BlockState)TILLABLES.get(var2.getBlockState(var3).getBlock());
         if (var4 != null) {
            Player var5 = var1.getPlayer();
            var2.playSound(var5, var3, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!var2.isClientSide) {
               var2.setBlock(var3, var4, 11);
               if (var5 != null) {
                  var1.getItemInHand().hurtAndBreak(1, var5, (var1x) -> {
                     var1x.broadcastBreakEvent(var1.getHand());
                  });
               }
            }

            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.PASS;
   }

   public boolean hurtEnemy(ItemStack var1, LivingEntity var2, LivingEntity var3) {
      var1.hurtAndBreak(1, var3, (var0) -> {
         var0.broadcastBreakEvent(EquipmentSlot.MAINHAND);
      });
      return true;
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot var1) {
      Multimap var2 = super.getDefaultAttributeModifiers(var1);
      if (var1 == EquipmentSlot.MAINHAND) {
         var2.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 0.0D, AttributeModifier.Operation.ADDITION));
         var2.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
      }

      return var2;
   }

   static {
      TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.defaultBlockState(), Blocks.GRASS_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT, Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));
   }
}
