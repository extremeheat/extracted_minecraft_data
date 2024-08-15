package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

public class ZombieVillagerRenderState extends ZombieRenderState implements VillagerDataHolderRenderState {
   public VillagerData villagerData = new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1);

   public ZombieVillagerRenderState() {
      super();
   }

   @Override
   public VillagerData getVillagerData() {
      return this.villagerData;
   }
}
