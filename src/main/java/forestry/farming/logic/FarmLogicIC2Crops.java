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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.*;

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
        return 10;
    }

    @Override
    public int getWaterConsumption(float hydrationModifier) {
        return (int) (40 * hydrationModifier);
    }

    @Override
    public boolean isAcceptedResource(ItemStack itemstack) {
        return false; //weedex maybe later
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
        return GLOBAL_CROP_REFERENCES.get(this.housing)
                .stream()
                .map(v -> PluginIC2.instance.babysitCrop(v.getTileEntity()))
                .reduce(false, (a, b) -> a || b);
    }

    //sigh, this logic gets reset *EACH TIME ITS CALLED FFS* well just place a static multimap here then...
    private static final HashMultimap<IFarmHousing, CropBasicIC2Crop> GLOBAL_CROP_REFERENCES = HashMultimap.create();
    private static final HashMultimap<IFarmHousing, FarmDirection> GLOBAL_VISITED_DIRECTIONS = HashMultimap.create();

    private Collection<ICrop> getCropSet() {
        Set<ICrop> set = new HashSet<>();
        Set<CropBasicIC2Crop> removal = new HashSet<>();
        Set<CropBasicIC2Crop> dis = GLOBAL_CROP_REFERENCES.get(this.housing);

        for (CropBasicIC2Crop cropBasicIC2Crop : dis)
            if (cropBasicIC2Crop == null || !cropBasicIC2Crop.isAlive())
                removal.add(cropBasicIC2Crop);
            else if (cropBasicIC2Crop.isHarvestable())
                set.add(cropBasicIC2Crop);

        dis.removeAll(removal);
        return set;
    }

    private static final byte SIDE_LENGTH = 17;

    private static byte getOffsetLength(){
        return (SIDE_LENGTH / 3) + 1; //PI = 3, TAKE IT OR LEAVE IT.
    }

    @Override
    public Collection<ICrop> harvest(int x, int y, int z, FarmDirection direction, int extent) {
        if (
                   this.housing.getWorld().getWorldTime() % 2 != 0
                && !GLOBAL_CROP_REFERENCES.get(this.housing).isEmpty()
                && GLOBAL_VISITED_DIRECTIONS.get(this.housing).contains(direction)
        )
            return getCropSet();

        if (
                   this.housing.getWorld().getWorldTime() % 2 == 0
                && GLOBAL_VISITED_DIRECTIONS.get(this.housing).containsAll(Arrays.asList(FarmDirection.values()))
        )
            GLOBAL_VISITED_DIRECTIONS.get(this.housing).clear();
        for (int lastExtent = 0; lastExtent <= extent; lastExtent++) {
            for (int lx = -getOffsetLength(); lx <= getOffsetLength(); lx++) {
                for (int ly = 0; ly < 6; ly++) {
                    for (int lz = -getOffsetLength(); lz <= getOffsetLength(); lz++) {
                        Vect pos = translateWithOffset(lx + x, ly + y + 1,lz + z, direction, lastExtent);
                        if (ly == 0)
                            getWorld().setBlock(pos.x,pos.y + 12,pos.z,Blocks.glowstone);
                        Optional.ofNullable(
                                getCrop(this.getWorld(), translateWithOffset(lx + x, ly + y + 1,lz + z, direction, lastExtent))
                        )
                                .ifPresent(f -> GLOBAL_CROP_REFERENCES.put(this.housing, f));
                    }
                }
            }
        }

        GLOBAL_VISITED_DIRECTIONS.put(this.housing, direction);
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
