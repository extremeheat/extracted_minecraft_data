package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.npc.VillagerData;

public class VillagerRenderState extends HoldingEntityRenderState implements VillagerDataHolderRenderState {
   public boolean isUnhappy;
   @Nullable
   public VillagerData villagerData;

   public VillagerRenderState() {
      super();
   }

   @Nullable
   public VillagerData getVillagerData() {
      return this.villagerData;
   }
}
