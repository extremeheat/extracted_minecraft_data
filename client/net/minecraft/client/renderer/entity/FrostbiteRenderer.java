package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.zombie.BabyFrostbiteModel;
import net.minecraft.client.model.monster.zombie.FrostbiteModel;
import net.minecraft.client.model.monster.zombie.ZombieModel;
import net.minecraft.client.renderer.entity.layers.FrostbiteOuterLayer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.zombie.Frostbite;

public class FrostbiteRenderer extends AbstractZombieRenderer<Frostbite, ZombieRenderState, ZombieModel<ZombieRenderState>> {
   private static final Identifier FROSTBITE_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/frostbite.png");
   private static final Identifier FROSTBITE_BABY_LOCATION = Identifier.withDefaultNamespace("textures/entity/zombie/frostbite_baby.png");

   public FrostbiteRenderer(final EntityRendererProvider.Context context) {
      super(context, new FrostbiteModel(context.bakeLayer(ModelLayers.FROSTBITE)), new BabyFrostbiteModel(context.bakeLayer(ModelLayers.FROSTBITE_BABY)), ArmorModelSet.bake(ModelLayers.FROSTBITE_ARMOR, context.getModelSet(), FrostbiteModel::new), ArmorModelSet.bake(ModelLayers.FROSTBITE_BABY_ARMOR, context.getModelSet(), BabyFrostbiteModel::new));
      this.addLayer(new FrostbiteOuterLayer(this, context.getModelSet()));
   }

   public ZombieRenderState createRenderState() {
      return new ZombieRenderState();
   }

   public Identifier getTextureLocation(final ZombieRenderState state) {
      return state.isBaby ? FROSTBITE_BABY_LOCATION : FROSTBITE_LOCATION;
   }
}
