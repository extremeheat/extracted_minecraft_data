package net.minecraft.client.renderer.state;

import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;

public class GameRenderState {
   public final LevelRenderState levelRenderState = new LevelRenderState();
   public final LightmapRenderState lightmapRenderState = new LightmapRenderState();
   public final GuiRenderState guiRenderState = new GuiRenderState();
   public final OptionsRenderState optionsRenderState = new OptionsRenderState();
   public final WindowRenderState windowRenderState = new WindowRenderState();
   public int framerateLimit;

   public GameRenderState() {
      super();
   }
}
