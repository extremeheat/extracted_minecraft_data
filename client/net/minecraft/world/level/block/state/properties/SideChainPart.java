package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum SideChainPart implements StringRepresentable {
   UNCONNECTED("unconnected"),
   RIGHT("right"),
   CENTER("center"),
   LEFT("left");

   private final String name;

   private SideChainPart(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.getSerializedName();
   }

   public String getSerializedName() {
      return this.name;
   }

   public boolean isConnected() {
      return this != UNCONNECTED;
   }

   public boolean isConnectionTowards(final SideChainPart endPart) {
      return this == CENTER || this == endPart;
   }

   public boolean isChainEnd() {
      return this != CENTER;
   }

   public SideChainPart whenConnectedToTheRight() {
      SideChainPart var10000;
      switch (this.ordinal()) {
         case 0:
         case 3:
            var10000 = LEFT;
            break;
         case 1:
         case 2:
            var10000 = CENTER;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public SideChainPart whenConnectedToTheLeft() {
      SideChainPart var10000;
      switch (this.ordinal()) {
         case 0:
         case 1:
            var10000 = RIGHT;
            break;
         case 2:
         case 3:
            var10000 = CENTER;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public SideChainPart whenDisconnectedFromTheRight() {
      SideChainPart var10000;
      switch (this.ordinal()) {
         case 0:
         case 3:
            var10000 = UNCONNECTED;
            break;
         case 1:
         case 2:
            var10000 = RIGHT;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public SideChainPart whenDisconnectedFromTheLeft() {
      SideChainPart var10000;
      switch (this.ordinal()) {
         case 0:
         case 1:
            var10000 = UNCONNECTED;
            break;
         case 2:
         case 3:
            var10000 = LEFT;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static SideChainPart[] $values() {
      return new SideChainPart[]{UNCONNECTED, RIGHT, CENTER, LEFT};
   }
}
