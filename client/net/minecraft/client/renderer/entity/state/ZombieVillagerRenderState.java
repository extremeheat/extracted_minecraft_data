package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.npc.VillagerData;
import org.jspecify.annotations.Nullable;

public class ZombieVillagerRenderState extends ZombieRenderState implements VillagerDataHolderRenderState {
   public @Nullable VillagerData villagerData;

   public ZombieVillagerRenderState() {
      super();
   }

   public @Nullable VillagerData getVillagerData() {
      return this.villagerData;
   }
}
