package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;

public class PlayerRenderState extends HumanoidRenderState {
   public PlayerSkin skin = DefaultPlayerSkin.getDefaultSkin();
   public float capeFlap;
   public float capeLean;
   public float capeLean2;
   public int arrowCount;
   public int stingerCount;
   public int useItemRemainingTicks;
   public boolean isSpectator;
   public boolean showHat = true;
   public boolean showJacket = true;
   public boolean showLeftPants = true;
   public boolean showRightPants = true;
   public boolean showLeftSleeve = true;
   public boolean showRightSleeve = true;
   public boolean showCape = true;
   public float fallFlyingTimeInTicks;
   public boolean shouldApplyFlyingYRot;
   public float flyingYRot;
   public boolean swinging;
   @Nullable
   public Component scoreText;
   @Nullable
   public Parrot.Variant parrotOnLeftShoulder;
   @Nullable
   public Parrot.Variant parrotOnRightShoulder;
   public int id;
   public String name = "Steve";
   public final ItemStackRenderState heldOnHead = new ItemStackRenderState();

   public PlayerRenderState() {
      super();
   }

   public float fallFlyingScale() {
      return Mth.clamp(this.fallFlyingTimeInTicks * this.fallFlyingTimeInTicks / 100.0F, 0.0F, 1.0F);
   }
}
