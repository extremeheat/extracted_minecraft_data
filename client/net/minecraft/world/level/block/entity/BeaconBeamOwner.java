package net.minecraft.world.level.block.entity;

import java.util.List;

public interface BeaconBeamOwner {
   List<Section> getBeamSections();

   public static class Section {
      private final int color;
      private int height;

      public Section(final int color) {
         super();
         this.color = color;
         this.height = 1;
      }

      public void increaseHeight() {
         ++this.height;
      }

      public int getColor() {
         return this.color;
      }

      public int getHeight() {
         return this.height;
      }
   }
}
