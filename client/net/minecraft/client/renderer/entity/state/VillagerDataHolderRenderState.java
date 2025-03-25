package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.world.entity.npc.VillagerData;

public interface VillagerDataHolderRenderState {
   @Nullable
   VillagerData getVillagerData();
}
