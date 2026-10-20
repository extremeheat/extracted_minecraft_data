package net.minecraft.world.inventory;

import java.util.OptionalInt;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;

public class JukeboxMenu extends ChestMenu {
   private final Container container;
   private final ContainerData data;

   public JukeboxMenu(final int containerId, final Inventory inventory) {
      this(containerId, inventory, new SimpleContainer(27), new SimpleContainerData(1));
   }

   public JukeboxMenu(final int containerId, final Inventory inventory, final Container container, final ContainerData data) {
      super(MenuType.JUKEBOX, containerId, inventory, container, 3);
      this.container = container;
      this.data = data;
      this.addDataSlots(data);
   }

   public OptionalInt getPlayingNextPos() {
      int pos = this.data.get(0);
      return pos != -1 ? JukeboxBlockEntity.findNextPlaylistPosition(this.container, pos) : OptionalInt.empty();
   }
}
