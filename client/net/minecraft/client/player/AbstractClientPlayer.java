package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractClientPlayer extends Player {
   @Nullable
   private PlayerInfo playerInfo;
   protected Vec3 deltaMovementOnPreviousTick;
   public float elytraRotX;
   public float elytraRotY;
   public float elytraRotZ;
   public final ClientLevel clientLevel;

   public AbstractClientPlayer(ClientLevel var1, GameProfile var2) {
      super(var1, var1.getSharedSpawnPos(), var1.getSharedSpawnAngle(), var2);
      this.deltaMovementOnPreviousTick = Vec3.ZERO;
      this.clientLevel = var1;
   }

   public boolean isSpectator() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 != null && var1.getGameMode() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 != null && var1.getGameMode() == GameType.CREATIVE;
   }

   @Nullable
   protected PlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUUID());
      }

      return this.playerInfo;
   }

   public void tick() {
      this.deltaMovementOnPreviousTick = this.getDeltaMovement();
      super.tick();
   }

   public Vec3 getDeltaMovementLerped(float var1) {
      return this.deltaMovementOnPreviousTick.lerp(this.getDeltaMovement(), (double)var1);
   }

   public PlayerSkin getSkin() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 == null ? DefaultPlayerSkin.get(this.getUUID()) : var1.getSkin();
   }

   public float getFieldOfViewModifier() {
      float var1 = 1.0F;
      if (this.getAbilities().flying) {
         var1 *= 1.1F;
      }

      var1 *= ((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) / this.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
      if (this.getAbilities().getWalkingSpeed() == 0.0F || Float.isNaN(var1) || Float.isInfinite(var1)) {
         var1 = 1.0F;
      }

      ItemStack var2 = this.getUseItem();
      if (this.isUsingItem()) {
         if (var2.is(Items.BOW)) {
            int var3 = this.getTicksUsingItem();
            float var4 = (float)var3 / 20.0F;
            if (var4 > 1.0F) {
               var4 = 1.0F;
            } else {
               var4 *= var4;
            }

            var1 *= 1.0F - var4 * 0.15F;
         } else if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.isScoping()) {
            return 0.1F;
         }
      }

      return Mth.lerp(((Double)Minecraft.getInstance().options.fovEffectScale().get()).floatValue(), 1.0F, var1);
   }
}
