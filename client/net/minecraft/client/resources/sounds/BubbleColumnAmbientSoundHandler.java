package net.minecraft.client.resources.sounds;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BubbleColumnAmbientSoundHandler implements AmbientSoundHandler {
   private final LocalPlayer player;
   private boolean wasInBubbleColumn;
   private boolean firstTick = true;

   public BubbleColumnAmbientSoundHandler(LocalPlayer var1) {
      super();
      this.player = var1;
   }

   public void tick() {
      Level var1 = this.player.level;
      BlockState var2 = (BlockState)var1.getBlockStatesIfLoaded(this.player.getBoundingBox().inflate(0.0D, -0.4000000059604645D, 0.0D).deflate(1.0E-6D)).filter((var0) -> {
         return var0.is(Blocks.BUBBLE_COLUMN);
      }).findFirst().orElse((Object)null);
      if (var2 != null) {
         if (!this.wasInBubbleColumn && !this.firstTick && var2.is(Blocks.BUBBLE_COLUMN) && !this.player.isSpectator()) {
            boolean var3 = (Boolean)var2.getValue(BubbleColumnBlock.DRAG_DOWN);
            if (var3) {
               this.player.playSound(SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0F, 1.0F);
            } else {
               this.player.playSound(SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
            }
         }

         this.wasInBubbleColumn = true;
      } else {
         this.wasInBubbleColumn = false;
      }

      this.firstTick = false;
   }
}
