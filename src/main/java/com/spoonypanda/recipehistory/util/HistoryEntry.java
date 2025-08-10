package com.spoonypanda.recipehistory.util;

import java.lang.reflect.Method;
import java.util.Objects;

import com.spoonypanda.recipehistory.RecipeHistoryJEIPlugin;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.runtime.IIngredientManager;

import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class HistoryEntry {
    public static final HistoryEntry EMPTY = new HistoryEntry(null, null, null);
    public final Object recipe;
    public final IFocus<?> focus;
    public final IIngredientType<?> type;

    public HistoryEntry(Object recipe, IFocus<?> focus, IIngredientType<?> type) {
        this.recipe = recipe;
        this.focus = focus;
        this.type = type;
    }

    public String getModName() {
        Object value = focus.getValue();
        if (value instanceof ItemStack) {
            ResourceLocation id = ((ItemStack) value).getItem().getRegistryName();
            return id != null ? id.getNamespace() : "unknown";
        } else if (value instanceof FluidStack) {
            ResourceLocation id = ((FluidStack) value).getFluid().getRegistryName();
            return id != null ? id.getNamespace() : "unknown";
        } else {
            try {
                Method getTypeMethod = value.getClass().getMethod("getType");
                Object fluidOrChemical = getTypeMethod.invoke(value);
                if (fluidOrChemical instanceof net.minecraftforge.registries.IForgeRegistryEntry) {
                    ResourceLocation id = ((IForgeRegistryEntry<?>) fluidOrChemical).getRegistryName();
                    return id != null ? id.getNamespace() : "unknown";
                }
            } catch (Exception ignored) {}
        }
        return "unknown";
    }

    @Override
    public String toString() {
        Object value = focus != null ? focus.getValue() : null;
        return value != null ? value.toString() : "unknown";
    }

    public boolean matches(HistoryEntry other) {
        if (other == null || this.focus == null || other.focus == null) return false;
        if (!Objects.equals(this.type, other.type)) return false;

        Object thisValue = this.focus.getValue();
        Object otherValue = other.focus.getValue();

        IJeiRuntime rt = RecipeHistoryJEIPlugin.getRuntime();
        if(rt == null) {
            if (thisValue.getClass() != otherValue.getClass()) return false;
            return thisValue.equals(otherValue);
        }
        IIngredientManager ingredientManager = rt.getIngredientManager();

        IIngredientHelper<Object> helper = (IIngredientHelper<Object>) ingredientManager.getIngredientHelper(this.type);

        String idA = helper.getUniqueId(thisValue, UidContext.Ingredient);
        String idB = helper.getUniqueId(otherValue, UidContext.Ingredient);

        return Objects.equals(idA, idB);
    }

}
