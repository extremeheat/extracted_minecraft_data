package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.npc.VillagerData;

public class ZombieVillagerRenderState extends ZombieRenderState implements VillagerDataHolderRenderState {
   @Nullable
   public VillagerData villagerData;

   public ZombieVillagerRenderState() {
      super();
   }

   @Nullable
   public VillagerData getVillagerData() {
      return this.villagerData;
   }
}
