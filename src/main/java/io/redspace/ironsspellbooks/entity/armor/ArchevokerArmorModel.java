package io.redspace.ironsspellbooks.entity.armor;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.item.armor.ArchevokerArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ArchevokerArmorModel extends GeoArmorRenderer<ArchevokerArmorItem> {

    public ArchevokerArmorModel(){
        super();

    }
    @Override
    public ResourceLocation getModelResource(ArchevokerArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "geo/archevoker_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArchevokerArmorItem object) {
        return new ResourceLocation(IronsSpellbooks.MODID, "textures/models/armor/archevoker.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArchevokerArmorItem animatable) {
        return new ResourceLocation(IronsSpellbooks.MODID, "animations/wizard_armor_animation.json");
    }
//    public static String listOfBonesToString(List<IBone> list){
//        String s = "";
//        for (IBone o:list)
//            s += o.getName()+", ";
//        return s;
//    }
}