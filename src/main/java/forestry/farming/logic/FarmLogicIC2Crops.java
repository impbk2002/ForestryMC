/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.farming.logic;

import com.google.common.collect.HashMultimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.farming.FarmDirection;
import forestry.api.farming.ICrop;
import forestry.api.farming.IFarmHousing;
import forestry.core.utils.vect.Vect;
import forestry.plugins.compat.PluginIC2;
import ic2.api.item.IC2Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FarmLogicIC2Crops extends FarmLogic {

    public FarmLogicIC2Crops(IFarmHousing housing) {
        super(housing);
    }

    @Override
    public boolean isAcceptedWindfall(ItemStack stack) {
        return false;
    }

    @Override
    public int getFertilizerConsumption() {
        return 50;
    }

    @Override
    public int getWaterConsumption(float hydrationModifier) {
        return (int) (200 * hydrationModifier);
    }

    @Override
    public boolean isAcceptedResource(ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean isAcceptedGermling(ItemStack itemstack) {
        return false;
    }

    @Override
    public Collection<ItemStack> collect() {
        return null;
    }

    @Override
    public boolean cultivate(int x, int y, int z, FarmDirection direction, int extent) {
        GLOBAL_CROP_LOCATIONS.get(this.housing).forEach(v -> PluginIC2.instance.babysitCrop(v.getTileEntity()));
        return true;
    }

    //sigh, this logic gets reset *EACH TIME ITS CALLED FFS* well just place a static multimap here then...
    private static final HashMultimap<IFarmHousing, CropBasicIC2Crop> GLOBAL_CROP_LOCATIONS = HashMultimap.create();
    private static final HashMultimap<IFarmHousing, FarmDirection> GLOBAL_VISITED_DIRECTIONS = HashMultimap.create();

    private Collection<ICrop> getCropSet() {
        return GLOBAL_CROP_LOCATIONS
                .get(this.housing)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent) {
        if (this.housing.getWorld().getWorldTime() % 1200 != 0 && !GLOBAL_CROP_LOCATIONS.get(this.housing).isEmpty() && GLOBAL_VISITED_DIRECTIONS.get(this.housing).contains(direction))
            return getCropSet();

        Vect start = new Vect(x,y,z);
        for (int lastExtent = 0; lastExtent < extent; lastExtent++) {
            for (int i = -2; i < 3; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        Vect position = translateWithOffset(x, y + 1, z, direction, lastExtent);
                        Vect candidate = position.add(i, j, k);
                        if (Math.abs(candidate.x - start.x) > 5)
                            continue;

                        if (Math.abs(candidate.z - start.z) > 5)
                            continue;

                        Optional.ofNullable(
                                getCrop(this.getWorld(), candidate)
                        )
                                .ifPresent(f -> GLOBAL_CROP_LOCATIONS.get(this.housing).add(f));
                    }
                }
            }
        }

        GLOBAL_VISITED_DIRECTIONS.get(this.housing).add(direction);

        return getCropSet();
    }

    private CropBasicIC2Crop getCrop(World world, Vect position) {
        return (CropBasicIC2Crop) new FarmableBasicIC2Crop().getCropAt(world, position.x, position.y, position.z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return IC2Items.getItem("cropSeed").getIconIndex();
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal("for.circuit.manualIC2Crop");
    }
}
