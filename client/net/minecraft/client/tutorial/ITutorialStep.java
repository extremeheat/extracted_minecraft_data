package net.minecraft.client.tutorial;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public interface ITutorialStep {
   default void func_193248_b() {
   }

   default void func_193245_a() {
   }

   default void func_193247_a(MovementInput var1) {
   }

   default void func_195870_a(double var1, double var3) {
   }

   default void func_193246_a(WorldClient var1, RayTraceResult var2) {
   }

   default void func_193250_a(WorldClient var1, BlockPos var2, IBlockState var3, float var4) {
   }

   default void func_193251_c() {
   }

   default void func_193252_a(ItemStack var1) {
   }
}
