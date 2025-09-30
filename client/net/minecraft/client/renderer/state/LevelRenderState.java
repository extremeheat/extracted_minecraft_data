package net.minecraft.client.renderer.state;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class LevelRenderState {
   public CameraRenderState cameraRenderState = new CameraRenderState();
   public final List<EntityRenderState> entityRenderStates = new ArrayList();
   public final List<BlockEntityRenderState> blockEntityRenderStates = new ArrayList();
   public boolean haveGlowingEntities;
   @Nullable
   public BlockOutlineRenderState blockOutlineRenderState;
   public final List<BlockBreakingRenderState> blockBreakingRenderStates = new ArrayList();
   public final WeatherRenderState weatherRenderState = new WeatherRenderState();
   public final WorldBorderRenderState worldBorderRenderState = new WorldBorderRenderState();
   public final SkyRenderState skyRenderState = new SkyRenderState();

   public LevelRenderState() {
      super();
   }

   public void reset() {
      this.entityRenderStates.clear();
      this.blockEntityRenderStates.clear();
      this.blockBreakingRenderStates.clear();
      this.haveGlowingEntities = false;
      this.blockOutlineRenderState = null;
      this.weatherRenderState.reset();
      this.worldBorderRenderState.reset();
      this.skyRenderState.reset();
   }
}
