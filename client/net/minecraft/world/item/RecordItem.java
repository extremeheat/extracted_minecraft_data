package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RecordItem extends Item {
   private static final Map<SoundEvent, RecordItem> BY_NAME = Maps.newHashMap();
   private final int analogOutput;
   private final SoundEvent sound;

   protected RecordItem(int var1, SoundEvent var2, Item.Properties var3) {
      super(var3);
      this.analogOutput = var1;
      this.sound = var2;
      BY_NAME.put(this.sound, this);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (var4.is(Blocks.JUKEBOX) && !(Boolean)var4.getValue(JukeboxBlock.HAS_RECORD)) {
         ItemStack var5 = var1.getItemInHand();
         if (!var2.isClientSide) {
            ((JukeboxBlock)Blocks.JUKEBOX).setRecord(var2, var3, var4, var5);
            var2.levelEvent((Player)null, 1010, var3, Item.getId(this));
            var5.shrink(1);
            Player var6 = var1.getPlayer();
            if (var6 != null) {
               var6.awardStat(Stats.PLAY_RECORD);
            }
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public int getAnalogOutput() {
      return this.analogOutput;
   }

   public void appendHoverText(ItemStack var1, @Nullable Level var2, List<Component> var3, TooltipFlag var4) {
      var3.add(this.getDisplayName().withStyle(ChatFormatting.GRAY));
   }

   public MutableComponent getDisplayName() {
      return new TranslatableComponent(this.getDescriptionId() + ".desc");
   }

   @Nullable
   public static RecordItem getBySound(SoundEvent var0) {
      return (RecordItem)BY_NAME.get(var0);
   }

   public SoundEvent getSound() {
      return this.sound;
   }
}
