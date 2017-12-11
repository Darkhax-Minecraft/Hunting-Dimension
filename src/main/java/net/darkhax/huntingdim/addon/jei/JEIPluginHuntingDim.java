package net.darkhax.huntingdim.addon.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.darkhax.bookshelf.registry.IVariant;
import net.darkhax.huntingdim.HuntingDim;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

@JEIPlugin
public class JEIPluginHuntingDim implements IModPlugin {

    @Override
    public void register (IModRegistry registry) {

        for (final Item item : HuntingDim.REGISTRY.getItems()) {

            if (item instanceof IVariant) {

                final String[] variants = ((IVariant) item).getVariant();

                for (int meta = 0; meta < variants.length; meta++) {

                    final String key = "jei." + item.getUnlocalizedName() + "." + variants[meta];
                    registry.addIngredientInfo(new ItemStack(item, 1, meta), ItemStack.class, key);
                }
            }

            else {

                final String key = "jei." + item.getUnlocalizedName();
                registry.addIngredientInfo(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE), ItemStack.class, key);
            }
        }
    }
}