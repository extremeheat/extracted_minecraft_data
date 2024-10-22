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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractClientPlayer extends Player {
   @Nullable
   private PlayerInfo playerInfo;
   protected Vec3 deltaMovementOnPreviousTick = Vec3.ZERO;
   public float elytraRotX;
   public float elytraRotY;
   public float elytraRotZ;
   public final ClientLevel clientLevel;
   public float walkDistO;
   public float walkDist;

   public AbstractClientPlayer(ClientLevel var1, GameProfile var2) {
      super(var1, var1.getSharedSpawnPos(), var1.getSharedSpawnAngle(), var2);
      this.clientLevel = var1;
   }

   @Override
   public boolean isSpectator() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 != null && var1.getGameMode() == GameType.SPECTATOR;
   }

   @Override
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

   @Override
   public void tick() {
      this.walkDistO = this.walkDist;
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

   public float getFieldOfViewModifier(boolean var1, float var2) {
      float var3 = 1.0F;
      if (this.getAbilities().flying) {
         var3 *= 1.1F;
      }

      float var4 = this.getAbilities().getWalkingSpeed();
      if (var4 != 0.0F) {
         float var5 = (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) / var4;
         var3 *= (var5 + 1.0F) / 2.0F;
      }

      if (this.isUsingItem()) {
         if (this.getUseItem().is(Items.BOW)) {
            float var6 = Math.min((float)this.getTicksUsingItem() / 20.0F, 1.0F);
            var3 *= 1.0F - Mth.square(var6) * 0.15F;
         } else if (var1 && this.isScoping()) {
            return 0.1F;
         }
      }

      return Mth.lerp(var2, 1.0F, var3);
   }
}
