package io.redspace.ironsspellbooks.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@OnlyIn(Dist.CLIENT)
public class SpellBookCurioRenderer implements ICurioRenderer {
    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (renderLayerParent.getModel() instanceof HumanoidModel<?>) {
            var humanoidModel = (HumanoidModel<LivingEntity>) renderLayerParent.getModel();
            humanoidModel.setupAnim(slotContext.entity(), limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            humanoidModel.prepareMobModel(slotContext.entity(), limbSwing, limbSwingAmount, partialTicks);

            poseStack.pushPose();
            humanoidModel.body.translateAndRotate(poseStack);
            //Negative X is right, Negative Z is Forward
            //Scale by 1/16th, we are now dealing with units of pixels
            poseStack.translate(-4 * .0625f, 11 * .0625f, 0);
            poseStack.mulPose(Vector3f.YP.rotation(Mth.PI * .5f));
            poseStack.mulPose(Vector3f.ZP.rotation(Mth.PI));
            poseStack.scale(.4f, .4f, .4f);
            itemRenderer.renderStatic(itemStack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, renderTypeBuffer, 0);
            poseStack.popPose();
        }
    }
}
