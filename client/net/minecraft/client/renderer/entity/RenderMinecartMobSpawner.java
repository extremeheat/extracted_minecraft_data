package net.minecraft.client.renderer.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.init.Blocks;

public class RenderMinecartMobSpawner extends RenderMinecart<EntityMinecartMobSpawner> {
   public RenderMinecartMobSpawner(RenderManager var1) {
      super(var1);
   }

   protected void func_180560_a(EntityMinecartMobSpawner var1, float var2, IBlockState var3) {
      super.func_180560_a(var1, var2, var3);
      if (var3.func_177230_c() == Blocks.field_150474_ac) {
         TileEntityMobSpawnerRenderer.func_147517_a(var1.func_98039_d(), var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2);
      }

   }
}
