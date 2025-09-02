package net.minecraft.client.player;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.Scoreboard;

public abstract class AbstractClientPlayer extends Player implements ClientAvatarEntity {
   @Nullable
   private PlayerInfo playerInfo;
   private final boolean showExtraEars = "deadmau5".equals(this.getGameProfile().name());
   private final ClientAvatarState clientAvatarState = new ClientAvatarState();

   public AbstractClientPlayer(ClientLevel var1, GameProfile var2) {
      super(var1, var2);
   }

   @Nullable
   public GameType gameMode() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 != null ? var1.getGameMode() : null;
   }

   @Nullable
   protected PlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUUID());
      }

      return this.playerInfo;
   }

   public void tick() {
      this.clientAvatarState.tick(this.position(), this.getDeltaMovement());
      super.tick();
   }

   protected void addWalkedDistance(float var1) {
      this.clientAvatarState.addWalkDistance(var1);
   }

   public ClientAvatarState avatarState() {
      return this.clientAvatarState;
   }

   @Nullable
   public Component belowNameDisplay() {
      Scoreboard var1 = this.level().getScoreboard();
      Objective var2 = var1.getDisplayObjective(DisplaySlot.BELOW_NAME);
      if (var2 != null) {
         ReadOnlyScoreInfo var3 = var1.getPlayerScoreInfo(this, var2);
         MutableComponent var4 = ReadOnlyScoreInfo.safeFormatValue(var3, var2.numberFormatOrDefault(StyledFormat.NO_STYLE));
         return Component.empty().append((Component)var4).append(CommonComponents.SPACE).append(var2.getDisplayName());
      } else {
         return null;
      }
   }

   public PlayerSkin getSkin() {
      PlayerInfo var1 = this.getPlayerInfo();
      return var1 == null ? DefaultPlayerSkin.get(this.getUUID()) : var1.getSkin();
   }

   @Nullable
   public Parrot.Variant getParrotVariantOnShoulder(boolean var1) {
      return (Parrot.Variant)(var1 ? this.getShoulderParrotLeft() : this.getShoulderParrotRight()).orElse((Object)null);
   }

   public void rideTick() {
      super.rideTick();
      this.avatarState().resetBob();
   }

   public void aiStep() {
      this.updateBob();
      super.aiStep();
   }

   protected void updateBob() {
      float var1;
      if (this.onGround() && !this.isDeadOrDying() && !this.isSwimming()) {
         var1 = Math.min(0.1F, (float)this.getDeltaMovement().horizontalDistance());
      } else {
         var1 = 0.0F;
      }

      this.avatarState().updateBob(var1);
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

   public boolean showExtraEars() {
      return this.showExtraEars;
   }
}
