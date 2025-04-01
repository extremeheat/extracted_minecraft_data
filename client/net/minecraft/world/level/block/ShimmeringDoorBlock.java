package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;

public class ShimmeringDoorBlock extends DoorBlock {
   public static final MapCodec<ShimmeringDoorBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter(DoorBlock::type), propertiesCodec()).apply(var0, ShimmeringDoorBlock::new));
   private static final MutableComponent REJECT = Component.translatable("door.say_the_thing");

   public MapCodec<? extends ShimmeringDoorBlock> codec() {
      return CODEC;
   }

   protected ShimmeringDoorBlock(BlockSetType var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (!var1.is(Items.TRIAL_KEY) && !var1.is(Items.OMINOUS_TRIAL_KEY)) {
         return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
      } else {
         var5.getInventory().add(TrophyBlock.createTrophy(TrophyType.NO_MEDAL));
         return InteractionResult.FAIL;
      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      var4.displayClientMessage(REJECT, true);
      return InteractionResult.PASS;
   }
}
