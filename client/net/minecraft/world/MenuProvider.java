package net.minecraft.world;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuConstructor;

public interface MenuProvider extends MenuConstructor {
   Component getDisplayName();

   default List<Integer> getAdditionalData() {
      return List.of();
   }
}
