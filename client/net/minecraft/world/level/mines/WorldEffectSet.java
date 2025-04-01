package net.minecraft.world.level.mines;

import java.util.ArrayList;
import java.util.List;

public final class WorldEffectSet {
   private final List<WorldEffect> effects = new ArrayList();
   private final boolean exclusive;

   public WorldEffectSet(boolean var1) {
      super();
      this.exclusive = var1;
   }

   public List<WorldEffect> effects() {
      return this.effects;
   }

   public boolean exclusive() {
      return this.exclusive;
   }

   public void register(WorldEffect var1) {
      this.effects.add(var1);
   }
}
