package net.minecraft.client.resources.sounds;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;

public class RidingMinecartSoundInstance extends RidingEntitySoundInstance {
   private final Player player;
   private final AbstractMinecart minecart;
   private final boolean underwaterSound;

   public RidingMinecartSoundInstance(Player var1, AbstractMinecart var2, boolean var3, SoundEvent var4, float var5, float var6, float var7) {
      super(var1, var2, var3, var4, SoundSource.NEUTRAL, var5, var6, var7);
      this.player = var1;
      this.minecart = var2;
      this.underwaterSound = var3;
   }

   protected boolean shouldNotPlayUnderwaterSound() {
      return this.underwaterSound != this.player.isUnderWater();
   }

   protected float getEntitySpeed() {
      return (float)this.minecart.getDeltaMovement().horizontalDistance();
   }

   protected boolean shoudlPlaySound() {
      return this.minecart.isOnRails() || !(this.minecart.getBehavior() instanceof NewMinecartBehavior);
   }
}
