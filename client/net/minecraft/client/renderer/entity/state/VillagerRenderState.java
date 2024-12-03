package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class VillagerRenderState extends HoldingEntityRenderState implements VillagerDataHolderRenderState {
   public boolean isUnhappy;
   public VillagerData villagerData;

   public VillagerRenderState() {
      super();
      this.villagerData = new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1);
   }

   public VillagerData getVillagerData() {
      return this.villagerData;
   }
}
