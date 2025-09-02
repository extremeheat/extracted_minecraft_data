package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class WeatheringCopperGolemStatueBlock extends CopperGolemStatueBlock implements WeatheringCopper {
   public static final MapCodec<WeatheringCopperGolemStatueBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge), propertiesCodec()).apply(var0, WeatheringCopperGolemStatueBlock::new));

   public MapCodec<WeatheringCopperGolemStatueBlock> codec() {
      return CODEC;
   }

   public WeatheringCopperGolemStatueBlock(WeatheringCopper.WeatherState var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return WeatheringCopper.getNext(var1.getBlock()).isPresent();
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.changeOverTime(var1, var2, var3, var4);
   }

   public WeatheringCopper.WeatherState getAge() {
      return this.getWeatheringState();
   }

   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      BlockEntity var9 = var3.getBlockEntity(var4);
      if (var9 instanceof CopperGolemStatueBlockEntity var8) {
         if (!var1.is(ItemTags.AXES)) {
            if (var1.is(Items.HONEYCOMB)) {
               return InteractionResult.PASS;
            }

            this.updatePose(var3, var2, var4, var5);
            return InteractionResult.SUCCESS;
         }

         if (this.getAge().equals(WeatheringCopper.WeatherState.UNAFFECTED)) {
            CopperGolem var10 = var8.removeStatue(var2);
            var1.hurtAndBreak(1, var5, (EquipmentSlot)var6.asEquipmentSlot());
            if (var10 != null) {
               var3.addFreshEntity(var10);
               var3.removeBlock(var4, false);
               return InteractionResult.SUCCESS;
            }
         }
      }

      return InteractionResult.PASS;
   }

   // $FF: synthetic method
   public Enum getAge() {
      return this.getAge();
   }
}
