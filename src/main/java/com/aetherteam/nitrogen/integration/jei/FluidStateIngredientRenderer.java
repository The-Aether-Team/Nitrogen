package com.aetherteam.nitrogen.integration.jei;

import com.aetherteam.nitrogen.client.renderer.state.FluidStateRenderState;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record FluidStateIngredientRenderer<T>(IPlatformFluidHelperInternal<T> fluidHelper) implements IIngredientRenderer<T> {

    @Override
    public void render(GuiGraphics graphics, @Nullable T ingredient) {
        if (ingredient != null) {
            IIngredientTypeWithSubtypes<Fluid, T> type = this.fluidHelper.getFluidIngredientType();
            Fluid fluidType = type.getBase(ingredient);
            FluidState fluidState = fluidType.defaultFluidState();

            //grab the x and y translation from the matrix for proper positioning
            int x = (int) graphics.pose().m20();
            int y = (int) graphics.pose().m21();

            graphics.submitPictureInPictureRenderState(new FluidStateRenderState(fluidState, x, y, x + 256, y + 256, graphics.peekScissorStack()));
        }
    }

    @Override
    public List<Component> getTooltip(T ingredient, TooltipFlag tooltipFlag) {
        List<Component> components = new ArrayList<>();
        if (ingredient instanceof FluidStack fluidStack) {
            Fluid fluid = fluidStack.getFluid();
            if (fluid.isSame(Fluids.EMPTY)) {
                return List.of();
            }

            Component displayName = this.fluidHelper().getDisplayName(ingredient);
            components.add(displayName);

            if (tooltipFlag.isAdvanced()) {
                Registry<Fluid> fluidRegistry = RegistryUtil.getRegistry(Registries.FLUID);
                Identifier resourceLocation = fluidRegistry.getKey(fluid);
                if (resourceLocation != null && resourceLocation != BuiltInRegistries.FLUID.getDefaultKey()) {
                    MutableComponent advancedId = Component.literal(resourceLocation.toString())
                        .withStyle(ChatFormatting.DARK_GRAY);
                    components.add(advancedId);
                }
            }
        }
        return components;
    }
}
