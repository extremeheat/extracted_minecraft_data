package net.minecraft.world.item;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class PotionItem extends Item {
   public PotionItem(Item.Properties var1) {
      super(var1);
   }

   public ItemStack getDefaultInstance() {
      ItemStack var1 = super.getDefaultInstance();
      var1.set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER));
      return var1;
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      Player var4 = var1.getPlayer();
      ItemStack var5 = var1.getItemInHand();
      PotionContents var6 = (PotionContents)var5.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      BlockState var7 = var2.getBlockState(var3);
      if (var1.getClickedFace() != Direction.DOWN && var7.is(BlockTags.CONVERTABLE_TO_MUD) && var6.is(Potions.WATER)) {
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
         var4.setItemInHand(var1.getHand(), ItemUtils.createFilledResult(var5, var4, new ItemStack(Items.GLASS_BOTTLE)));
         var4.awardStat(Stats.ITEM_USED.get(var5.getItem()));
         if (!var2.isClientSide) {
            ServerLevel var8 = (ServerLevel)var2;

            for(int var9 = 0; var9 < 5; ++var9) {
               var8.sendParticles(ParticleTypes.SPLASH, (double)var3.getX() + var2.random.nextDouble(), (double)(var3.getY() + 1), (double)var3.getZ() + var2.random.nextDouble(), 1, 0.0, 0.0, 0.0, 1.0);
            }
         }

         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
         var2.gameEvent((Entity)null, GameEvent.FLUID_PLACE, var3);
         var2.setBlockAndUpdate(var3, Blocks.MUD.defaultBlockState());
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public Component getName(ItemStack var1) {
      PotionContents var2 = (PotionContents)var1.get(DataComponents.POTION_CONTENTS);
      return var2 != null ? var2.getName(this.descriptionId + ".effect.") : super.getName(var1);
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      PotionContents var5 = (PotionContents)var1.get(DataComponents.POTION_CONTENTS);
      if (var5 != null) {
         Objects.requireNonNull(var3);
         var5.addPotionTooltip(var3::add, 1.0F, var2.tickRate());
      }
   }
}
