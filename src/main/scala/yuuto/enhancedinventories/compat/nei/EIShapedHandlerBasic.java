package yuuto.enhancedinventories.compat.nei;

import codechicken.nei.NEIClientUtils;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.DefaultOverlayRenderer;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.api.IStackPositioner;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import yuuto.enhancedinventories.compat.nei.positionstacks.ChildPositionStack;
import yuuto.enhancedinventories.compat.nei.positionstacks.ParentPositionStack;
import yuuto.enhancedinventories.config.recipe.RecipeDecorative;
import yuuto.enhancedinventories.gui.ContainerCraftingDummy;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EIShapedHandlerBasic extends TemplateRecipeHandler {
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), "crafting"));
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiCrafting.class;
    }

    @Override
    public String getRecipeName() {
        return NEIClientUtils.translate("recipe.shaped");
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if(!loadCraftingRecipesFromArray(outputId, results))
            super.loadCraftingRecipes(outputId, results);
    }

    public boolean loadCraftingRecipesFromArray(String outputId, Object[] results) {
        if (outputId.equals("crafting")) {
            ((List<IRecipe>) CraftingManager.getInstance().getRecipeList()).forEach(irecipe -> {
                CachedEIShapedRecipe recipe = null;

                if (irecipe instanceof RecipeDecorative) {
                    recipe = getCachedRecipe((RecipeDecorative) irecipe);
                }

                if (recipe != null) {
                    recipe.computeVisuals();
                    arecipes.add(recipe);
                }
            });

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        ((List<IRecipe>) CraftingManager.getInstance().getRecipeList()).forEach(irecipe -> {
            if (irecipe instanceof RecipeDecorative) {
                if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
                    CachedEIShapedRecipe recipe = getCachedRecipe((RecipeDecorative) irecipe);

                    if (recipe != null) {
                        recipe.computeVisuals();
                        arecipes.add(recipe);
                    }
                }
            }
        });
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        if (ingredient == null) {
            return;
        }

        ((List<IRecipe>) CraftingManager.getInstance().getRecipeList()).forEach(irecipe -> {
            if (irecipe != null && irecipe instanceof RecipeDecorative) {
                CachedEIShapedRecipe recipe = getCachedRecipe((RecipeDecorative) irecipe);

                if (recipe != null && recipe.contains(recipe.ingredients, ingredient.getItem())) {
                    recipe.computeVisuals();

                    if (recipe.contains(recipe.ingredients, ingredient)) {
                        recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                        arecipes.add(recipe);
                    }
                }
            }
        });
    }

    @Nullable
    private CachedEIShapedRecipe getCachedRecipe(RecipeDecorative recipe) {
        for (Object inpt : recipe.getInput()) {
            if (inpt instanceof ArrayList && ((ArrayList) inpt).isEmpty()) {
                return null;
            }
        }

        return new CachedEIShapedRecipe(recipe);
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "crafting";
    }

    @Override
    public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
        return super.hasOverlay(gui, container, recipe) ||
            isRecipe2x2(recipe) && RecipeInfo.hasDefaultOverlay(gui, "crafting2x2");
    }

    @Override
    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe) {
        IRecipeOverlayRenderer renderer = super.getOverlayRenderer(gui, recipe);
        if (renderer != null) {
            return renderer;
        }

        IStackPositioner positioner = RecipeInfo.getStackPositioner(gui, "crafting2x2");
        if (positioner == null) {
            return null;
        }

        return new DefaultOverlayRenderer(getIngredientStacks(recipe), positioner);
    }

    @Override
    public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe) {
        IOverlayHandler handler = super.getOverlayHandler(gui, recipe);
        if (handler != null) {
            return handler;
        }

        return RecipeInfo.getOverlayHandler(gui, "crafting2x2");
    }

    private boolean isRecipe2x2(int recipe) {
        for (PositionedStack stack : getIngredientStacks(recipe)) {
            if (stack.relx > 43 || stack.rely > 24) {
                return false;
            }
        }

        return true;
    }

    public class CachedEIShapedRecipe extends CachedRecipe {
        RecipeDecorative recipe;
        ArrayList<PositionedStack> ingredients;
        ArrayList<PositionedStack> uniIngredients;
        PositionedStack result;
        ParentPositionStack core;
        ParentPositionStack color;

        ContainerCraftingDummy dummyContainer;
        InventoryCrafting craftMatrix;

        public CachedEIShapedRecipe(RecipeDecorative recipe) {
            this.recipe = recipe;
            this.ingredients = new ArrayList<>();
            this.uniIngredients = new ArrayList<>();
            this.result = new PositionedStack(recipe.getRecipeOutput(), 119, 24);
            this.core = null;
            this.color = null;

            this.dummyContainer = new ContainerCraftingDummy(null);
            this.craftMatrix = dummyContainer.craftingMatrix();

            setIngredients(recipe);
        }

        public void setIngredients(RecipeDecorative recipe) {
            for (int x = 0; x < recipe.width(); x++) {
                for (int y = 0; y < recipe.height(); y++) {
                    int i = y * recipe.width() + x;

                    if (recipe.getInput()[i] != null) {
                        if (Arrays.asList(recipe.cores()).contains(i)) {
                            PositionedStack stack;

                            if (core == null) {
                                core = new ParentPositionStack(recipe.getInput()[i], 24 + x * 18, 6 + y * 18, false);
                                stack = core;
                                uniIngredients.add(stack);
                            } else {
                                stack = new ChildPositionStack(core, 25 + x * 18, 6 + y * 18);
                            }

                            stack.setMaxSize(1);
                            ingredients.add(stack);
                        } else if (Arrays.asList(recipe.colors()).contains(i)) {
                            PositionedStack stack;

                            if (color == null) {
                                color = new ParentPositionStack(recipe.getInput()[i], 25 + x * 18, 6 + y * 18, false);
                                stack = color;
                                uniIngredients.add(stack);
                            } else {
                                stack = new ChildPositionStack(color, 25 + x * 18, 6 + y * 18);
                            }

                            stack.setMaxSize(1);
                            ingredients.add(stack);
                        } else {
                            PositionedStack stack = new PositionedStack(recipe.getInput()[i], 25 + x * 18, 6 + y * 18, false);
                            stack.setMaxSize(1);
                            ingredients.add(stack);
                            uniIngredients.add(stack);
                        }
                    }
                }
            }
        }

        @Override
        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 20, ingredients);
        }

        @Override
        public List<PositionedStack> getCycledIngredients(int cycle, List<PositionedStack> ingredients) {
            for (int itemIndex = 0; itemIndex < ingredients.size(); itemIndex++) {
                if (!(ingredients.get(itemIndex) instanceof ChildPositionStack)) {
                    super.randomRenderPermutation(ingredients.get(itemIndex), cycle + itemIndex);
                }
            }

            return ingredients;
        }

        @Override
        public PositionedStack getResult() {
            return result;
        }

        public void computeVisuals() {
            uniIngredients.forEach(PositionedStack::generatePermutations);

            for (int i = 0; i < ingredients.size(); i++) {
                craftMatrix.setInventorySlotContents(i, ingredients.get(i).item);
            }

            result.item = this.recipe.getCraftingResult(craftMatrix);
        }
    }
}
