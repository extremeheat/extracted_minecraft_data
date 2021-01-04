package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.BufferBuilder;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CompiledChunk {
   public static final CompiledChunk UNCOMPILED = new CompiledChunk() {
      protected void setChanged(BlockLayer var1) {
         throw new UnsupportedOperationException();
      }

      public void layerIsPresent(BlockLayer var1) {
         throw new UnsupportedOperationException();
      }

      public boolean facesCanSeeEachother(Direction var1, Direction var2) {
         return false;
      }
   };
   private final boolean[] hasBlocks = new boolean[BlockLayer.values().length];
   private final boolean[] hasLayer = new boolean[BlockLayer.values().length];
   private boolean isCompletelyEmpty = true;
   private final List<BlockEntity> renderableBlockEntities = Lists.newArrayList();
   private VisibilitySet visibilitySet = new VisibilitySet();
   private BufferBuilder.State transparencyState;

   public CompiledChunk() {
      super();
   }

   public boolean hasNoRenderableLayers() {
      return this.isCompletelyEmpty;
   }

   protected void setChanged(BlockLayer var1) {
      this.isCompletelyEmpty = false;
      this.hasBlocks[var1.ordinal()] = true;
   }

   public boolean isEmpty(BlockLayer var1) {
      return !this.hasBlocks[var1.ordinal()];
   }

   public void layerIsPresent(BlockLayer var1) {
      this.hasLayer[var1.ordinal()] = true;
   }

   public boolean hasLayer(BlockLayer var1) {
      return this.hasLayer[var1.ordinal()];
   }

   public List<BlockEntity> getRenderableBlockEntities() {
      return this.renderableBlockEntities;
   }

   public void addRenderableBlockEntity(BlockEntity var1) {
      this.renderableBlockEntities.add(var1);
   }

   public boolean facesCanSeeEachother(Direction var1, Direction var2) {
      return this.visibilitySet.visibilityBetween(var1, var2);
   }

   public void setVisibilitySet(VisibilitySet var1) {
      this.visibilitySet = var1;
   }

   public BufferBuilder.State getTransparencyState() {
      return this.transparencyState;
   }

   public void setTransparencyState(BufferBuilder.State var1) {
      this.transparencyState = var1;
   }
}
